package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.MainGame;
import com.fruitslash000900.app9751.UiFactory;

/**
 * Shown after any game mode ends.
 * Displays score count-up, sliced/missed stats, personal best, and navigation buttons.
 */
public class SliceResultScreen implements Screen {

    private static final String BG = "ui/slice_result.png";

    private final MainGame game;
    private final int finalScore;
    private final int slicedCount;
    private final int missedCount;
    private final String gameMode;
    private final String highScoreKey;
    private final String leaderboardKey;

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer sr;
    private Texture bgTexture;

    // Score count-up
    private float countUpTimer = 0f;
    private int   displayedScore = 0;
    private boolean countUpDone = false;

    // Personal best
    private int personalBest;
    private boolean isNewBest;

    // Particles for celebration
    private float celebTimer = 0f;
    private static final int NUM_CONFETTI = 28;
    private float[] confX   = new float[NUM_CONFETTI];
    private float[] confY   = new float[NUM_CONFETTI];
    private float[] confVX  = new float[NUM_CONFETTI];
    private float[] confVY  = new float[NUM_CONFETTI];
    private float[] confR   = new float[NUM_CONFETTI];
    private Color[] confCol = new Color[NUM_CONFETTI];

    public SliceResultScreen(MainGame game, int finalScore, int slicedCount, int missedCount,
                             String gameMode, String highScoreKey, String leaderboardKey) {
        this.game           = game;
        this.finalScore     = finalScore;
        this.slicedCount    = slicedCount;
        this.missedCount    = missedCount;
        this.gameMode       = gameMode;
        this.highScoreKey   = highScoreKey;
        this.leaderboardKey = leaderboardKey;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }
        bgTexture = game.manager.get(BG, Texture.class);

        personalBest = game.prefs.getInteger(highScoreKey, 0);
        isNewBest    = finalScore >= personalBest && finalScore > 0;

        initConfetti();
        buildUI();
        registerInput();
    }

    private void initConfetti() {
        com.badlogic.gdx.math.MathUtils mu = null; // static access
        Color[] palette = {
            Color.valueOf(Constants.COLOR_PRIMARY),
            Color.valueOf(Constants.COLOR_ACCENT),
            new Color(1f, 0.87f, 0f, 1f),
            new Color(0.1f, 0.9f, 0.4f, 1f),
            Color.WHITE
        };
        for (int i = 0; i < NUM_CONFETTI; i++) {
            confX[i]   = com.badlogic.gdx.math.MathUtils.random(0f, Constants.WORLD_WIDTH);
            confY[i]   = com.badlogic.gdx.math.MathUtils.random(Constants.WORLD_HEIGHT * 0.7f, Constants.WORLD_HEIGHT + 50f);
            confVX[i]  = com.badlogic.gdx.math.MathUtils.random(-60f, 60f);
            confVY[i]  = com.badlogic.gdx.math.MathUtils.random(-200f, -80f);
            confR[i]   = com.badlogic.gdx.math.MathUtils.random(4f, 9f);
            confCol[i] = new Color(palette[com.badlogic.gdx.math.MathUtils.random(palette.length - 1)]);
        }
    }

    private void buildUI() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        float cx = Constants.WORLD_WIDTH * 0.5f;

        // PLAY AGAIN
        TextButton playAgainBtn = UiFactory.makeButton("PLAY AGAIN", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        playAgainBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        playAgainBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 190f);
        playAgainBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                Screen fresh = createSameModeScreen();
                game.setScreen(fresh);
            }
        });

        // LEADERBOARD
        TextButton lbBtn = UiFactory.makeButton("LEADERBOARD", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        lbBtn.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        lbBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 126f);
        lbBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        // MAIN MENU
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle,
                Constants.BTN_W_SECONDARY, Constants.BTN_H_SECONDARY);
        menuBtn.setColor(new Color(0.6f, 0.6f, 0.6f, 1f));
        menuBtn.setPosition(cx - Constants.BTN_W_SECONDARY * 0.5f, 66f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(playAgainBtn);
        stage.addActor(lbBtn);
        stage.addActor(menuBtn);
    }

    private Screen createSameModeScreen() {
        switch (gameMode) {
            case "Classic": return new ClassicGameScreen(game);
            case "Zen":     return new ZenGameScreen(game);
            case "Arcade":  return new ArcadeGameScreen(game);
            default:        return new ModeSelectScreen(game);
        }
    }

    @Override
    public void render(float delta) {
        countUpTimer += delta;
        celebTimer   += delta;

        // Score count-up over COUNT_UP_DURATION seconds
        if (!countUpDone) {
            float frac = com.badlogic.gdx.math.MathUtils.clamp(
                    countUpTimer / Constants.COUNT_UP_DURATION, 0f, 1f);
            displayedScore = (int)(finalScore * frac);
            if (frac >= 1f) { displayedScore = finalScore; countUpDone = true; }
        }

        // Confetti physics
        if (isNewBest) {
            for (int i = 0; i < NUM_CONFETTI; i++) {
                confX[i] += confVX[i] * delta;
                confY[i] += confVY[i] * delta;
                confVY[i] -= 120f * delta;
                if (confY[i] < -20f) {
                    // Respawn at top
                    confX[i]  = com.badlogic.gdx.math.MathUtils.random(0f, Constants.WORLD_WIDTH);
                    confY[i]  = Constants.WORLD_HEIGHT + 10f;
                    confVX[i] = com.badlogic.gdx.math.MathUtils.random(-60f, 60f);
                    confVY[i] = com.badlogic.gdx.math.MathUtils.random(-200f, -80f);
                }
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Confetti
        if (isNewBest) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < NUM_CONFETTI; i++) {
                sr.setColor(confCol[i]);
                sr.rect(confX[i], confY[i], confR[i], confR[i]);
            }
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        // Text
        game.batch.begin();
        drawText();
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void drawText() {
        float cx = Constants.WORLD_WIDTH * 0.5f;

        // MODE label
        game.fontSmall.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        game.fontSmall.draw(game.batch, gameMode.toUpperCase() + " MODE",
                cx - 60f, Constants.WORLD_HEIGHT - 55f);

        // GAME OVER / NICE
        String headline = isNewBest ? "NEW BEST!" : "GAME OVER";
        game.fontTitle.setColor(isNewBest
                ? Color.valueOf(Constants.COLOR_ACCENT)
                : Color.valueOf(Constants.COLOR_PRIMARY));
        game.fontTitle.draw(game.batch, headline, cx - 120f, Constants.WORLD_HEIGHT - 100f);

        // Score count-up
        game.fontScore.setColor(Color.WHITE);
        String scoreStr = String.valueOf(displayedScore);
        game.fontScore.draw(game.batch, scoreStr, cx - scoreStr.length() * 20f, Constants.WORLD_HEIGHT - 200f);

        // Stats
        float statsY = Constants.WORLD_HEIGHT - 300f;
        game.fontBody.setColor(new Color(0.85f, 0.85f, 0.85f, 1f));
        game.fontBody.draw(game.batch, "Sliced:  " + slicedCount, cx - 120f, statsY);
        game.fontBody.draw(game.batch, "Missed:  " + missedCount, cx - 120f, statsY - 36f);

        // Accuracy
        int total = slicedCount + missedCount;
        if (total > 0) {
            int acc = (int)(slicedCount * 100f / total);
            game.fontBody.draw(game.batch, "Accuracy: " + acc + "%", cx - 120f, statsY - 72f);
        }

        // Personal best
        game.fontSmall.setColor(new Color(0.7f, 0.7f, 0.7f, 1f));
        game.fontSmall.draw(game.batch, "Best: " + personalBest,
                cx - 50f, statsY - 110f);
        game.fontSmall.setColor(Color.WHITE);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    @Override public void show()   { registerInput(); }
    @Override public void hide()   { Gdx.input.setInputProcessor(null); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void dispose() { stage.dispose(); sr.dispose(); }
}
