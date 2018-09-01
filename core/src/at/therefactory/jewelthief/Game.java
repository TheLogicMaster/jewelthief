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

package at.therefactory.jewelthief;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import at.therefactory.jewelthief.actors.Enemy;
import at.therefactory.jewelthief.actors.Player;
import at.therefactory.jewelthief.constants.PrefsKeys;
import at.therefactory.jewelthief.jewels.Jewel;
import at.therefactory.jewelthief.misc.Utils;
import at.therefactory.jewelthief.net.HttpServer;
import at.therefactory.jewelthief.ui.buttons.GrayButton;

import static at.therefactory.jewelthief.constants.Config.PLUS_ONE_MAN_INTERVAL;
import static at.therefactory.jewelthief.constants.Config.START_LEVEL;
import static at.therefactory.jewelthief.constants.Config.WINDOW_HEIGHT;
import static at.therefactory.jewelthief.constants.Config.WINDOW_WIDTH;
import static at.therefactory.jewelthief.constants.Config.levels;
import static at.therefactory.jewelthief.constants.I18NKeys.APPLAUSE_PHRASES;
import static at.therefactory.jewelthief.constants.I18NKeys.EXIT_TO_MENU;
import static at.therefactory.jewelthief.constants.I18NKeys.GAME_END_PHRASE;
import static at.therefactory.jewelthief.constants.I18NKeys.GET_READY;
import static at.therefactory.jewelthief.constants.I18NKeys.GIVING_UP_ALREADY;
import static at.therefactory.jewelthief.constants.I18NKeys.IMPROVED_HIGHSCORE;
import static at.therefactory.jewelthief.constants.I18NKeys.MISSED_HIGHSCORE;
import static at.therefactory.jewelthief.constants.I18NKeys.MOTIVATION_PHRASES;
import static at.therefactory.jewelthief.constants.I18NKeys.NO;
import static at.therefactory.jewelthief.constants.I18NKeys.PLAY_AGAIN;
import static at.therefactory.jewelthief.constants.I18NKeys.RESTART;
import static at.therefactory.jewelthief.constants.I18NKeys.YES;

public class Game {

    // game state
    private final Player player;
    private final Array<Enemy> enemies;
    private final Array<Jewel> jewels;
    private final Array<Enemy> newEnemies;
    private short currentLevel = 0;
    private short numSeconds = 0;

    // gfx
    private Sprite spriteBackground;
    private Rectangle rectangleEnemyField;
    private Rectangle rectangleJewelField;
    private Rectangle rectanglePlayerField;
    private Rectangle rectangleGetReady;
    private Rectangle rectangleShowMenu;
    private Rectangle rectangleGameOver;
    private final BitmapFont font;
    private GrayButton buttonYes;
    private GrayButton buttonNo;
    private GrayButton buttonRestart;
    private GrayButton buttonPlayAgain;
    private GrayButton buttonExit;
    private boolean renderFireworksEffect = false;
    private boolean showGetReady = true;
    private boolean showMenu = false;
    private I18NBundle bundle;
    private String gameEndPhrase;

    // sfx
    private boolean soundEnabled; // loaded in constructor
    private Sound soundCollectJewel;
    private Sound soundOuch;
    private Sound soundApplause;
    private Sound soundLose;

    // other
    private final Preferences prefs;
    private short frames = 0;
    private boolean debug = false;
    private boolean paused = true;

    public Game(Player player) {
        font = JewelThief.getInstance().getFont();
        prefs = JewelThief.getInstance().getPreferences();
        bundle = JewelThief.getInstance().getBundle();

        soundEnabled = prefs.getBoolean(PrefsKeys.ENABLE_SOUND);
        font.setColor(Color.BLACK);
        this.player = player;

        enemies = new Array<Enemy>();
        newEnemies = new Array<Enemy>();
        jewels = new Array<Jewel>();

        loadAssets();
        initGuiElements();
        resetGame();
    }

    private void initGuiElements() {
        // sprites
        spriteBackground = new Sprite(new Texture("levels/" + levels[currentLevel].getLevelName() + ".png"));
        spriteBackground.setPosition((WINDOW_WIDTH - spriteBackground.getWidth()) / 2, (WINDOW_HEIGHT - spriteBackground.getHeight() - 32) / 2);

        // rectangles
        rectanglePlayerField = new Rectangle(spriteBackground.getX(), spriteBackground.getY(), spriteBackground.getWidth() - 1,
                spriteBackground.getHeight() - 1);
        rectangleGetReady = new Rectangle(WINDOW_WIDTH / 2 - 120 / 2, WINDOW_HEIGHT / 2 - 48 / 2, 120, 48);
        rectangleShowMenu = new Rectangle(WINDOW_WIDTH / 2 - 230 / 2, WINDOW_HEIGHT / 2 - 120 / 2, 230, 120);
        rectangleGameOver = new Rectangle(WINDOW_WIDTH / 2 - 320 / 2, WINDOW_HEIGHT / 2 - 130 / 2, 320, 130);

        // buttons
        buttonYes = new GrayButton(bundle.get(YES), 150, 95, 55, 40);
        buttonNo = new GrayButton(bundle.get(NO), 215, buttonYes.getY(), 55, buttonYes.getHeight());
        buttonRestart = new GrayButton(bundle.get(RESTART), 280, buttonYes.getY(), 80,
                buttonYes.getHeight());
        buttonPlayAgain = new GrayButton(bundle.get(PLAY_AGAIN), 155, 90, 90, buttonYes.getHeight());
        buttonExit = new GrayButton(bundle.get(EXIT_TO_MENU), 260, buttonPlayAgain.getY(), 110,
                buttonYes.getHeight());
    }

    private void loadAssets() {
        AssetManager manager = JewelThief.getInstance().getAssetManager();
        manager.load("audio/sounds/collect.ogg", Sound.class);
        manager.load("audio/sounds/coin.ogg", Sound.class);
        manager.load("audio/sounds/applause.ogg", Sound.class);
        manager.load("audio/sounds/one_blow_from_party_horn.ogg", Sound.class);
        manager.finishLoading();
        soundCollectJewel = manager.get("audio/sounds/collect.ogg", Sound.class);
        soundOuch = manager.get("audio/sounds/coin.ogg", Sound.class);
        soundApplause = manager.get("audio/sounds/applause.ogg", Sound.class);
        soundLose = manager.get("audio/sounds/one_blow_from_party_horn.ogg", Sound.class);
    }

    private void loadLevel(int currentLevel) {
        spriteBackground.setTexture(new Texture("levels/" + levels[currentLevel].getLevelName() + ".png"));
        rectangleEnemyField = new Rectangle(spriteBackground.getX(), spriteBackground.getY(), spriteBackground.getWidth(), spriteBackground.getHeight());
        rectangleJewelField = new Rectangle(spriteBackground.getX(), spriteBackground.getY(), spriteBackground.getWidth(), 145);
        if (currentLevel == 13) {
            rectangleJewelField.height = 92;
        }
        if (currentLevel == 0 || currentLevel == 2 || currentLevel == 8 || currentLevel == 9 || currentLevel == 13) {
            rectangleEnemyField.height = 145;
        }

        // spawn enemies
        int deltaEnemies = levels[currentLevel].getNumEnemies() - enemies.size;

        // remove exceeding enemies
        for (int i = 0; deltaEnemies < 0 && i < Math.abs(deltaEnemies); i++) {
            enemies.pop();
        }

        // update existing enemies while preserving their positions and movement directions
        try {
            newEnemies.clear();
            for (int i = 0; i < enemies.size; i++) {
                Enemy enemy = levels[currentLevel].getEnemyClass().newInstance();
                Enemy oldEnemy = enemies.get(i);
                enemy.setMovementInverter(oldEnemy.getMovementInverter());
                enemy.setPosition(oldEnemy.getSprite().getX(), oldEnemy.getSprite().getY());
                enemy.update();
                newEnemies.add(enemy);
            }
            enemies.clear();
            for (Enemy newEnemy : newEnemies) {
                enemies.add(newEnemy);
            }

            // add additional enemies with new random position
            for (int i = 0; deltaEnemies > 0 && i < deltaEnemies; i++) {
                Enemy enemy = levels[currentLevel].getEnemyClass().newInstance();
                enemy.setPosition(
                        Utils.randomWithin(rectangleEnemyField.getX() + enemy.getSprite().getWidth() / 2, rectangleEnemyField.getX()
                                + rectangleEnemyField.getWidth() - enemy.getSprite().getWidth() / 2), 100);
                enemy.update();
                enemies.add(enemy);
            }

            rearrangeEnemies();

            // spawn jewels
            for (int i = 0; i < levels[currentLevel].getNumJewels(); i++) {
                Jewel jewel = levels[currentLevel].getJewelClass().newInstance();
                jewel.setPosition(Utils.randomWithin(rectangleJewelField.getX() + jewel.getSprite().getWidth() / 2,
                        rectangleJewelField.getX() + rectangleJewelField.getWidth() - jewel.getSprite().getWidth() / 2), Utils
                        .randomWithin(rectangleJewelField.getY() + jewel.getSprite().getHeight() / 2,
                                (rectangleJewelField.getY() + rectangleJewelField.getHeight()) - (jewel.getSprite().getHeight())));
                jewel.update();
                jewels.add(jewel);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void resetGame() {
        renderFireworksEffect = false;
        JewelThief.getInstance().resetFireworksEffects();
        player.turnTemporarilyRed(false);
        soundEnabled = prefs.getBoolean(PrefsKeys.ENABLE_SOUND);
        showMenu = false;
        currentLevel = START_LEVEL;
        enemies.clear();
        jewels.clear();
        numSeconds = 0;
        player.reset();
        showGetReady = true;
        pause();

        // initialize level
        loadLevel(currentLevel);

        // initial update for all entities
        player.setPosition(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 50);
        player.update();
        for (Enemy enemy : enemies)
            enemy.update(rectangleEnemyField);
        for (Jewel jewel : jewels)
            jewel.update();
    }

    public void update() {
        if (paused) {
            return;
        }

        player.update();
        Rectangle rectanglePlayerBounds = player.getSprite().getBoundingRectangle();

        // collision with wall
        boolean touchingWall = !rectanglePlayerField.contains(rectanglePlayerBounds);
        if (touchingWall) {
            if (!player.isAlreadyTouchingWall()) {
                player.setAlreadyTouchingWall(true);
                player.decrementNumMen();
                player.turnTemporarilyRed(true);
                if (soundEnabled)
                    soundOuch.play(.5f);
            }
            player.setAlreadyTouchingWall(true);
        }
        if (!touchingWall)
            player.setAlreadyTouchingWall(false);

        // game duration
        frames++;
        if (frames >= 60) {
            frames = 0;
            numSeconds++;
        }

        // collision detection with enemies
        boolean rectangularCollision = false;
        Polygon playerPolygon = player.getPolygon();
        for (Enemy enemy : enemies) {
            if (!debug)
                enemy.update(rectangleEnemyField);

            // rectangular collision detection
            if (enemy.getSprite().getBoundingRectangle().overlaps(rectanglePlayerBounds)) {
                rectangularCollision = true;
                if (!player.isAlreadyTouchingEnemy()) {

                    // polygon collision detection
                    if (Intersector.overlapConvexPolygons(playerPolygon, enemy.getPolygon())) {
                        player.setAlreadyTouchingEnemy(true);
                        player.decrementNumMen();
                        player.turnTemporarilyRed(true);
                        if (soundEnabled)
                            soundOuch.play(.5f);
                        break;
                    }
                }
            }
        }
        if (!rectangularCollision) {
            player.setAlreadyTouchingEnemy(false);
        }

        // collecting jewels
        for (int i = 0; i < jewels.size; i++) {
            Jewel jewel = jewels.get(i);
            jewel.update();
            if (Intersector.overlapConvexPolygons(player.getPolygon(), jewel.getPolygon())) {
                jewels.removeIndex(i);
                if (soundEnabled) {
                    long id = soundCollectJewel.play(.6f);
                    soundCollectJewel.setPitch(id, Utils.randomWithin(1f, 1.25f));
                }
                player.incrementNumCollectedJewels();
                if (player.getNumCollectedJewels() % PLUS_ONE_MAN_INTERVAL == 0)
                    player.incrementNumMen();
            }
        }

        // proceed to next level
        if (jewels.size == 0) {
            if (currentLevel + 1 >= levels.length) {
                //gameWon = true; // currently deprecated due to endless gameplay
                currentLevel = 0;
            } else {
                currentLevel++;
            }
            loadLevel(currentLevel);
        }

        if (player.getNumMen() <= 0) {
            calculateScore();
            pause();
        }
    }

    private void calculateScore() {
        int bestScore = prefs.contains(PrefsKeys.BEST_SCORE) ? prefs.getInteger(PrefsKeys.BEST_SCORE) : 0;
        int currentScore = (player.getNumCollectedJewels() - numSeconds);

        // build game end phrase to display in dialog
        String[] applausePhrases = bundle.get(APPLAUSE_PHRASES).split(";");
        String[] motivationPhrases = bundle.get(MOTIVATION_PHRASES).split(";");
        gameEndPhrase = bundle
                .format(GAME_END_PHRASE,
                        Math.max(0, currentScore),
                        player.getNumCollectedJewels(),
                        Utils.secondsToTimeString(numSeconds),
                        currentScore > bestScore ? bundle.get(IMPROVED_HIGHSCORE) : bundle
                                .get(MISSED_HIGHSCORE), currentScore > bestScore ? (currentScore - bestScore)
                                : Math.abs(Math.max(0, currentScore) - bestScore),
                        currentScore > bestScore ? applausePhrases[Utils.randomWithin(0, applausePhrases.length - 1)]
                                : motivationPhrases[Utils.randomWithin(0, motivationPhrases.length - 1)]);

        if (currentScore > bestScore) {
            renderFireworksEffect = true;
            if (soundEnabled)
                soundApplause.play();
            prefs.putInteger(PrefsKeys.BEST_SCORE, currentScore);
            prefs.putInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS, player.getNumCollectedJewels());
            prefs.putInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS, numSeconds);
            prefs.flush();
            HttpServer.submitHighscores(prefs.getString("id"), prefs.getString(PrefsKeys.PLAYER_NAME), player.getNumCollectedJewels(), numSeconds);
        } else {
            if (soundEnabled)
                soundLose.play();
        }
    }

    public void render() {
        // do nothing
    }

    public void postRender(ShapeRenderer sr) {
        if (player.getNumMen() <= 0) {
            sr.setColor(Color.WHITE);
            sr.rect(rectangleGameOver.x, rectangleGameOver.y, rectangleGameOver.width, rectangleGameOver.height);
            sr.setColor(Color.DARK_GRAY);
            sr.rect(rectangleGameOver.x, rectangleGameOver.y + rectangleGameOver.height - 28, rectangleGameOver.width, 28);
            buttonPlayAgain.renderShape(sr);
            buttonExit.renderShape(sr);
        } else if (showMenu) {
            sr.setColor(Color.WHITE);
            sr.rect(rectangleShowMenu.x, rectangleShowMenu.y, rectangleShowMenu.width, rectangleShowMenu.height);
            sr.setColor(Color.DARK_GRAY);
            sr.rect(rectangleShowMenu.x, rectangleShowMenu.y + rectangleShowMenu.height - 28, rectangleShowMenu.width, 28);
            buttonYes.renderShape(sr);
            buttonNo.renderShape(sr);
            buttonRestart.renderShape(sr);
        } else if (showGetReady) {
            sr.setColor(Color.BLUE);
            sr.rect(rectangleGetReady.x + 1, rectangleGetReady.y + 1, rectangleGetReady.width - 2, rectangleGetReady.height - 2);
            sr.setColor(Color.WHITE);
            sr.rect(rectangleGetReady.x + 5, rectangleGetReady.y + 5, rectangleGetReady.width - 10, rectangleGetReady.height - 10);
        }
    }

    public void render(SpriteBatch batch, float delta) {
        spriteBackground.draw(batch);
        for (Jewel jewel : jewels)
            jewel.getSprite().draw(batch);
        for (Enemy enemy : enemies)
            enemy.getSprite().draw(batch);
        player.getSprite().draw(batch);

        // fade sprite
        if (isPaused() && (showMenu || player.getNumMen() <= 0)) {
            JewelThief.getInstance().getFadeSprite().setAlpha(0.4f);
            JewelThief.getInstance().getFadeSprite().draw(batch);
        }
        if (renderFireworksEffect) {
            JewelThief.getInstance().renderFireworksEffect(batch, delta);
        }
    }

    public void postRender(SpriteBatch batch) {
        if (player.getNumMen() <= 0) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Jewel Thief", 220, 198);
            font.setColor(Color.BLACK);
            font.draw(batch, gameEndPhrase, WINDOW_WIDTH / 2 - 145, WINDOW_HEIGHT / 2 + 25);
            buttonPlayAgain.setCaption(bundle.get(PLAY_AGAIN));
            buttonPlayAgain.renderCaption(batch);
            buttonExit.setCaption(bundle.get(EXIT_TO_MENU));
            buttonExit.renderCaption(batch);
        } else if (showMenu) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Jewel Thief", 220, 193);
            font.setColor(Color.BLACK);
            font.draw(batch, bundle.get(GIVING_UP_ALREADY), 200, 157);
            buttonYes.setCaption(bundle.get(YES));
            buttonYes.renderCaption(batch);
            buttonNo.setCaption(bundle.get(NO));
            buttonNo.renderCaption(batch);
            buttonRestart.setCaption(bundle.get(RESTART));
            buttonRestart.renderCaption(batch);
        } else if (showGetReady) {
            font.draw(batch, bundle.get(GET_READY) + "...", WINDOW_WIDTH / 2 - 43, WINDOW_HEIGHT / 2 + 3);
        }
    }

    public void debug(ShapeRenderer sr) {
        if (debug) {
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            sr.polygon(player.getPolygon().getVertices());
            for (Enemy enemy : enemies)
                sr.polygon(enemy.getPolygon().getVertices());
            for (Jewel jewel : jewels)
                sr.polygon(jewel.getPolygon().getVertices());
            if (showMenu) {
                sr.rect(buttonYes.getX(), buttonYes.getY(), buttonYes.getWidth(), buttonYes.getHeight());
                sr.rect(buttonNo.getX(), buttonNo.getY(), buttonNo.getWidth(), buttonNo.getHeight());
                sr.rect(buttonRestart.getX(), buttonRestart.getY(), buttonRestart.getWidth(),
                        buttonRestart.getHeight());
            } else if (player.getNumMen() <= 0) {
                sr.rect(WINDOW_WIDTH / 2 - 63, WINDOW_HEIGHT / 2 - 45, 58, 28);
                sr.rect(WINDOW_WIDTH / 2 + 5, WINDOW_HEIGHT / 2 - 45, 57, 28);
            } else {
                sr.setColor(Color.RED);
                sr.rect(rectangleEnemyField.x, rectangleEnemyField.y, rectangleEnemyField.width, rectangleEnemyField.height);
                sr.setColor(Color.GREEN);
                sr.rect(rectangleJewelField.x, rectangleJewelField.y, rectangleJewelField.width, rectangleJewelField.height);
                sr.setColor(Color.WHITE);
                sr.rect(rectanglePlayerField.x, rectanglePlayerField.y, rectanglePlayerField.width, rectanglePlayerField.height);
            }
        }
        sr.end();
    }

    public short getCurrentLevel() {
        return currentLevel;
    }

    public Sprite getSpriteBackground() {
        return spriteBackground;
    }

    public Player getPlayer() {
        return player;
    }

    public void pause() {
        paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public void play() {
        paused = false;
        showGetReady = false;
    }

    public String getCurrentJewelName() {
        return levels[currentLevel].getJewelClass().getSimpleName();
    }

    public int getGameDuration() {
        return numSeconds;
    }

    public void showMenu() {
        pause();
        showMenu = true;
    }

    public boolean isMenuShown() {
        return showMenu;
    }

    public void hideMenu() {
        showMenu = false;
        showGetReady = true;
    }

    public void switchDebug() {
        debug = !debug;
    }

    public void dispose() {
        soundCollectJewel.dispose();
        soundOuch.dispose();
        soundApplause.dispose();
        soundLose.dispose();
    }

    public boolean isGetReadyShown() {
        return showGetReady;
    }

    public void showGetReady() {
        showGetReady = true;
    }

    /**
     * Checks and rectifies the positions of enemies if they are outside of the enemy field.
     */
    public void rearrangeEnemies() {
        for (Enemy enemy : enemies) {
            Sprite sprite = enemy.getSprite();
            if (sprite.getBoundingRectangle().x < rectangleEnemyField.x) {
                enemy.setPosition(rectangleEnemyField.x + sprite.getWidth() / 2, enemy.getPosition().y);
            }
            if (sprite.getBoundingRectangle().x > rectangleEnemyField.x + rectangleEnemyField.width) {
                enemy.setPosition(rectangleEnemyField.x + rectangleEnemyField.width - sprite.getWidth() / 2, enemy.getPosition().y);
            }
            if (sprite.getBoundingRectangle().y < rectangleEnemyField.y) {
                enemy.setPosition(enemy.getPosition().x, enemy.getPosition().y + sprite.getHeight() / 2);
            }
            if (sprite.getBoundingRectangle().y > rectangleEnemyField.y + rectangleEnemyField.height) {
                enemy.setPosition(enemy.getPosition().x, rectangleEnemyField.y + rectangleEnemyField.height - sprite.getHeight() / 2);
            }
            enemy.update();
        }
    }

    public GrayButton getButtonRestart() {
        return buttonRestart;
    }

    public GrayButton getButtonNo() {
        return buttonNo;
    }

    public GrayButton getButtonYes() {
        return buttonYes;
    }

    public GrayButton getButtonPlayAgain() {
        return buttonPlayAgain;
    }

    public GrayButton getButtonExit() {
        return buttonExit;
    }

    public void collectAllJewels() {
        jewels.clear();
    }

    public void debug(SpriteBatch batch) {
        if (debug) {
            String sb = "currentLevel=" + currentLevel + "\n" + "numCollectedJewels=" + player.getNumCollectedJewels() + "\n";
            batch.begin();
            font.setColor(Color.BLACK);
            font.draw(batch, sb, 5, 20);
            font.setColor(Color.RED);
            font.draw(batch, "rectangleEnemyField", rectangleEnemyField.getX(), rectangleEnemyField.getY() + rectangleEnemyField.getHeight());
            font.setColor(Color.GREEN);
            font.draw(batch, "rectangleJewelField", rectangleJewelField.getX() + rectangleJewelField.getWidth() - 65, rectangleJewelField.getY()
                    + rectangleJewelField.getHeight());
            font.setColor(Color.WHITE);
            font.draw(batch, "rectanglePlayerField", rectanglePlayerField.getX() + rectanglePlayerField.getWidth() - 70, rectanglePlayerField.getY()
                    + rectanglePlayerField.getHeight());
            batch.end();
        }
    }

    public void show() {
        renderFireworksEffect = false;
        JewelThief.getInstance().resetFireworksEffects();
        bundle = JewelThief.getInstance().getBundle();
    }
}
