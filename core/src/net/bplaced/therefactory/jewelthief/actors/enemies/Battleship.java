package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import net.bplaced.therefactory.jewelthief.actors.Enemy;

public class Battleship extends Enemy {

    public Battleship() {
        super(Battleship.class.getSimpleName(), 1.9f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 16, y - 5,
                x + 16, y + 3,
                x + 2, y + 5,
                x + 2, y + 12,
                x - 4, y + 12,
                x - 6, y,
                x - 16, y,
                x - 16, y - 9,
                x - 14, y - 12,
                x + 11, y - 12
        };
        return new Polygon(vertices);
    }

}
