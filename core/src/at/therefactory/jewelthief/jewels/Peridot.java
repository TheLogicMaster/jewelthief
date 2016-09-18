package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Peridot extends Jewel {

    public Peridot() {
        super(Peridot.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 8, y,
                x, y + 8,
                x - 8, y,
                x, y - 8
        };
        return new Polygon(vertices);
    }

}
