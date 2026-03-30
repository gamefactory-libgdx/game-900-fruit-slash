package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
 * Shown when the player loses all lives in Classic mode.
 *
 * @param score        final score for this run
 * @param fruitsSliced number of fruits sliced this run (passed as `extra`)
 */
public class GameOverScreen implements Screen {

    // No generated game_over.png — use the slice_result background which matches this layout
    private static final String BG = "ui/slice_result.png";
    // Fallback in case slice_result is somehow unavailable
    private static final String BG_FALLBACK = "backgrounds/menu/1.png";

    private final MainGame game;
    private final int score;
    private final int fruitsSliced;
    private final StretchViewport viewport;
    private final Stage stage;

    public GameOverScreen(MainGame game, int score, int extra) {
        this.game = game;
        this.score = score;
        this.fruitsSliced = extra;

        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(viewport, game.batch);

        ensureBackgroundLoaded();
        buildStage();
        setupInput();
        game.playMusicOnce("sounds/music/music_game_over.ogg");
        game.playSound("sounds/sfx/sfx_game_over.ogg");
    }

    private void ensureBackgroundLoaded() {
        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }
    }

    private String getBg() {
        return game.manager.isLoaded(BG) ? BG : BG_FALLBACK;
    }

    private void buildStage() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle titleStyle  = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label.LabelStyle bodyStyle   = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle scoreStyle  = new Label.LabelStyle(game.fontScore, Color.valueOf(Constants.COLOR_ACCENT));

        // Personal best — max across all modes
        int bestClassic = game.prefs.getInteger(Constants.PREF_HIGHSCORE_CLASSIC, 0);
        int bestZen     = game.prefs.getInteger(Constants.PREF_HIGHSCORE_ZEN,     0);
        int bestArcade  = game.prefs.getInteger(Constants.PREF_HIGHSCORE_ARCADE,  0);
        int personalBest = Math.max(bestClassic, Math.max(bestZen, bestArcade));
        boolean isNewBest = score > personalBest;

        // RESULT header — topY=160, h=36 → libgdxY = 854-160-36 = 658
        Label resultLbl = new Label("GAME OVER", titleStyle);
        resultLbl.setPosition((Constants.WORLD_WIDTH - resultLbl.getPrefWidth()) / 2f, 658f);
        stage.addActor(resultLbl);

        // Score — topY=215, h=60 → libgdxY = 854-215-60 = 579
        Label scoreLbl = new Label(String.valueOf(score), scoreStyle);
        scoreLbl.setPosition((Constants.WORLD_WIDTH - scoreLbl.getPrefWidth()) / 2f, 579f);
        stage.addActor(scoreLbl);

        // Fruits sliced — topY=295, h=36 → libgdxY = 854-295-36 = 523
        Label fruitsLbl = new Label("FRUITS SLICED: " + fruitsSliced, bodyStyle);
        fruitsLbl.setPosition((Constants.WORLD_WIDTH - fruitsLbl.getPrefWidth()) / 2f, 523f);
        stage.addActor(fruitsLbl);

        // Personal best — topY=345, h=36 → libgdxY = 854-345-36 = 473
        String bestText = isNewBest ? "NEW BEST!" : "BEST: " + personalBest;
        Label bestLbl = new Label(bestText, bodyStyle);
        bestLbl.setPosition((Constants.WORLD_WIDTH - bestLbl.getPrefWidth()) / 2f, 473f);
        if (isNewBest) bestLbl.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        stage.addActor(bestLbl);

        // PLAY AGAIN — topY=470, h=52 → libgdxY = 854-470-52 = 332
        TextButton retryBtn = UiFactory.makeButton("PLAY AGAIN", rectStyle, Constants.BTN_W_PRIMARY, 52f);
        retryBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 332f);
        retryBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        retryBtn.getLabel().setColor(Color.WHITE);
        retryBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new ModeSelectScreen(game));
            }
        });
        stage.addActor(retryBtn);

        // MENU — topY=540, h=52 → libgdxY = 854-540-52 = 262
        TextButton menuBtn = UiFactory.makeButton("MENU", rectStyle, Constants.BTN_W_PRIMARY, 52f);
        menuBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 262f);
        menuBtn.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        menuBtn.getLabel().setColor(Color.WHITE);
        menuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);

        // LEADERBOARD — topY=610, h=52 → libgdxY = 854-610-52 = 192
        TextButton lbBtn = UiFactory.makeButton("LEADERBOARD", rectStyle, Constants.BTN_W_PRIMARY, 52f);
        lbBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 192f);
        lbBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        lbBtn.getLabel().setColor(Color.WHITE);
        lbBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new LeaderboardScreen(game));
            }
        });
        stage.addActor(lbBtn);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    @Override
    public void show() {
        setupInput();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        game.batch.draw(game.manager.get(getBg(), Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
