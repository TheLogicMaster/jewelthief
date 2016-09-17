package net.bplaced.therefactory.jewelthief.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.bplaced.therefactory.jewelthief.misc.Util;

public abstract class Enemy extends Actor {

	protected final float speed;
	protected Vector2 movementInverter = new Vector2(Util.randomSignum(), Util.randomSignum());

	protected Enemy(String spriteId, float speed) {
		super(spriteId);
		this.speed = Util.randomWithin(speed - 0.3f, speed + 1f);
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
