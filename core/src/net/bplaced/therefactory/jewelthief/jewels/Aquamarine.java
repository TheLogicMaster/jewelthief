package net.bplaced.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Aquamarine extends Jewel {

    public Aquamarine() {
        super(Aquamarine.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 6, y - 1,
                x, y + 6,
                x - 6, y - 1,
                x - 4, y - 6,
                x + 4, y - 6
        };
        return new Polygon(vertices);
    }
}
