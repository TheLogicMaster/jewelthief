package at.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import at.therefactory.jewelthief.actors.Enemy;

public class Cloud extends Enemy {

    public Cloud() {
        super(Cloud.class.getSimpleName(), 1.3f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 36, y - 1,
                x + 18, y + 11,
                x + 5, y + 8,
                x - 5, y + 12,
                x - 25, y + 10,
                x - 36, y + 2,
                x - 32, y - 10,
                x - 15, y - 8,
                x + 2, y - 12,
                x + 18, y - 12,
                x + 34, y - 7,
        };
        return new Polygon(vertices);
    }

    @Override
    public void update(Rectangle enemyField) {
        if (!sprite.getBoundingRectangle().contains(enemyField)) {
            if (sprite.getBoundingRectangle().x + sprite.getBoundingRectangle().width >= enemyField.x + enemyField.width) {
                movementInverter.x = -1;
            } else if (sprite.getBoundingRectangle().x <= enemyField.x) {
                movementInverter.x = 1;
            } else if (sprite.getBoundingRectangle().y <= enemyField.y) {
                position.y = (enemyField.y + enemyField.getHeight());
            }
        }
        position.x += speed * movementInverter.x;
        position.y -= speed;
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);
    }

}
