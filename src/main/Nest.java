package main;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Nest class used to designate locations where aliens spawn.
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public class Nest extends Entity {
	// Graphics constants
	private static final double NEST_WIDTH = 25;
	private static final double NEST_HEIGHT = 25;

	// Sprite image declaration
	private BufferedImage img;

	private Random r;

	private double alienSpawnDelay;
	private double timeOfLastAlienSpawn;
	private String alienType;

	/**
	 * Creates a nest based on the type of alien the nest spawns and the coordinates
	 * of the nest.
	 * 
	 * @param type - Type of alien being spawned
	 * @param x    - X coordinate of nest location
	 * @param y    - Y coordinate of nest location
	 */
	public Nest(String type, int x, int y) {
		this.r = new Random();

		this.xVal = x;
		this.yVal = y;
		this.constructHitbox();

		this.alienType = type;

		this.alienSpawnDelay = 3000; // First alien spawns at 3 seconds
		this.timeOfLastAlienSpawn = 0;
	}

	/**
	 * Creates hitbox used for checking collisions.
	 */
	private void constructHitbox() {
		this.hitbox = new Rectangle2D.Double(this.xVal - (NEST_WIDTH / 2), this.yVal - (NEST_HEIGHT / 2), NEST_WIDTH,
				NEST_HEIGHT);
	}

	/**
	 * Getter to return the alien's type.
	 * 
	 * @return - Type of alien spawning from this nest
	 */
	public String getAlienType() {
		return this.alienType;
	}

	/**
	 * Calls all methods needed to update every tick, allows aliens to be spawned every 4-10 seconds.
	 * 
	 * @return Whether the nest is ready for spawning or not because aliens have active hero
	 */
	public boolean updateOnTick() {
		double curTime = System.currentTimeMillis();
		if ((curTime - timeOfLastAlienSpawn) >= alienSpawnDelay) {
			alienSpawnDelay = r.nextInt(6000) + 4000; // Between 4 and 10 seconds
			timeOfLastAlienSpawn = curTime;
			return true;
		}
		return false;
	}

	/**
	 * Draws the sprite for the nest.
	 * 
	 * @param g2 - Graphics object used to draw
	 */
	public void drawOn(Graphics2D g2) {
		super.paintComponent(g2);

		// Drawing methods
		try {
			img = ImageIO.read(new File("Sprites/nestSprite.png"));
		} catch (IOException e) {

		}
		g2.drawImage(img, (int) this.hitbox.getX(), (int) this.hitbox.getY(), null);
	}
}
