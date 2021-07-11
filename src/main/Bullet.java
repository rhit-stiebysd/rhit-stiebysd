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
 * @author Sam Stieby and James Kelley
 * 
 * 	Bullet class
 *	
 */
public class Bullet extends Entity {
	// Graphics constants
	private static final double BULLET_WIDTH = 10;
	private static final double BULLET_HEIGHT = 10;
	private static BufferedImage IMAGE;

	// Frame constants
	private static final double X_MIN_LIMIT = -(BULLET_WIDTH / 2);
	private static final double X_MAX_LIMIT = 1280 + (BULLET_WIDTH / 2);
	private static final double Y_MIN_LIMIT = -(BULLET_HEIGHT / 2);
	private static final double Y_MAX_LIMIT = 720 + (BULLET_HEIGHT / 2);

	// Other constants
	private static final double BULLET_TRAVEL_DISTANCE = 600;

	// Other fields
	private int travelDist;

	public Bullet(double x, double y, double hVel, double vVel) {
		// Entity position and hitbox
		this.xVal = x - (BULLET_WIDTH / 2);
		this.yVal = y - (BULLET_HEIGHT / 2);
		this.constructHitbox();

		// Other fields
		this.travelDist = 0;

		// Initial conditions (these do not change)
		this.horizontalVelocity = hVel;
		this.verticalVelocity = vVel;
	}

	private void constructHitbox() {
		this.hitbox = new Rectangle2D.Double(this.xVal, this.yVal, BULLET_WIDTH, BULLET_HEIGHT);
	}

	private void constrainPosition() {
		// Constrain X
		if (this.xVal < X_MIN_LIMIT) {
			this.xVal += X_MAX_LIMIT - X_MIN_LIMIT;
		} else if (this.xVal > X_MAX_LIMIT) {
			this.xVal -= X_MAX_LIMIT - X_MIN_LIMIT;
		}
		
		// Constrain Y, will hit platform if wrapped
		if (this.yVal < Y_MIN_LIMIT) {
			this.yVal += Y_MAX_LIMIT - Y_MIN_LIMIT;
		} else if (this.yVal > Y_MAX_LIMIT) {
			this.yVal = Y_MAX_LIMIT - Y_MIN_LIMIT;
		}
	}

	public void updateOnTick() {
		// Don't update velocity (speed is constant), just position
		this.updatePosition();
		this.travelDist += Math.sqrt(Math.pow(this.horizontalVelocity, 2) + Math.pow(this.verticalVelocity, 2));
		this.constrainPosition();
		this.constructHitbox();
	}

	public boolean isReadyForRemoval() {
		if (this.travelDist >= BULLET_TRAVEL_DISTANCE) {
			return true;
		} else {
			return false;
		}
	}

	public void drawOn(Graphics2D g2) { // called every frame
		super.paintComponent(g2);
		try {
			IMAGE = ImageIO.read(new File("Sprites/bulletSprite.png"));
		} catch (IOException e) {

		}
		// Drawing methods
		g2.drawImage(IMAGE, (int) this.xVal, (int) this.yVal, null);
	}

	@Override
	protected void paintComponent(Graphics arg0) {
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) BULLET_WIDTH, (int) BULLET_HEIGHT);
	}
}
