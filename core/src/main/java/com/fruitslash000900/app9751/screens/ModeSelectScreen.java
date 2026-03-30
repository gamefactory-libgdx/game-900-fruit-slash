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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.MainGame;
import com.fruitslash000900.app9751.UiFactory;

public class ModeSelectScreen implements Screen {

    private static final String BG = "ui/mode_select.png";

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private Texture bgTexture;

    public ModeSelectScreen(MainGame game) {
        this.game = game;
        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }
        bgTexture = game.manager.get(BG, Texture.class);

        buildUI();
        registerInput();
    }

    private void buildUI() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall, new Color(0.85f, 0.85f, 0.85f, 1f));
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);

        float cx = Constants.WORLD_WIDTH * 0.5f;

        // Title
        Label title = new Label("SELECT MODE", titleStyle);
        title.setPosition(cx - title.getPrefWidth() * 0.5f, 750f);
        stage.addActor(title);

        // ── Classic ───────────────────────────────────────────────────────────
        TextButton classicBtn = UiFactory.makeButton("CLASSIC", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        classicBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        classicBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 590f);
        classicBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new ClassicGameScreen(game));
            }
        });

        Label classicDesc = new Label("3 lives • Survive as long as possible", smallStyle);
        classicDesc.setPosition(cx - classicDesc.getPrefWidth() * 0.5f, 572f);

        // ── Zen ───────────────────────────────────────────────────────────────
        TextButton zenBtn = UiFactory.makeButton("ZEN", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        zenBtn.setColor(new Color(0.30f, 0.80f, 0.60f, 1f));
        zenBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 480f);
        zenBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new ZenGameScreen(game));
            }
        });

        Label zenDesc = new Label("90 seconds • No bombs • Relax & score", smallStyle);
        zenDesc.setPosition(cx - zenDesc.getPrefWidth() * 0.5f, 462f);

        // ── Arcade ────────────────────────────────────────────────────────────
        TextButton arcadeBtn = UiFactory.makeButton("ARCADE", rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        arcadeBtn.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        arcadeBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 370f);
        arcadeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new ArcadeGameScreen(game));
            }
        });

        Label arcadeDesc = new Label("60 seconds • Power-ups • High speed", smallStyle);
        arcadeDesc.setPosition(cx - arcadeDesc.getPrefWidth() * 0.5f, 352f);

        // ── Back ──────────────────────────────────────────────────────────────
        TextButton backBtn = UiFactory.makeButton("< BACK", rectStyle,
                Constants.BTN_W_SMALL, Constants.BTN_H_SMALL);
        backBtn.setColor(new Color(0.6f, 0.6f, 0.6f, 1f));
        backBtn.setPosition(12f, Constants.WORLD_HEIGHT - Constants.BTN_H_SMALL - 12f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Best scores per mode
        Label bestLabel = new Label("BEST SCORES", new Label.LabelStyle(game.fontSmall, Color.valueOf(Constants.COLOR_ACCENT)));
        bestLabel.setPosition(cx - bestLabel.getPrefWidth() * 0.5f, 290f);
        stage.addActor(bestLabel);

        int bestClassic = game.prefs.getInteger(Constants.PREF_HIGHSCORE_CLASSIC, 0);
        int bestZen     = game.prefs.getInteger(Constants.PREF_HIGHSCORE_ZEN,     0);
        int bestArcade  = game.prefs.getInteger(Constants.PREF_HIGHSCORE_ARCADE,  0);

        Label scores = new Label(
                "Classic: " + bestClassic + "   Zen: " + bestZen + "   Arcade: " + bestArcade,
                new Label.LabelStyle(game.fontSmall, new Color(0.85f, 0.85f, 0.85f, 1f)));
        scores.setPosition(cx - scores.getPrefWidth() * 0.5f, 262f);

        stage.addActor(title);
        stage.addActor(classicBtn);
        stage.addActor(classicDesc);
        stage.addActor(zenBtn);
        stage.addActor(zenDesc);
        stage.addActor(arcadeBtn);
        stage.addActor(arcadeDesc);
        stage.addActor(backBtn);
        stage.addActor(scores);
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void show()   { registerInput(); game.playMusic("sounds/music/music_menu.ogg"); }
    @Override public void hide()   { Gdx.input.setInputProcessor(null); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void dispose() { stage.dispose(); }
}
