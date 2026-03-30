package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.MainGame;

/**
 * Zen mode: 90-second countdown, no bombs, no life penalty for missed fruits.
 * Slice as many fruits as possible before time runs out.
 */
public class ZenGameScreen extends BaseGameScreen {

    private float timeLeft;

    public ZenGameScreen(MainGame game) {
        super(game);
        timeLeft = Constants.ZEN_DURATION;
    }

    // ── Mode contract ─────────────────────────────────────────────────────────

    @Override protected float   getInitialSpawnInterval() { return Constants.ZEN_SPAWN_INTERVAL; }
    @Override protected boolean shouldSpawnBombs()        { return false; } // no bombs in Zen
    @Override protected String  getBgPath()               { return "ui/zen_game.png"; }
    @Override protected String  getHighScorePrefKey()     { return Constants.PREF_HIGHSCORE_ZEN; }
    @Override protected String  getLeaderboardPrefKey()   { return Constants.PREF_LEADERBOARD_ZEN; }
    @Override protected String  getGameMode()             { return "Zen"; }

    @Override protected void handleBombSliced() { /* no bombs in Zen */ }

    @Override
    protected void handleFruitMissed() {
        // Zen is forgiving — no penalty
    }

    @Override protected boolean isGameOver() { return timeLeft <= 0f; }

    @Override
    protected void updateMode(float delta) {
        timeLeft -= delta;
        if (timeLeft < 0f) timeLeft = 0f;
    }

    // ── HUD ───────────────────────────────────────────────────────────────────

    @Override
    protected void drawHUD() {
        float topY = Constants.WORLD_HEIGHT - 10f;

        // Score — centered top
        game.fontScore.setColor(Color.WHITE);
        game.fontScore.draw(game.batch, String.valueOf(score),
                Constants.WORLD_WIDTH * 0.5f - 30f, topY);

        // Timer — top-left as text
        int secs = MathUtils.ceil(timeLeft);
        Color timerColor = secs <= 10 ? new Color(1f, 0.3f, 0.2f, 1f) : Color.valueOf(Constants.COLOR_ACCENT);
        game.fontBody.setColor(timerColor);
        game.fontBody.draw(game.batch, String.format("%02d", secs), 12f, topY);
        game.fontBody.setColor(Color.WHITE);

        // Draw circular timer arc (top-left area)
        // (arc drawn via ShapeRenderer — we must end batch, draw, begin batch again)
        game.batch.end();
        drawTimerArc();
        game.batch.begin();
    }

    private void drawTimerArc() {
        float cx = 40f;
        float cy = Constants.WORLD_HEIGHT - 60f;
        float radius = 22f;
        float fraction = timeLeft / Constants.ZEN_DURATION;

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        // Background arc (gray)
        sr.setColor(0.35f, 0.35f, 0.35f, 0.7f);
        sr.arc(cx, cy, radius, 0f, 360f, 24);

        // Foreground arc
        Color arcColor = fraction > 0.33f
                ? Color.valueOf(Constants.COLOR_ACCENT)
                : new Color(1f, 0.3f, 0.2f, 1f);
        sr.setColor(arcColor);
        sr.arc(cx, cy, radius, 90f, fraction * 360f, 24);

        sr.end();
    }
}
