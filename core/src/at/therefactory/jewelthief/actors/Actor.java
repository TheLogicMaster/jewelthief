package at.therefactory.jewelthief.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import at.therefactory.jewelthief.JewelThief;

abstract class Actor {

	protected Sprite sprite;
	protected final Vector2 position = new Vector2();

	Actor(String spriteId) {
		sprite = JewelThief.getInstance().getTextureAtlas().createSprite(spriteId);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void update() {
		sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
	}

	public abstract Polygon getPolygon();

}
