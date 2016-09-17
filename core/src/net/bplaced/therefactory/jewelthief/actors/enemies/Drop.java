package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Drop extends Enemy {

    public Drop() {
        super(Drop.class.getSimpleName(), 1.3f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 3,
                x, y + 9,
                x - 5, y - 3,
                x - 2, y - 9,
                x + 2, y - 9
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
