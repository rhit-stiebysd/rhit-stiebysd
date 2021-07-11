package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The main class for our arcade game. Executes the entire game, sets up
 * graphics, sets up listeners, loads levels.
 * 
 * 
 * @author James Kelley & Sam Stieby
 *
 */
public class Main {
	/**
	 * Primary method call
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}

	private static final double FPS = 60;
	private static final double DELAY = 1000 / FPS;
	private static final int DATA_PANEL_HEIGHT = 50;

	private static final boolean REGULAR_CONTROLS = false;

	GameState game;
	Level level;
	GameAdvanceListener gameAdvancer;
	Timer timer;
	JFrame frame;
	JPanel panel;
	JPanel dataPanel;
	JButton startButton;
	JLabel controlsLabel;
	JLabel levelLabel;
	JLabel scoreLabel;
	JLabel livesLabel;
	JLabel partsLabel;
	JLabel fuelLabel;

	/**
	 * Constructor for the game.
	 */
	public Main() {
		this.game = new GameState();
		this.game.setPlayState("Setting Up Graphics");
		setUpGraphics();
		setUpLevelListener();
		while (this.game.getPlayState() == "Click Start to Play") {
			System.out.print("");
		}

		String levelFile = "levels/level" + Integer.toString(this.game.getLevelCounter()) + ".txt";
		loadLevel(levelFile);
	}

	/**
	 * Establishes a JFrame, JPanels, JButtons, an actionlistener for the JButton,
	 * and a default play state for the beginning of the game.
	 */
	private void setUpGraphics() {
		frame = new JFrame();
		panel = new JPanel();
		dataPanel = new JPanel();
		int frameWidth = 1280;
		int frameHeight = 720;

		frame.setSize(frameWidth, frameHeight);
		frame.setTitle("A-803 Jet-Pac");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(0, 0);

		panel.setBackground(Color.BLACK); // Black background
		panel.setSize(frameWidth, frameHeight - DATA_PANEL_HEIGHT);

		dataPanel.setBackground(Color.LIGHT_GRAY);
		dataPanel.setSize(frameWidth, DATA_PANEL_HEIGHT);

		this.game.setPlayState("Click Start to Play");

		startButton = new JButton("Start (ENTER)");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				game.setPlayState("Playing Game");
				dataPanel.remove(startButton);
				levelLabel = new JLabel("<html>Level: " + game.getLevelCounter() + "</HTML>");
				dataPanel.add(levelLabel);
				scoreLabel = new JLabel("<html>Score: " + game.getScore() + "</HTML>");
				dataPanel.add(scoreLabel);
				livesLabel = new JLabel("<html>Lives: " + game.getLives() + "</HTML>");
				dataPanel.add(livesLabel);
				partsLabel = new JLabel("<html>Parts: " + game.getParts() + " / 3</HTML>");
				dataPanel.add(partsLabel);
				fuelLabel = new JLabel("<html>Fuel: " + game.getFuel() + "%</HTML>");
				dataPanel.add(fuelLabel);
				dataPanel.add(controlsLabel, BorderLayout.EAST);
				dataPanel.revalidate();
			}
		});
		startButton.setFocusable(false);
		dataPanel.add(startButton);

		controlsLabel = new JLabel("            WASD = movement, SPACE = shoot, PERIOD = drop collectible, MINUS = degress level, PLUS = progress level");
		
		frame.add(panel, BorderLayout.CENTER);
		frame.add(dataPanel, BorderLayout.NORTH);

		frame.setVisible(true);

		frame.getContentPane().setPreferredSize(new Dimension(1280, 720));
		frame.pack();
	}

	/**
	 * Establishes a keylistener in order to receive input from the keyboard to play
	 * the game.
	 */
	private void setUpLevelListener() {
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (game.getPlayState().equals("Playing Game")) {
					if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
						timer.stop();
						panel.removeAll();
						frame.repaint();
						progressLevel();
					} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
						timer.stop();
						panel.removeAll();
						frame.repaint();
						degressLevel();
					} else if (e.getKeyCode() == KeyEvent.VK_W) {
						level.messageHero("up");
					} else if (e.getKeyCode() == KeyEvent.VK_S) {
						level.messageHero("down");
					} else if (e.getKeyCode() == KeyEvent.VK_A) {
						level.messageHero("left");
					} else if (e.getKeyCode() == KeyEvent.VK_D) {
						level.messageHero("right");
					} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						level.messageHero("space");
					} else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
						level.messageHero("period");
					} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						game.setPlayState("Game Over");
					}
				} else if (game.getPlayState().equals("Click Start to Play")) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						startButton.doClick();
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				if (game.getPlayState().equals("Playing Game")) {
					if (e.getKeyCode() == KeyEvent.VK_W) {
						level.messageHero("stop up");
					} else if (e.getKeyCode() == KeyEvent.VK_S) {
						level.messageHero("stop down");
					} else if (e.getKeyCode() == KeyEvent.VK_A) {
						level.messageHero("stop left");
					} else if (e.getKeyCode() == KeyEvent.VK_D) {
						level.messageHero("stop right");
					} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						level.messageHero("stop space");
					}
				}
			}

			public void keyTyped(KeyEvent e) {}
		});
	}

	/**
	 * Loads levels based on the name of the file, adds a timer and starts it,
	 * creates a GameAdvanceListener that listens to the level that is created, If
	 * the level text file specified by levelFileName cannot be found, this method
	 * will load level 1.
	 */
	private void loadLevel(String levelFileName) {
		try {
			level = new Level(levelFileName, frame, panel, game);
			panel.add(level);
			gameAdvancer = new GameAdvanceListener(level);
			timer = new Timer((int) DELAY, gameAdvancer);
			timer.start();
		} catch (FileNotFoundException e) {
			System.out.println("File " + levelFileName + " does not exist. Trying Level 1.");
			panel.removeAll();
			frame.remove(level);
			game.setLevel(1);
			String levelFile = "levels/level" + Integer.toString(game.getLevelCounter()) + ".txt";
			loadLevel(levelFile);
		}
	}

	/**
	 * Listens to play states, sets fuel and parts needed per level depending on the
	 * play state. Informs player in the console that the game is over if play state
	 * is game over.
	 */
	public class GameAdvanceListener implements ActionListener {
		Level level;

		public GameAdvanceListener(Level lvl) {
			level = lvl;
		}

		/**
		 * Listens to play states
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// Has a Timer attached to allow for an action every frame
			if (game.getPlayState().equals("Next Level")) {
				level.closeLevel();
				game.setPlayState("Close Repaint Progress Level");
				frame.repaint();
			} else if (game.getPlayState().equals("Previous Level")) {
				level.closeLevel();
				game.setPlayState("Close Repaint Playing Game");
				game.setParts(0);
				game.setFuel(0);
				timer.stop();
				panel.removeAll();
				frame.repaint();
				degressLevel();
			} else if (game.getPlayState().equals("Playing Game")) {
				level.updateOnTick();
				setLabels();
				frame.repaint();
			} else if (game.getPlayState().contains("Close Repaint")) {
				frame.repaint();
			} else if (game.getPlayState().equals("Progress Level")) {
				game.setPlayState("Playing Game");
				game.setParts(0);
				game.setFuel(0);
				timer.stop();
				panel.removeAll();
				progressLevel();
			} else if (game.getPlayState().equals("Game Over")) {
				game.setPlayState("Exit");
				setLabels();
			} else if (game.getPlayState().equals("Exit")) {
				double endGameStartTime = System.currentTimeMillis();
				while (System.currentTimeMillis() < (endGameStartTime + 2600)) {
					System.out.print("");
				}
				System.exit(0);
			}
		}
	}
	
	/**
	 * Helper method for the GameAdvanceListener class. Updates the labels every frame.
	 */
	private void setLabels() {
		levelLabel.setText("<html>Level: " + game.getLevelCounter() + "</HTML>");
		scoreLabel.setText("<html>Score: " + game.getScore() + "</HTML>");
		livesLabel.setText("<html>Lives: " + game.getLives() + "</HTML>");
		partsLabel.setText("<html>Parts: " + game.getParts() + " / 3</HTML>");
		fuelLabel.setText("<html>Fuel: " + game.getFuel() + "%</HTML>");
	}

	/**
	 * Loads next level.
	 */
	private void progressLevel() {
		game.increaseLevel();
		String levelFile = "levels/level" + Integer.toString(game.getLevelCounter()) + ".txt";
		loadLevel(levelFile);
	}

	/**
	 * Loads previous level.
	 */
	private void degressLevel() {
		game.decreaseLevel();
		String levelFile = "levels/level" + Integer.toString(game.getLevelCounter()) + ".txt";
		loadLevel(levelFile);
	}

}