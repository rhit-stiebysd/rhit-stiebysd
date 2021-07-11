package main;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Rocket class, handles drawing, location, and sprite loading.
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public class Rocket extends Entity {
	// Graphics constants
	private static final double ROCKET_WIDTH = 75;
	private static final double ROCKET_HEIGHT = 150;

	// Fields
	private GameState game;
	private BufferedImage img;

	/**
	 * Creates a rocket at location dictated by x coordinate, with number of parts
	 * and fuel needed to continue onto next level.
	 * 
	 * @param g     -
	 * @param parts - Number of parts needed
	 * @param fuel  - Number of fuel cans needed
	 * @param x     - X coordinate where rocket spawns
	 */
	public Rocket(GameState g, int parts, int fuel, double x) {
		// (X,Y) is the center of the bottom
		this.xVal = x;
		this.yVal = 1280;
		this.constructHitbox();

		// Fields
		game = g;
	}

	/**
	 * Sets rocket on top of platform at x specified in constructor.
	 * 
	 * @param p - Platform rocket sits on
	 */
	public void setOnTopOfPlatform(Platform p) { // Falling sand
		Rectangle2D pHitbox = p.getHitbox();
		if (pHitbox.contains(this.xVal, pHitbox.getCenterY())) {
			this.yVal = Math.min(this.yVal, pHitbox.getMinY());
		}
	}

	/**
	 * Creates hitbox used to compare locations for collision.
	 */
	private void constructHitbox() {
		this.hitbox = new Rectangle2D.Double(this.xVal - (ROCKET_WIDTH / 2), this.yVal - ROCKET_HEIGHT, ROCKET_WIDTH,
				ROCKET_HEIGHT);
	}

	/**
	 * Setter for vertical velocity.
	 * 
	 * @param vVel - Vertical velocity to be set
	 */
	public void setVerticalVelocity(double vVel) {
		this.verticalVelocity = vVel;
	}

	/**
	 * Draws rocket on frame, using sprite image
	 * 
	 * @param g2 - Graphics object used to draw
	 */
	public void drawOn(Graphics2D g2) { // called every frame
		super.paintComponent(g2);

		this.updatePosition();
		this.constructHitbox();

		if (this.yVal <= 0) {
			if (game.getPlayState().contains("Close Repaint")) {
				String temp = game.getPlayState().substring(14);
				game.setPlayState(temp);
			}
		}

		// Drawing methods
		try {
			img = ImageIO.read(new File("Sprites/rocketSprite.png"));
		} catch (IOException e) {

		}
		g2.drawImage(img, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
	}

	/**
	 * Ensures rocket dimensions are valid.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) ROCKET_WIDTH, (int) ROCKET_HEIGHT);
	}
}
