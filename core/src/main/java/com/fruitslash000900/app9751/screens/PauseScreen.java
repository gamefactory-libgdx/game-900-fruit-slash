package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
 * Pause overlay drawn over the game screen.
 * Resumes the same game instance (no state loss).
 */
public class PauseScreen implements Screen {

    private final MainGame game;
    private final Screen previousScreen;

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer sr;

    public PauseScreen(MainGame game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        buildUI();
        registerInput();
    }

    private void buildUI() {
        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager,  game.fontBody);
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontBody);

        float cx = Constants.WORLD_WIDTH * 0.5f;

        // RESUME
        TextButton resumeBtn = UiFactory.makeButton("RESUME", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        resumeBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        resumeBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 480f);
        resumeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(previousScreen);
            }
        });

        // RESTART — new instance of the same mode
        TextButton restartBtn = UiFactory.makeButton("RESTART", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        restartBtn.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        restartBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 400f);
        restartBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                Screen fresh = createFreshScreen();
                game.setScreen(fresh);
            }
        });

        // MAIN MENU
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle,
                Constants.BTN_W_SECONDARY, Constants.BTN_H_SECONDARY);
        menuBtn.setColor(new Color(0.6f, 0.6f, 0.6f, 1f));
        menuBtn.setPosition(cx - Constants.BTN_W_SECONDARY * 0.5f, 326f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Title label
        Label.LabelStyle ls = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label title = new Label("PAUSED", ls);
        title.setPosition(cx - title.getPrefWidth() * 0.5f, 570f);

        stage.addActor(title);
        stage.addActor(resumeBtn);
        stage.addActor(restartBtn);
        stage.addActor(menuBtn);
    }

    private Screen createFreshScreen() {
        if (previousScreen instanceof ClassicGameScreen) return new ClassicGameScreen(game);
        if (previousScreen instanceof ZenGameScreen)     return new ZenGameScreen(game);
        if (previousScreen instanceof ArcadeGameScreen)  return new ArcadeGameScreen(game);
        // Fallback
        return new ModeSelectScreen(game);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(previousScreen);
                    return true;
                }
                return false;
            }
        }));
    }

    @Override
    public void render(float delta) {
        // Draw the paused game screen first (frozen)
        previousScreen.render(0f); // delta=0 → no updates

        // Dark overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.62f);
        sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        sr.end();

        // Panel
        float panelW = 320f, panelH = 340f;
        float panelX = (Constants.WORLD_WIDTH  - panelW) * 0.5f;
        float panelY = (Constants.WORLD_HEIGHT - panelH) * 0.5f + 20f;
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.12f, 0.07f, 0.04f, 0.94f);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);
        stage.draw();
    }

    @Override public void show()   { registerInput(); }
    @Override public void hide()   { Gdx.input.setInputProcessor(null); }
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }

    @Override public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
