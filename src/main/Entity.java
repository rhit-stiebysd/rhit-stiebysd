package main;

import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Entity abstract class, extends to Hero, Alien, Collectible, Bullet, and Nest.
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public abstract class Entity extends JComponent {
	public double xVal;
	public double yVal;
	public Rectangle2D hitbox;

	public double horizontalVelocity;
	public double verticalVelocity;

	public int points;

	/**
	 * Changes position for entities.
	 */
	public void updatePosition() {
		// DONE 2/9/2021, James Kelley
		this.xVal += this.horizontalVelocity;
		this.yVal += this.verticalVelocity;
	}

	/**
	 * Getter for hitboxes.
	 * 
	 * @return hitbox to compare to other hitboxes for collision
	 */
	public Rectangle2D getHitbox() {
		return this.hitbox;
	}

	/**
	 * Checks if one Entity collides with another Entity.
	 * 
	 * @param ent - Other entity to compare
	 * @return - If Other Entity is colliding with this one
	 */
	public boolean intersectsEntity(Entity ent) {
		if (this.hitbox.intersects(ent.getHitbox())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Getter for x value.
	 * 
	 * @return - X value of Entity
	 */
	public double getXVal() {
		return this.xVal;
	}

	/**
	 * Getter for y value.
	 * 
	 * @return - Y value of Entity
	 */
	public double getYVal() {
		return this.yVal;
	}

	/**
	 * Getter for horizontal velocity.
	 * 
	 * @return - Horizontal velocity of Entity
	 */
	public double getHVelocity() {
		return this.horizontalVelocity;
	}

	/**
	 * Getter for vertical velocity.
	 * 
	 * @return - Vertical velocity of entity
	 */
	public double getVVelocity() {
		return this.verticalVelocity;
	}

	/**
	 * Getter for points
	 * 
	 * @return - Number of points the Entity returns
	 */
	public int getPoints() {
		return this.points;
	}

	// TODO add abstract methods to match sub-classes
}