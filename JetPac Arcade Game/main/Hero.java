package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * Hero class: the main controllable character in the game.
 *
 * @author Sam Stieby and James Kelley
 *
 */
public class Hero extends Entity {
	// Graphics constants
	private static final double HERO_WIDTH = 50;
	private static final double HERO_HEIGHT = 100;
	private static final double VISOR_WIDTH = 35;
	private static final double VISOR_HEIGHT = 15;
	private static final double GUN_WIDTH = 40;
	private static final double GUN_HEIGHT = 10;

	// Image declarations;
	BufferedImage leftImg;
	BufferedImage rightImg;

	// Physics constants
	private static final double MAX_HOR_VEL = 6;
	private static final double MAX_RISE_VEL = 6;
	private static final double FALL_ACCELERATION = 0.2;
	private static final double MAX_FALL_VEL = 20;
	private static final double KINETIC_FRICTION_ACCELERATION = 0.2;
	private static final double STATIC_FRICTION_SPEED_CUTOFF = 0.3;

	// Default constants
	private static final double DEFAULT_START_X = 500;
	private static final int DEFAULT_START_DIR = 1;

	// Frame constants
	private static final double X_MIN_LIMIT = 0;
	private static final double X_MAX_LIMIT = 1280;
	private static final double Y_MIN_LIMIT = 0 + HERO_HEIGHT;
	private static final double Y_MAX_LIMIT = 720;

	// Other constants
	private static final int SHOT_DELAY = 30;
	private static final int POINTS = 0;

	// Movement inputs
	private boolean upPressed;
	private boolean downPressed;
	private boolean leftPressed;
	private boolean rightPressed;

	// Shooting inputs
	private boolean spacePressed;
	private boolean spaceReleased;

	// Physics states
	private boolean crouching;
	private boolean doFriction;

	// Calculated
	private int direction; // Either -1 (left) or +1 (right)

	// Shooting fields
	private ArrayList<Bullet> bullets;
	private int ticksSinceLastShot;

	// Other fields
	private Collectible collectible; // Either the held Collectible or null

	public Hero() {
		new Hero(DEFAULT_START_X);
	}

	/**
	 * Creates a Hero based on the x-coordinate it will spawn at. Assigns multiple
	 * variables based on movement.
	 * 
	 * @param x
	 */
	public Hero(double x) {
		// (X,Y) is the center of the standing hitbox OR top middle of crouching hitbox
		this.xVal = x;
		this.yVal = 1280;
		this.constructHitbox();

		// Movement inputs
		this.upPressed = false;
		this.downPressed = false;
		this.leftPressed = false;
		this.rightPressed = false;

		// Shooting inputs
		this.spacePressed = false;
		this.spaceReleased = true;

		// Physics states
		this.crouching = false;
		this.doFriction = false;

		// Initial conditions
		this.direction = DEFAULT_START_DIR;

		// Other
		this.points = POINTS;
		this.collectible = null;
	}

	/**
	 * Places the Hero on top of a platform, if the platform is underneath. Simulates
	 * falling sand physics.
	 * 
	 * @param p
	 */
	public void setOnTopOfPlatform(Platform p) {
		if (p.getHitbox().contains(this.xVal, p.getHitbox().getCenterY())) {
			this.yVal = Math.min(this.yVal, p.getHitbox().getMinY());
		}
	}

	/**
	 * Handles keyboard input
	 * 
	 * @param str - String that holds a key that is pressed
	 */
	public void handle(String str) {
		if (str.equals("up")) {
			this.upPressed = true;
		} else if (str.equals("down")) {
			this.downPressed = true;
		} else if (str.equals("left")) {
			this.leftPressed = true;
		} else if (str.equals("right")) {
			this.rightPressed = true;
		} else if (str.equals("space")) {
			// Promotes spamming the space bar
			if (this.spaceReleased) {
				this.spacePressed = true;
				this.spaceReleased = false;
				this.ticksSinceLastShot = SHOT_DELAY;
			}
		} else if (str.equals("period")) {
			this.drop();
		} else if (str.equals("stop up")) {
			this.upPressed = false;
		} else if (str.equals("stop down")) {
			this.downPressed = false;
		} else if (str.equals("stop left")) {
			this.leftPressed = false;
		} else if (str.equals("stop right")) {
			this.rightPressed = false;
		} else if (str.equals("stop space")) {
			this.spacePressed = false;
			this.spaceReleased = true;
		}
	}

	/**
	 * Changes velocity based on keys pressed and state of gravity/movement.
	 */
	public void updateVelocity() {
		updateHorizontalVelocity();
		if (this.doFriction) {
			this.addFriction();
		}
		updateVerticalVelocity();
	}

	/**
	 * Helper function for updateVelocity. Determines horizontal velocity from
	 * control inputs.
	 */
	private void updateHorizontalVelocity() {
		if (leftPressed && rightPressed) {
			this.horizontalVelocity = 0;
		} else {
			if (leftPressed) { // Just leftward
				this.horizontalVelocity = -4;
				this.doFriction = false;
			} else if (rightPressed) { // Just rightward
				this.horizontalVelocity = 4;
				this.doFriction = false;
			} else { // Neither, allows friction
				this.doFriction = true;
			}
		}
	}
	
	/**
	 * Helper function for updateVelocity. Determines vertical velocity from
	 * control inputs.
	 */
	private void updateVerticalVelocity() {
		if (upPressed && downPressed) {
		} else {
			if (upPressed) { // Jetpack physics, not falling
				this.verticalVelocity = Math.min(this.verticalVelocity, 4); // Allows feathering to float downward
				this.verticalVelocity += -1;
				this.crouching = false;
			} else if (downPressed) { // Heavy fall
				this.horizontalVelocity /= 2; // Move slower sideways as you force downward
				this.verticalVelocity += 4 * FALL_ACCELERATION; // Totals 4 * Gravity
				this.crouching = true;
			} else { // Just fall
				this.crouching = false;
				this.verticalVelocity += FALL_ACCELERATION; // Gravity
			}
		}
	}
	
	/**
	 * Applies friction to movement
	 */
	private void addFriction() {
		// Kinetic Friction
		if (this.horizontalVelocity > 0) { // If going right, slow down toward left
			this.horizontalVelocity -= KINETIC_FRICTION_ACCELERATION;
		}
		if (this.horizontalVelocity < 0) { // If going left, slow down toward right
			this.horizontalVelocity += KINETIC_FRICTION_ACCELERATION;
		}

		// Static friction
		if (Math.abs(this.horizontalVelocity) < STATIC_FRICTION_SPEED_CUTOFF) { // If moving slowly, stop
			this.horizontalVelocity = 0;
			this.doFriction = false;
		}
	}

	/**
	 * Keeps Hero's velocity in spec. If Hero is falling, velocity is faster.
	 */
	private void constrainVelocity() {
		this.horizontalVelocity = Math.max(this.horizontalVelocity, -MAX_HOR_VEL);
		this.horizontalVelocity = Math.min(this.horizontalVelocity, MAX_HOR_VEL);

		this.verticalVelocity = Math.max(this.verticalVelocity, -MAX_RISE_VEL); // Can't go faster than MAX_RISE_FALL
		if (crouching) {
			this.verticalVelocity = Math.min(this.verticalVelocity, 2 * MAX_FALL_VEL); // Heavy Fall
		} else {
			this.verticalVelocity = Math.min(this.verticalVelocity, MAX_FALL_VEL); // Fall
		}

		// Compute direction
		try {
			this.direction = (((int) this.horizontalVelocity / Math.abs((int) this.horizontalVelocity)));
		} catch (ArithmeticException e) { // Division by 0 error doesn't change current direction
		}
	}

	/**
	 * Restricts Hero's position to within the frame.
	 */
	private void constrainPosition() {
		if (this.xVal < X_MIN_LIMIT) {
			this.xVal += X_MAX_LIMIT - X_MIN_LIMIT; // If on left side of screen, teleport to right side of screen
		} else if (this.xVal > X_MAX_LIMIT) {
			this.xVal -= X_MAX_LIMIT - X_MIN_LIMIT; // If on right side of screen, teleport to left side of screen
		}

		// Constrain yVal to frame
		this.yVal = Math.max(this.yVal, Y_MIN_LIMIT); // Prevent leaving top of screen
		this.yVal = Math.min(this.yVal, Y_MAX_LIMIT); // Prevent erroneously leaving bottom of screen
	}

	/**
	 * Picks up collectible.
	 * 
	 * @param c - collectible to be picked up
	 */
	public void collect(Collectible c) {
		if (this.collectible == null) { // Detects if not already holding a collectible
			if(c.setActiveHero(this)) { // Sets owner if possible;
				this.collectible = c;
			}
		}
	}

	/**
	 * Drops held collectible.
	 */
	public void drop() {
		if (collectible != null) { // Detects if holding a collectible
			this.collectible.removeActiveHero(); // Removes owner
			this.collectible = null;
		}
	}

	/**
	 * Shoots a bullet outside of gun. Has no vertical velocity.
	 */
	private void shoot() {
		if (this.ticksSinceLastShot >= SHOT_DELAY) {
			this.ticksSinceLastShot = 0;
			double bullet_hVel = (MAX_HOR_VEL + 1) * direction; // Fires left or right faster than moving
			double bullet_x = this.hitbox.getCenterX() + ((HERO_WIDTH / 2 + 10) * direction); // Spawns bullet outside
																								// of this.hitbox
			Bullet b = new Bullet(bullet_x, this.hitbox.getCenterY(), bullet_hVel, 0);
			bullets.add(b);
		}
	}

	/**
	 * Creates hitbox to calculate collisions.
	 */
	private void constructHitbox() {
		// Constructs from the bottom center of the hero
		if (crouching) { // Half-size hitbox
			hitbox = new Rectangle2D.Double(this.xVal - (HERO_WIDTH / 2), this.yVal - (HERO_HEIGHT / 2), HERO_WIDTH,
					(HERO_HEIGHT / 2));
		} else {
			hitbox = new Rectangle2D.Double(this.xVal - (HERO_WIDTH / 2), this.yVal - HERO_HEIGHT, HERO_WIDTH,
					HERO_HEIGHT);
		}
	}

	/**
	 * Calls all functional methods every tick.
	 */
	public void updateOnTick() { // called every frame
		// Movement mechanics
		this.updateVelocity();
		this.constrainVelocity();
		this.updatePosition();
		this.constrainPosition();
		this.constructHitbox();

		// Shooting mechanics
		this.ticksSinceLastShot++;
		if (this.spacePressed) {
			this.shoot();
		}
	}

	/**
	 * Sets bullets list to prevent message chaining.
	 * 
	 * @param b - Arraylist of bullets to initialize.
	 */
	public void setBullets(ArrayList<Bullet> b) {
		this.bullets = b;
	}
	
	/**
	 * Checks platforms' hitboxes and compares to Hero's hitbox to keep Hero from phasing through platforms
	 * 
	 * @param platform - Platform to be avoided
	 */
	public void avoidPlatform(Platform platform) {
		Rectangle2D pHitbox = platform.getHitbox();
		constructHitbox();

		if (this.hitbox.intersects(pHitbox)) {
			// Hero Bounds, reduces method calls for speed
			double hMaxX = this.hitbox.getMaxX();
			double hMinX = this.hitbox.getMinX();
			double hMaxY = this.hitbox.getMaxY();
			double hMinY = this.hitbox.getMinY();

			// Platform Bounds, reduces method calls for speed
			double pMaxX = pHitbox.getMaxX();
			double pMinX = pHitbox.getMinX();
			double pMaxY = pHitbox.getMaxY();
			double pMinY = pHitbox.getMinY();

			// Corner Cases
			double distToTop = Math.max(pMinY - hMinY, 0);
			double distToBottom = Math.max(hMaxY - pMaxY, 0);
			double distToLeft = Math.max(pMinX - hMinX, 0);
			double distToRight = Math.max(hMaxX - pMaxX, 0);

			if ((distToTop > 0) && (distToBottom > 0)) {
				distToTop = 0;
				distToBottom = 0;
			}

			boolean containsUL = this.hitbox.contains(pMinX, pMinY);
			boolean containsLL = this.hitbox.contains(pMinX, pMaxY);
			boolean containsUR = this.hitbox.contains(pMaxX, pMinY);
			boolean containsLR = this.hitbox.contains(pMaxX, pMaxY);

			double directionToMove = 0; // Is a double variable for speed

			if ((distToTop == 0) && (distToBottom == 0)) {
				if (containsUL || containsLL) {
					directionToMove = distToLeft;
				} else {
					directionToMove = distToRight;
				}
			} else {
				if (containsUL && containsLL) {
					directionToMove = distToLeft;
				} else if (containsUR && containsLR) {
					directionToMove = distToRight;
				} else {
					if (containsUL) {
						double heroRightToPlatLeft = pMinX - hMaxX;
						double heroBottomToPlatTop = pMinY - hMaxY;
						if (heroRightToPlatLeft < heroBottomToPlatTop) {
							directionToMove = distToBottom;
						} else {
							directionToMove = distToLeft;
						}
					} else if (containsLL) {
						double heroRightToPlatLeft = pMinX - hMaxX;
						double heroTopToPlatBottom = hMinY - pMaxY;
						if (heroRightToPlatLeft < heroTopToPlatBottom) {
							directionToMove = distToTop;
						} else {
							directionToMove = distToLeft;
						}
					} else if (containsUR) {
						double heroLeftToPlatRight = hMinX - pMaxX;
						double heroBottomToPlatTop = pMinY - hMaxY;
						if (heroLeftToPlatRight < heroBottomToPlatTop) {
							directionToMove = distToBottom;
						} else {
							directionToMove = distToRight;
						}
					} else if (containsLR) {
						double heroLeftToPlatRight = hMinX - pMaxX;
						double heroTopToPlatBottom = hMinY - pMaxY;
						if (heroLeftToPlatRight < heroTopToPlatBottom) {
							directionToMove = distToTop;
						} else {
							directionToMove = distToRight;
						}
					}
				}
			}

			if (directionToMove == distToTop) {
				this.yVal = pMaxY + this.hitbox.getHeight();
				this.verticalVelocity = 0;
			} else if (directionToMove == distToBottom) {
				this.yVal = pMinY;
				this.verticalVelocity = 0;
			} else if (directionToMove == distToLeft) {
				this.xVal = pMinX - (HERO_WIDTH / 2);
				this.horizontalVelocity = 0;
			} else if (directionToMove == distToRight) {
				this.xVal = pMaxX + (HERO_WIDTH / 2);
				this.horizontalVelocity = 0;
			}
		} else {
		}
	}
	
	/**
	 * Draws the designated sprite at the location.  Sprite switches if Hero changes direction.
	 * @param g2 - Graphics object used to draw
	 */
	public void drawOn(Graphics2D g2) { // called every frame
		super.paintComponent(g2);
		this.constructHitbox();

		try {
			leftImg = ImageIO.read(new File("Sprites/heroLeftSprite.png"));
			rightImg = ImageIO.read(new File("Sprites/heroRightSprite.png"));
		} catch (IOException e) {

		}
		// Drawing Methods
		// Draw Body

		if (this.crouching) {
			if (this.direction == -1) {
				g2.drawImage(leftImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), (int) HERO_WIDTH,
						(int) HERO_HEIGHT / 2, null);
			} else {
				g2.drawImage(rightImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), (int) HERO_WIDTH,
						(int) HERO_HEIGHT / 2, null);
			}
		} else {
			if (this.direction == 1) {
				g2.drawImage(rightImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
			} else {
				g2.drawImage(leftImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
			}
		}

		// Draw Visor
		Ellipse2D visor = new Ellipse2D.Double(this.hitbox.getCenterX() + VISOR_WIDTH * (((direction + 1) / 2) - 1),
				this.hitbox.getCenterY() - this.hitbox.getHeight() * 0.45, VISOR_WIDTH, VISOR_HEIGHT);
		g2.setColor(Color.BLACK);
		g2.draw(visor);
		g2.setColor(Color.GRAY);
		g2.fill(visor);

		// Draw Gun
		Rectangle2D gun = new Rectangle2D.Double(this.hitbox.getCenterX() + GUN_WIDTH * (((direction + 1) / 2) - 1),
				this.hitbox.getCenterY() - (GUN_HEIGHT / 2), GUN_WIDTH, GUN_HEIGHT);
		g2.setColor(Color.DARK_GRAY);
		g2.draw(gun);
		g2.fill(gun);
	}

	/**
	 * Ensures the alien's dimension is correct for drawing.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) HERO_WIDTH, (int) HERO_HEIGHT);
	}
} // Hero