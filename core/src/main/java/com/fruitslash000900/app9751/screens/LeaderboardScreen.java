package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.MainGame;
import com.fruitslash000900.app9751.UiFactory;

public class LeaderboardScreen implements Screen {

    private static final String BG = "ui/leaderboard.png";

    // Tab indices
    private static final int TAB_CLASSIC = 0;
    private static final int TAB_ZEN     = 1;
    private static final int TAB_ARCADE  = 2;

    private static final String[] TAB_LABELS    = {"CLASSIC", "ZEN", "ARCADE"};
    private static final String[] TAB_PREF_KEYS = {
            Constants.PREF_LEADERBOARD_CLASSIC,
            Constants.PREF_LEADERBOARD_ZEN,
            Constants.PREF_LEADERBOARD_ARCADE
    };

    private final MainGame game;
    private final StretchViewport viewport;
    private final Stage stage;

    private int selectedTab = TAB_CLASSIC;
    private final TextButton[] tabBtns = new TextButton[3];
    private Group rowGroup;

    // ─────────────────────────────────────────────────────────────────────────
    // Static API — call from game screens to persist a score
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Add a Classic-mode score to the top-10 leaderboard.
     * Convenience overload — equivalent to {@code addScore(PREF_LEADERBOARD_CLASSIC, score)}.
     */
    public static void addScore(int score) {
        addScore(Constants.PREF_LEADERBOARD_CLASSIC, score);
    }

    /**
     * Add a score to the specified mode leaderboard (use {@code Constants.PREF_LEADERBOARD_*}).
     * Scores are stored as a comma-separated string, sorted descending, capped at 10 entries.
     */
    public static void addScore(String prefKey, int score) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int[] scores = loadScores(prefs, prefKey);

        // Insert the new score into sorted array
        int[] updated = new int[scores.length + 1];
        System.arraycopy(scores, 0, updated, 0, scores.length);
        updated[scores.length] = score;
        java.util.Arrays.sort(updated);

        // Reverse to descending order and keep top 10
        int keep = Math.min(updated.length, Constants.LEADERBOARD_MAX_ENTRIES);
        StringBuilder sb = new StringBuilder();
        for (int i = updated.length - 1; i >= updated.length - keep; i--) {
            if (sb.length() > 0) sb.append(',');
            sb.append(updated[i]);
        }
        prefs.putString(prefKey, sb.toString());
        prefs.flush();
    }

    /** Load scores from SharedPreferences as a sorted descending int array. */
    private static int[] loadScores(Preferences prefs, String prefKey) {
        String raw = prefs.getString(prefKey, "");
        if (raw.isEmpty()) return new int[0];
        String[] parts = raw.split(",");
        int[] scores = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try { scores[i] = Integer.parseInt(parts[i].trim()); }
            catch (NumberFormatException e) { scores[i] = 0; }
        }
        return scores;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Screen lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    public LeaderboardScreen(MainGame game) {
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
        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontSmall);
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);

        // LEADERBOARD title — topY=22, h=40 → libgdxY = 792
        Label titleLbl = new Label("LEADERBOARD", titleStyle);
        titleLbl.setPosition((Constants.WORLD_WIDTH - titleLbl.getPrefWidth()) / 2f, 792f);
        stage.addActor(titleLbl);

        // BACK button — topY=20, h=44, x=20 → libgdxY = 790
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

        // Tab buttons — topY=125, h=36 → libgdxY = 854-125-36 = 693
        // CLASSIC: x=20, w=130 | ZEN: x=centered=(480-130)/2=175 | ARCADE: x=480-20-130=330
        float[] tabX = {20f, 175f, 330f};
        for (int i = 0; i < 3; i++) {
            final int tabIdx = i;
            TextButton tabBtn = UiFactory.makeButton(TAB_LABELS[i], rectStyle, 130f, 36f);
            tabBtn.setPosition(tabX[i], 693f);
            applyTabColor(tabBtn, i == selectedTab);
            tabBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    selectTab(tabIdx);
                }
            });
            tabBtns[i] = tabBtn;
            stage.addActor(tabBtn);
        }

        // Score rows container — rebuilt when tab changes
        rowGroup = new Group();
        stage.addActor(rowGroup);
        refreshRows();
    }

    /** Switch the active tab and refresh the score list. */
    private void selectTab(int tabIdx) {
        selectedTab = tabIdx;
        for (int i = 0; i < tabBtns.length; i++) {
            applyTabColor(tabBtns[i], i == selectedTab);
        }
        refreshRows();
    }

    /** Rebuild the row labels from SharedPreferences for the current tab. */
    private void refreshRows() {
        rowGroup.clearChildren();

        Label.LabelStyle rowStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);

        int[] scores = loadScores(game.prefs, TAB_PREF_KEYS[selectedTab]);

        // Rows: topY=175, 10 rows × 36px each
        // Row i libgdxY = 854 - (175 + i*36) - 36 = 643 - i*36
        for (int i = 0; i < Constants.LEADERBOARD_MAX_ENTRIES; i++) {
            String rankText;
            if (i < scores.length && scores[i] > 0) {
                rankText = (i + 1) + ".  " + scores[i];
            } else {
                rankText = (i + 1) + ".  ---";
            }

            Label rowLbl = new Label(rankText, rowStyle);
            float rowY = 643f - i * 36f;
            rowLbl.setPosition(60f, rowY);
            rowGroup.addActor(rowLbl);
        }
    }

    private void applyTabColor(TextButton btn, boolean active) {
        btn.setColor(active
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
        refreshRows();
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
