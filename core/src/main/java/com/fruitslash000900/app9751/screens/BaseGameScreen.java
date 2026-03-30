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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.fruitslash000900.app9751.BombObject;
import com.fruitslash000900.app9751.ComboDetailOverlay;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.FruitHalf;
import com.fruitslash000900.app9751.FruitObject;
import com.fruitslash000900.app9751.JuiceParticle;
import com.fruitslash000900.app9751.MainGame;
import com.fruitslash000900.app9751.SwipeTrail;
import com.fruitslash000900.app9751.UiFactory;

/**
 * Abstract base for all three game-mode screens.
 * Handles: spawning, physics, swipe detection, slice/bomb logic, rendering.
 * Subclasses implement mode-specific HUD, end conditions, and power-ups.
 */
public abstract class BaseGameScreen implements Screen {

    protected final MainGame game;

    // ── Rendering ─────────────────────────────────────────────────────────────
    protected OrthographicCamera camera;
    protected StretchViewport viewport;
    protected Stage stage;
    protected ShapeRenderer sr;
    protected Texture bgTexture;

    // ── Game objects ──────────────────────────────────────────────────────────
    protected final Array<FruitObject> fruits     = new Array<>();
    protected final Array<FruitHalf>   halves     = new Array<>();
    protected final Array<BombObject>  bombs      = new Array<>();
    protected final Array<JuiceParticle> particles = new Array<>();
    protected final SwipeTrail trail = new SwipeTrail();
    protected final ComboDetailOverlay comboOverlay = new ComboDetailOverlay();

    // ── Swipe state ───────────────────────────────────────────────────────────
    private final Vector2 lastWorld = new Vector2();
    private boolean touching = false;
    private int fruitsThisSwipe = 0;
    private int swipeBonusScore = 0;

    // ── Score / stats ─────────────────────────────────────────────────────────
    protected int score      = 0;
    protected int sliced     = 0;
    protected int missed     = 0;

    // ── Speed modifier (FREEZE / FRENZY power-ups) ────────────────────────────
    protected float speedMul = 1f;

    // ── Spawn ─────────────────────────────────────────────────────────────────
    protected float spawnTimer;
    protected float spawnInterval;

    // ── Bomb flash ────────────────────────────────────────────────────────────
    private float bombFlashTimer = 0f;

    // ── Miss indicator ────────────────────────────────────────────────────────
    private float missIndicatorTimer = 0f;

    // ─────────────────────────────────────────────────────────────────────────

    public BaseGameScreen(MainGame game) {
        this.game = game;
        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        spawnInterval = getInitialSpawnInterval();
        spawnTimer    = spawnInterval * 0.5f; // first fruit quickly

        loadBackground();
        addPauseButton();
        registerInput();
    }

    // ── Subclass contract ─────────────────────────────────────────────────────

    protected abstract float getInitialSpawnInterval();
    protected abstract boolean shouldSpawnBombs();
    protected abstract void   handleBombSliced();
    protected abstract void   handleFruitMissed();
    protected abstract boolean isGameOver();
    protected abstract void   updateMode(float delta);
    protected abstract void   drawHUD();
    protected abstract String getHighScorePrefKey();
    protected abstract String getLeaderboardPrefKey();
    protected abstract String getGameMode();
    /** Path of the generated UI background PNG for this mode. */
    protected abstract String getBgPath();

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void loadBackground() {
        String path = getBgPath();
        if (!game.manager.isLoaded(path)) {
            game.manager.load(path, Texture.class);
            game.manager.finishLoading();
        }
        bgTexture = game.manager.get(path, Texture.class);
    }

    private void addPauseButton() {
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontSmall);
        TextButton pauseBtn = UiFactory.makeRoundButton("II", roundStyle);
        pauseBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
        pauseBtn.setPosition(Constants.WORLD_WIDTH - 66f, Constants.WORLD_HEIGHT - 70f);
        pauseBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new PauseScreen(game, BaseGameScreen.this));
            }
        });
        stage.addActor(pauseBtn);
    }

    protected void registerInput() {
        InputAdapter swipeInput = new InputAdapter() {
            @Override public boolean touchDown(int sx, int sy, int pointer, int button) {
                if (pointer != 0) return false;
                Vector3 wp = new Vector3(sx, sy, 0);
                viewport.unproject(wp);
                trail.begin(wp.x, wp.y);
                lastWorld.set(wp.x, wp.y);
                touching = true;
                fruitsThisSwipe = 0;
                swipeBonusScore = 0;
                return false;
            }
            @Override public boolean touchDragged(int sx, int sy, int pointer) {
                if (pointer != 0 || !touching) return false;
                Vector3 wp = new Vector3(sx, sy, 0);
                viewport.unproject(wp);
                checkSlice(lastWorld.x, lastWorld.y, wp.x, wp.y);
                trail.addPoint(wp.x, wp.y);
                lastWorld.set(wp.x, wp.y);
                return false;
            }
            @Override public boolean touchUp(int sx, int sy, int pointer, int button) {
                if (pointer != 0) return false;
                touching = false;
                trail.end();
                // Add combo bonus if >=2 fruits sliced this swipe
                if (fruitsThisSwipe >= 2) {
                    int bonus = swipeBonusScore * (Math.min(fruitsThisSwipe, Constants.COMBO_MAX_MULTIPLIER) - 1);
                    score += bonus;
                }
                if (fruitsThisSwipe >= Constants.COMBO_OVERLAY_THRESHOLD) {
                    comboOverlay.show(fruitsThisSwipe);
                }
                fruitsThisSwipe = 0;
                swipeBonusScore = 0;
                return false;
            }
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(new InputMultiplexer(swipeInput, stage));
    }

    // ── Core update ───────────────────────────────────────────────────────────

    protected void update(float delta) {
        trail.update(delta);
        comboOverlay.update(delta);

        if (bombFlashTimer > 0f) bombFlashTimer -= delta;
        if (missIndicatorTimer > 0f) missIndicatorTimer -= delta;

        // Update fruits
        for (int i = fruits.size - 1; i >= 0; i--) {
            FruitObject f = fruits.get(i);
            f.update(delta);
            if (f.isOffScreen()) {
                if (!f.sliced) { handleFruitMissed(); missed++; missIndicatorTimer = Constants.MISS_INDICATOR_DURATION; }
                fruits.removeIndex(i);
            }
        }

        // Update halves
        for (int i = halves.size - 1; i >= 0; i--) {
            FruitHalf h = halves.get(i);
            h.update(delta);
            if (!h.active) halves.removeIndex(i);
        }

        // Update bombs
        for (int i = bombs.size - 1; i >= 0; i--) {
            BombObject b = bombs.get(i);
            b.update(delta);
            if (b.isOffScreen()) bombs.removeIndex(i);
        }

        // Update particles
        for (int i = particles.size - 1; i >= 0; i--) {
            JuiceParticle p = particles.get(i);
            p.update(delta);
            if (!p.active) particles.removeIndex(i);
        }

        // Spawn
        spawnTimer -= delta;
        if (spawnTimer <= 0f) {
            spawnTimer = spawnInterval;
            if (shouldSpawnBombs() && score >= Constants.BOMB_START_SCORE && MathUtils.randomBoolean(0.22f)) {
                spawnBomb();
            } else {
                spawnFruit();
            }
        }

        updateMode(delta);

        if (isGameOver()) endGame();
    }

    // ── Slice detection ───────────────────────────────────────────────────────

    private void checkSlice(float x1, float y1, float x2, float y2) {
        for (FruitObject f : fruits) {
            if (f.active && !f.sliced && f.intersectsSegment(x1, y1, x2, y2)) {
                sliceFruit(f, x1, y1, x2, y2);
            }
        }
        for (BombObject b : bombs) {
            if (b.active && !b.exploded && b.intersectsSegment(x1, y1, x2, y2)) {
                sliceBomb(b);
            }
        }
        onSliceSegment(x1, y1, x2, y2);
    }

    /** Hook: subclasses check additional objects (e.g. power-ups) against the swipe segment. */
    protected void onSliceSegment(float x1, float y1, float x2, float y2) {}

    protected void sliceFruit(FruitObject f, float sx1, float sy1, float sx2, float sy2) {
        f.sliced = true;
        f.active = false;

        // Compute swipe direction for half spread
        float dx = sx2 - sx1, dy = sy2 - sy1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0.001f) { dx /= len; dy /= len; }

        float spread = Constants.HALF_SPREAD_SPEED + MathUtils.random(40f);
        float inheritX = f.vx * 0.4f;
        float inheritY = f.vy * 0.4f;
        halves.add(new FruitHalf(f.x, f.y, -dy * spread + inheritX,  dx * spread + inheritY, f.type, true));
        halves.add(new FruitHalf(f.x, f.y,  dy * spread + inheritX, -dx * spread + inheritY, f.type, false));

        // Juice particles
        for (int i = 0; i < Constants.JUICE_PARTICLE_COUNT; i++) {
            particles.add(new JuiceParticle(f.x, f.y, f.type.getColor()));
        }

        int pts = f.type.getScore() * getScoreMultiplier();
        score += pts;
        sliced++;
        fruitsThisSwipe++;
        swipeBonusScore += pts;

        onScoreChanged();
        game.playSound("sounds/sfx/sfx_collect.ogg");
        game.vibrate(25);
    }

    protected void sliceBomb(BombObject b) {
        b.exploded = true;
        b.active = false;
        bombFlashTimer = Constants.BOMB_FLASH_DURATION;
        // Dark explosion particles
        for (int i = 0; i < 12; i++) {
            particles.add(new JuiceParticle(b.x, b.y, new Color(0.25f, 0.25f, 0.25f, 1f)));
        }
        game.playSound("sounds/sfx/sfx_hit.ogg");
        game.vibrate(80);
        handleBombSliced();
    }

    /** Multiplier for points (overridden by Arcade's double-points power-up). */
    protected int getScoreMultiplier() { return 1; }

    /** Called whenever score changes — subclasses adjust difficulty here. */
    protected void onScoreChanged() {}

    // ── Spawn helpers ─────────────────────────────────────────────────────────

    protected void spawnFruit() {
        float x     = MathUtils.random(50f, Constants.WORLD_WIDTH - 50f);
        float angle = MathUtils.random(Constants.FRUIT_ANGLE_MIN, Constants.FRUIT_ANGLE_MAX)
                      * MathUtils.degreesToRadians;
        float speed = MathUtils.random(Constants.FRUIT_SPEED_MIN, Constants.FRUIT_SPEED_MAX) * speedMul;
        fruits.add(new FruitObject(x, -60f, MathUtils.cos(angle) * speed,
                MathUtils.sin(angle) * speed, FruitObject.FruitType.random()));
    }

    protected void spawnBomb() {
        float x     = MathUtils.random(50f, Constants.WORLD_WIDTH - 50f);
        float angle = MathUtils.random(65f, 115f) * MathUtils.degreesToRadians;
        float speed = MathUtils.random(Constants.FRUIT_SPEED_MIN, Constants.FRUIT_SPEED_MAX - 50f);
        bombs.add(new BombObject(x, -60f, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed));
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        // Stage always runs — pause button must fire even when subclass might pause time
        stage.act(delta);

        update(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Bomb flash overlay
        if (bombFlashTimer > 0f) {
            float alpha = (bombFlashTimer / Constants.BOMB_FLASH_DURATION) * Constants.BOMB_FLASH_ALPHA;
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(1f, 0.1f, 0f, alpha);
            sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        // Miss indicator — red top flash
        if (missIndicatorTimer > 0f) {
            float alpha = (missIndicatorTimer / Constants.MISS_INDICATOR_DURATION) * 0.4f;
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0.9f, 0f, 0f, alpha);
            sr.rect(0, Constants.WORLD_HEIGHT - 60f, Constants.WORLD_WIDTH, 60f);
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);

        // Particles
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (JuiceParticle p : particles) {
            if (p.active) {
                sr.setColor(p.color.r, p.color.g, p.color.b, p.alpha);
                sr.circle(p.x, p.y, p.radius, 8);
            }
        }
        sr.end();

        // Fruit halves
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (FruitHalf h : halves) {
            if (!h.active) continue;
            Color c = h.type.getColor();
            sr.setColor(c.r, c.g, c.b, h.alpha);
            // Draw as filled arc (half circle)
            float startAngle = h.isLeft ? 180f : 0f;
            sr.arc(h.x, h.y, h.radius, startAngle + h.rotation, 180f, 16);
            // Inner flesh
            Color ci = h.type.getInnerColor();
            sr.setColor(ci.r, ci.g, ci.b, h.alpha * 0.7f);
            sr.arc(h.x, h.y, h.radius * 0.65f, startAngle + h.rotation, 180f, 12);
        }
        sr.end();

        // Fruits
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (FruitObject f : fruits) {
            if (!f.active) continue;
            Color c = f.type.getColor();
            // Outer
            sr.setColor(c.r, c.g, c.b, 1f);
            sr.circle(f.x, f.y, f.radius, 20);
            // Inner highlight
            Color ci = f.type.getInnerColor();
            sr.setColor(ci.r, ci.g, ci.b, 0.55f);
            sr.circle(f.x + f.radius * 0.2f, f.y + f.radius * 0.2f, f.radius * 0.42f, 14);
            // Shine
            sr.setColor(1f, 1f, 1f, 0.35f);
            sr.circle(f.x + f.radius * 0.35f, f.y + f.radius * 0.35f, f.radius * 0.18f, 8);
        }
        sr.end();

        // Bombs
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (BombObject b : bombs) {
            if (!b.active) continue;
            sr.setColor(0.12f, 0.12f, 0.12f, 1f);
            sr.circle(b.x, b.y, b.radius, 18);
            // Skull-ish highlight
            sr.setColor(0.3f, 0.3f, 0.3f, 0.5f);
            sr.circle(b.x + b.radius * 0.2f, b.y + b.radius * 0.25f, b.radius * 0.28f, 10);
            // Fuse
            sr.setColor(0.75f, 0.55f, 0.1f, 1f);
            sr.rectLine(b.x - 2f, b.y + b.radius, b.x + 8f, b.y + b.radius + 14f, 3f);
            // Fuse spark
            float spark = (MathUtils.sinDeg(System.currentTimeMillis() / 5f % 360f) + 1f) * 0.5f;
            sr.setColor(1f, spark, 0f, 1f);
            sr.circle(b.x + 8f, b.y + b.radius + 16f, 5f, 8);
        }
        sr.end();

        // Swipe trail
        sr.begin(ShapeRenderer.ShapeType.Filled);
        trail.render(sr);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // HUD
        game.batch.setColor(Color.WHITE);
        game.batch.begin();
        drawHUD();
        game.batch.end();

        // Combo overlay (manages its own batch begin/end and sr begin/end)
        if (comboOverlay.isVisible()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            comboOverlay.render(game.batch, sr, game.fontBody, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        // Stage (buttons)
        stage.draw();
    }

    // ── End game ──────────────────────────────────────────────────────────────

    protected void endGame() {
        // Save high score
        int prev = game.prefs.getInteger(getHighScorePrefKey(), 0);
        if (score > prev) {
            game.prefs.putInteger(getHighScorePrefKey(), score);
        }
        // Update leaderboard (top 10 as comma-separated)
        updateLeaderboard();
        game.prefs.flush();

        game.playMusicOnce("sounds/music/music_game_over.ogg");
        game.setScreen(new SliceResultScreen(game, score, sliced, missed, getGameMode(),
                getHighScorePrefKey(), getLeaderboardPrefKey()));
    }

    private void updateLeaderboard() {
        String key  = getLeaderboardPrefKey();
        String raw  = game.prefs.getString(key, "");
        Array<Integer> entries = new Array<>();
        if (!raw.isEmpty()) {
            for (String s : raw.split(",")) {
                try { entries.add(Integer.parseInt(s.trim())); } catch (NumberFormatException ignored) {}
            }
        }
        entries.add(score);
        // Sort descending
        entries.sort((a, b) -> b - a);
        // Keep top 10
        while (entries.size > Constants.LEADERBOARD_MAX_ENTRIES) entries.removeIndex(entries.size - 1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.size; i++) {
            if (i > 0) sb.append(",");
            sb.append(entries.get(i));
        }
        game.prefs.putString(key, sb.toString());
    }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override public void show()   { registerInput(); game.playMusic("sounds/music/music_gameplay.ogg"); }
    @Override public void hide()   { Gdx.input.setInputProcessor(null); }
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
