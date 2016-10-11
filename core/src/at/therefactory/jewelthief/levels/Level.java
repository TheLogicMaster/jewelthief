package at.therefactory.jewelthief.levels;

import at.therefactory.jewelthief.jewels.Jewel;
import at.therefactory.jewelthief.actors.Enemy;

public class Level {

	private String levelName;
	private Class<Jewel> jewelClass;
	private Class<Enemy> enemyClass;
	private short numJewels;
	private short numEnemies;

	public Level(String levelName, Class<?> jewelClass, short numJewels, Class<?> enemyClass, short numEnemies) {
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

	public void setNumJewels(short numJewels) {
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

	public void setNumEnemies(short numEnemies) {
		this.numEnemies = numEnemies;
	}

}
