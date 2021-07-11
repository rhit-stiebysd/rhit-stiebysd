package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Collectible is a class that extends the Entity class. Collectible allows
 * different types of collectibles to be created and drawn on the frame.
 * Collectibles can also be picked up and dropped by a Hero.
 * 
 * 
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public class Collectible extends Entity {
	private static final double COLLECTIBLE_WIDTH = 40;
	private static final double COLLECTIBLE_HEIGHT = 25;

	BufferedImage valImg;
	BufferedImage fuelImg;
	BufferedImage partImg;

	private static final double MAX_FALL_VEL = 20;
	private static final double PICKUP_DELAY = 500;
	private static final int VALUABLE_POINTS = 30;
	private static final int FUEL_POINTS = 10;
	private static final int PART_POINTS = 10;

	private int type;
	private Hero activeHero;
	private double lastHeld; // Last time when held
	private boolean onPlatform;

	/**
	 * Default constructor.
	 */
	public Collectible() {
		new Collectible(0, 100);
	}

	/**
	 * Collectible constructor using a Collectible type and the x value where the
	 * Collectible will spawn.
	 * 
	 * @param colType - Collectible type
	 * @param colX    - Collectible x value
	 */
	public Collectible(int colType, double colX) {
		this.activeHero = null;
		this.type = colType;
		this.xVal = colX;
		this.yVal = 0;
		this.horizontalVelocity = 0;
		this.verticalVelocity = -12;
		constructHitbox();

		if (colType == 0) {
			this.points = VALUABLE_POINTS;
		} else if (colType == 1) {
			this.points = FUEL_POINTS;
		} else if (colType == 2) {
			this.points = PART_POINTS;
		}

		lastHeld = System.currentTimeMillis();
	}

	/**
	 * Updates velocity of the Collectible as it falls.
	 */
	private void updateVelocity() {
		if (onPlatform) {
			this.verticalVelocity = 0;
		} else {
			this.verticalVelocity += 0.2;
		}
	}

	/**
	 * Restricts velocity so physics is preserved.
	 */
	private void constrainVelocity() {
		this.verticalVelocity = Math.max(this.verticalVelocity, -MAX_FALL_VEL);
		this.verticalVelocity = Math.min(this.verticalVelocity, MAX_FALL_VEL);
	}

	/**
	 * Restricts position for Collectibles so they aren't off screen.
	 */
	private void constrainPosition() {
		if (this.xVal < (COLLECTIBLE_WIDTH / 2)) {
			this.xVal += 1280;

		} else if (this.xVal > (1280 + (COLLECTIBLE_WIDTH / 2))) {
			this.xVal -= 1280;
		}

		// Constrain yVal to frame
		this.yVal = Math.max(this.yVal, COLLECTIBLE_HEIGHT);
		this.yVal = Math.min(this.yVal, 720);
	}

	/**
	 * Update method, calls every method that needs to be updated per tick.
	 */
	public void updateOnTick() {
		this.updateVelocity();
		this.constrainVelocity();
		this.updatePosition();
		this.constrainPosition();
		this.constructHitbox();
	}

	/**
	 * Getter for Collectible type.
	 * 
	 * @return - type of Collectible
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Gets active Hero to know if the Collectible is being held.
	 * 
	 * @return - Hero holding the Collectible
	 */
	public Hero getActiveHero() {
		return this.activeHero;
	}

	/**
	 * Sets active Hero to make Hero hold this Collectible.
	 * 
	 * @param h - Hero holding the Collectible
	 * @return - Whether there is an active Hero or not
	 */
	public boolean setActiveHero(Hero h) {
		double curTime = System.currentTimeMillis();
		if (curTime >= (lastHeld + PICKUP_DELAY)) {
			this.activeHero = h;
			this.onPlatform = true;
			lastHeld = curTime;
			return true;
		}
		return false;
	}

	/**
	 * Undoes the setActiveHero() method.
	 */
	public void removeActiveHero() {
		this.activeHero = null;
		lastHeld = System.currentTimeMillis();
		this.onPlatform = false;
	}

	/**
	 * Creates hitbox to detect for collisions.
	 */
	private void constructHitbox() {
		this.hitbox = new Rectangle2D.Double(this.xVal - (COLLECTIBLE_WIDTH / 2), this.yVal - COLLECTIBLE_HEIGHT,
				COLLECTIBLE_WIDTH, COLLECTIBLE_HEIGHT);
	}

	/**
	 * Compares hitboxes with a Platform to do collisions.
	 * 
	 * @param p - Platform to compare hitboxes with
	 */
	public void avoidPlatform(Platform p) {
		Rectangle2D pHitbox = p.getHitbox();
		if (this.hitbox.intersects(pHitbox)) {
			this.onPlatform = true;
			this.yVal = pHitbox.getMinY();
		}
	}

	/**
	 * Draws sprite for each Collectible based on type.
	 * 
	 * @param g2 - Graphics object used for drawing.
	 */
	public void drawOn(Graphics2D g2) { // called every frame
		super.paintComponent(g2);

		if (this.activeHero != null) { // TODO move to update
			this.xVal = this.activeHero.getXVal();
			this.yVal = this.activeHero.getYVal();
		}
		this.constructHitbox();

		if (this.type == 2) {
			try {
				partImg = ImageIO.read(new File("Sprites/partsSprite.png"));
				g2.drawImage(partImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
			} catch (IOException e) {
			}
		} else if (this.type == 0) {
			try {
				valImg = ImageIO.read(new File("Sprites/valuableSprite.png"));
				g2.drawImage(valImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
			} catch (IOException e) {
			}
		} else if (this.type == 1) {
			try {
				fuelImg = ImageIO.read(new File("Sprites/fuelSprite.png"));
				g2.drawImage(fuelImg, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
			} catch (IOException e) {
			}
		}

		// Drawing methods
	}

	/**
	 * Gets preferred size to prevent bugs in certain places.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) COLLECTIBLE_WIDTH, (int) COLLECTIBLE_HEIGHT);
	}
}
