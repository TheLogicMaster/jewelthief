package at.therefactory.jewelthief.jewels;

import com.badlogic.gdx.math.Polygon;

public class Coin extends Jewel {

    public Coin() {
        super(Coin.class.getSimpleName());
    }

    @Override
    public Polygon getPolygon() {
        float x = getPosition().x;
        float y = getPosition().y;
        float[] vertices = {
                x, y - 6,
                x + 6, y,
                x, y + 6,
                x - 6, y
        };
        return new Polygon(vertices);
    }

}
