package com.fruitslash000900.app9751;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.fruitslash000900.app9751.screens.MainMenuScreen;

public class MainGame extends Game {

    // ── Rendering ─────────────────────────────────────────────────────────────
    public SpriteBatch batch;
    public AssetManager manager;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    /** Large title / big score display  — Crackman */
    public BitmapFont fontTitle;
    /** Medium body text / buttons        — Ferrum  */
    public BitmapFont fontBody;
    /** Small labels / stats              — Ferrum  */
    public BitmapFont fontSmall;
    /** Score counter (large animated)    — Crackman */
    public BitmapFont fontScore;

    // ── Audio state ───────────────────────────────────────────────────────────
    public boolean musicEnabled  = true;
    public boolean sfxEnabled    = true;
    public boolean hapticEnabled = true;
    public Music   currentMusic  = null;

    // ── Preferences ───────────────────────────────────────────────────────────
    public Preferences prefs;

    // ── Haptic ────────────────────────────────────────────────────────────────
    public HapticInterface haptic;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();
        prefs   = Gdx.app.getPreferences(Constants.PREFS_NAME);

        // Load persisted audio settings
        musicEnabled  = prefs.getBoolean(Constants.PREF_MUSIC,  true);
        sfxEnabled    = prefs.getBoolean(Constants.PREF_SFX,    true);
        hapticEnabled = prefs.getBoolean(Constants.PREF_HAPTIC, true);

        generateFonts();
        loadCoreAssets();

        setScreen(new MainMenuScreen(this));
    }

    // ── Font generation ───────────────────────────────────────────────────────

    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Crackman.otf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Ferrum.otf"));

        FreeTypeFontParameter p = new FreeTypeFontParameter();
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);

        // fontTitle — 52 pt, thick outline
        p.size        = 52;
        p.borderWidth = 3;
        fontTitle = titleGen.generateFont(p);

        // fontScore — 64 pt, thick outline
        p.size        = 64;
        p.borderWidth = 3;
        fontScore = titleGen.generateFont(p);

        // fontBody — 28 pt
        p.size        = 28;
        p.borderWidth = 2;
        fontBody = bodyGen.generateFont(p);

        // fontSmall — 20 pt
        p.size        = 20;
        p.borderWidth = 1;
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // ── Core asset loading ────────────────────────────────────────────────────

    private void loadCoreAssets() {
        // Button textures (always present)
        manager.load("ui/buttons/button_rectangle_depth_gradient.png", com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_rectangle_depth_flat.png",     com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_round_depth_gradient.png",     com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_round_depth_flat.png",         com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/star.png",                            com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/star_outline.png",                    com.badlogic.gdx.graphics.Texture.class);

        // Settings gear icon
        manager.load("ui/icons/settings_gear.png", com.badlogic.gdx.graphics.Texture.class);

        // Music tracks
        manager.load("sounds/music/music_menu.ogg",      Music.class);
        manager.load("sounds/music/music_gameplay.ogg",  Music.class);
        manager.load("sounds/music/music_game_over.ogg", Music.class);

        // SFX
        manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
        manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
        manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);
        manager.load("sounds/sfx/sfx_collect.ogg",        Sound.class);

        manager.finishLoading();
    }

    // ── Music helpers ─────────────────────────────────────────────────────────

    /** Start a looping music track. No-ops if the same track is already playing. */
    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play a music track exactly once (e.g. game over jingle). */
    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /** Play an SFX sound if SFX is enabled. */
    public void playSound(String path) {
        if (sfxEnabled) manager.get(path, Sound.class).play(1.0f);
    }

    /** Trigger a short haptic pulse if enabled. */
    public void vibrate(int ms) {
        if (!hapticEnabled) return;
        if (haptic != null) {
            haptic.vibrate(ms);
        } else {
            try { Gdx.input.vibrate(ms); } catch (Exception ignored) {}
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        fontTitle.dispose();
        fontScore.dispose();
        fontBody.dispose();
        fontSmall.dispose();
        batch.dispose();
        manager.dispose();
    }
}
