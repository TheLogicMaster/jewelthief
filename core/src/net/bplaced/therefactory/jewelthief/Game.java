package net.bplaced.therefactory.jewelthief;

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

import net.bplaced.therefactory.jewelthief.actors.Enemy;
import net.bplaced.therefactory.jewelthief.actors.Player;
import net.bplaced.therefactory.jewelthief.constants.Config;
import net.bplaced.therefactory.jewelthief.constants.I18NKeys;
import net.bplaced.therefactory.jewelthief.constants.PrefsKeys;
import net.bplaced.therefactory.jewelthief.jewels.Jewel;
import net.bplaced.therefactory.jewelthief.misc.Util;
import net.bplaced.therefactory.jewelthief.net.HTTP;
import net.bplaced.therefactory.jewelthief.ui.buttons.GrayButton;

public class Game {

    // game state
    private final Player player;
    private final Array<Enemy> enemies;
    private final Array<Jewel> jewels;
    private final Array<Enemy> newEnemies;
    private int currentLevel = 0;
    private int numSeconds = 0;

    // gfx
    private final Sprite background;
    private Rectangle enemyField;
    private Rectangle jewelField;
    private final Rectangle playerField;
    private final Rectangle getreadyRect;
    private final Rectangle showMenuRect;
    private final Rectangle gameOverRect;
    private final BitmapFont font;
    private final GrayButton menuYesBtn;
    private final GrayButton menuNoBtn;
    private final GrayButton menuRestartBtn;
    private final GrayButton gameOverPlayAgainBtn;
    private final GrayButton gameOverExitBtn;
    private boolean renderFireworksEffect = false;
    private boolean showGetReady = true;
    private boolean showMenu = false;
    private I18NBundle bundle;
    private String gameEndPhrase;

    // sfx
    private boolean soundEnabled; // loaded in constructor
    private final Sound soundCollectJewel;
    private final Sound soundOuch;
    private final Sound soundApplause;
    private final Sound soundLose;

    // other
    private final Preferences prefs;
    private int frames = 0;
    private boolean debug = false;
    private boolean paused = true;

    public Game(Player player) {
        font = JewelThief.getInstance().getFont();
        font.setColor(Color.BLACK);
        prefs = JewelThief.getInstance().getPreferences();
        soundEnabled = prefs.getBoolean(PrefsKeys.ENABLE_SOUND);
        this.bundle = JewelThief.getInstance().getBundle();
        this.player = player;
        this.enemies = new Array<Enemy>();
        this.newEnemies = new Array<Enemy>();
        this.jewels = new Array<Jewel>();

        // load resources
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
        Sprite fade = JewelThief.getInstance().getTextureAtlas().createSprite("fade");
        fade.setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        fade.setPosition(0, 0);
        fade.setAlpha(0.6f);

        // sprites
        background = new Sprite(new Texture("levels/" + Config.levels[currentLevel].getLevelName() + ".png"));
        float fieldBorder = (Config.WINDOW_WIDTH - background.getWidth()) / 2;
        background.setPosition(fieldBorder, (Config.WINDOW_HEIGHT - background.getHeight() - 32) / 2);

        // rectangles
        playerField = new Rectangle(background.getX(), background.getY(), background.getWidth() - 1,
                background.getHeight() - 1);
        getreadyRect = new Rectangle(Config.WINDOW_WIDTH / 2 - 120 / 2, Config.WINDOW_HEIGHT / 2 - 48 / 2, 120, 48);
        showMenuRect = new Rectangle(Config.WINDOW_WIDTH / 2 - 230 / 2, Config.WINDOW_HEIGHT / 2 - 120 / 2, 230, 120);
        gameOverRect = new Rectangle(Config.WINDOW_WIDTH / 2 - 320 / 2, Config.WINDOW_HEIGHT / 2 - 130 / 2, 320, 130);

        // buttons
        menuYesBtn = new GrayButton(bundle.get(I18NKeys.YES), 150, 95, 55, 40);
        menuNoBtn = new GrayButton(bundle.get(I18NKeys.NO), 215, menuYesBtn.getY(), 55, menuYesBtn.getHeight());
        menuRestartBtn = new GrayButton(bundle.get(I18NKeys.RESTART), 280, menuYesBtn.getY(), 80,
                menuYesBtn.getHeight());
        gameOverPlayAgainBtn = new GrayButton(bundle.get(I18NKeys.PLAY_AGAIN), 155, 90, 90, menuYesBtn.getHeight());
        gameOverExitBtn = new GrayButton(bundle.get(I18NKeys.EXIT_TO_MENU), 260, gameOverPlayAgainBtn.getY(), 110,
                menuYesBtn.getHeight());

        resetGame();
    }

    private void loadLevel(int currentLevel) {
        background.setTexture(new Texture("levels/" + Config.levels[currentLevel].getLevelName() + ".png"));
        enemyField = new Rectangle(background.getX(), background.getY(), background.getWidth(), background.getHeight());
        jewelField = new Rectangle(background.getX(), background.getY(), background.getWidth(), 145);
        if (currentLevel == 13) {
            jewelField.height = 92;
        }
        if (currentLevel == 0 || currentLevel == 2 || currentLevel == 8 || currentLevel == 9 || currentLevel == 13) {
            enemyField.height = 145;
        }

        // spawn enemies
        int deltaEnemies = Config.levels[currentLevel].getNumEnemies() - enemies.size;

        // remove exceeding enemies
        for (int i = 0; deltaEnemies < 0 && i < Math.abs(deltaEnemies); i++) {
            enemies.pop();
        }

        int numRemainingEnemies = enemies.size;

        // update existing enemies while preserving their positions and movement directions
        try {
            newEnemies.clear();
            for (int i = 0; i < numRemainingEnemies; i++) {
                Enemy enemy = Config.levels[currentLevel].getEnemyClass().newInstance();
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
                Enemy enemy = Config.levels[currentLevel].getEnemyClass().newInstance();
                enemy.setPosition(
                        Util.randomWithin(enemyField.getX() + enemy.getSprite().getWidth() / 2, enemyField.getX()
                                + enemyField.getWidth() - enemy.getSprite().getWidth() / 2), 100);
                enemy.update();
                enemies.add(enemy);
            }

            rearrangeEnemies();

            // spawn jewels
            for (int i = 0; i < Config.levels[currentLevel].getNumJewels(); i++) {
                Jewel jewel = Config.levels[currentLevel].getJewelClass().newInstance();
                jewel.setPosition(Util.randomWithin(jewelField.getX() + jewel.getSprite().getWidth() / 2,
                        jewelField.getX() + jewelField.getWidth() - jewel.getSprite().getWidth() / 2), Util
                        .randomWithin(jewelField.getY() + jewel.getSprite().getHeight() / 2,
                                (jewelField.getY() + jewelField.getHeight()) - (jewel.getSprite().getHeight())));
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
        currentLevel = Config.START_LEVEL;
        enemies.clear();
        jewels.clear();
        numSeconds = 0;
        player.reset();
        showGetReady = true;
        pause();

        // initialize level
        loadLevel(currentLevel);

        // initial update for all entities
        player.setPosition(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2 + 50);
        player.update();
        for (Enemy enemy : enemies)
            enemy.update(enemyField);
        for (Jewel jewel : jewels)
            jewel.update();
    }

    public void update(float delta) {
        if (paused) {
            return;
        }

        player.update();
        Rectangle playerBoundingRectangle = player.getSprite().getBoundingRectangle();

        // collision with wall
        boolean touchingWall = !playerField.contains(playerBoundingRectangle);
        if (touchingWall) {
            if (!player.isAlreadyTouchingWall()) {
                player.setAlreadyTouchingWall(true);
                player.decrementNumMen();
                player.turnTemporarilyRed(true);
                if (soundEnabled)
                    soundOuch.play(0.5f);
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
                enemy.update(enemyField);

            // rectangular collision detection
            if (enemy.getSprite().getBoundingRectangle().overlaps(playerBoundingRectangle)) {
                rectangularCollision = true;
                if (!player.isAlreadyTouchingEnemy()) {

                    // polygon collision detection
                    if (Intersector.overlapConvexPolygons(playerPolygon, enemy.getPolygon())) {
                        player.setAlreadyTouchingEnemy(true);
                        player.decrementNumMen();
                        player.turnTemporarilyRed(true);
                        if (soundEnabled)
                            soundOuch.play(0.5f);
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
                    soundCollectJewel.play(0.6f);
                }
                player.incrementNumCollectedJewels();
                if (player.getNumCollectedJewels() % Config.PLUS_ONE_MAN_INTERVAL == 0)
                    player.incrementNumMen();
            }
        }

        // proceed to next level
        if (jewels.size == 0) {
            if (currentLevel + 1 >= Config.levels.length) {
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
        String[] applausePhrases = bundle.get(I18NKeys.APPLAUSE_PHRASES).split(";");
        String[] motivationPhrases = bundle.get(I18NKeys.MOTIVATION_PHRASES).split(";");
        gameEndPhrase = bundle
                .format(I18NKeys.GAME_END_PHRASE,
                        Math.max(0, currentScore),
                        player.getNumCollectedJewels(),
                        Util.secondsToTimeString(numSeconds),
                        currentScore > bestScore ? bundle.get(I18NKeys.IMPROVED_HIGHSCORE) : bundle
                                .get(I18NKeys.MISSED_HIGHSCORE), currentScore > bestScore ? (currentScore - bestScore)
                                : Math.abs(Math.max(0, currentScore) - bestScore),
                        currentScore > bestScore ? applausePhrases[Util.randomWithin(0, applausePhrases.length - 1)]
                                : motivationPhrases[Util.randomWithin(0, motivationPhrases.length - 1)]);

        if (currentScore > bestScore) {
            renderFireworksEffect = true;
            if (soundEnabled)
                soundApplause.play();
            prefs.putInteger(PrefsKeys.BEST_SCORE, currentScore);
            prefs.putInteger(PrefsKeys.BEST_SCORE_NUM_JEWELS, player.getNumCollectedJewels());
            prefs.putInteger(PrefsKeys.BEST_SCORE_NUM_SECONDS, numSeconds);
            prefs.flush();
            HTTP.submitHighscores(prefs.getString("id"), prefs.getString(PrefsKeys.PLAYER_NAME), player.getNumCollectedJewels(), numSeconds);
        } else {
            if (soundEnabled)
                soundLose.play();
        }
    }

    public void render(ShapeRenderer sr) {
        // do nothing
    }

    public void postRender(ShapeRenderer sr) {
        if (player.getNumMen() <= 0) {
            sr.setColor(Color.WHITE);
            sr.rect(gameOverRect.x, gameOverRect.y, gameOverRect.width, gameOverRect.height);
            sr.setColor(Color.DARK_GRAY);
            sr.rect(gameOverRect.x, gameOverRect.y + gameOverRect.height - 28, gameOverRect.width, 28);
            gameOverPlayAgainBtn.renderShape(sr);
            gameOverExitBtn.renderShape(sr);
        } else if (showMenu) {
            sr.setColor(Color.WHITE);
            sr.rect(showMenuRect.x, showMenuRect.y, showMenuRect.width, showMenuRect.height);
            sr.setColor(Color.DARK_GRAY);
            sr.rect(showMenuRect.x, showMenuRect.y + showMenuRect.height - 28, showMenuRect.width, 28);
            menuYesBtn.renderShape(sr);
            menuNoBtn.renderShape(sr);
            menuRestartBtn.renderShape(sr);
        } else if (showGetReady) {
            sr.setColor(Color.BLUE);
            sr.rect(getreadyRect.x + 1, getreadyRect.y + 1, getreadyRect.width - 2, getreadyRect.height - 2);
            sr.setColor(Color.WHITE);
            sr.rect(getreadyRect.x + 5, getreadyRect.y + 5, getreadyRect.width - 10, getreadyRect.height - 10);
        }
    }

    public void render(SpriteBatch batch, float delta) {
        background.draw(batch);
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

    public void postRender(SpriteBatch batch, float delta) {
        if (player.getNumMen() <= 0) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Jewel Thief", 220, 198);
            font.setColor(Color.BLACK);
            font.draw(batch, gameEndPhrase, Config.WINDOW_WIDTH / 2 - 145, Config.WINDOW_HEIGHT / 2 + 25);
            gameOverPlayAgainBtn.setCaption(bundle.get(I18NKeys.PLAY_AGAIN));
            gameOverPlayAgainBtn.renderCaption(batch);
            gameOverExitBtn.setCaption(bundle.get(I18NKeys.EXIT_TO_MENU));
            gameOverExitBtn.renderCaption(batch);
        } else if (showMenu) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Jewel Thief", 220, 193);
            font.setColor(Color.BLACK);
            font.draw(batch, bundle.get(I18NKeys.GIVING_UP_ALREADY), 200, 157);
            menuYesBtn.setCaption(bundle.get(I18NKeys.YES));
            menuYesBtn.renderCaption(batch);
            menuNoBtn.setCaption(bundle.get(I18NKeys.NO));
            menuNoBtn.renderCaption(batch);
            menuRestartBtn.setCaption(bundle.get(I18NKeys.RESTART));
            menuRestartBtn.renderCaption(batch);
        } else if (showGetReady) {
            font.draw(batch, bundle.get(I18NKeys.GET_READY) + "...", Config.WINDOW_WIDTH / 2 - 43, Config.WINDOW_HEIGHT / 2 + 3);
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
                sr.rect(menuYesBtn.getX(), menuYesBtn.getY(), menuYesBtn.getWidth(), menuYesBtn.getHeight());
                sr.rect(menuNoBtn.getX(), menuNoBtn.getY(), menuNoBtn.getWidth(), menuNoBtn.getHeight());
                sr.rect(menuRestartBtn.getX(), menuRestartBtn.getY(), menuRestartBtn.getWidth(),
                        menuRestartBtn.getHeight());
            } else if (player.getNumMen() <= 0) {
                sr.rect(Config.WINDOW_WIDTH / 2 - 63, Config.WINDOW_HEIGHT / 2 - 45, 58, 28);
                sr.rect(Config.WINDOW_WIDTH / 2 + 5, Config.WINDOW_HEIGHT / 2 - 45, 57, 28);
            } else {
                sr.setColor(Color.RED);
                sr.rect(enemyField.x, enemyField.y, enemyField.width, enemyField.height);
                sr.setColor(Color.GREEN);
                sr.rect(jewelField.x, jewelField.y, jewelField.width, jewelField.height);
                sr.setColor(Color.WHITE);
                sr.rect(playerField.x, playerField.y, playerField.width, playerField.height);
            }
        }
        sr.end();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public Sprite getBackground() {
        return background;
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
        return Config.levels[currentLevel].getJewelClass().getSimpleName();
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
            if (sprite.getBoundingRectangle().x < enemyField.x) {
                enemy.setPosition(enemyField.x + sprite.getWidth() / 2, enemy.getPosition().y);
            }
            if (sprite.getBoundingRectangle().x > enemyField.x + enemyField.width) {
                enemy.setPosition(enemyField.x + enemyField.width - sprite.getWidth() / 2, enemy.getPosition().y);
            }
            if (sprite.getBoundingRectangle().y < enemyField.y) {
                enemy.setPosition(enemy.getPosition().x, enemy.getPosition().y + sprite.getHeight() / 2);
            }
            if (sprite.getBoundingRectangle().y > enemyField.y + enemyField.height) {
                enemy.setPosition(enemy.getPosition().x, enemyField.y + enemyField.height - sprite.getHeight() / 2);
            }
            enemy.update();
        }
    }

    public GrayButton getMenuRestartBtn() {
        return menuRestartBtn;
    }

    public GrayButton getMenuNoBtn() {
        return menuNoBtn;
    }

    public GrayButton getMenuYesBtn() {
        return menuYesBtn;
    }

    public GrayButton getGameOverPlayAgainBtn() {
        return gameOverPlayAgainBtn;
    }

    public GrayButton getGameOverExitBtn() {
        return gameOverExitBtn;
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
            font.draw(batch, "enemyField", enemyField.getX(), enemyField.getY() + enemyField.getHeight());
            font.setColor(Color.GREEN);
            font.draw(batch, "jewelField", jewelField.getX() + jewelField.getWidth() - 65, jewelField.getY()
                    + jewelField.getHeight());
            font.setColor(Color.WHITE);
            font.draw(batch, "playerField", playerField.getX() + playerField.getWidth() - 70, playerField.getY()
                    + playerField.getHeight());
            batch.end();
        }
    }

    public void show() {
        renderFireworksEffect = false;
        JewelThief.getInstance().resetFireworksEffects();
        bundle = JewelThief.getInstance().getBundle();
    }
}
