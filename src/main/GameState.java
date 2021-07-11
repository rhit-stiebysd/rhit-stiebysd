package main;

public class GameState {
	private String playState;
	private int levelCounter;
	private int score;
	private int lives;
	private int parts;
	private double fuel;
	
	public GameState() {
		this.playState = "";
		this.levelCounter = 1;
		this.score = 0;
		this.lives = 3;
		this.parts = 0;
		this.fuel = 0;
	}
	
	public String getPlayState() {
		return this.playState;
	}
	
	public int getLevelCounter() {
		return this.levelCounter;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int getLives() {
		return this.lives;
	}
	
	public int getParts() {
		return this.parts;
	}
	
	public double getFuel() {
		return this.fuel;
	}
	
	public void setPlayState(String newPlayState) {
		this.playState = newPlayState;
	}
	
	public void setLevel(int levelNumber) {
		this.levelCounter = levelNumber;
	}
	
	public void increaseLevel() {
		this.levelCounter++;
	}
	
	public void decreaseLevel() {
		this.levelCounter--;
	}
	
	public void addToScore(int points) {
		this.score += points;
	}
	
	public void loseLife() {
		this.lives--;
		if(this.lives <= 0) {
			this.setPlayState("Game Over");
		}
	}
	
	public void setParts(int num) {
		this.parts = num;
	}
	
	public void setFuel(double num) {
		this.fuel = num;
	}
}
