package at.therefactory.jewelthief.ui.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import at.therefactory.jewelthief.constants.Colors;
import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.Config;

/**
 * Created by Christian on 08.06.2016.
 */
public class GrayButton {

    protected final float x;
    protected final float y;

    protected float width;
    protected float height;
    protected float pressedOffset;
    protected float xCaptionOffset;
    protected float yCaptionOffset;

	private boolean adaptWidthToCaption;
    private final GlyphLayout layout;
    protected final BitmapFont font;
    private Color captionColor;
    private int borderSize = 3;
    private String caption;

    public GrayButton(String caption, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.caption = caption;
        font = JewelThief.getInstance().getFont();
        captionColor = Color.DARK_GRAY;

        // horizontally and vertically align to center
        layout = new GlyphLayout(font, caption);
        xCaptionOffset = width/2 - layout.width/2;
        yCaptionOffset = height/2 + layout.height/2;
    }
    
    public GrayButton(String caption, float x, float y, float width, float height, boolean adaptWidthToCaption) {
    	this(caption, x, y, width, height);
    	this.adaptWidthToCaption = adaptWidthToCaption;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void renderShape(ShapeRenderer sr) {
        if (isPressed()) {
            sr.setColor(Colors.BUTTON_PRESSED_BORDER_DARK);
            sr.rect(x, y, borderSize, height);
            sr.rect(x + borderSize, y + height - borderSize, width - borderSize, borderSize);
            sr.setColor(Colors.BUTTON_PRESSED);
            sr.rect(x + borderSize, y, width - borderSize, height - borderSize);
        } else {
            sr.setColor(Colors.BUTTON_BORDER_LIGHT);
            sr.rect(x, y, borderSize, height);
            sr.rect(x + borderSize, y + height - borderSize, width - borderSize, borderSize);
            sr.setColor(Colors.BUTTON);
            sr.rect(x + borderSize, y + borderSize, width - borderSize * 2, height - borderSize * 2);
            sr.setColor(Colors.BUTTON_BORDER_DARK);
            sr.rect(x + borderSize, y, width - borderSize, borderSize);
            sr.rect(x + width - borderSize, y + borderSize, borderSize, height - borderSize * 2);
        }
    }

    public void renderCaption(SpriteBatch batch) {
        font.setColor(captionColor);
        font.draw(batch, caption, x + xCaptionOffset + pressedOffset, y + yCaptionOffset - pressedOffset);
    }

    public void release() {
        pressedOffset = 0;
    }

    public void press() {
        pressedOffset = Config.FONT_OFFSET_ON_BUTTON_PRESS;
    }

    public boolean isPressed() {
        return pressedOffset == Config.FONT_OFFSET_ON_BUTTON_PRESS;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
        layout.setText(font, caption);
        xCaptionOffset = width/2 - layout.width/2;
    	if (adaptWidthToCaption) {
	        width = layout.width + 20 + borderSize *2;
    	}
    }

    public float getPressedOffset() {
        return pressedOffset;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public void setCaptionOffsetY(float yOffset) {
        this.yCaptionOffset = yOffset;
    }

    public float getCaptionOffsetY() {
        return yCaptionOffset;
    }

    public void setCaptionColor(Color captionColor) {
        this.captionColor = captionColor;
    }
}
