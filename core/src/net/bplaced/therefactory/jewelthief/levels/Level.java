package net.bplaced.therefactory.jewelthief.levels;

import net.bplaced.therefactory.jewelthief.jewels.Jewel;
import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Level {

	private String levelName;
	private Class<Jewel> jewelClass;
	private Class<Enemy> enemyClass;
	private int numJewels;
	private int numEnemies;

	public Level(String levelName, Class<?> jewelClass, int numJewels, Class<?> enemyClass, int numEnemies) {
		this.levelName = levelName;
		this.jewelClass = (Class<Jewel>) jewelClass;
		this.numJewels = numJewels;
		this.enemyClass = (Class<Enemy>) enemyClass;
		this.numEnemies = numEnemies;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public Class<Jewel> getJewelClass() {
		return jewelClass;
	}

	public void setJewelClass(Class<Jewel> jewelClass) {
		this.jewelClass = jewelClass;
	}

	public int getNumJewels() {
		return numJewels;
	}

	public void setNumJewels(int numJewels) {
		this.numJewels = numJewels;
	}

	public Class<Enemy> getEnemyClass() {
		return enemyClass;
	}

	public void setEnemyClass(Class<Enemy> enemyClass) {
		this.enemyClass = enemyClass;
	}

	public int getNumEnemies() {
		return numEnemies;
	}

	public void setNumEnemies(int numEnemies) {
		this.numEnemies = numEnemies;
	}

}
