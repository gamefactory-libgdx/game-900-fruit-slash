package com.fruitslash000900.app9751;

public final class Constants {

    private Constants() {}

    // ── Viewport ──────────────────────────────────────────────────────────────
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // ── Color palette ─────────────────────────────────────────────────────────
    public static final String COLOR_PRIMARY = "#E85D04";   // vivid orange — main action buttons
    public static final String COLOR_ACCENT  = "#FFB703";   // warm gold — highlights / accents
    public static final String COLOR_BG      = "#3D1F0A";   // dark wood brown — fallback clear color

    // ── Physics ───────────────────────────────────────────────────────────────
    public static final float GRAVITY              = 400f;   // px/s²
    public static final float FRUIT_SPEED_MIN      = 350f;   // px/s initial vertical velocity
    public static final float FRUIT_SPEED_MAX      = 500f;
    public static final float FRUIT_ANGLE_MIN      = 55f;    // degrees from horizontal
    public static final float FRUIT_ANGLE_MAX      = 125f;
    public static final float FRUIT_ROTATION_SPEED = 120f;   // degrees/s (randomised ±)
    public static final float FRUIT_RADIUS         = 40f;    // collision radius px

    // ── Swipe trail ───────────────────────────────────────────────────────────
    public static final float TRAIL_FADE       = 0.2f;  // seconds
    public static final float TRAIL_WIDTH_MAX  = 12f;   // px at newest point
    public static final float TRAIL_WIDTH_MIN  = 2f;

    // ── Juice particles ───────────────────────────────────────────────────────
    public static final int   JUICE_PARTICLE_COUNT = 8;
    public static final float JUICE_SPEED_MIN      = 80f;
    public static final float JUICE_SPEED_MAX      = 160f;
    public static final float PARTICLE_LIFETIME    = 0.6f;
    public static final int   STAR_PARTICLE_COUNT  = 10;
    public static final float STAR_LIFETIME        = 0.8f;

    // ── Fruit halves ──────────────────────────────────────────────────────────
    public static final float HALF_FADE_DURATION = 1.2f;
    public static final float HALF_SPREAD_SPEED  = 60f;   // px/s lateral spread on split

    // ── Combo overlay ─────────────────────────────────────────────────────────
    public static final float COMBO_OVERLAY_DURATION  = 1.5f;
    public static final int   COMBO_OVERLAY_THRESHOLD = 3;   // show overlay at ×3+
    public static final int   COMBO_MAX_MULTIPLIER    = 10;

    // ── Bomb flash ────────────────────────────────────────────────────────────
    public static final float BOMB_FLASH_DURATION = 0.3f;
    public static final float BOMB_FLASH_ALPHA    = 0.55f;

    // ── Classic mode ──────────────────────────────────────────────────────────
    public static final int   CLASSIC_LIVES      = 3;
    public static final float CLASSIC_SPAWN_START = 1.2f;
    public static final float CLASSIC_SPAWN_MIN   = 0.4f;
    public static final int   BOMB_START_SCORE   = 100;

    // ── Zen mode ──────────────────────────────────────────────────────────────
    public static final float ZEN_DURATION       = 90f;
    public static final float ZEN_SPAWN_INTERVAL = 1.0f;

    // ── Arcade mode ───────────────────────────────────────────────────────────
    public static final float ARCADE_DURATION       = 60f;
    public static final float ARCADE_SPAWN_START    = 1.0f;
    public static final float ARCADE_SPAWN_MIN      = 0.3f;
    public static final float ARCADE_RAMP_INTERVAL  = 15f;   // reduce interval every N seconds
    public static final float ARCADE_RAMP_STEP      = 0.1f;  // reduction per ramp
    public static final float POWERUP_SPAWN_INTERVAL = 20f;
    public static final int   ARCADE_MAX_LIVES      = 5;

    // ── Power-up durations ────────────────────────────────────────────────────
    public static final float FRENZY_DURATION        = 5f;
    public static final float FRENZY_TIME_BONUS      = 10f;
    public static final float FRENZY_SPEED_MUL       = 2.0f;
    public static final float FREEZE_DURATION        = 4f;
    public static final float FREEZE_SPEED_MUL       = 0.3f;
    public static final float DOUBLE_POINTS_DURATION = 8f;

    // ── Score / leaderboard ───────────────────────────────────────────────────
    public static final float COUNT_UP_DURATION      = 1.5f;
    public static final int   LEADERBOARD_MAX_ENTRIES = 10;

    // ── Miss indicator ────────────────────────────────────────────────────────
    public static final float MISS_INDICATOR_DURATION = 1.0f;

    // ── Button sizes (world units) ────────────────────────────────────────────
    public static final float BTN_W_PRIMARY   = 280f;
    public static final float BTN_H_PRIMARY   = 56f;
    public static final float BTN_W_SECONDARY = 220f;
    public static final float BTN_H_SECONDARY = 50f;
    public static final float BTN_W_SMALL     = 160f;
    public static final float BTN_H_SMALL     = 44f;
    public static final float BTN_ROUND_SIZE  = 56f;

    // ── SharedPreferences ─────────────────────────────────────────────────────
    public static final String PREFS_NAME               = "fruit_slash_prefs";
    public static final String PREF_TUTORIAL_SHOWN      = "tutorial_shown";
    public static final String PREF_SFX                 = "sfx_enabled";
    public static final String PREF_MUSIC               = "music_enabled";
    public static final String PREF_HAPTIC              = "haptic_enabled";
    public static final String PREF_HIGHSCORE_CLASSIC   = "highscore_classic";
    public static final String PREF_HIGHSCORE_ZEN       = "highscore_zen";
    public static final String PREF_HIGHSCORE_ARCADE    = "highscore_arcade";
    public static final String PREF_LEADERBOARD_CLASSIC = "leaderboard_classic";
    public static final String PREF_LEADERBOARD_ZEN     = "leaderboard_zen";
    public static final String PREF_LEADERBOARD_ARCADE  = "leaderboard_arcade";
    public static final String PREF_TOTAL_FRUITS_SLICED = "total_fruits_sliced";
    public static final String PREF_TOTAL_BOMBS_HIT     = "total_bombs_hit";
}
