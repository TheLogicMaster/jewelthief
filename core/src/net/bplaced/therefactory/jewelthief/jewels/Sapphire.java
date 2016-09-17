package net.bplaced.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Sapphire extends Jewel {

    public Sapphire() {
        super(Sapphire.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 6, y - 5,
                x + 6, y + 5,
                x + 3, y + 8,
                x - 3, y + 8,
                x - 6, y + 5,
                x - 6, y - 5,
                x - 3, y - 8,
                x + 3, y - 8
        };
        return new Polygon(vertices);
    }
}
