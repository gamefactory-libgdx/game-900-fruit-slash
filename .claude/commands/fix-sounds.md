# Fix Sounds

Audit and fix all sound issues in this game. Checks for wrong sounds, missing sounds, and sounds not loaded.

---

## Step 1 — Discover what's available and what's used

```
ls assets/sounds/sfx/
ls assets/sounds/music/
cat assets/ASSETS_MANIFEST.json
grep -rn "playSound\|playSfx\|playMusic\|playMusicOnce\|manager.load.*Sound\|manager.load.*Music" core/src/
```

---

## Step 2 — Check every sound reference is loaded

Find every `manager.get("sounds/...", Sound.class)` and `manager.get("sounds/...", Music.class)` call.
Cross-check against every `manager.load("sounds/...", ...)` call in `MainGame.java`.

**PROBLEM:** Sound referenced in a screen but never loaded → crash at runtime.

**HOW TO FIX:** Add the missing `manager.load(...)` call to `MainGame.loadAssets()`.

---

## Step 3 — Check every loaded sound actually exists

For every `manager.load("sounds/sfx/FILENAME.ogg", ...)`, verify: `ls assets/sounds/sfx/FILENAME.ogg`

**HOW TO FIX:** Substitute the closest available alternative:
- Missing `sfx_drum_hit.ogg` → use `sfx_hit.ogg`
- Missing `sfx_note_*.ogg` → use `sfx_jingle_hit_00.ogg`
- Missing `sfx_coin_collect.ogg` → use `sfx_coin.ogg`
- Missing `sfx_wrong.ogg` → use `sfx_error.ogg`

Update both the load call and all usage references.

---

## Step 4 — Check rhythm/music game hit sounds

Read `assets/ASSETS_MANIFEST.json` — if archetype is `piano-tiles` or any rhythm variant:

**PROBLEM:** Lane tap uses `sfx_hit.ogg` — a generic thud, not a musical note.

**HOW TO FIX:** Replace with per-lane musical note sounds from `assets/sounds/sfx/`:
- `sfx_jingle_hit_00.ogg` … `sfx_jingle_hit_16.ogg` — melodic hits
- `sfx_jingle_pizz_00.ogg` … `sfx_jingle_pizz_16.ogg` — pizzicato
- `sfx_jingle_8bit_00.ogg` … `sfx_jingle_8bit_16.ogg` — 8-bit tones

Assign one note per lane (e.g. indices 0, 4, 8, 12 for 4 lanes).
Load all used note sounds in `MainGame.loadAssets()`.

---

## Step 5 — Check music lifecycle

Read every screen's `show()`. Verify:
- **Menu screen** → `game.playMusic("sounds/music/music_menu.ogg")`
- **Gameplay screen** → `game.playMusic("sounds/music/music_gameplay.ogg")`
- **Game over screen** → `game.playMusicOnce("sounds/music/music_game_over.ogg")` — NOT `playMusic` (must not loop)
- **Pause screen** → pauses music on show, resumes on dismiss without restarting

**HOW TO FIX:** Add missing calls to `show()`. For pause:
```java
// PauseScreen show(): pause music
if (game.currentMusic != null) game.currentMusic.pause();
// On resume button: resume without restart
if (game.currentMusic != null && game.musicEnabled) game.currentMusic.play();
```

---

## Step 6 — Check button click sounds

Every `game.setScreen(...)` triggered by user input should be preceded by a sound.

**HOW TO FIX:**
```java
game.playSound("sounds/sfx/sfx_button_click.ogg");  // forward navigation
game.playSound("sounds/sfx/sfx_button_back.ogg");   // back navigation
game.setScreen(new TargetScreen(game));
```

---

## Step 7 — Build and verify

```
./gradlew android:assembleDebug --no-daemon 2>&1 | tail -30
```

Fix compilation errors. Loop until BUILD SUCCESSFUL.

---

## Step 8 — Report

| Issue | Count fixed | Details |
|-------|-------------|---------|
| Missing load calls | N | list sounds |
| Wrong file references | N | list replacements |
| Wrong hit sounds | N | screen names |
| Music lifecycle | N | screen names |
| Silent buttons | N | screen names |
