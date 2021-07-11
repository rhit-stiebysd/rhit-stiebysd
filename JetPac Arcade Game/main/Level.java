package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Level class: handles creation of levels based on text files
 * 
 * @author Sam Stieby and James Kelley
 *
 */
public class Level extends JComponent {
	private static final double INVINCIBLE_TIME = 1000; // 1000ms = 1s

	private Random r;
	private GameState game;
	private String file;
	private ArrayList<Platform> platforms;
	private Rocket rocket;
	private Hero hero;
	private double timeSinceLastLifeLost;
	private ArrayList<Alien> aliens;
	private ArrayList<Bullet> bullets;
	private ArrayList<Collectible> collectibles;
	private ArrayList<Nest> nests;
	private HashMap<Integer, Boolean> collectibleTypes;

	private int collectibleSpawnDelay;
	private double timeOfLastCollectibleSpawn;

	private int partsNeeded;
	private int fuelNeeded;
	private int partsAdded;
	private int fuelAdded;

	/**
	 * Level created based on file and graphics created by Main
	 * 
	 * @param fileName  - File to be read
	 * @param mainFrame - Frame to put objects on
	 * @param mainPanel - Panel to put information on
	 * @param g         - GameState to determine state of the game
	 * @throws FileNotFoundException
	 */
	public Level(String fileName, JFrame mainFrame, JPanel mainPanel, GameState g) throws FileNotFoundException {
		this.r = new Random();
		this.game = g;
		this.file = fileName;
		this.platforms = new ArrayList<Platform>();
		this.aliens = new ArrayList<Alien>();
		this.bullets = new ArrayList<Bullet>();
		this.collectibles = new ArrayList<Collectible>();
		this.nests = new ArrayList<Nest>();
		this.collectibleTypes = new HashMap<Integer, Boolean>();

		this.decodeTextFile(file);
		this.setSize(new Dimension(1280, 720));

		this.collectibleSpawnDelay = r.nextInt(4000) + 8000;
		this.timeOfLastCollectibleSpawn = 0;
	}

	/**
	 * Decodes text file to create the level.
	 * 
	 * @param fileName - Name of the file to be read
	 * @throws FileNotFoundException
	 */
	private void decodeTextFile(String fileName) throws FileNotFoundException {
		FileReader reader = new FileReader(fileName);
		Scanner s = new Scanner(reader);
		int linesRead = 0;

		this.platforms.add(new Platform());
		this.addCollectible();
		while (s.hasNext()) {
			String txtLine = s.nextLine();
			linesRead++;
			if (txtLine.contains("Platform")) {
				addPlatform(linesRead);
			} else if (txtLine.contains("Rocket")) {
				addRocket(linesRead);
				addHero(linesRead);
			} else if (txtLine.contains("Alien")) {
				addAlien(linesRead);
			}
		}
		s.close();
	}

	/**
	 * Removes Hero, Bullets, and Rocket from the level (The rocket blasts off
	 * first).
	 */
	public void closeLevel() {
		this.hero = null;
		for (int i = bullets.size() - 1; i >= 0; i--) {
			bullets.remove(i);
		}
		this.rocket.setVerticalVelocity(-2);
	}

	/**
	 * Creates Platforms based on lines from text file.
	 * 
	 * @param linesRead - Lines read from text file
	 */
	private void addPlatform(int linesRead) {
		try {
			FileReader fileReader = new FileReader(file);
			Scanner scan = new Scanner(fileReader);
			boolean toCreate = false;
			int platX = 0;
			int platY = 0;
			int platW = 0;
			int platH = 0;
			for (int i = 0; i < linesRead; i++) {
				scan.nextLine();
			}
			String temp = scan.next();
			if (temp.equals("corner:")) {
				platX = scan.nextInt();
				platY = scan.nextInt();
				temp = scan.next();
				if (temp.equals("size:")) {
					platW = scan.nextInt();
					platH = scan.nextInt();
					toCreate = true;
				}
			}
			scan.close();

			// if all have changed, then
			if (toCreate) {
				this.platforms.add(new Platform(platX, platY, platW, platH));
			}
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Creates Rocket based on lines read from text file.
	 * 
	 * @param linesRead - Lines read from text file
	 */
	private void addRocket(int linesRead) {
		try {
			FileReader fileReader = new FileReader(file);
			Scanner scan = new Scanner(fileReader);
			boolean toCreate = false;
			int rocketX = 640;
			boolean isBuilt = false;
			partsNeeded = 3;
			partsAdded = 0;
			fuelNeeded = 4;
			fuelAdded = 0;
			for (int i = 0; i < linesRead; i++) {
				scan.nextLine();
			}

			if (scan.next().equals("center:")) {
				rocketX = scan.nextInt();
				if (scan.next().equals("isBuilt:")) {
					isBuilt = scan.nextBoolean();
					if (scan.next().equals("fuelNeeded:")) {
						fuelNeeded = scan.nextInt();
						toCreate = true;
					}
				}
			}
			scan.close();

			// if all have changed, then
			if (toCreate) {
				Rocket r = new Rocket(game, partsNeeded, fuelNeeded, rocketX);
				if (isBuilt) {
					this.partsAdded = this.partsNeeded;
					this.collectibleTypes.replace(1, true);
					this.collectibleTypes.replace(2, false);
					r = new Rocket(game, 0, fuelNeeded, rocketX);
				}
				this.game.setParts(this.partsAdded);
				if (fuelNeeded == -1) {
					this.collectibleTypes.replace(1, false);
					this.collectibleTypes.replace(2, false);
				}
				for (Platform p : this.platforms) {
					r.setOnTopOfPlatform(p);
				}
				this.rocket = r;
			}
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Adds Hero from lines read from text file.
	 * 
	 * @param linesRead - Lines read from text file
	 */
	private void addHero(int linesRead) {
		try {
			FileReader fileReader = new FileReader(file);
			Scanner scan = new Scanner(fileReader);
			int heroX = 640;
			for (int i = 0; i < linesRead; i++) {
				scan.nextLine();
			}
			if (scan.next().equals("center:")) {
				heroX = scan.nextInt();
			}
			scan.close();

			// if all have changed, then
			Hero h = new Hero(heroX);
			for (Platform p : platforms) {
				h.setOnTopOfPlatform(p);
			}
			h.setBullets(this.bullets);
			this.hero = h;
			this.timeSinceLastLifeLost = System.currentTimeMillis();
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Sends message to Hero saying what key is pressed.
	 * 
	 * @param str - Key that is pressed
	 */
	public void messageHero(String str) {
		this.hero.handle(str);
	}

	/**
	 * Adds Collectibles to the level.
	 */
	private void addCollectible() {
		collectibleTypes.put(0, true);
		collectibleTypes.put(1, false);
		collectibleTypes.put(2, true);
	}

	/**
	 * Adds Aliens based on lines read from text file.
	 * 
	 * @param linesRead - Lines read from text file
	 */
	private void addAlien(int linesRead) {
		try {
			FileReader fileReader = new FileReader(file);
			Scanner scan = new Scanner(fileReader);
			for (int i = 0; i < linesRead; i++) {
				scan.nextLine();
			}

			while (scan.hasNext()) {
				String temp = scan.nextLine();
				if (temp.equals(""))
					break;
				try {
					if (temp.contains("type:")) {
						temp = temp.substring(6);
					} else {
						continue;
					}
					temp = temp.trim();
					String aType = "";
					if (temp.equals("regular"))
						aType = "regular";
					if (temp.equals("special"))
						aType = "special";
					int nestX = 640;
					int nestY = 360;
					while (true) {
						nestX = r.nextInt(1180) + 50; // Not on edge of frame
						nestY = r.nextInt(620) + 50; // Not on edge of frame
						boolean inObject = false;

						for (Platform p : this.platforms) {
							if (p.getHitbox().contains(nestX, nestY)) {
								inObject = true;
							}
						}
						if (this.rocket.getHitbox().contains(nestX, nestY)) {
							inObject = true;
						}

						if (!inObject)
							break;
					}
					nests.add(new Nest(aType, nestX, nestY));
				} catch (StringIndexOutOfBoundsException e) {
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Calls methods that update each object per tick.
	 */
	public void updateOnTick() {
		double curTime = System.currentTimeMillis();
		for (Nest n : nests) {
			if (n.updateOnTick()) {
				String alienType = n.getAlienType();
				double alienX = n.getXVal();
				double alienY = n.getYVal();
				Alien a = new Alien(alienType, alienX, alienY);
				a.setActiveHero(hero);
				a.setBullets(bullets);
				aliens.add(a);
			}
		}

		if ((curTime - timeOfLastCollectibleSpawn) >= collectibleSpawnDelay) {
			ArrayList<Integer> seen = new ArrayList<Integer>();
			int colType = r.nextInt(3);
			while (true) {
				colType = r.nextInt(3);
				if (colType == 0)
					break;
				if (!seen.contains(colType)) {
					seen.add(colType);
				} else {
					continue;
				}
				if (!collectibleTypes.get(colType)) {
					continue;
				}
				int colCount = 0;
				for (Collectible c : collectibles) {
					if (c.getType() == colType)
						colCount++;
				}
				if ((colType == 1) && (colCount < (this.fuelNeeded - this.fuelAdded))) {
					break;
				} else if ((colType == 2) && (colCount < (this.partsNeeded - this.partsAdded))) {
					break;
				}
			}
			Collectible c = new Collectible(colType, r.nextInt(1280));
			collectibles.add(c);

			collectibleSpawnDelay = r.nextInt(4000) + 2000 + 2000 * collectibles.size(); // Between 2 and 6 seconds + 2
																							// seconds per collectible
																							// on screen
			timeOfLastCollectibleSpawn = curTime;
		}

		// Update Hero
		this.hero.updateOnTick();

		// Update Aliens
		for (Alien a : aliens) {
			a.updateOnTick();
		}

		// Alien-Hero Collisions
		int temp1 = aliens.size();
		for (int i = 0; i < temp1; i++) {
			Alien a = aliens.get(i);
			if (this.hero.intersectsEntity(a)) {
				if (curTime > (this.timeSinceLastLifeLost + INVINCIBLE_TIME)) {
					this.timeSinceLastLifeLost = curTime;
					this.game.loseLife();
					this.removeEntity(a);
					i--;
					temp1--;
				}
			}
		}

		temp1 = bullets.size();
		for (int i = 0; i < temp1; i++) {
			Bullet b = new Bullet(0, 0, 0, 0);
			try {
				b = bullets.get(i);
				b.updateOnTick();
			} catch (IndexOutOfBoundsException e) {
				continue;
			}
			boolean hitPlatform = false;
			for (Platform p : platforms) {
				if (b.getHitbox().intersects(p.getHitbox())) {
					this.removeEntity(b);
					hitPlatform = true;
					i--;
					temp1--;
				}
			}
			if (hitPlatform) {
				continue;
			}
			if (b.isReadyForRemoval()) {
				this.removeEntity(b);
				i--;
				temp1--;
			} else if (this.hero.intersectsEntity(b)) {
				this.removeEntity(b);
				if (curTime > (this.timeSinceLastLifeLost + INVINCIBLE_TIME)) {
					this.timeSinceLastLifeLost = curTime;
					this.game.loseLife();
				}
				i--;
				temp1--;
			} else {
				int temp2 = aliens.size();
				for (int j = 0; j < temp2; j++) {
					Alien a = aliens.get(j);
					if (b.intersectsEntity(a)) {
						this.removeEntity(a);
						this.removeEntity(b);
						i--;
						temp1--;
						j--;
						temp2--;
					}
				}
			}
		}

		temp1 = collectibles.size();
		for (int i = 0; i < temp1; i++) {
			Collectible c = collectibles.get(i);
			if (c.getActiveHero() == null) {
				if (this.hero.intersectsEntity(c)) {
					if (c.getType() == 0) {
						this.removeEntity(c);
						i--;
						temp1--;
					} else {
						this.hero.collect(c);
					}
				}
			}
			if (c.getHitbox().intersects(rocket.getHitbox())) {
				if (c.getType() == 1) {
					if (this.partsAdded >= this.partsNeeded) {
						this.fuelAdded++;
						this.fuelAdded = Math.min(this.fuelAdded, this.fuelNeeded);
						double temp = ((double) fuelAdded / (double) fuelNeeded) * 100;
						this.game.setFuel(temp);
						if (this.fuelAdded >= this.fuelNeeded) {
							this.game.setPlayState("Next Level");
						}
						if (c.getActiveHero() != null) {
							c.getActiveHero().drop();
						}
						this.removeEntity(c);
						i--;
						temp1--;
					}
				} else if (c.getType() == 2) {
					this.partsAdded++;
					this.partsAdded = Math.min(this.partsAdded, this.partsNeeded);
					if (this.partsAdded >= this.partsNeeded) {
						this.collectibleTypes.replace(1, true);
						this.collectibleTypes.replace(2, false);
					}
					this.game.setParts(this.partsAdded);
					if (this.fuelAdded >= this.fuelNeeded) {
						this.game.setPlayState("Next Level");
					}
					if (c.getActiveHero() != null) {
						c.getActiveHero().drop();
					}
					this.removeEntity(c);
					i--;
					temp1--;
				}
			}
			c.updateOnTick();
		}

		// Draw hero and platforms such that hero avoids platform
		updatePlatform();
	}

	/**
	 * Updates Platform, called in updateOnTick method above.
	 */
	private void updatePlatform() {
		for (Platform p : platforms) {
			hero.avoidPlatform(p);
			for (Alien a : aliens) {
				a.avoidPlatform(p);
			}
			for (Collectible c : collectibles) {
				c.avoidPlatform(p);
			}
		}
	}

	/**
	 * Removes Entity from Level.
	 * 
	 * @param ent - Entity to be removed
	 */
	private void removeEntity(Entity ent) {
		// TODO
		if (ent.equals(hero)) {
			this.game.loseLife();
		}
		this.aliens.remove(ent);
		this.bullets.remove(ent);
		this.collectibles.remove(ent);
		this.game.addToScore(ent.getPoints());
	}

	/**
	 * Makes Rocket do animation on level end.
	 */
	public void playExitAnimation() {
		rocket.setVerticalVelocity(-2);
	}

	/**
	 * Paints all objects on the frame.
	 * 
	 * @param g - Graphics object used to draw
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// TODO implement collectibles
		// TODO do more collisions
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Must manually draw each
		for (Platform p : platforms) {
			p.drawOn(g2);
		}
		rocket.drawOn(g2);
		if (this.hero != null) {
			this.hero.drawOn(g2);
		}

		for (Nest n : nests) {
			n.drawOn(g2);
		}
		for (Alien a : aliens) {
			a.drawOn(g2);
		}
		for (Bullet b : bullets) {
			b.drawOn(g2);
		}
		for (Collectible c : collectibles) {
			c.drawOn(g2);
		}
	}

	/**
	 * Sets dimension to ensure correct functionality.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1280, 720);
	}

	/**
	 * Sets dimension to ensure correct functionality.
	 */
	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
	}

} // Level