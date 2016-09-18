package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Onyx extends Jewel {

    public Onyx() {
        super(Onyx.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 6, y - 6,
                x, y + 7,
                x - 6, y - 6
        };
        return new Polygon(vertices);
    }
}
