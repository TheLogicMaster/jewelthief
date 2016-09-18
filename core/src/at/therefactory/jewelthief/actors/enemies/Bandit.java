package at.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

import at.therefactory.jewelthief.actors.Enemy;

public class Bandit extends Enemy {

    public Bandit() {
        super(Bandit.class.getSimpleName(), 1f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 10,
                x + 13, y + 2,
                x + 13, y + 7,
                x + 10, y + 10,
                x + 7, y,
                x, y + 10,
                x - 4, y + 10,
                x - 14, y + 5,
                x - 14, y - 1,
                x - 12, y - 8,
                x - 14, y - 8,
                x - 14, y - 11,
                x - 9, y - 11,
                x - 5, y - 5,
                x, y - 10
        };
        return new Polygon(vertices);
    }
}
