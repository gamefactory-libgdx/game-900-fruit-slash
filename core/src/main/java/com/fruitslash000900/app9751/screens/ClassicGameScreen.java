package com.fruitslash000900.app9751.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.fruitslash000900.app9751.Constants;
import com.fruitslash000900.app9751.MainGame;

/**
 * Classic mode: 3 lives, slice fruits, survive as long as possible.
 * Slice a bomb → instant game over.
 * Miss a fruit  → lose 1 life.
 * Difficulty ramps as score increases.
 */
public class ClassicGameScreen extends BaseGameScreen {

    private int lives;
    private final GlyphLayout layout = new GlyphLayout();

    // Lives icon from ui/buttons/star.png (always loaded)
    private Texture starTex;
    private Texture starOutline;

    public ClassicGameScreen(MainGame game) {
        super(game);
        lives     = Constants.CLASSIC_LIVES;
        starTex     = game.manager.get("ui/buttons/star.png",         Texture.class);
        starOutline = game.manager.get("ui/buttons/star_outline.png", Texture.class);
    }

    // ── Mode contract ─────────────────────────────────────────────────────────

    @Override protected float  getInitialSpawnInterval() { return Constants.CLASSIC_SPAWN_START; }
    @Override protected boolean shouldSpawnBombs()       { return true; }
    @Override protected String getBgPath()               { return "ui/classic_game.png"; }
    @Override protected String getHighScorePrefKey()     { return Constants.PREF_HIGHSCORE_CLASSIC; }
    @Override protected String getLeaderboardPrefKey()   { return Constants.PREF_LEADERBOARD_CLASSIC; }
    @Override protected String getGameMode()             { return "Classic"; }

    @Override
    protected void handleBombSliced() {
        // Bomb in Classic → instant game over
        lives = 0;
    }

    @Override
    protected void handleFruitMissed() {
        lives = Math.max(0, lives - 1);
        game.playSound("sounds/sfx/sfx_hit.ogg");
        game.vibrate(60);
    }

    @Override protected boolean isGameOver() { return lives <= 0; }

    @Override
    protected void onScoreChanged() {
        // Scale difficulty: faster spawns, capped at min
        int tier = score / 50;
        spawnInterval = Math.max(Constants.CLASSIC_SPAWN_MIN,
                Constants.CLASSIC_SPAWN_START - tier * 0.08f);
    }

    @Override protected void updateMode(float delta) { /* lives-based — nothing extra */ }

    // ── HUD ───────────────────────────────────────────────────────────────────

    @Override
    protected void drawHUD() {
        float topY = Constants.WORLD_HEIGHT - 10f;

        // Score — centered top
        game.fontScore.setColor(Color.WHITE);
        layout.setText(game.fontScore, String.valueOf(score));
        game.fontScore.draw(game.batch, String.valueOf(score),
                (Constants.WORLD_WIDTH - layout.width) * 0.5f, topY);

        // Lives — top-left as star icons
        float iconSize = 30f;
        float iconGap  = 6f;
        float startX   = 10f;
        float iconY    = Constants.WORLD_HEIGHT - iconSize - 12f;
        for (int i = 0; i < Constants.CLASSIC_LIVES; i++) {
            Texture tex = (i < lives) ? starTex : starOutline;
            game.batch.setColor(i < lives ? Color.valueOf(Constants.COLOR_PRIMARY) : new Color(0.5f, 0.5f, 0.5f, 1f));
            game.batch.draw(tex, startX + i * (iconSize + iconGap), iconY, iconSize, iconSize);
        }
        game.batch.setColor(Color.WHITE);
    }
}
