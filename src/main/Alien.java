package main;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Alien class enables creation and drawing of alien based on location in the
 * level text files and type also chosen in the level text files. Also handles
 * movement for different aliens.
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public class Alien extends Entity {
	// Graphics constants
	private static final double ALIEN_WIDTH = 25;
	private static final double ALIEN_HEIGHT = 25;

	// Image constants
	BufferedImage img1;
	BufferedImage img2;

	// Physics constants
	private static final double MAX_HOR_VEL = 3;
	private static final double MAX_VERT_VEL = 3;

	// Frame constants
	private static final double X_LIMIT = 1280;
	private static final double Y_LIMIT = 720 - ALIEN_HEIGHT;

	// Default constants
	private static final String DEFAULT_TYPE = "regular";
	private static final double START_X = 500;
	private static final double START_Y = 500;
	private static final int POINTS = 5;

	// Other constants
	private static final int SHOT_DELAY = 180;
	private static final int MOVE_DELAY = 1080;

	// Common fields
	private String type;
	private Hero activeHero;
	private ArrayList<Bullet> bullets;

	// Calculated
	private int direction; // Either -1 (left) or +1 (right)

	// Other fields
	private int ticksSinceLastShot;
	private double lastMoveChange;

	public Alien() {
		new Alien(DEFAULT_TYPE, START_X, START_Y);
	}

	public Alien(String alienType, double x, double y) {
		// (X,Y) is upper-left corner
		this.xVal = x - (ALIEN_WIDTH / 2);
		this.yVal = y - (ALIEN_HEIGHT / 2);
		this.constructHitbox();

		// Given
		this.type = alienType;

		// Initial conditions
		this.direction = 1;
		this.points = POINTS;
		this.ticksSinceLastShot = (int) (new Random().nextFloat() * SHOT_DELAY);
		this.lastMoveChange = 0;
	}

	/**
	 * Moves the alien based on type and a random choice of velocity values in
	 * arrays. Changes direction every 1.08 seconds. Alien shoots and has its
	 * position and velocity constrained.
	 */
	private void strategy1() { // Can shoot
		double yVelo = 1;
		double xVelo = 1;
		double[] xVelos = { -1, 2, -2, 1, -2, 2 };
		double[] yVelos = { -1, 2, -2, 1, -2, 2 };
		xVelo = xVelos[new Random().nextInt(xVelos.length)];
		yVelo = yVelos[new Random().nextInt(yVelos.length)];
		if ((System.currentTimeMillis() - lastMoveChange) >= MOVE_DELAY) {
			this.horizontalVelocity = xVelo;
			this.verticalVelocity = yVelo;
			lastMoveChange = System.currentTimeMillis();
		}

		this.constrainVelocity();
		this.updatePosition();
		this.constrainPosition();

		this.shoot();
	}

	/**
	 * Moves special aliens on the most direct path towards the hero. Position and
	 * velocity are constrained.
	 */
	private void strategy2() {
		// Goes directly toward the active hero
		this.horizontalVelocity = activeHero.getHitbox().getCenterX() - (this.xVal + (ALIEN_WIDTH / 2));
		this.verticalVelocity = activeHero.getHitbox().getCenterY() - (this.yVal + (ALIEN_HEIGHT / 2));

		double velocitySpeed = Math.sqrt(Math.pow(this.horizontalVelocity, 2) + Math.pow(this.verticalVelocity, 2));

		this.horizontalVelocity = MAX_HOR_VEL * this.horizontalVelocity / velocitySpeed;
		this.verticalVelocity = MAX_VERT_VEL * this.verticalVelocity / velocitySpeed;

		this.constrainVelocity();
		this.updatePosition();
		this.constrainPosition();
	}

	/**
	 * Restricts the velocity so an alien never moves faster than it should.
	 */
	private void constrainVelocity() {
		// Constrain to +/- MAX_HOR_VEL px/frame
		this.horizontalVelocity = Math.max(this.horizontalVelocity, -MAX_HOR_VEL);
		this.horizontalVelocity = Math.min(this.horizontalVelocity, MAX_HOR_VEL);

		// Constrain to +/- MAX_VERT_VEL px/frame
		this.verticalVelocity = Math.max(this.verticalVelocity, -MAX_VERT_VEL);
		this.verticalVelocity = Math.min(this.verticalVelocity, MAX_VERT_VEL);

		// Compute direction
		try {
			this.direction = (((int) this.horizontalVelocity / Math.abs((int) this.horizontalVelocity)));
		} catch (ArithmeticException e) {
		}
	}

	/**
	 * Restricts aliens to the frame so they are visible at all times.
	 */
	private void constrainPosition() {
		// Constrain xVal to frame
		if (this.xVal < 0) {
			this.xVal += X_LIMIT; // If on left side of screen, teleport to right side of screen
		} else if (this.xVal > X_LIMIT) {
			this.xVal -= X_LIMIT; // If on right side of screen, teleport to left side of screen
		}

		// Constrain yVal to frame
		this.yVal = Math.max(this.yVal, 0);
		this.yVal = Math.min(this.yVal, Y_LIMIT);
	}

	/**
	 * Calls every tick, updates changes resulting from alien strategies based on
	 * type.
	 */
	public void updateOnTick() {
		if (this.type == "regular") {
			this.strategy1();
		} else if (this.type == "special") {
			this.strategy2();
		}
	}

	/**
	 * Creates a bullet that the alien "shoots". Restricts bullets from spawning
	 * inside alien hitboxes.
	 */
	public void shoot() {
		if (this.ticksSinceLastShot >= SHOT_DELAY) {
			this.ticksSinceLastShot = 0;
			double bullet_x = this.hitbox.getCenterX();
			bullet_x += ((ALIEN_WIDTH / 2) + 10) * direction;
			double bullet_hVel = this.horizontalVelocity;
			bullet_hVel *= 3;
			bullet_hVel = Math.max(-3 * MAX_HOR_VEL, bullet_hVel);
			bullet_hVel = Math.min(3 * MAX_HOR_VEL, bullet_hVel);
			double bullet_vVel = this.verticalVelocity;
			bullet_vVel *= 3;
			bullet_vVel = Math.max(-3 * MAX_VERT_VEL, bullet_vVel);
			bullet_vVel = Math.min(3 * MAX_VERT_VEL, bullet_vVel);
			Bullet b = new Bullet(bullet_x, this.hitbox.getCenterY(), bullet_hVel, bullet_vVel);
			bullets.add(b);
		}
	}

	/**
	 * Creates hitbox for aliens.
	 */
	private void constructHitbox() {
		hitbox = new Rectangle2D.Double(this.xVal, this.yVal, ALIEN_WIDTH, ALIEN_HEIGHT);
	}

	/**
	 * Sets active hero for special aliens that follow the hero specified.
	 * 
	 * @param h - Hero to set active
	 */
	public void setActiveHero(Hero h) {
		this.activeHero = h;
	}

	/**
	 * Stores the bullets list to promote encapsulation.
	 * 
	 * @param b - Arraylist of bullets to initialize.
	 */
	public void setBullets(ArrayList<Bullet> b) {
		this.bullets = b;
	}

	/**
	 * Restricts aliens from phasing into platforms.
	 * 
	 * @param platform - Platform to be avoided.
	 */
	public void avoidPlatform(Platform platform) {
		Rectangle2D pHitbox = platform.getHitbox();
		constructHitbox();
		if (this.hitbox.intersects(pHitbox)) {

			// Platform Bounds
			double pMaxX = pHitbox.getMaxX();
			double pMinX = pHitbox.getMinX();
			double pMaxY = pHitbox.getMaxY();
			double pMinY = pHitbox.getMinY();

			// Corner Cases
			double distToTop = Math.max(pMinY - this.hitbox.getMinY(), 0);
			double distToBottom = Math.max(this.hitbox.getMaxY() - pMaxY, 0);
			double distToLeft = Math.max(pMinX - this.hitbox.getMinX(), 0);
			double distToRight = Math.max(this.hitbox.getMaxX() - pMaxX, 0);

			if ((distToTop > 0) && (distToBottom > 0)) {
				distToTop = 0;
				distToBottom = 0;
			}

			boolean containsUL = this.hitbox.contains(pMinX, pMinY);
			boolean containsLL = this.hitbox.contains(pMinX, pMaxY);
			boolean containsUR = this.hitbox.contains(pMaxX, pMinY);
			boolean containsLR = this.hitbox.contains(pMaxX, pMaxY);

			double highest = -1;
			// Assumes taller than platform
			if ((distToTop == 0) && (distToBottom == 0)) {
				if (containsUL || containsLL) {
					highest = distToLeft;
				} else if (containsUR || containsLR) {
					highest = distToRight;
				} else {
					if (this.hitbox.contains(pMinX, this.hitbox.getCenterY())) {
						highest = distToLeft;
					} else if (this.hitbox.contains(pMaxX, this.hitbox.getCenterY())) {
						highest = distToRight;
					}
				}
			} else {
				if (containsUL && containsLL) {
					highest = distToLeft;
				} else if (containsUR && containsLR) {
					highest = distToRight;
				} else {
					if (containsUL) {
						double alienRightToPlatLeft = pMinX - this.hitbox.getMaxX();
						double alienBottomToPlatTop = pMinY - this.hitbox.getMaxY();
						if (alienRightToPlatLeft < alienBottomToPlatTop) {
							highest = distToTop;
						} else {
							highest = distToLeft;
						}
					} else if (containsLL) {
						double alienRightToPlatLeft = pMinX - this.hitbox.getMaxX();
						double alienTopToPlatBottom = this.hitbox.getMinY() - pMaxY;
						if (alienRightToPlatLeft < alienTopToPlatBottom) {
							highest = distToBottom;
						} else {
							highest = distToLeft;
						}
					} else if (containsUR) {
						double alienLeftToPlatRight = this.hitbox.getMinX() - pMaxX;
						double alienBottomToPlatTop = pMinY - this.hitbox.getMaxY();
						if (alienLeftToPlatRight < alienBottomToPlatTop) {
							highest = distToTop;
						} else {
							highest = distToRight;
						}
					} else if (containsLR) {
						double alienLeftToPlatRight = this.hitbox.getMinX() - pMaxX;
						double alienTopToPlatBottom = this.hitbox.getMinY() - pMaxY;
						if (alienLeftToPlatRight < alienTopToPlatBottom) {
							highest = distToBottom;
						} else {
							highest = distToRight;
						}
					} else {
						if (this.hitbox.contains(this.hitbox.getCenterX(), pMaxY)) {
							highest = distToBottom;
						} else if (this.hitbox.contains(this.hitbox.getCenterX(), pMinY)) {
							highest = distToTop;
						}
					}
				}
			}

			if (highest == distToTop) {
				this.yVal = pMinY - ALIEN_HEIGHT;
				this.verticalVelocity = 0;
			} else if (highest == distToBottom) {
				this.yVal = pMaxY;
				this.verticalVelocity = 0;
			} else if (highest == distToLeft) {
				this.xVal = pMinX - ALIEN_WIDTH;
				this.horizontalVelocity = 0;
			} else if (highest == distToRight) {
				this.xVal = pMaxX;
				this.horizontalVelocity = 0;
			}
		}
	}

	/**
	 * Draws the alien on the frame, sets sprites to be drawn, creates hitbox for
	 * alien.
	 * 
	 * @param g2
	 */
	public void drawOn(Graphics2D g2) { // called every frame
		super.paintComponent(g2);
		// Set the sprites
		try {
			img1 = ImageIO.read(new File("Sprites/regularSprite.png"));
			img2 = ImageIO.read(new File("Sprites/specialSprite.png"));
		} catch (IOException e) {

		}
		this.ticksSinceLastShot++;

		if (this.type == "regular") {
			g2.drawImage(img1, (int) this.xVal, (int) this.yVal, null);
		} else if (this.type == "special") {
			g2.drawImage(img2, (int) this.xVal, (int) this.yVal, null);
		}
		this.constructHitbox();

		// Drawing methods
	}
	
	/**
	 * Ensures the alien's dimension is correct for drawing.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) ALIEN_WIDTH, (int) ALIEN_HEIGHT);
	}
}