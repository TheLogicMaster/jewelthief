package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import at.therefactory.jewelthief.JewelThief;

public abstract class Jewel {

    private final Sprite sprite;
    private final Vector2 position = new Vector2();

    Jewel(String spriteId) {
        sprite = JewelThief.getInstance().getTextureAtlas().createSprite(spriteId);

        // initially hidden from visible screen
        setPosition(-100, -100);
        sprite.setPosition(-100, -100);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public void update() {
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }

    Vector2 getPosition() {
        return position;
    }

    public abstract Polygon getPolygon();

}
