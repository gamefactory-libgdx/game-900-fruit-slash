package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.FruitObject;
import com.fruitslash000900.app9751.MainGame;
import com.fruitslash000900.app9751.PowerUpObject;

/**
 * Arcade mode: 60-second countdown, power-ups, starts with 3 lives (max 5 via Extra Life).
 * Slice a bomb  → lose 5 seconds.
 * Miss fruits   → no life penalty.
 * Power-ups spawn every 20 seconds — slice them to activate.
 */
public class ArcadeGameScreen extends BaseGameScreen {

    private float timeLeft;
    private float powerUpSpawnTimer;
    private float rampTimer;
    private int   lives;

    // In-flight power-ups (sliceable objects)
    private final Array<PowerUpObject> powerUpsInFlight = new Array<>();

    // Active power-up effect
    private PowerUpObject.PowerUpType activePowerUp    = null;
    private float                     activePowerUpTimer = 0f;
    private boolean                   doublePoints     = false;

    private Texture starTex;
    private Texture starOutline;

    public ArcadeGameScreen(MainGame game) {
        super(game);
        timeLeft          = Constants.ARCADE_DURATION;
        powerUpSpawnTimer = Constants.POWERUP_SPAWN_INTERVAL;
        rampTimer         = Constants.ARCADE_RAMP_INTERVAL;
        lives             = 3;
        starTex     = game.manager.get("ui/buttons/star.png",         Texture.class);
        starOutline = game.manager.get("ui/buttons/star_outline.png", Texture.class);
    }

    // ── Mode contract ─────────────────────────────────────────────────────────

    @Override protected float   getInitialSpawnInterval() { return Constants.ARCADE_SPAWN_START; }
    @Override protected boolean shouldSpawnBombs()        { return true; }
    @Override protected String  getBgPath()               { return "ui/arcade_game.png"; }
    @Override protected String  getHighScorePrefKey()     { return Constants.PREF_HIGHSCORE_ARCADE; }
    @Override protected String  getLeaderboardPrefKey()   { return Constants.PREF_LEADERBOARD_ARCADE; }
    @Override protected String  getGameMode()             { return "Arcade"; }
    @Override protected int     getScoreMultiplier()      { return doublePoints ? 2 : 1; }

    @Override
    protected void handleBombSliced() {
        timeLeft = Math.max(0f, timeLeft - 5f);
    }

    @Override protected void handleFruitMissed() { /* no penalty in Arcade */ }

    @Override protected boolean isGameOver() { return timeLeft <= 0f; }

    // ── Swipe hook for power-up slicing ──────────────────────────────────────

    @Override
    protected void onSliceSegment(float x1, float y1, float x2, float y2) {
        for (int i = powerUpsInFlight.size - 1; i >= 0; i--) {
            PowerUpObject p = powerUpsInFlight.get(i);
            if (p.active && p.intersectsSegment(x1, y1, x2, y2)) {
                activatePowerUp(p);
                powerUpsInFlight.removeIndex(i);
            }
        }
    }

    // ── Mode update ───────────────────────────────────────────────────────────

    @Override
    protected void updateMode(float delta) {
        timeLeft -= delta;
        if (timeLeft < 0f) timeLeft = 0f;

        // Difficulty ramp
        rampTimer -= delta;
        if (rampTimer <= 0f) {
            rampTimer = Constants.ARCADE_RAMP_INTERVAL;
            spawnInterval = Math.max(Constants.ARCADE_SPAWN_MIN,
                    spawnInterval - Constants.ARCADE_RAMP_STEP);
        }

        // Power-up spawn
        powerUpSpawnTimer -= delta;
        if (powerUpSpawnTimer <= 0f) {
            powerUpSpawnTimer = Constants.POWERUP_SPAWN_INTERVAL;
            spawnPowerUp();
        }

        // Active power-up countdown
        if (activePowerUp != null) {
            activePowerUpTimer -= delta;
            if (activePowerUpTimer <= 0f) deactivatePowerUp();
        }

        // Update in-flight power-ups
        for (int i = powerUpsInFlight.size - 1; i >= 0; i--) {
            PowerUpObject p = powerUpsInFlight.get(i);
            p.update(delta);
            if (p.isOffScreen()) powerUpsInFlight.removeIndex(i);
        }
    }

    // ── Power-up management ───────────────────────────────────────────────────

    private void spawnPowerUp() {
        float x     = MathUtils.random(60f, Constants.WORLD_WIDTH - 60f);
        float angle = MathUtils.random(65f, 115f) * MathUtils.degreesToRadians;
        float speed = MathUtils.random(Constants.FRUIT_SPEED_MIN, Constants.FRUIT_SPEED_MAX);
        powerUpsInFlight.add(new PowerUpObject(x, -60f,
                MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed,
                PowerUpObject.PowerUpType.random()));
    }

    private void activatePowerUp(PowerUpObject p) {
        p.active = false;
        deactivatePowerUp();
        activePowerUp = p.type;
        game.playSound("sounds/sfx/sfx_power_up.ogg");
        game.vibrate(40);

        switch (p.type) {
            case FRENZY:
                activePowerUpTimer = Constants.FRENZY_DURATION;
                timeLeft += Constants.FRENZY_TIME_BONUS;
                speedMul = Constants.FRENZY_SPEED_MUL;
                spawnInterval = Math.max(spawnInterval * 0.5f, Constants.ARCADE_SPAWN_MIN);
                break;
            case FREEZE:
                activePowerUpTimer = Constants.FREEZE_DURATION;
                for (FruitObject f : fruits) {
                    f.vx *= Constants.FREEZE_SPEED_MUL;
                    f.vy *= Constants.FREEZE_SPEED_MUL;
                }
                speedMul = Constants.FREEZE_SPEED_MUL;
                break;
            case DOUBLE_POINTS:
                activePowerUpTimer = Constants.DOUBLE_POINTS_DURATION;
                doublePoints = true;
                break;
            case EXTRA_LIFE:
                activePowerUpTimer = 0.01f;
                lives = Math.min(lives + 1, Constants.ARCADE_MAX_LIVES);
                break;
        }
    }

    private void deactivatePowerUp() {
        if (activePowerUp == null) return;
        switch (activePowerUp) {
            case FRENZY:
            case FREEZE:
                speedMul = 1f;
                break;
            case DOUBLE_POINTS:
                doublePoints = false;
                break;
            default: break;
        }
        activePowerUp      = null;
        activePowerUpTimer = 0f;
    }

    // ── HUD ───────────────────────────────────────────────────────────────────

    @Override
    protected void drawHUD() {
        float topY = Constants.WORLD_HEIGHT - 10f;

        // Score — center
        game.fontScore.setColor(Color.WHITE);
        game.fontScore.draw(game.batch, String.valueOf(score),
                Constants.WORLD_WIDTH * 0.5f - 30f, topY);

        // Lives — top-left stars
        float iconSize = 26f, iconGap = 4f, startX = 10f;
        float iconY = Constants.WORLD_HEIGHT - iconSize - 14f;
        for (int i = 0; i < Math.min(lives, Constants.ARCADE_MAX_LIVES); i++) {
            Texture tex = starTex;
            Color  col  = (i < 3) ? Color.valueOf(Constants.COLOR_PRIMARY)
                                  : Color.valueOf(Constants.COLOR_ACCENT);
            game.batch.setColor(col);
            game.batch.draw(tex, startX + i * (iconSize + iconGap), iconY, iconSize, iconSize);
        }
        // Empty slots
        for (int i = lives; i < 3; i++) {
            game.batch.setColor(0.35f, 0.35f, 0.35f, 1f);
            game.batch.draw(starOutline, startX + i * (iconSize + iconGap), iconY, iconSize, iconSize);
        }
        game.batch.setColor(Color.WHITE);

        // Active power-up label
        if (activePowerUp != null && activePowerUpTimer > 0.05f) {
            game.fontSmall.setColor(activePowerUp.getColor());
            String lbl = activePowerUp.getLabel() + "  " + (int) Math.ceil(activePowerUpTimer) + "s";
            game.fontSmall.draw(game.batch, lbl, 12f, Constants.WORLD_HEIGHT - 80f);
            game.fontSmall.setColor(Color.WHITE);
        }

        if (doublePoints) {
            game.fontSmall.setColor(Color.valueOf(Constants.COLOR_ACCENT));
            game.fontSmall.draw(game.batch, "x2", Constants.WORLD_WIDTH * 0.5f + 52f, topY - 10f);
            game.fontSmall.setColor(Color.WHITE);
        }

        // Timer bar + power-ups in flight (require ShapeRenderer)
        game.batch.end();
        drawTimerBar();
        drawPowerUpsInFlight();
        game.batch.begin();
    }

    private void drawTimerBar() {
        float barW = 200f, barH = 12f;
        float barX = (Constants.WORLD_WIDTH - barW) * 0.5f;
        float barY = Constants.WORLD_HEIGHT - barH - 4f;
        float frac = MathUtils.clamp(timeLeft / Constants.ARCADE_DURATION, 0f, 1f);

        Color barColor;
        if (frac > 0.5f)       barColor = Color.valueOf(Constants.COLOR_ACCENT);
        else if (frac > 0.25f) barColor = new Color(1f, 0.75f, 0.1f, 1f);
        else                   barColor = new Color(1f, 0.20f, 0.10f, 1f);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.2f, 0.2f, 0.2f, 0.7f);
        sr.rect(barX, barY, barW, barH);
        sr.setColor(barColor);
        sr.rect(barX, barY, barW * frac, barH);
        sr.end();
    }

    private void drawPowerUpsInFlight() {
        if (powerUpsInFlight.size == 0) return;

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (PowerUpObject p : powerUpsInFlight) {
            if (!p.active) continue;
            Color c = p.type.getColor();
            float pulse = (MathUtils.sin(p.pulse) + 1f) * 0.5f;
            float r = p.radius + pulse * 4f;
            sr.setColor(c.r, c.g, c.b, 0.28f);
            sr.circle(p.x, p.y, r + 8f, 20);
            sr.setColor(c.r, c.g, c.b, 1f);
            sr.circle(p.x, p.y, r, 20);
            sr.setColor(1f, 1f, 1f, 0.5f);
            sr.circle(p.x, p.y, r * 0.35f, 12);
        }
        sr.end();

        game.batch.begin();
        for (PowerUpObject p : powerUpsInFlight) {
            if (!p.active) continue;
            game.fontSmall.setColor(Color.valueOf(Constants.COLOR_BG));
            String lbl = p.type.getLabel();
            game.fontSmall.draw(game.batch, lbl, p.x - lbl.length() * 5f, p.y + 9f);
        }
        game.batch.end();
    }
}
