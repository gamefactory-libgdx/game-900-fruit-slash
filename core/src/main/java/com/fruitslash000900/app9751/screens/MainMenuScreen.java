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

public class MainMenuScreen implements Screen {

    private static final String BG = "ui/main_menu.png";

    private final MainGame game;
    private final StretchViewport viewport;
    private final Stage stage;

    public MainMenuScreen(MainGame game) {
        this.game = game;
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        buildStage();
        setupInput();
        game.playMusic("sounds/music/music_menu.ogg");
    }

    private void buildStage() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // Title label — topY=200, h=80 → libgdxY = 854-200-80 = 574
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLbl = new Label("FRUIT SLASH", titleStyle);
        titleLbl.setPosition((Constants.WORLD_WIDTH - titleLbl.getPrefWidth()) / 2f, 574f);
        stage.addActor(titleLbl);

        // PLAY — topY=420, h=56 → libgdxY = 378
        TextButton playBtn = UiFactory.makePrimaryButton("PLAY", rectStyle);
        playBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 378f);
        playBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        playBtn.getLabel().setColor(Color.WHITE);
        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                boolean tutorialShown = game.prefs.getBoolean(Constants.PREF_TUTORIAL_SHOWN, false);
                if (!tutorialShown) {
                    game.setScreen(new TutorialScreen(game));
                } else {
                    game.setScreen(new ModeSelectScreen(game));
                }
            }
        });
        stage.addActor(playBtn);

        // LEADERBOARD — topY=500, h=56 → libgdxY = 298
        TextButton lbBtn = UiFactory.makePrimaryButton("LEADERBOARD", rectStyle);
        lbBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 298f);
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

        // SETTINGS — topY=580, h=56 → libgdxY = 218
        TextButton settingsBtn = UiFactory.makePrimaryButton("SETTINGS", rectStyle);
        settingsBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 218f);
        settingsBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        settingsBtn.getLabel().setColor(Color.WHITE);
        settingsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsBtn);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        }));
    }

    @Override
    public void show() {
        setupInput();
        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
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
