package net.bplaced.therefactory.jewelthief.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.jewelthief.JewelThief;
import net.bplaced.therefactory.jewelthief.constants.Config;
import net.bplaced.therefactory.jewelthief.constants.I18NKeys;

public class GameIntroScreen extends ScreenAdapter {

	private final FitViewport viewport;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final BitmapFont font;

	private float alpha = 1;
	private boolean touched = false;

	public GameIntroScreen(SpriteBatch batch) {
		this.batch = batch;

		camera = new OrthographicCamera();
		viewport = new FitViewport(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT, camera);
		camera.position.set(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2, 0);
		camera.update();

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
			alpha = Math.min(1, alpha + Config.FADING_SPEED);
		else
			alpha = Math.max(0, alpha - Config.FADING_SPEED);
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
