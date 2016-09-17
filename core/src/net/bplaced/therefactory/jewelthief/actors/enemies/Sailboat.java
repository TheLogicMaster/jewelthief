package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Sailboat extends Enemy {

    public Sailboat() {
        super(Sailboat.class.getSimpleName(), 1.7f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 16, y - 5,
                x + 6, y + 10,
                x + 3, y + 8,
                x - 3, y + 14,
                x - 9, y + 11,
                x - 6, y + 7,
                x - 15, y + 5,
                x - 15, y - 13,
                x + 10, y - 13
        };
        return new Polygon(vertices);
    }

}
