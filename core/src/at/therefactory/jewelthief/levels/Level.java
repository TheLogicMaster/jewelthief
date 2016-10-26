/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Jewel Thief.
 *
 * Jewel Thief is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jewel Thief is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jewel Thief.  If not, see <http://www.gnu.org/licenses/>.
 */

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
