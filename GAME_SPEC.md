<function_calls>
<invoke name="Write">
<parameter name="path">/home/kaliuzhnyi/asocity/projects/game-900-fruit-slash/GAME_SPEC.md</parameter>
<parameter name="content"># GAME_SPEC — Fruit Slash

## 1. Game Identity

| Field | Value |
|-------|-------|
| Title | Fruit Slash |
| Java package | `com.factory.template` |
| Main class | `FruitSlashGame` |
| Genre | Slash-Arcade |
| Orientation | Portrait |
| Viewport | `FitViewport(360, 640)` — all coords in virtual px |
| Min Android API | 26 |
| Prefs file | `fruit_slash_prefs` |

---

## 2. Screen Inventory

| # | Java Class | Purpose | Entry From | Exits To |
|---|-----------|---------|------------|----------|
| 1 | `MainMenuScreen` | Title + PLAY / Settings / Leaderboard | app start, SliceResultScreen, SettingsScreen (back), LeaderboardScreen (back) | ModeSelectScreen (slide-up), TutorialScreen (first launch), SettingsScreen, LeaderboardScreen |
| 2 | `TutorialScreen` | 3-panel swipeable tutorial, first launch only | MainMenuScreen | ModeSelectScreen |
| 3 | `ModeSelectScreen` | Mode picker: Classic / Zen / Arcade | MainMenuScreen, TutorialScreen | ClassicGameScreen, ZenGameScreen, ArcadeGameScreen, MainMenuScreen (back) |
| 4 | `ClassicGameScreen` | Lives-based gameplay, no timer, bombs | ModeSelectScreen, SliceResultScreen (Play Again) | SliceResultScreen, MainMenuScreen (pause→Menu) |
| 5 | `ZenGameScreen` | 90-second countdown, no bombs | ModeSelectScreen, SliceResultScreen (Play Again) | SliceResultScreen, MainMenuScreen (pause→Menu) |
| 6 | `ArcadeGameScreen` | 60-second countdown, power-ups, lives | ModeSelectScreen, SliceResultScreen (Play Again) | SliceResultScreen, MainMenuScreen (pause→Menu) |
| 7 | `SliceResultScreen` | Post-game stats + score count-up | any GameScreen (game over) | same GameScreen (Play Again), MainMenuScreen, LeaderboardScreen |
| 8 | `LeaderboardScreen` | Per-mode top-10 local table (3 tabs) | MainMenuScreen, SliceResultScreen | previous screen (back) |
| 9 | `SettingsScreen` | Music/SFX/haptic toggles, reset scores | MainMenuScreen | MainMenuScreen (back) |

`ComboDetailScreen` is NOT a full screen — it is an **overlay** rendered inside each GameScreen.

---

## 3. Screen Flow

```
[App Start]
     │
     ▼
MainMenuScreen ──[first launch]──► TutorialScreen ──► ModeSelectScreen
     │                                                      │
     ├──[Settings]──► SettingsScreen ──(back)──► ┤         ├──[Classic]──► ClassicGameScreen ─┐
     ├──[Leaderboard]─► LeaderboardScreen ──────► ┤         ├──[Zen]──────► ZenGameScreen ─────┤
     └──[Play]────────────────────────────────────► ModeSelectScreen      └──[Arcade]───► ArcadeGameScreen ─┘
                                                                                                │
                                                                                          game over/time up
                                                                                                │
                                                                                                ▼
                                                                                       SliceResultScreen
                                                                                         ├──[Play Again]──► same GameScreen
                                                                                         ├──[Menu]────────► MainMenuScreen
                                                                                         └──[Leaderboard]─► LeaderboardScreen

In-game overlay (all 3 game screens):
  combo ≥ ×3 ──► ComboDetailScreen overlay (1.5 s auto-dismiss or tap; game continues under it)
  pause icon ──► PauseOverlay (Resume / Restart / Menu; game freezes)
```

---

## 4. Game Objects

### 4.1 `FruitSlashGame extends Game`

```java
// Fields
SpriteBatch batch;
AssetManager assets;
Preferences prefs;
BitmapFont font1Large, font1Medium, font1Small;
BitmapFont font2Medium, font2Small;

// All screen instances (created once, reused)
MainMenuScreen mainMenuScreen;
TutorialScreen tutorialScreen;
ModeSelectScreen modeSelectScreen;
ClassicGameScreen classicGameScreen;
ZenGameScreen zenGameScreen;
ArcadeGameScreen arcadeGameScreen;
SliceResultScreen sliceResultScreen;
LeaderboardScreen leaderboardScreen;
SettingsScreen settingsScreen;

// Methods
void create()          // load assets (blocking), init prefs, create all screens, route to MainMenuScreen or TutorialScreen
void dispose()         // dispose all screens + batch + assets
```

### 4.2 `GameMode` enum

```java
CLASSIC, ZEN, ARCADE
```

### 4.3 `FruitType` enum

```java
// name, spriteBase, basePoints, weight
APPLE("fruit_apple", 10, 30),
WATERMELON("fruit_watermelon", 10, 30),
ORANGE("fruit_orange", 10, 30),
BANANA("fruit_banana", 15, 20),
STRAWBERRY("fruit_strawberry", 15, 20),
PINEAPPLE("fruit_pineapple", 20, 10),
KIWI("fruit_kiwi", 20, 10);

// Fields
String spriteBase;   // e.g. "fruit_apple" → assets fruit_apple.png, fruit_apple_half_a.png, fruit_apple_half_b.png
int basePoints;
int weight;          // used for weighted random selection
```

### 4.4 `PowerUpType` enum (Arcade only)

```java
FRENZY("pu_frenzy"),
FREEZE("pu_freeze"),
DOUBLE_POINTS("pu_double"),
EXTRA_LIFE("pu_extralife");

String spriteFile;
```

### 4.5 `GameObject`

Base class for all flying objects (fruits, bombs, power-ups).

```java
// Fields
float x, y;              // center position, virtual px
float vx, vy;            // velocity px/s
float rotation;          // degrees
float rotationSpeed;     // degrees/s
float radius;            // collision radius for AABB (half of sprite width)
boolean sliced;          // true once cut
boolean offScreen;       // true once exits bounds → triggers miss logic
Texture texture;

// Methods
void update(float delta)   // x += vx*delta; y += vy*delta; vy -= GRAVITY*delta; rotation += rotationSpeed*delta
void draw(SpriteBatch b)
boolean isOffScreen()      // y < -radius || y > 640+radius || x < -radius || x > 360+radius
```

### 4.6 `FruitObject extends GameObject`

```java
FruitType type;
boolean sliced;
// On slice: spawn FruitHalf ×2, spawn JuiceParticle burst, play sfx_slice
```

### 4.7 `FruitHalf extends GameObject`

```java
FruitType type;
boolean isHalfA;   // selects half_a or half_b texture
float alpha;       // fades from 1.0 to 0 over HALF_FADE_DURATION (1.2 s)
```

### 4.8 `BombObject extends GameObject`

```java
// On slice: −2 lives, trigger red flash overlay, play sfx_bomb, haptic
```

### 4.9 `PowerUpObject extends GameObject`

```java
PowerUpType type;
// On slice: apply effect, play sfx_powerup
```

### 4.10 `JuiceParticle`

```java
float x, y, vx, vy;
float alpha;          // starts 1.0, fades to 0 over PARTICLE_LIFETIME (0.6 s)
Texture texture;      // one of particle_juice_*.png — chosen by fruit type
float scale;

void update(float delta)
void draw(SpriteBatch b)
boolean isDead()
```

Fruit → juice color mapping:
- APPLE, STRAWBERRY → `particle_juice_red.png`
- ORANGE, BANANA, PINEAPPLE → `particle_juice_orange.png`
- KIWI → `particle_juice_green.png`
- WATERMELON → `particle_juice_red.png`

Emit 6–10 particles per slice, random directions, speed 80–160 px/s.

### 4.11 `StarParticle`

```java
// Same structure as JuiceParticle but uses particle_star.png
// Emitted on combo milestone (×3+): 8–12 stars burst from slice point
float alpha;
float lifetime = 0.8f;
```

### 4.12 `SwipeTrail`

```java
Array<Vector2> points;   // touch positions sampled each touchDragged call
float age;               // seconds since last touch-up; trail fades over TRAIL_FADE (0.2 s)
boolean active;

void addPoint(float x, float y)
void update(float delta)   // increment age; if age > TRAIL_FADE clear points
void draw(SpriteBatch b, ShapeRenderer sr)
// Draw as connected quads from sprite_trail.png with alpha = 1 - age/TRAIL_FADE
// Width 12 px at newest point tapering to 2 px at oldest

boolean intersects(GameObject obj)
// For each consecutive pair of points in trail, test line segment vs circle (obj.x, obj.y, obj.radius)
// Use 2D line-segment-circle intersection math
```

### 4.13 `BaseGameScreen implements Screen, InputProcessor`

Abstract base for ClassicGameScreen, ZenGameScreen, ArcadeGameScreen.

```java
// Fields
FruitSlashGame game;
GameMode mode;
SpriteBatch batch;
ShapeRenderer shapeRenderer;
FitViewport viewport;
OrthographicCamera camera;

Array<GameObject> objects;       // active fruits, bombs, power-ups
Array<FruitHalf> halves;
Array<JuiceParticle> particles;
Array<StarParticle> stars;
SwipeTrail trail;

int score;
int lives;                       // Classic/Arcade = 3; Zen = unused
int fruitsSliced;
int bombsHit;
int bestCombo;

int comboCount;                  // consecutive multi-slice swipes
int comboMultiplier;             // = min(comboCount + 1, 10)
boolean doublePointsActive;
float doublePointsTimer;
boolean freezeActive;
float freezeTimer;
float speedMultiplier;           // 1.0 normally; 0.3 during Freeze; >1.0 during Frenzy
boolean frenzyActive;
float frenzyTimer;

float spawnTimer;
float spawnInterval;

boolean paused;
boolean gameOver;

// Overlay state
boolean comboOverlayVisible;
float comboOverlayTimer;
int comboOverlayMultiplier;
int comboOverlayFruits;
int comboOverlayPoints;

boolean bombFlashActive;
float bombFlashTimer;            // BOMB_FLASH_DURATION = 0.3 s

// Touch tracking (for combo: how many fruits cut in current swipe)
int fruitsSlicedThisSwipe;

// Methods
void spawnFruit()
void spawnBomb()                 // Classic/Arcade only
void spawnPowerUp()              // Arcade only
void sliceObject(GameObject obj, float sliceX, float sliceY)
void onFruitSliced(FruitObject fruit, float x, float y)
void onBombSliced(BombObject bomb)
void onPowerUpSliced(PowerUpObject pu)
void onFruitMissed(FruitObject fruit)
void checkSwipeIntersections()   // called in render; tests trail vs every active object once per frame
void updateDifficulty()          // updates spawnInterval + speedMultiplier based on score
void showComboOverlay(int multiplier, int fruits, int points)
void triggerBombFlash()
void endGame()                   // save scores, transition to SliceResultScreen
void drawHUD()                   // abstract — each subclass draws its own HUD
void drawPauseOverlay()
void drawComboOverlay()
void drawBombFlash()
```

### 4.14 `ClassicGameScreen extends BaseGameScreen`

```java
// Overrides
void drawHUD()   // 3 hearts top-left; score top-center; pause top-right
// No timer. Ends when lives == 0.
// Difficulty: see Constants.CLASSIC_DIFFICULTY table
```

### 4.15 `ZenGameScreen extends BaseGameScreen`

```java
float timeRemaining;   // starts at ZEN_DURATION (90 s)
// Overrides
void drawHUD()   // circular timer top-center; score top-right; no hearts
void update()    // timeRemaining -= delta; if <= 0 → endGame()
// No bombs. No life loss on miss. Spawn interval fixed ZEN_SPAWN_INTERVAL (1.0 s).
// Miss: draw small gray "×" at exit point for 1.0 s
```

### 4.16 `ArcadeGameScreen extends BaseGameScreen`

```java
float timeRemaining;        // starts at ARCADE_DURATION (60 s)
float powerUpSpawnTimer;    // resets to POWERUP_SPAWN_INTERVAL (20 s) after each spawn
PowerUpType activePowerUp;  // currently running power-up (null if none)

// Overrides
void drawHUD()   // 3 hearts top-left; horizontal timer bar top-center (green→yellow→red); score + active pu icon top-right
void update()    // timeRemaining -= delta; powerUpSpawnTimer logic; end if timer==0 or lives==0
// Difficulty ramp: every 15 s reduce spawnInterval by 0.1 s (floor ARCADE_SPAWN_MIN = 0.3 s)
```

### 4.17 `SliceResultScreen implements Screen`

```java
GameMode mode;
int finalScore;
int fruitsSliced;
int bestCombo;
int bombsHit;
boolean isNewBest;

float countUpTimer;     // score animates from 0 to finalScore over COUNT_UP_DURATION (1.5 s)
int displayedScore;

// Buttons: PLAY AGAIN / MENU / LEADERBOARD
```

### 4.18 `LeaderboardScreen implements Screen`

```java
int activeTab;    // 0=Classic, 1=Zen, 2=Arcade
// Loads per-mode JSON from prefs on show()
// Renders scrollable list: rank | score | date string
// Highlights row matching most-recent run score if navigated from SliceResultScreen
```

### 4.19 `SettingsScreen implements Screen`

```java
// Toggle buttons: music, sfx, haptic
// "Reset All Scores" → confirm dialog (TextButton) → clears all prefs keys
```

### 4.20 `ModeSelectScreen implements Screen`

```java
// 3 mode cards (ImageTextButton or manual draw): Classic, Zen, Arcade
// Each card: icon + title + one-line description
// Tap → transition to appropriate GameScreen
```

### 4.21 `TutorialScreen implements Screen`

```java
int currentPanel;    // 0, 1, 2
// Panel 0: "Swipe to slice fruits!" — animated swipe over fruit_apple.png
// Panel 1: "Multi-slice = Combo!" — two fruits, swipe line, ×2 badge
// Panel 2: "Avoid bombs!" — bomb.png, red × swipe
// Swipe left/right to change panels
// Skip button (top-right) → ModeSelectScreen, sets tutorial_shown=true
// "GOT IT!" on panel 2 → ModeSelectScreen, sets tutorial_shown=true
```

### 4.22 `MainMenuScreen implements Screen`

```java
// 3 decorative fruits arc/spin as idle animation (looping parabolic paths, non-interactive)
// PLAY button → ModeSelectScreen (or TutorialScreen if !tutorial_shown)
// Settings / Leaderboard icon buttons
// Version string (BuildConfig.VERSION_NAME) bottom-right
```

### 4.23 `LeaderboardEntry`

```java
int score;
String date;    // "yyyy-MM-dd"
```

Serialized to/from JSON string stored in prefs. Use `com.badlogic.gdx.utils.Json`.

### 4.24 `ScoreManager`

```java
// Static helper
static void saveScore(Preferences prefs, GameMode mode, int score)
// Reads existing JSON array for mode, inserts new entry, sorts descending, trims to top 10, saves back
// Also updates highscore_classic/zen/arcade if score > current

static Array<LeaderboardEntry> getLeaderboard(Preferences prefs, GameMode mode)
static int getHighScore(Preferences prefs, GameMode mode)
```

---

## 5. Asset Filenames

All assets live under `assets/` in the core module. Claude must create placeholder PNG/OGG files for
any that do not exist in the copied asset set. Use the `assets/ASSETS_MANIFEST.json` to check what was
copied; create fallbacks programmatically (via `Pixmap`) for any missing textures rather than crashing.

### Backgrounds
```
assets/backgrounds/game/bg_main.png
assets/backgrounds/game/bg_mode_select.png
assets/backgrounds/game/bg_result.png
```
If only one background exists from ASSETS_MANIFEST, use it for all three backgrounds.

### Fruits (whole + halves) — 21 files
```
assets/sprites/object/fruit_apple.png
assets/sprites/object/fruit_apple_half_a.png
assets/sprites/object/fruit_apple_half_b.png
assets/sprites/object/fruit_watermelon.png
assets/sprites/object/fruit_watermelon_half_a.png
assets/sprites/object/fruit_watermelon_half_b.png
assets/sprites/object/fruit_orange.png
assets/sprites/object/fruit_orange_half_a.png
assets/sprites/object/fruit_orange_half_b.png
assets/sprites/object/fruit_banana.png
assets/sprites/object/fruit_banana_half_a.png
assets/sprites/object/fruit_banana_half_b.png
assets/sprites/object/fruit_strawberry.png
assets/sprites/object/fruit_strawberry_half_a.png
assets/sprites/object/fruit_strawberry_half_b.png
assets/sprites/object/fruit_pineapple.png
assets/sprites/object/fruit_pineapple_half_a.png
assets/sprites/object/fruit_pineapple_half_b.png
assets/sprites/object/fruit_kiwi.png
assets/sprites/object/fruit_kiwi_half_a.png
assets/sprites/object/fruit_kiwi_half_b.png
```

### Bombs & Power-ups
```
assets/sprites/object/bomb.png
assets/sprites/object/pu_frenzy.png
assets/sprites/object/pu_freeze.png
assets/sprites/object/pu_double.png
assets/sprites/object/pu_extralife.png
```

### UI Icons & HUD
```
assets/sprites/ui/ic_heart_full.png
assets/sprites/ui/ic_heart_empty.png
assets/sprites/ui/ic_pause.png
assets/sprites/ui/ic_settings.png
assets/sprites/ui/ic_leaderboard.png
assets/sprites/ui/ic_back.png
assets/sprites/ui/ic_new_best.png
assets/sprites/ui/icon_classic.png
assets/sprites/ui/icon_zen.png
assets/sprites/ui/icon_arcade.png
```

### Particles & Effects
```
assets/sprites/ui/particle_juice_red.png
assets/sprites/ui/particle_juice_orange.png
assets/sprites/ui/particle_juice_yellow.png
assets/sprites/ui/particle_juice_green.png
assets/sprites/ui/particle_star.png
assets/sprites/ui/effect_bomb_flash.png
assets/sprites/ui/sprite_trail.png
```

### Fonts
```
assets/fonts/font1.ttf
assets/fonts/font2.ttf
assets/fonts/Roboto-Regular.ttf
```

### Sounds
```
assets/sounds/music_menu.ogg
assets/sounds/music_game.ogg
assets/sounds/sfx_slice.ogg
assets/sounds/sfx_bomb.ogg
assets/sounds/sfx_combo.ogg
assets/sounds/sfx_powerup.ogg
assets/sounds/sfx_miss.ogg
assets/sounds/sfx_gameover.ogg
assets/sounds/sfx_button.ogg
```

**Fallback strategy for missing assets:**
- Missing texture → generate 64×64 `Pixmap` filled with a recognisable color (fruits: each a distinct
  hue; bombs: black; hearts: red/gray; UI icons: white).
- Half-fruit textures → if `_half_a` / `_half_b` not present, reuse the whole fruit texture at 0.6×
  scale with different horizontal flip.
- Missing sounds → silently skip playback (null-check before play).

---

## 6. Constants (`Constants.java`)

```java
public final class Constants {

    // Viewport
    public static final int VIEWPORT_WIDTH  = 360;
    public static final int VIEWPORT_HEIGHT = 640;

    // Physics
    public static final float GRAVITY              = 400f;   // px/s²
    public static final float FRUIT_SPEED_MIN      = 350f;   // px/s initial vertical velocity
    public static final float FRUIT_SPEED_MAX      = 500f;
    public static final float FRUIT_ANGLE_MIN      = 55f;    // degrees from horizontal
    public static final float FRUIT_ANGLE_MAX      = 125f;
    public static final float FRUIT_ROTATION_SPEED = 120f;   // degrees/s (randomised ± this)
    public static final float FRUIT_RADIUS         = 40f;    // collision radius px

    // Trail
    public static final float TRAIL_FADE           = 0.2f;   // seconds
    public static final float TRAIL_WIDTH_MAX      = 12f;    // px at newest point
    public static final float TRAIL_WIDTH_MIN      = 2f;

    // Particles
    public static final int   JUICE_PARTICLE_COUNT = 8;
    public static final float JUICE_SPEED_MIN      = 80f;
    public static final float JUICE_SPEED_MAX      = 160f;
    public static final float PARTICLE_LIFETIME    = 0.6f;
    public static final int   STAR_PARTICLE_COUNT  = 10;
    public static final float STAR_LIFETIME        = 0.8f;

    // Fruit halves
    public static final float HALF_FADE_DURATION   = 1.2f;
    public static final float HALF_SPREAD_SPEED    = 60f;    // px/s lateral spread on split

    // Combo overlay
    public static final float COMBO_OVERLAY_DURATION = 1.5f;
    public static final int   COMBO_OVERLAY_THRESHOLD = 3;   // show at ×3+

    // Bomb flash
    public static final float BOMB_FLASH_DURATION  = 0.3f;
    public static final float BOMB_FLASH_ALPHA     = 0.55f;

    // Modes
    public static final int   CLASSIC_LIVES        = 3;
    public static final float ZEN_DURATION         = 90f;    // seconds
    public static final float ARCADE_DURATION      = 60f;

    // Classic spawn / difficulty table
    //   score < 200  → interval 1.2s, bombRatio 0,   speedMul 1.0
    //   score < 500  → interval 1.0s, bombRatio 1/6, speedMul 1.1
    //   score < 1000 → interval 0.8s, bombRatio 1/5, speedMul 1.2
    //   score < 2000 → interval 0.6s, bombRatio 1/4, speedMul 1.35
    //   score ≥ 2000 → interval 0.4s, bombRatio 1/4, speedMul 1.5
    public static final float CLASSIC_SPAWN_START  = 1.2f;
    public static final float CLASSIC_SPAWN_MIN    = 0.4f;
    public static final int   BOMB_START_SCORE     = 100;

    // Zen
    public static final float ZEN_SPAWN_INTERVAL   = 1.0f;

    // Arcade
    public static final float ARCADE_SPAWN_START   = 1.0f;
    public static final float ARCADE_SPAWN_MIN     = 0.3f;
    public static final float ARCADE_RAMP_INTERVAL = 15f;    // reduce interval every N seconds
    public static final float ARCADE_RAMP_STEP     = 0.1f;   // reduction per ramp
    public static final float POWERUP_SPAWN_INTERVAL = 20f;

    // Power-up durations
    public static final float FRENZY_DURATION       = 5f;
    public static final float FRENZY_TIME_BONUS     = 10f;
    public static final float FRENZY_SPEED_MUL      = 2.0f;
    public static final float FREEZE_DURATION       = 4f;
    public static final float FREEZE_SPEED_MUL      = 0.3f;
    public static final float DOUBLE_POINTS_DURATION= 8f;
    public static final int   ARCADE_MAX_LIVES      = 5;

    // Combo
    public static final int   COMBO_MAX_MULTIPLIER  = 10;

    // Score count-up animation
    public static final float COUNT_UP_DURATION     = 1.5f;

    // Leaderboard
    public static final int   LEADERBOARD_MAX_ENTRIES = 10;

    // Miss indicator
    public static final float MISS_INDICATOR_DURATION = 1.0f;

    // Prefs keys
    public static final String PREFS_NAME               = "fruit_slash_prefs";
    public static final String PREF_TUTORIAL_SHOWN      = "tutorial_shown";
    public static final String PREF_SFX_ENABLED         = "sfx_enabled";
    public static final String PREF_MUSIC_ENABLED       = "music_enabled";
    public static final String PREF_HAPTIC_ENABLED      = "haptic_enabled";
    public static final String PREF_HIGHSCORE_CLASSIC   = "highscore_classic";
    public static final String PREF_HIGHSCORE_ZEN       = "highscore_zen";
    public static final String PREF_HIGHSCORE_ARCADE    = "highscore_arcade";
    public static final String PREF_LEADERBOARD_CLASSIC = "leaderboard_classic";
    public static final String PREF_LEADERBOARD_ZEN     = "leaderboard_zen";
    public static final String PREF_LEADERBOARD_ARCADE  = "leaderboard_arcade";
    public static final String PREF_TOTAL_FRUITS_SLICED = "total_fruits_sliced";
    public static final String PREF_TOTAL_BOMBS_HIT     = "total_bombs_hit";
}
```

---

## 7. Data Persistence

All data in `Gdx.app.getPreferences(Constants.PREFS_NAME)`.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `tutorial_shown` | boolean | false | Set true after TutorialScreen skip or "Got it!" |
| `sfx_enabled` | boolean | true | SFX playback on/off |
| `music_enabled` | boolean | true | Music playback on/off |
| `haptic_enabled` | boolean | true | Vibration on/off |
| `highscore_classic` | integer | 0 | All-time best Classic score |
| `highscore_zen` | integer | 0 | All-time best Zen score |
| `highscore_arcade` | integer | 0 | All-time best Arcade score |
| `leaderboard_classic` | string | "[]" | JSON array of `{score:int, date:string}`, max 10, sorted desc |
| `leaderboard_zen` | string | "[]" | Same for Zen |
| `leaderboard_arcade` | string | "[]" | Same for Arcade |
| `total_fruits_sliced` | integer | 0 | Lifetime counter |
| `total_bombs_hit` | integer | 0 | Lifetime counter |

**Score save flow:**  
`ScoreManager.saveScore(prefs, mode, score)` →  
1. Parse existing JSON array for mode.  
2. Append `{score, date}` entry (date = `new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())`).  
3. Sort descending by score.  
4. Trim to 10 entries.  
5. Re-serialize to JSON and save.  
6. Update highscore key if `score > current`.  
7. `prefs.flush()`.

---

## 8. Audio Management

```java
// In FruitSlashGame, after AssetManager.finishLoading():
Music menuMusic  = assets.get("sounds/music_menu.ogg", Music.class);
Music gameMusic  = assets.get("sounds/music_game.ogg", Music.class);

Sound sfxSlice   = assets.get("sounds/sfx_slice.ogg",   Sound.class);
Sound sfxBomb    = assets.get("sounds/sfx_bomb.ogg",    Sound.class);
Sound sfxCombo   = assets.get("sounds/sfx_combo.ogg",   Sound.class);
Sound sfxPowerup = assets.get("sounds/sfx_powerup.ogg", Sound.class);
Sound sfxMiss    = assets.get("sounds/sfx_miss.ogg",    Sound.class);
Sound sfxGameover= assets.get("sounds/sfx_gameover.ogg",Sound.class);
Sound sfxButton  = assets.get("sounds/sfx_button.ogg",  Sound.class);
```

- `Music.setLooping(true)` for both music tracks.
- Check `prefs.getBoolean(PREF_MUSIC_ENABLED)` before `music.play()`.
- Check `prefs.getBoolean(PREF_SFX_ENABLED)` before `sound.play()`.
- Swap music tracks when transitioning menu↔game: stop old, play new.

---

## 9. Haptic

```java
// In AndroidLauncher, expose via interface:
interface HapticInterface { void vibrate(int ms); }

// In FruitSlashGame, store HapticInterface instance.
// Call vibrate(30) on: fruit sliced, bomb hit (50 ms).
// Guard with prefs.getBoolean(PREF_HAPTIC_ENABLED).
// On desktop/headless: no-op implementation.
```

Implement in `AndroidLauncher`:
```java
@Override public void vibrate(int ms) {
    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    if (v != null) v.vibrate(ms);
}
```

---

## 10. Rendering Order (per frame, each GameScreen)

1. Clear screen (brown wood color `#5C3A1E` as fallback if no background texture).
2. `batch.begin()` → draw background texture (full 360×640).
3. Draw `FruitHalf` objects (below active objects).
4. Draw active `GameObject` list (fruits, bombs, power-ups) with rotation.
5. Draw `JuiceParticle` list.
6. Draw `StarParticle` list.
7. `batch.end()`
8. `shapeRenderer` → draw `SwipeTrail` (GL_BLEND, additive or standard alpha).
9. `batch.begin()` → draw HUD (hearts, score, timer).
10. If `bombFlashActive` → draw semi-transparent red rect over full screen (`shapeRenderer` or red 1×1 texture tinted).
11. If `comboOverlayVisible` → draw combo overlay panel.
12. If `paused` → draw pause overlay.
13. `batch.end()`

---

## 11. Swipe-to-Slice Algorithm

```
touchDown(x, y):
    trail.reset(); trail.addPoint(x, y); trail.active = true
    fruitsSlicedThisSwipe = 0

touchDragged(x, y):
    trail.addPoint(x, y)
    for each obj in objects (not already sliced):
        if trail.intersects(obj):
            sliceObject(obj, x, y)
            if obj is FruitObject: fruitsSlicedThisSwipe++

touchUp(x, y):
    trail.active = false
    // Combo logic:
    if fruitsSlicedThisSwipe >= 2:
        comboCount++
        comboMultiplier = min(comboCount + 1, COMBO_MAX_MULTIPLIER)
        if comboMultiplier >= COMBO_OVERLAY_THRESHOLD:
            showComboOverlay(comboMultiplier, fruitsSlicedThisSwipe, lastSwipePoints)
    else if fruitsSlicedThisSwipe == 1:
        comboCount = 0
        comboMultiplier = 1
    // fruitsSlicedThisSwipe == 0: combo unchanged (miss doesn't break combo)
    fruitsSlicedThisSwipe = 0
```

Line-segment vs circle intersection (for each consecutive pair of trail points P1, P2):
```
d = P2 - P1
f = P1 - center
a = d·d; b = 2f·d; c = f·f - r²
discriminant = b²-4ac
if discriminant >= 0: intersection exists
```

---

## 12. Difficulty Ramp Logic

### Classic

Called in `updateDifficulty()` after each score change:

```java
if (score < 200)      { spawnInterval = 1.2f; speedMul = 1.0f; bombRatio = 0; }
else if (score < 500) { spawnInterval = 1.0f; speedMul = 1.1f; bombRatio = 6; }
else if (score < 1000){ spawnInterval = 0.8f; speedMul = 1.2f; bombRatio = 5; }
else if (score < 2000){ spawnInterval = 0.6f; speedMul = 1.35f;bombRatio = 4; }
else                  { spawnInterval = 0.4f; speedMul = 1.5f; bombRatio = 4; }
// bombRatio: spawn 1 bomb per N fruits (0 = never); only active if score >= BOMB_START_SCORE (100)
```

### Arcade

Time-based ramp in `update()`:
```java
arcadeRampTimer += delta;
if (arcadeRampTimer >= ARCADE_RAMP_INTERVAL) {
    arcadeRampTimer = 0;
    spawnInterval = Math.max(ARCADE_SPAWN_MIN, spawnInterval - ARCADE_RAMP_STEP);
}
```

---

## 13. Fruit Spawn Logic

```java
void spawnFruit() {
    // Pick FruitType by weighted random
    FruitType type = weightedRandom(FruitType.values());

    // Spawn point: random x in [20, 340], y = -20 (just below screen)
    float x = MathUtils.random(20, 340);
    float y = -20;

    // Arc velocity
    float angle = MathUtils.random(FRUIT_ANGLE_MIN, FRUIT_ANGLE_MAX); // degrees
    float speed = MathUtils.random(FRUIT_SPEED_MIN, FRUIT_SPEED_MAX) * speedMultiplier;
    float vx = MathUtils.cosDeg(angle) * speed;
    float vy = MathUtils.sinDeg(angle) * speed;

    FruitObject fruit = new FruitObject(type, x, y, vx, vy);
    objects.add(fruit);
}

void spawnBomb() {
    // Same arc logic; uses bomb.png
    BombObject bomb = new BombObject(x, y, vx, vy);
    objects.add(bomb);
}

// Spawn decision (called from update when spawnTimer <= 0):
spawnTimer = spawnInterval;
if (bombRatio > 0 && score >= BOMB_START_SCORE && MathUtils.random(1, bombRatio) == 1):
    spawnBomb()
else:
    spawnFruit()
```

---

## 14. HUD Layouts

### ClassicGameScreen HUD
```
[♥ ♥ ♥]  (top-left, x=10, y=620; each heart 32×32, gap 8px)
SCORE: 0   (top-center, x=180, y=625, font2Medium, center-align)
[⏸]       (top-right, x=330, y=620, 32×32)
```

### ZenGameScreen HUD
```
Circular arc timer (center x=180, y=605, radius 28px)
  - full circle = 90 s; arc depletes clockwise; white outline, cyan fill
SCORE: 0   (top-right, x=340, y=625, font2Medium, right-align)
```

### ArcadeGameScreen HUD
```
[♥ ♥ ♥]   (top-left, x=10, y=620)
Timer bar   (top-center, x=90 to x=270, y=618, height 12px)
  - full=60s; color: >30s green, 15-30s yellow, <15s red
SCORE      (top-right, x=340, y=630, font2Small, right-align)
[PU_ICON]  (top-right, x=330, y=608, 24×24, only if power-up active)
```

---

## 15. Pause Overlay

Drawn over gameplay when `paused == true`. Fruits freeze (stop updating).

```
Semi-transparent black rect: full screen, alpha 0.6
Panel: centered 240×160, rounded rect, dark brown
  [RESUME]   button
  [RESTART]  button → restart same mode (re-create screen or reset state)
  [MENU]     button → MainMenuScreen
```

---

## 16. ModeSelectScreen Card Layout

```
Back arrow (top-left, 32×32, ic_back.png)
Title "SELECT MODE" (top-center, font1Medium)

Card 1 — Classic (y ≈ 430, height 120)
  Left: icon_classic.png 64×64
  Right: "CLASSIC" font1Small bold; "Slice fruits. Lose a life for every miss or bomb." font2Small

Card 2 — Zen (y ≈ 290)
  Left: icon_zen.png
  Right: "ZEN" / "90 seconds of pure slicing. No bombs."

Card 3 — Arcade (y ≈ 150)
  Left: icon_arcade.png
  Right: "ARCADE" / "60 seconds. Power-ups. Maximum chaos."

Cards: rounded rect, semi-transparent warm beige (#F5DEB3 at 0.85 alpha), touch feedback (darken).
```

---

## 17. SliceResultScreen Layout

```
Background: bg_result.png (full screen)
Mode label: "CLASSIC" / "ZEN" / "ARCADE" (font2Medium, top-center, y=580)
"SCORE" label (font2Small, center, y=490)
Score number (font1Large, center, y=450; animates count-up)
ic_new_best.png (64×64, overlaid top-right of score, only if isNewBest)

Stats row (y=350):
  "FRUITS" / fruitsSliced  |  "BEST COMBO" / bestCombo  |  "BOMBS HIT" / bombsHit
  (font2Small label, font2Medium value, spaced in thirds of 360px width)

Buttons (centered, stacked, y=240/170/100):
  [PLAY AGAIN]  (primary)
  [MENU]
  [LEADERBOARD]
```

---

## 18. Java File List

All files in `core/src/main/java/com/factory/template/`:

```
FruitSlashGame.java
Constants.java
GameMode.java
FruitType.java
PowerUpType.java
ScoreManager.java
LeaderboardEntry.java
HapticInterface.java

gameobject/
  GameObject.java
  FruitObject.java
  FruitHalf.java
  BombObject.java
  PowerUpObject.java
  JuiceParticle.java
  StarParticle.java

input/
  SwipeTrail.java

screen/
  MainMenuScreen.java
  TutorialScreen.java
  ModeSelectScreen.java
  BaseGameScreen.java
  ClassicGameScreen.java
  ZenGameScreen.java
  ArcadeGameScreen.java
  SliceResultScreen.java
  LeaderboardScreen.java
  SettingsScreen.java
```

Android launcher: `android/src/main/java/com/factory/template/android/AndroidLauncher.java`  
Implements `HapticInterface`.

---

## 19. Out of Scope

- Online leaderboards (Firebase, Play Games).
- Multiplayer.
- IAP, ads, rewarded video.
- Unlockable skins or themes.
- Level editor.
- Tablet / landscape layouts.
- Achievements system.
- Social score sharing.
- Replay recording / playback.
- Complex haptic patterns (only single `vibrate(ms)` calls).
- Analytics or crash reporting.
</parameter>
</invoke>
</function_calls>

`GAME_SPEC.md` written to `projects/game-900-fruit-slash/GAME_SPEC.md`.

Key decisions made in the spec:

- **`BaseGameScreen`** abstracts shared state (objects, particles, trail, combo, power-up timers) so Classic/Zen/Arcade only override `drawHUD()` and mode-specific end conditions — avoids duplicated game loop code.
- **`SwipeTrail`** owns intersection math (line-segment vs circle); called once per `touchDragged`, not per-frame scan — keeps input latency low.
- **Combo tracking** lives on `touchUp`: count fruits cut in the swipe, then decide combo increment/reset there, not during the drag.
- **`ScoreManager`** is a static helper so any screen can save scores without holding a reference to the game object.
- **`HapticInterface`** pattern bridges core→android without a platform dependency in core.
- **Fallback strategy** for missing assets uses `Pixmap`-generated colored squares rather than crashing — important since the pipeline copies sprites from CATALOG packs and may not have exact filenames matching the GDD.
- All magic numbers centralised in `Constants.java` with exact values from the GDD (gravity 400 px/s², trail fade 0.2 s, etc.).