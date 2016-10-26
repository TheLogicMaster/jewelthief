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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import at.therefactory.jewelthief.Game;
import at.therefactory.jewelthief.actors.Player;
import at.therefactory.jewelthief.input.GameScreenInputAdapter;
import at.therefactory.jewelthief.ui.Hud;

public class GameScreen extends ScreenAdapter {

    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Hud hud;
    private final Game game;

    public GameScreen(SpriteBatch batch, ShapeRenderer shapeRenderer, FitViewport viewport, OrthographicCamera camera) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.viewport = viewport;
        this.camera = camera;
        Player player = new Player();
        game = new Game(player);
        hud = new Hud(game);
    }

    @Override
    public void show() {
        super.show();
        hud.show();
        game.show();
        Gdx.input.setInputProcessor(new GameScreenInputAdapter(game, viewport, hud));
    }

    @Override
    public void hide() {
        super.hide();
        game.pause();
        game.showGetReady();
    }

    @Override
    public void pause() {
        super.pause();
        game.pause();
        game.showGetReady();
    }

    @Override
    public void render(float delta) {

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // simulate world
        update();

        // render shapes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hud.render(shapeRenderer);
        game.render();
        shapeRenderer.end();

        // render sprites/fonts
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hud.render(batch);
        game.render(batch, delta);
        batch.end();

        // render shapes for dialogs
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        hud.postRender(shapeRenderer);
        game.postRender(shapeRenderer);
        shapeRenderer.end();

        // render sprites/fonts for dialogs
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hud.postRender(batch);
        game.postRender(batch);
        batch.end();

//        if (DEBUG_MODE) {
//            shapeRenderer.setProjectionMatrix(camera.combined);
//            game.debug(shapeRenderer);
//            batch.setProjectionMatrix(camera.combined);
//            game.debug(batch);
//        }
    }

    private void update() {
        game.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void resetGame() {
        game.resetGame();
    }

    @Override
    public void dispose() {
        super.dispose();
        game.dispose();
    }

}
