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
