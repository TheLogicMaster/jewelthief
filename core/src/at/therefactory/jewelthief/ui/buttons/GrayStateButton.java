package at.therefactory.jewelthief.ui.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import at.therefactory.jewelthief.JewelThief;

/**
 * Created by Christian on 12.06.2016.
 */
public class GrayStateButton extends GrayButton {

    private final Sprite[] sprites;
    private final String[] captions;
    private int state;

    public GrayStateButton(String[] captions, String[] spriteIds, int initState, boolean adaptWidthToCaption,
                           float x, float y, float width, float height) {
        super(captions[0], x, y, width, height, adaptWidthToCaption);
        this.state = initState;
        this.captions = captions;
        this.sprites = new Sprite[spriteIds.length];
        for (int i = 0; i < spriteIds.length; i++) {
        	sprites[i] = JewelThief.getInstance().getTextureAtlas().createSprite(spriteIds[i]);
        }
    }

    @Override
    public void renderCaption(SpriteBatch batch) {
        font.setColor(Color.DARK_GRAY);
        font.draw(batch, captions[state], x + xCaptionOffset + 5 + pressedOffset, y + yCaptionOffset - pressedOffset);
        sprites[state].setPosition(x + 10 + pressedOffset, y + height/2 - sprites[state].getHeight()/2 + 1 - pressedOffset);
        sprites[state].draw(batch);
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
        captions[state] = caption;
    }
    
    public void nextState() {
    	state = (state + 1) % sprites.length;
    }

    public int getState() {
        return state;
    }
    
}
