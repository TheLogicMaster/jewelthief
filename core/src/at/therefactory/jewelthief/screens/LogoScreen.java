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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

import at.therefactory.jewelthief.JewelThief;

import static at.therefactory.jewelthief.constants.Config.DEBUG_MODE;
import static at.therefactory.jewelthief.constants.Config.FADING_SPEED;
import static at.therefactory.jewelthief.constants.Config.WINDOW_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;

public class LogoScreen extends ScreenAdapter {

    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Music musicLibGdxJingle;
    private final Sprite spriteThere;
    private final Sprite spriteFactory;
    private final Sprite spriteLibGdxLogo;
    private short numFrames, deltaXThere, deltaXCursor, deltaXFactory = 64;
    private boolean showCursor = true;
    private float alpha;

    // flags for states in the animation
    private boolean writeTheFactory = true;
    private boolean writeRe = false;
    private final Sound soundTypeTheRefactory;
    private final Sound soundGoBackOnKeyboard;
    private final Sound soundTypeRe;
    private long timestamp;
    private boolean libGdxLogoFinished;

    public LogoScreen(SpriteBatch batch, ShapeRenderer shapeRenderer, FitViewport viewport, OrthographicCamera camera) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.viewport = viewport;
        this.camera = camera;

        spriteThere = new Sprite(new Texture("there.png"));
        spriteFactory = new Sprite(new Texture("factory.png"));
        spriteLibGdxLogo = new Sprite(new Texture("libgdx.png"));
        spriteLibGdxLogo.setSize(spriteLibGdxLogo.getWidth() / 2, spriteLibGdxLogo.getHeight() / 2);
        spriteLibGdxLogo.setPosition(WINDOW_WIDTH / 2 - spriteLibGdxLogo.getWidth() / 2,
                WINDOW_HEIGHT / 2 - spriteLibGdxLogo.getHeight() / 2);

        AssetManager am = JewelThief.getInstance().getAssetManager();
        am.load("audio/sounds/keyboard.ogg", Sound.class);
        am.load("audio/sounds/keyboard_go_back.ogg", Sound.class);
        am.load("audio/sounds/re.ogg", Sound.class);
        am.load("audio/sounds/libgdx.ogg", Music.class);
        am.finishLoading();

        soundTypeTheRefactory = am.get("audio/sounds/keyboard.ogg", Sound.class);
        soundGoBackOnKeyboard = am.get("audio/sounds/keyboard_go_back.ogg", Sound.class);
        soundTypeRe = am.get("audio/sounds/re.ogg", Sound.class);
        musicLibGdxJingle = am.get("audio/sounds/libgdx.ogg", Music.class);

        resetAnimation();
    }

    @Override
    public void render(float delta) {
        if (!libGdxLogoFinished) {
            Gdx.gl.glClearColor(1 - alpha, 1 - alpha, 1 - alpha, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updateAndRenderLibGdxLogo(delta);
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updateAndRenderTheRefactoryLogo(delta);
        }
    }

    private void updateAndRenderLibGdxLogo(float delta) {
        updateLibGdxLogo(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        spriteLibGdxLogo.draw(batch, 1 - alpha);

        //JewelThief.getInstance().getFadeSprite().setAlpha(alpha);
        //JewelThief.getInstance().getFadeSprite().draw(batch);

        batch.end();
    }

    private void updateLibGdxLogo(float delta) {

        // go to next screen as soon as user taps the screen
        if (Gdx.input.justTouched()) {
            musicLibGdxJingle.stop();
            alpha = 1;
            libGdxLogoFinished = true;
            return;
        }

        // fade in
        if (timestamp == 0 && alpha > 0) {
            alpha = Math.max(0, alpha - FADING_SPEED / 4);
        } else {

            // wait a bit
            if (timestamp == 0)
                timestamp = System.currentTimeMillis();
            if (System.currentTimeMillis() - 1200 > timestamp) {

                // fade out
                alpha = Math.min(1, alpha + FADING_SPEED / 4);

                if (alpha == 1) {
                    libGdxLogoFinished = true;
                }
            }
        }
    }

    private void updateAndRenderTheRefactoryLogo(float delta) {
        updateTheRefactory(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        spriteThere.setX((WINDOW_WIDTH - spriteThere.getWidth() - spriteFactory.getWidth()) / 2);
        spriteThere.setY(WINDOW_HEIGHT / 2 - spriteThere.getHeight() / 2);
        spriteThere.draw(batch);

        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);

        // black background of "spriteFactory" for overlaying the "spriteThere" sprite
        shapeRenderer.rect(spriteFactory.getX(), 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        shapeRenderer.end();

        batch.begin();
        spriteFactory.setX(spriteThere.getX() + spriteThere.getWidth() + 1 - deltaXFactory);
        spriteFactory.setY(spriteThere.getY());
        spriteFactory.draw(batch);

        JewelThief.getInstance().getFadeSprite().setAlpha(alpha);
        JewelThief.getInstance().getFadeSprite().draw(batch);
        batch.end();

        shapeRenderer.begin(ShapeType.Filled);

        // hide letters
        shapeRenderer.rect(spriteThere.getX() + deltaXThere, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        // cursor
        if (showCursor) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(spriteThere.getX() + deltaXCursor, WINDOW_HEIGHT / 2 - 50 / 2, 4, 50);
        }
        shapeRenderer.end();
    }

    private void updateTheRefactory(float delta) {

        // go to main menu as soon as user taps the screen
        if (Gdx.input.justTouched()) {
            JewelThief.getInstance().switchToMainMenu();
        }

        // show logo animation
        numFrames++;
        int typeSpeedForOneLetter = 8;
        if (numFrames < 0) {
            blinkCursor();
        } else {
            if (writeTheFactory) {
                if (numFrames == 1) {
                    alpha = 0;
                    showCursor = true;
                    soundTypeTheRefactory.play();
                }
                if (numFrames == 9 * typeSpeedForOneLetter) {
                    deltaXThere += 41;
                } else if (numFrames % typeSpeedForOneLetter == 0 && numFrames < 9 * typeSpeedForOneLetter) {
                    deltaXThere += 33;
                }
                deltaXCursor = deltaXThere;
                if (numFrames > 10 * typeSpeedForOneLetter) {
                    blinkCursor();
                    if (numFrames > 17 * typeSpeedForOneLetter) {
                        writeTheFactory = false;
                        writeRe = true;
                        deltaXCursor -= 41;
                        soundGoBackOnKeyboard.play();
                        showCursor = true;
                    }
                }
            } else if (writeRe) {
                if (numFrames % typeSpeedForOneLetter == 0 && numFrames < 24 * typeSpeedForOneLetter) {
                    deltaXCursor -= 33;
                } else if (numFrames >= 24 * typeSpeedForOneLetter && numFrames < 26 * typeSpeedForOneLetter) {
                    blinkCursor();
                }

                // insert letter "R"
                else if (numFrames == 26 * typeSpeedForOneLetter) {
                    soundTypeRe.play();
                    showCursor = true;
                    deltaXFactory -= 33;
                    deltaXCursor += 33;
                    deltaXThere += 33;
                }

                // insert letter "E"
                else if (numFrames == 30 * typeSpeedForOneLetter) {
                    deltaXFactory -= 33;
                    deltaXCursor += 33;
                    deltaXThere += 33;
                } else if (numFrames > 32 * typeSpeedForOneLetter && numFrames <= 37 * typeSpeedForOneLetter) {
                    //blinkCursor();
                    showCursor = false;

                }

                // fade out
                else if (numFrames > 37 * typeSpeedForOneLetter) {
                    showCursor = false;
                    alpha = Math.min(1, alpha + FADING_SPEED / 4);
                }

                if (alpha == 1) {
                    if (DEBUG_MODE) {
                        resetAnimation();
                    } else {
                        JewelThief.getInstance().switchToMainMenu();
                    }
                }
            }
        }
    }

    private void resetAnimation() {
        timestamp = 0;
        libGdxLogoFinished = false;
        alpha = 1;
        numFrames = -40;
        deltaXThere = 0;
        deltaXCursor = 0;
        deltaXFactory = 64;
        showCursor = true;
        writeTheFactory = true;
        writeRe = false;
        musicLibGdxJingle.play();
    }

    private void blinkCursor() {
        if ((numFrames % 20 == 0)) {
            showCursor = !showCursor;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        soundGoBackOnKeyboard.dispose();
        soundTypeTheRefactory.dispose();
        soundTypeRe.dispose();
        musicLibGdxJingle.dispose();
    }

    @Override
    public void hide() {
        super.hide();
        soundGoBackOnKeyboard.stop();
        soundTypeTheRefactory.stop();
        soundTypeRe.stop();
        musicLibGdxJingle.stop();
    }

}
