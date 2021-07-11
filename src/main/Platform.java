package main;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
/**
 * Platform class: Used for Rocket and Hero to stand on.
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public class Platform extends JComponent {
	private static final Color DEFAULT_COLOR = new Color(0, 100, 0);
	private static final int DEFAULT_X = -50;
	private static final int DEFAULT_Y = 680;
	private static final int DEFAULT_W = 1330;
	private static final int DEFAULT_H = 40;
	
	private Rectangle2D hitbox;
	private Color color;
	/**
	 * Default constructor.
	 */
	public Platform() { // Floor platform
		this(DEFAULT_X, DEFAULT_Y, DEFAULT_W, DEFAULT_H);
	}
	/**
	 * Creates Plaform based on location, height, and width
	 * @param x - X value
	 * @param y - Y value
	 * @param w - Width value
	 * @param h - Height value
	 */
	public Platform(int x, int y, int w, int h) {
		this.color = DEFAULT_COLOR;
		this.hitbox = new Rectangle2D.Double(x, y, w, h);
	}
	/**
	 * Getter for hitbox.
	 * @return - Hitbox
	 */
	public Rectangle2D getHitbox() {
		return this.hitbox;
	}
	/**
	 * Draws Platform on level.
	 * @param g2 - Graphics object used to draw
	 */
	public void drawOn(Graphics2D g2) {
		super.paintComponent(g2);
		
		// Drawing methods
		g2.setColor(this.color);
		g2.draw(this.hitbox);
		g2.fill(this.hitbox);
	}
	
	/**
	 * Sets preferred size to prevent bugs.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) this.hitbox.getWidth(), (int) this.hitbox.getHeight());
	}
} // Platform