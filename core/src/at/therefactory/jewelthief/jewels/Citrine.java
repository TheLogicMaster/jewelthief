package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Citrine extends Jewel {

    public Citrine() {
        super(Citrine.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x + 5, y - 3,
                x, y + 9,
                x - 5, y - 3,
                x - 2, y - 9,
                x + 2, y - 9
        };
        return new Polygon(vertices);
    }

}
