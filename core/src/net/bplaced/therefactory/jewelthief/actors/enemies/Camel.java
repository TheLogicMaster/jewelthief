package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Camel extends Enemy {

    public Camel() {
        super(Camel.class.getSimpleName(), 1.4f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 6, y - 10,
                x + 9, y - 5,
                x + 6, y - 2,
                x + 10, y + 3,
                x + 10, y + 7,
                x + 13, y + 7,
                x + 13, y + 10,
                x + 7, y + 10,
                x + 7, y + 4,
                x + 3, y + 4,
                x + 1, y + 10,
                x - 4, y + 8,
                x - 9, y + 10,
                x - 13, y - 2,
                x - 13, y - 10,
                x - 7, y - 10,
                x - 3, y - 4,
                x + 1, y - 10
        };
        return new Polygon(vertices);
    }
}
