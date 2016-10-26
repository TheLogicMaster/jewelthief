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

package at.therefactory.jewelthief.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import at.therefactory.jewelthief.misc.Utils;

public abstract class Enemy extends Actor {

	protected final float speed;
	protected Vector2 movementInverter;

	protected Enemy(String spriteId, float speed) {
		super(spriteId);
		this.speed = Utils.randomWithin(speed - .3f, speed + 1);
		this.movementInverter = new Vector2(Utils.randomSignum(), Utils.randomSignum());
	}

	public void update(Rectangle enemyField) {
		if (!enemyField.contains(sprite.getBoundingRectangle())) {
			if (sprite.getBoundingRectangle().x + sprite.getBoundingRectangle().width >= enemyField.x + enemyField.width) {
				movementInverter.x = -1;
			} else if (sprite.getBoundingRectangle().x <= enemyField.x) {
				movementInverter.x = 1;
			} else if (sprite.getBoundingRectangle().y + sprite.getBoundingRectangle().getHeight() >= enemyField.y + enemyField.height) {
				movementInverter.y = -1;
			} else if (sprite.getBoundingRectangle().y <= enemyField.y) {
				movementInverter.y = 1;
			}
		}
		position.x += speed * movementInverter.x;
		position.y += speed * movementInverter.y;
		sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
	}

	public void setMovementInverter(Vector2 movementInverter) {
		this.movementInverter = movementInverter;
	}

	public Vector2 getMovementInverter() {
		return movementInverter;
	}

}
