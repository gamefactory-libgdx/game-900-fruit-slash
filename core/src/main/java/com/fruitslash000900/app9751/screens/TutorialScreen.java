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
import com.badlogic.gdx.math.MathUtils;
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
 * 3-panel swipeable tutorial. Shown on first launch (PREF_TUTORIAL_SHOWN = false).
 * Marks the flag on completion so it never shows again unless reset.
 */
public class TutorialScreen implements Screen {

    private static final String BG = "ui/tutorial.png";

    // Tutorial panel content
    private static final String[] TITLES = {
        "SWIPE TO SLASH",
        "AVOID BOMBS",
        "COMBO SLICES"
    };
    private static final String[] DESCS = {
        "Drag your finger across\nfruits to slice them.\nThe faster the swipe,\nthe more satisfying!",
        "Dark bombs appear as you\nscore higher. Slicing one\ncosts you a life in Classic\nor time in Arcade.",
        "Slice 2+ fruits in a\nsingle swipe for a combo!\nChain 3+ for a bonus\nmultiplier overlay."
    };
    private static final Color[] PANEL_COLORS = {
        new Color(0.91f, 0.12f, 0.39f, 0.15f), // pink-ish
        new Color(0.14f, 0.14f, 0.14f, 0.18f), // dark
        new Color(1.00f, 0.72f, 0.05f, 0.15f)  // gold
    };

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer sr;
    private Texture bgTexture;

    private int currentPanel = 0;
    private final int totalPanels = TITLES.length;

    // Fruit animation for each panel
    private float animTime = 0f;

    public TutorialScreen(MainGame game) {
        this.game = game;
        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }
        bgTexture = game.manager.get(BG, Texture.class);

        buildUI();
        registerInput();
    }

    private void buildUI() {
        buildPanelButtons();
    }

    private void buildPanelButtons() {
        stage.clear();

        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        float cx = Constants.WORLD_WIDTH * 0.5f;

        // Next / Done button
        boolean isLast = (currentPanel == totalPanels - 1);
        String btnLabel = isLast ? "START SLICING!" : "NEXT >";
        TextButton nextBtn = UiFactory.makeButton(btnLabel, rectStyle,
                Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
        nextBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        nextBtn.setPosition(cx - Constants.BTN_W_PRIMARY * 0.5f, 110f);
        nextBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                if (currentPanel < totalPanels - 1) {
                    currentPanel++;
                    animTime = 0f;
                    buildPanelButtons();
                } else {
                    completeTutorial();
                }
            }
        });

        // Skip button (always visible)
        TextButton skipBtn = UiFactory.makeButton("SKIP", rectStyle,
                Constants.BTN_W_SMALL, Constants.BTN_H_SMALL);
        skipBtn.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
        skipBtn.setPosition(cx - Constants.BTN_W_SMALL * 0.5f, 54f);
        skipBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                completeTutorial();
            }
        });

        // Back (if not first panel)
        if (currentPanel > 0) {
            TextButton backBtn = UiFactory.makeButton("< BACK", rectStyle,
                    Constants.BTN_W_SMALL, Constants.BTN_H_SMALL);
            backBtn.setColor(new Color(0.55f, 0.55f, 0.55f, 1f));
            backBtn.setPosition(12f, 110f);
            backBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    currentPanel--;
                    animTime = 0f;
                    buildPanelButtons();
                }
            });
            stage.addActor(backBtn);
        }

        stage.addActor(nextBtn);
        stage.addActor(skipBtn);
    }

    private void completeTutorial() {
        game.prefs.putBoolean(Constants.PREF_TUTORIAL_SHOWN, true);
        game.prefs.flush();
        game.setScreen(new ModeSelectScreen(game));
    }

    @Override
    public void render(float delta) {
        animTime += delta;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Panel card
        drawPanelCard();

        // Panel illustration (animated fruit for panel 0, bomb for 1, multiple fruits for 2)
        drawIllustration();

        // Text content
        game.batch.begin();
        drawPanelText();
        game.batch.end();

        // Dots indicator
        drawDots();

        stage.act(delta);
        stage.draw();
    }

    private void drawPanelCard() {
        float panelW = 420f, panelH = 560f;
        float panelX = (Constants.WORLD_WIDTH - panelW) * 0.5f;
        float panelY = (Constants.WORLD_HEIGHT - panelH) * 0.5f + 30f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        Color pc = PANEL_COLORS[currentPanel];
        sr.setColor(0.08f + pc.r, 0.04f + pc.g, 0.02f + pc.b, 0.88f);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.valueOf(Constants.COLOR_ACCENT));
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawIllustration() {
        float cx = Constants.WORLD_WIDTH * 0.5f;
        float cy = 600f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        switch (currentPanel) {
            case 0: { // Single fruit with swipe arc
                float bob = MathUtils.sin(animTime * 2.5f) * 10f;
                // Fruit
                sr.setColor(0.98f, 0.20f, 0.14f, 1f);
                sr.circle(cx, cy + bob, 40f, 20);
                sr.setColor(1f, 0.75f, 0.70f, 0.55f);
                sr.circle(cx + 12f, cy + bob + 14f, 14f, 12);
                // Swipe trail suggestion
                float trailAlpha = (MathUtils.sin(animTime * 3f) + 1f) * 0.5f;
                for (int i = 0; i < 8; i++) {
                    float t = (float) i / 8f;
                    float tx = cx - 80f + t * 160f;
                    float ty = cy + bob + MathUtils.sin(t * MathUtils.PI) * 20f;
                    sr.setColor(1f, 1f, 1f, t * trailAlpha * 0.7f);
                    sr.circle(tx, ty, 3f + t * 5f, 8);
                }
                break;
            }
            case 1: { // Bomb (dark) vs fruit (red)
                float bob = MathUtils.sin(animTime * 2f) * 8f;
                // Bomb
                sr.setColor(0.12f, 0.12f, 0.12f, 1f);
                sr.circle(cx - 50f, cy + bob, 36f, 18);
                sr.setColor(0.75f, 0.55f, 0.1f, 1f);
                sr.rectLine(cx - 52f, cy + bob + 36f, cx - 42f, cy + bob + 50f, 3f);
                sr.setColor(1f, 0.85f, 0f, 1f);
                sr.circle(cx - 42f, cy + bob + 52f, 5f, 8);
                // Fruit (safe)
                sr.setColor(0.10f, 0.62f, 0.25f, 1f);
                sr.circle(cx + 50f, cy + bob, 36f, 18);
                sr.setColor(0.80f, 1.00f, 0.50f, 0.5f);
                sr.circle(cx + 62f, cy + bob + 12f, 12f, 10);
                // X over bomb
                sr.setColor(1f, 0.2f, 0.1f, 0.85f);
                sr.rectLine(cx - 70f, cy + bob + 56f, cx - 28f, cy + bob + 18f, 4f);
                sr.rectLine(cx - 28f, cy + bob + 56f, cx - 70f, cy + bob + 18f, 4f);
                break;
            }
            case 2: { // 3 fruits in a line with score multiplier
                for (int i = 0; i < 3; i++) {
                    float bob = MathUtils.sin(animTime * 2f + i * 1.2f) * 8f;
                    float fx = cx - 80f + i * 80f;
                    Color[] colors = {
                        new Color(0.98f, 0.20f, 0.14f, 1f),
                        new Color(1.00f, 0.87f, 0.10f, 1f),
                        new Color(0.10f, 0.62f, 0.25f, 1f)
                    };
                    sr.setColor(colors[i]);
                    sr.circle(fx, cy + bob, 32f, 18);
                }
                // "3x COMBO" badge
                float badgePulse = (MathUtils.sin(animTime * 4f) + 1f) * 0.5f;
                sr.setColor(1f, 0.87f * (0.7f + badgePulse * 0.3f), 0f, 0.9f);
                sr.rect(cx - 56f, cy - 76f, 112f, 34f);
                break;
            }
        }

        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // "3x COMBO!" text for panel 2
        if (currentPanel == 2) {
            game.batch.begin();
            game.fontSmall.setColor(Color.valueOf(Constants.COLOR_BG));
            game.fontSmall.draw(game.batch, "3x COMBO!", cx - 46f, cy - 46f);
            game.fontSmall.setColor(Color.WHITE);
            game.batch.end();
        }
    }

    private void drawPanelText() {
        float cx = Constants.WORLD_WIDTH * 0.5f;

        // Title
        game.fontTitle.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        String title = TITLES[currentPanel];
        game.fontTitle.draw(game.batch, title, cx - 180f, 490f);

        // Description (multiline via newlines)
        game.fontBody.setColor(Color.WHITE);
        String[] lines = DESCS[currentPanel].split("\n");
        float lineH = 34f;
        float startY = 440f;
        for (int i = 0; i < lines.length; i++) {
            game.fontBody.draw(game.batch, lines[i], cx - 180f, startY - i * lineH);
        }
    }

    private void drawDots() {
        float dotR = 7f, gap = 20f;
        float totalW = totalPanels * dotR * 2 + (totalPanels - 1) * gap;
        float startX = (Constants.WORLD_WIDTH - totalW) * 0.5f;
        float dotY   = 170f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < totalPanels; i++) {
            float cx = startX + i * (dotR * 2 + gap) + dotR;
            if (i == currentPanel) {
                sr.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
                sr.circle(cx, dotY, dotR, 12);
            } else {
                sr.setColor(0.45f, 0.45f, 0.45f, 1f);
                sr.circle(cx, dotY, dotR * 0.65f, 10);
            }
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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

    @Override public void show()   { registerInput(); game.playMusic("sounds/music/music_menu.ogg"); }
    @Override public void hide()   { Gdx.input.setInputProcessor(null); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void dispose() { stage.dispose(); sr.dispose(); }
}
