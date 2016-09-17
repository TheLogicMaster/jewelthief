package net.bplaced.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Ruby extends Jewel {

    public Ruby() {
        super(Ruby.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 8, y,
                x + 5, y + 8,
                x - 5, y + 8,
                x - 8, y,
                x, y - 8
        };
        return new Polygon(vertices);
    }

}
