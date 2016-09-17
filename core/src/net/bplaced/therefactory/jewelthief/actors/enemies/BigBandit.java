package net.bplaced.therefactory.jewelthief.actors.enemies;

import com.badlogic.gdx.math.Polygon;

public class BigBandit extends net.bplaced.therefactory.jewelthief.actors.Enemy {

    public BigBandit() {
        super(BigBandit.class.getSimpleName(), 1.3f);
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x, y - 16,
                x + 2, y - 8,
                x + 16, y + 2,
                x + 16, y + 12,
                x, y + 12,
                x - 1, y + 15,
                x - 5, y + 15,
                x - 8, y + 10,
                x - 11, y + 16,
                x - 14, y + 12,
                x - 11, y + 1,
                x - 16, y,
                x - 16, y - 3,
                x - 8, y - 8,
                x - 7, y - 15
        };
        return new Polygon(vertices);
    }
}
