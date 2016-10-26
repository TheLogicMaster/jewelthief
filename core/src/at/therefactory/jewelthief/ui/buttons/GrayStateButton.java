/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Jewel Thief.
 *
 * Jewel Thief is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jewel Thief is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jewel Thief.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.therefactory.jewelthief.ui.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import at.therefactory.jewelthief.JewelThief;

public class GrayStateButton extends GrayButton {

    private final Sprite[] sprites;
    private final String[] captions;
    private short state;

    public GrayStateButton(String[] captions, String[] spriteIds, short initState, boolean adaptWidthToCaption,
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
    	state = (short) ((state + 1) % sprites.length);
    }

    public int getState() {
        return state;
    }
    
}
