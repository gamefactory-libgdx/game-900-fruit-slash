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

public class SettingsScreen implements Screen {

    private static final String BG = "ui/settings.png";

    private final MainGame game;
    private final StretchViewport viewport;
    private final Stage stage;

    private TextButton musicToggleBtn;
    private TextButton sfxToggleBtn;
    private TextButton hapticToggleBtn;

    public SettingsScreen(MainGame game) {
        this.game = game;
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        buildStage();
        setupInput();
    }

    private void buildStage() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);

        // SETTINGS title — topY=22, h=40 → libgdxY = 854-22-40 = 792
        Label titleLbl = new Label("SETTINGS", titleStyle);
        titleLbl.setPosition((Constants.WORLD_WIDTH - titleLbl.getPrefWidth()) / 2f, 792f);
        stage.addActor(titleLbl);

        // BACK button — topY=20, h=44, x=20 → libgdxY = 854-20-44 = 790
        TextButton backBtn = UiFactory.makeSmallButton("BACK", rectStyle);
        backBtn.setPosition(20f, 790f);
        backBtn.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        backBtn.getLabel().setColor(Color.WHITE);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);

        // MUSIC row — topY=180, h=56 → libgdxY=618; label slightly above centre
        Label musicLbl = new Label("MUSIC", bodyStyle);
        musicLbl.setPosition(60f, 636f);
        stage.addActor(musicLbl);

        musicToggleBtn = UiFactory.makeSecondaryButton(game.musicEnabled ? "ON" : "OFF", rectStyle);
        musicToggleBtn.setPosition(Constants.WORLD_WIDTH - 60f - Constants.BTN_W_SECONDARY, 618f);
        applyToggleColor(musicToggleBtn, game.musicEnabled);
        musicToggleBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.musicEnabled = !game.musicEnabled;
                game.prefs.putBoolean(Constants.PREF_MUSIC, game.musicEnabled);
                game.prefs.flush();
                if (game.currentMusic != null) {
                    if (game.musicEnabled) game.currentMusic.play();
                    else game.currentMusic.pause();
                }
                musicToggleBtn.setText(game.musicEnabled ? "ON" : "OFF");
                applyToggleColor(musicToggleBtn, game.musicEnabled);
                game.playSound("sounds/sfx/sfx_toggle.ogg");
            }
        });
        stage.addActor(musicToggleBtn);

        // SFX row — topY=248, h=56 → libgdxY=550; label slightly above centre
        Label sfxLbl = new Label("SFX", bodyStyle);
        sfxLbl.setPosition(60f, 568f);
        stage.addActor(sfxLbl);

        sfxToggleBtn = UiFactory.makeSecondaryButton(game.sfxEnabled ? "ON" : "OFF", rectStyle);
        sfxToggleBtn.setPosition(Constants.WORLD_WIDTH - 60f - Constants.BTN_W_SECONDARY, 550f);
        applyToggleColor(sfxToggleBtn, game.sfxEnabled);
        sfxToggleBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.sfxEnabled = !game.sfxEnabled;
                game.prefs.putBoolean(Constants.PREF_SFX, game.sfxEnabled);
                game.prefs.flush();
                sfxToggleBtn.setText(game.sfxEnabled ? "ON" : "OFF");
                applyToggleColor(sfxToggleBtn, game.sfxEnabled);
                // Only play toggle sound if SFX just turned ON
                if (game.sfxEnabled) game.playSound("sounds/sfx/sfx_toggle.ogg");
            }
        });
        stage.addActor(sfxToggleBtn);

        // HAPTIC row — topY=316, h=56 → libgdxY=482; label slightly above centre
        Label hapticLbl = new Label("HAPTIC", bodyStyle);
        hapticLbl.setPosition(60f, 500f);
        stage.addActor(hapticLbl);

        hapticToggleBtn = UiFactory.makeSecondaryButton(game.hapticEnabled ? "ON" : "OFF", rectStyle);
        hapticToggleBtn.setPosition(Constants.WORLD_WIDTH - 60f - Constants.BTN_W_SECONDARY, 482f);
        applyToggleColor(hapticToggleBtn, game.hapticEnabled);
        hapticToggleBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.hapticEnabled = !game.hapticEnabled;
                game.prefs.putBoolean(Constants.PREF_HAPTIC, game.hapticEnabled);
                game.prefs.flush();
                hapticToggleBtn.setText(game.hapticEnabled ? "ON" : "OFF");
                applyToggleColor(hapticToggleBtn, game.hapticEnabled);
                game.playSound("sounds/sfx/sfx_toggle.ogg");
            }
        });
        stage.addActor(hapticToggleBtn);

        // RESET SCORES — topY=440, h=52 → libgdxY = 854-440-52 = 362
        TextButton resetBtn = UiFactory.makeButton("RESET SCORES", rectStyle, Constants.BTN_W_PRIMARY, 52f);
        resetBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_W_PRIMARY) / 2f, 362f);
        resetBtn.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        resetBtn.getLabel().setColor(Color.WHITE);
        resetBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.prefs.remove(Constants.PREF_HIGHSCORE_CLASSIC);
                game.prefs.remove(Constants.PREF_HIGHSCORE_ZEN);
                game.prefs.remove(Constants.PREF_HIGHSCORE_ARCADE);
                game.prefs.remove(Constants.PREF_LEADERBOARD_CLASSIC);
                game.prefs.remove(Constants.PREF_LEADERBOARD_ZEN);
                game.prefs.remove(Constants.PREF_LEADERBOARD_ARCADE);
                game.prefs.remove(Constants.PREF_TOTAL_FRUITS_SLICED);
                game.prefs.remove(Constants.PREF_TOTAL_BOMBS_HIT);
                game.prefs.flush();
                game.playSound("sounds/sfx/sfx_button_click.ogg");
            }
        });
        stage.addActor(resetBtn);
    }

    private void applyToggleColor(TextButton btn, boolean isOn) {
        btn.setColor(isOn
                ? Color.valueOf(Constants.COLOR_PRIMARY)
                : new Color(0.35f, 0.35f, 0.35f, 1f));
        btn.getLabel().setColor(Color.WHITE);
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
