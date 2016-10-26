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

package at.therefactory.jewelthief.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import at.therefactory.jewelthief.JewelThief;
import at.therefactory.jewelthief.constants.I18NKeys;

import static at.therefactory.jewelthief.constants.Config.FADING_SPEED;

public class GameIntroScreen extends ScreenAdapter {

	private final FitViewport viewport;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final BitmapFont font;

	private float alpha;
	private boolean touched;

	public GameIntroScreen(SpriteBatch batch, FitViewport viewport, OrthographicCamera camera) {
		this.batch = batch;
		this.viewport = viewport;
		this.camera = camera;
        alpha = 1;
        touched = false;
		font = JewelThief.getInstance().getFont();
		font.setColor(Color.WHITE);
	}

	@Override
	public void render(float delta) {

		// clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// simulate world
		update(delta);

		// renderCaption world
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
        font.draw(batch, JewelThief.getInstance().getBundle().get(I18NKeys.INTRO_TEXT), 40, 175);
        
        JewelThief.getInstance().getFadeSprite().setAlpha(alpha);
		JewelThief.getInstance().getFadeSprite().draw(batch);

        batch.end();
	}

	private void update(float delta) {
		if (Gdx.input.isTouched()) {
			touched = true;
		}
		if (touched && alpha == 1) {
			JewelThief.getInstance().startSinglePlayerGame();
		}
		if (touched)
			alpha = Math.min(1, alpha + FADING_SPEED);
		else
			alpha = Math.max(0, alpha - FADING_SPEED);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void show() {
		super.show();
		alpha = 1;
		touched = false;
        font.setColor(Color.WHITE);
		Gdx.input.setInputProcessor(null);
	}

}
