# CLAUDE.md — Global Rules for libGDX Android Games

> **READ THIS FIRST.** Before writing any code, read `GAME_SPEC.md` and `GDD.md`
> in this project root. All rules here apply to every game. Do not deviate.

---

## 1. Tech Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Framework  | libGDX 1.14.0                     |
| Language   | Java 17                           |
| Build      | Gradle wrapper (./gradlew only)   |
| UI         | Scene2D exclusively               |
| Assets     | AssetManager exclusively          |
| Target     | Android only                      |

---

## 2. Exact Project Structure

```
project-root/
├── CLAUDE.md
├── GAME_SPEC.md
├── GDD.md
├── assets/                          ← all game assets go here
│   ├── backgrounds/
│   ├── fonts/                       ← Roboto-Regular.ttf (fallback) + 2 game-specific fonts pre-copied by pipeline
│   ├── sounds/                      ← music + sfx .ogg files (pre-copied by pipeline)
│   ├── ui/
│   └── sprites/                     ← curated pixel-art sprite packs (craftpix, pre-copied by pipeline)
│       └── (subdirs: character/ tileset/ enemy/ vehicle/ object/ ui/)
├── core/
│   └── src/main/java/com/factory/GAME_SLUG/
│       ├── MainGame.java            ← extends Game
│       ├── Constants.java           ← ALL magic numbers
│       └── screens/
│           ├── MainMenuScreen.java
│           ├── SettingsScreen.java
│           ├── GameOverScreen.java
│           ├── LeaderboardScreen.java
│           └── ... (all other screens from GAME_SPEC)
├── android/
│   └── src/main/java/com/factory/GAME_SLUG/android/
│       └── AndroidLauncher.java     ← entry point only, do not modify
└── build.gradle
```

**IMPORTANT:** Replace `GAME_SLUG` with the actual game slug from GAME_SPEC.
Example: if slug is `brick-smash`, package is `com.asocity.bricksmash123456`
(The 6-digit suffix is appended by the pipeline — use whatever packageName is in GAME_SPEC.)
Java files go in: `core/src/main/java/com/factory/bricksmash/`

---

## 3. Screen Requirements — MANDATORY

Every game MUST have **between 8 and 10 screens**. Hard requirement.

### Always required:
| Screen | Purpose |
|--------|---------|
| `MainMenuScreen` | Title, play button, navigation hub |
| `SettingsScreen` | Music toggle, SFX toggle |
| `GameOverScreen` | Score, personal best, retry, menu |
| `LeaderboardScreen` | Local top 10 — SharedPreferences |

### Fill remaining 4-6 from GAME_SPEC screen list.

### Screen rules:
- Every screen is a separate Java class implementing `com.badlogic.gdx.Screen`
- Constructor receives the main `Game` instance
- Navigate via `game.setScreen(new XxxScreen(game))`
- Dispose old screen after setting new one

---

## 4. Physics Constants — MANDATORY

### Hold-to-fly / jetpack mechanic
If the game uses a "hold screen to fly/rise" mechanic (jetpack, flappy, balloon, rocket):
- `JETPACK_THRUST` (or equivalent) **MUST be at least 2× |GRAVITY|**
- With additive physics both forces apply every frame: net = THRUST + GRAVITY
- If THRUST < |GRAVITY| the player can never rise — game is unplayable

```java
// CORRECT — thrust overcomes gravity, net force is upward when holding
public static final float GRAVITY        = -900f;
public static final float JETPACK_THRUST = 1800f; // 2× gravity → net +900 upward
public static final float MAX_FALL_SPEED = -700f;

// WRONG — thrust less than gravity, player always falls even when holding
public static final float GRAVITY        = -900f;
public static final float JETPACK_THRUST =  600f; // net -300 → never rises
```

### Touch input in GameScreen with a HUD Stage
When a HUD `Stage` is in the `InputMultiplexer`, always poll input via `Gdx.input.isTouched()`
for game physics — never rely on Stage touch events for continuous hold detection.

```java
// CORRECT — polling works regardless of whether Stage consumed the event
boolean thrusting = Gdx.input.isTouched();

// WRONG — Stage touchDown fires once, not continuously while held
```

---

## 5. Sprites — MANDATORY

### How sprites work
The pipeline copies curated pixel-art sprite packs (craftpix) into type-based subdirectories
under `assets/sprites/` before Claude runs. `assets/ASSETS_MANIFEST.json` lists every pack selected.

**Before writing ANY game object code:**
1. Read `assets/ASSETS_MANIFEST.json` — it is always present
2. `ls` each subdirectory listed in the manifest to discover actual filenames — **never hardcode filenames**
3. Never use plain colored rectangles for game objects when sprites are available
4. Never use plain `TextButton` — buttons are drawn programmatically via `UiFactory.drawButton()`

### Sprite subdirectories (archetype-dependent)
The exact subdirectories present depend on the game archetype. Always check the manifest first.

| Subdirectory | Asset type | How to discover files |
|---|---|---|
| `sprites/character/` | Player / hero sprites | `ls assets/sprites/character/` |
| `sprites/tileset/` | Level tiles, platforms, floors | `ls assets/sprites/tileset/` |
| `sprites/enemy/` | Enemy sprites | `ls assets/sprites/enemy/` |
| `sprites/vehicle/` | Ships, cars, planes | `ls assets/sprites/vehicle/` |
| `sprites/object/` | Collectibles, projectiles, props | `ls assets/sprites/object/` |
| `sprites/ui/` | UI elements, icons (always present) | `ls assets/sprites/ui/` |
| `backgrounds/menu/` | Menu background images | `ls assets/backgrounds/menu/` |
| `backgrounds/game/` | Gameplay background images | `ls assets/backgrounds/game/` |

**Never hardcode a sprite filename.** Always `ls` the directory first, then use the filenames you find.

### Loading sprites with AssetManager

**Pattern: always `ls` the directory first, then load the filenames you find. Never invent filenames.**

```java
// 1. ls assets/sprites/character/  -> discover filenames, e.g. "hero_idle.png", "hero_walk1.png"
// 2. In your loading screen:
manager.load("sprites/character/hero_idle.png",  Texture.class);
manager.load("sprites/character/hero_walk1.png", Texture.class);
manager.load("sprites/tileset/ground_tile.png",  Texture.class);

// 3. In your screen (after finishLoading):
Texture playerTex = manager.get("sprites/character/hero_idle.png", Texture.class);
```

### Animation example (walk cycle)

```java
// Declare
private Animation<TextureRegion> walkAnim;
private float stateTime = 0f;

// In constructor (after assets loaded) — use filenames discovered via ls:
TextureRegion[] frames = {
    new TextureRegion(manager.get("sprites/character/hero_walk1.png", Texture.class)),
    new TextureRegion(manager.get("sprites/character/hero_walk2.png", Texture.class))
};
walkAnim = new Animation<>(0.15f, frames);

// In render():
stateTime += delta;
TextureRegion frame = walkAnim.getKeyFrame(stateTime, true);
batch.draw(frame, x, y, width, height);
```

### UI button rules — MANDATORY
Every screen uses sprite-backed TextButtons via `UiFactory`. Never use ShapeRenderer for buttons.

**Button sprites are pre-copied to `assets/ui/buttons/`** by the pipeline. Always run:
```bash
ls assets/ui/buttons/
```
You will find: `button_rectangle_depth_gradient.png` (normal), `button_rectangle_depth_flat.png` (pressed),
`button_round_depth_gradient.png` (round normal), `button_round_depth_flat.png` (round pressed),
`star.png`, `star_outline.png`.

**Agent1 creates `UiFactory.java`** with these methods — ALL screens call them, never roll their own:

```java
public class UiFactory {
    // Call once from MainGame.create() or first screen show()
    public static TextButton.TextButtonStyle makeRectStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = font;
        s.up   = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_rectangle_depth_gradient.png", Texture.class)));
        s.down = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_rectangle_depth_flat.png", Texture.class)));
        return s;
    }

    public static TextButton.TextButtonStyle makeRoundStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = font;
        s.up   = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_round_depth_gradient.png", Texture.class)));
        s.down = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_round_depth_flat.png", Texture.class)));
        return s;
    }

    // Convenience — create and size a rectangle button in one call
    public static TextButton makeButton(String label, TextButton.TextButtonStyle style, float w, float h) {
        TextButton btn = new TextButton(label, style);
        btn.setSize(w, h);
        return btn;
    }
}
```

**Usage in every Screen:**
```java
// In show() — load button textures (add to AssetManager in advance)
if (!game.manager.isLoaded("ui/buttons/button_rectangle_depth_gradient.png")) {
    game.manager.load("ui/buttons/button_rectangle_depth_gradient.png", Texture.class);
    game.manager.load("ui/buttons/button_rectangle_depth_flat.png", Texture.class);
    game.manager.finishLoading();
}
TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, bodyFont);
TextButton playBtn = UiFactory.makeButton("PLAY", btnStyle, 280, 56);
playBtn.setPosition((Constants.WORLD_WIDTH - 280) / 2f, 378f);
playBtn.addListener(new ChangeListener() {
    @Override public void changed(ChangeEvent e, Actor a) { game.setScreen(new GameScreen(game)); }
});
stage.addActor(playBtn);
```

Standard button sizes: main `280x56` · secondary `220x50` · small `160x44` · round icon `56x56`

### Button color tinting — MANDATORY
Button PNGs are white/gray by default. **Always tint them** to match the game palette using `setColor()`.
Add `COLOR_PRIMARY`, `COLOR_ACCENT`, `COLOR_BG` as hex-string constants in `Constants.java`, then apply:
```java
// In Constants.java:
public static final String COLOR_PRIMARY = "#F5F5F5"; // from pipeline idea colors
public static final String COLOR_ACCENT  = "#E53935";
public static final String COLOR_BG      = "#2D2D2D";

// When creating buttons — tint to match game theme:
TextButton playBtn = UiFactory.makeButton("PLAY", btnStyle, 280, 56);
playBtn.setColor(Color.valueOf(Constants.COLOR_PRIMARY)); // tint button texture
playBtn.getLabel().setColor(Color.valueOf(Constants.COLOR_BG)); // contrast text
```
Use `COLOR_PRIMARY` for main action buttons, `COLOR_ACCENT` for danger/confirm buttons.
This ensures every game's buttons visually match its color palette.

**Settings toggle on/off state — never use hardcoded green/red:**
```java
// WRONG — hardcoded green/red:
// COLOR_ON = new Color(0.3f, 0.8f, 0.3f, 1f); // ❌
// COLOR_OFF = new Color(0.7f, 0.2f, 0.2f, 1f); // ❌

// CORRECT — use game palette:
Color toggleOn  = Color.valueOf(Constants.COLOR_PRIMARY); // active = game accent
Color toggleOff = new Color(0.35f, 0.35f, 0.35f, 1f);   // inactive = neutral gray
btn.setColor(isOn ? toggleOn : toggleOff);
```

### Settings screen — icons for toggles
`assets/sprites/ui/` always contains icon PNGs. Run `ls assets/sprites/ui/` to see what is available.
Use the closest match for music-on/off and sfx-on/off toggles. If suitable icon PNGs exist:

```java
// ls assets/sprites/ui/ first — then use the actual filenames you find:
ImageButton musicBtn = new ImageButton(
    new TextureRegionDrawable(manager.get("sprites/ui/<music_on_icon>.png",  Texture.class)),
    new TextureRegionDrawable(manager.get("sprites/ui/<music_off_icon>.png", Texture.class))
);
```
If no suitable icon PNG is found, draw the toggle as a labelled `UiFactory.drawButton()` button.

### Settings button — gear icon vs text
A gear icon is **permanently in the template** at `assets/ui/icons/settings_gear.png`.

**Rule:** use the gear icon ONLY when the settings button is a small round/square icon (corner of screen, HUD overlay, etc.).
When settings appears as a full-width text button in a menu list alongside "PLAY", "LEADERBOARD" etc., use a normal `TextButton` with label `"SETTINGS"`.

```java
// Small icon button (corner/HUD) — USE GEAR IMAGE:
Texture gearTex = game.manager.get("ui/icons/settings_gear.png", Texture.class);
ImageButton settingsBtn = new ImageButton(new TextureRegionDrawable(gearTex));
settingsBtn.setSize(56f, 56f);

// Full menu list button — USE TEXT:
TextButton settingsBtn = UiFactory.makeButton("SETTINGS", btnStyle, 280, 56); // ✓
// WRONG — never use a single letter as label:
// UiFactory.makeButton("S", roundStyle, 60f, 60f); // ❌
```
Add `game.manager.load("ui/icons/settings_gear.png", Texture.class)` to your AssetManager loading list.

### HUD icons — MANDATORY source
**Never reference a sprite path without checking `assets/ASSETS_MANIFEST.json` first.**
Only use directories that appear in the manifest. `assets/sprites/ui/` is always present.

Run `ls assets/sprites/ui/` to discover available icon files. Use the closest match for
hearts, stars, coins, timers, settings, etc. **Never assume specific filenames — always ls first.**

**Never** create an `assets/icons/` folder — it is redundant and will be ignored.

### Batch color reset — MANDATORY
LibGDX `SpriteBatch` retains its color between frames. Tinted actors (buttons, sprites) leave the batch dirty. **Always reset before drawing backgrounds:**
```java
// In every screen render() — BEFORE game.batch.begin():
game.batch.setColor(Color.WHITE);
game.batch.begin();
game.batch.draw(bgTexture, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
game.batch.end();
// then stage.draw() after
```
Skipping this causes backgrounds to inherit tint from the previous frame's button rendering.

Game-specific power-up icons (e.g. `ui/power_up_laser_icon.png` from Figma) may be loaded
from `assets/ui/` — those are Figma exports and are game-specific, **not** generic HUD icons.

```java
// WRONG — hardcoding filenames without ls check first:
manager.load("sprites/ui/icon_heart.png", Texture.class);  // may not exist in this pack

// CORRECT — ls first, then load what you actually find:
manager.load("sprites/ui/<actual_icon_filename>.png", Texture.class);
```

### knife-hit archetype — specific sprite rules
For the `knife-hit` archetype the pipeline copies 3 sprite folders:
- `assets/sprites/object/` — log/board/target objects
- `assets/sprites/weapon/` — blade/knife/stave sprites (use these for the throwable blade)
- `assets/sprites/collectible/` — food/fruit sprites including `apple.png` for the bonus collectible

**Throwable blade:** Load a sprite from `assets/sprites/weapon/` — pick the most knife/blade-like PNG (run `ls assets/sprites/weapon/`). Render as `Sprite`, NOT ShapeRenderer sticks:
```java
Texture bladeTex = game.manager.get("sprites/weapon/<blade_filename>.png", Texture.class);
Sprite bladeSprite = new Sprite(bladeTex);
bladeSprite.setSize(12f, 48f); // tall thin proportions for a knife
bladeSprite.setOriginCenter(); // rotate around center when stuck in log
```

**Apple bonus collectible:** Load from `assets/sprites/collectible/` — find the apple PNG (run `ls assets/sprites/collectible/`). **Never use `coinGold.png` for an apple.** Sound must be `sfx_collect.ogg`, NOT `sfx_coin.ogg`:
```java
private static final String APPLE_TEX = "sprites/collectible/apple.png"; // ls to confirm filename
// sound: playSound("sounds/sfx/sfx_collect.ogg", 1.0f);
```

### Other important sprite rules
- Load sprites via `AssetManager` — zero `new Texture("sprites/...")` calls
- Scale sprites to world units (not pixel size): typical player is 64×64 world units
- If no suitable sprite is found for a game element, draw it with `ShapeRenderer` as fallback

---

## 6. Fonts — MANDATORY

The pipeline pre-copies two TTF fonts into `assets/fonts/` before Claude runs.

### Assigned fonts — READ THIS FIRST

> **The pipeline has already selected the two fonts for YOUR game.**
> Look at the **ASSIGNED FONTS** section in the task prompt you received.
> It looks like this:
> ```
> ## ASSIGNED FONTS (use ONLY these two — do not use any other font)
>   Title font:  assets/fonts/SomeTitleFont.ttf
>   Body font:   assets/fonts/SomeBodyFont.ttf
> ```
> **Copy those filenames character-for-character. Never invent or guess a font name.**
> `Roboto-Regular.ttf` is also present as a UI fallback, but only use it if it is
> explicitly listed as your body font.

**NEVER load fonts from `assets/ui/` or any other directory — they are always in `assets/fonts/`.**

> **⛔ Wrong font filename = fatal crash.** If unsure which fonts exist, run `ls assets/fonts/` first.
> Never invent names like `Nunito-Bold.ttf` or `Poppins-Bold.ttf`. Use ONLY filenames from ASSIGNED FONTS.

### Loading fonts — FreeTypeFontGenerator pattern

Fonts are TTF files and must be converted to `BitmapFont` at runtime using `FreeTypeFontGenerator`.
Do this once in `MainGame.create()` and store the results as public fields so all screens can share them.

```java
// In MainGame — public fields
public BitmapFont fontBody;   // body/buttons — filename from ASSIGNED FONTS (Body font)
public BitmapFont fontTitle;  // titles/scores — filename from ASSIGNED FONTS (Title font)

// In MainGame.create() — generate fonts BEFORE loading other assets
// ⚠️  Replace YOUR_BODY_FONT and YOUR_TITLE_FONT with the exact filenames
//     given in "ASSIGNED FONTS" in your task prompt (e.g. "DPComic.ttf", "YosterIsland.ttf")
FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(Gdx.files.internal("fonts/YOUR_BODY_FONT"));
FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/YOUR_TITLE_FONT"));

FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

// ALWAYS add outline — without it text looks flat and unreadable on varied backgrounds
param.borderWidth = 2;                              // outline thickness
param.borderColor = new Color(0f, 0f, 0f, 0.85f);  // near-black semi-transparent outline

param.size = 28;
fontBody = bodyGen.generateFont(param);

param.size = 48;
param.borderWidth = 3;  // thicker outline for large title text
fontTitle = titleGen.generateFont(param);

bodyGen.dispose();
titleGen.dispose();
```

Generate additional sizes as needed (e.g. `param.size = 20` for small labels, `param.size = 72` for big score).
**For each size set `param.borderWidth` appropriately: 1 for sizes <=16, 2 for sizes 17-36, 3 for sizes >=37.**
Always dispose the generator immediately after generating — keep the `BitmapFont`, not the generator.

### Using fonts in button styles

```java
// CORRECT — use game.fontBody, not skin.getFont()
private TextButton.TextButtonStyle makeButtonStyle(String upFile, String downFile) {
    TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
    s.font      = game.fontBody;   // ← always reference the shared font field
    s.up        = new TextureRegionDrawable(manager.get(upFile,   Texture.class));
    s.down      = new TextureRegionDrawable(manager.get(downFile, Texture.class));
    s.fontColor = Color.WHITE;
    return s;
}

// WRONG — skin.getFont() fails if no skin is loaded
s.font = game.skin.getFont("default-font");
```

### Dispose in MainGame.dispose()

```java
@Override
public void dispose() {
    fontBody.dispose();
    fontTitle.dispose();
    manager.dispose();
}
```

### Required Gradle dependency

The `gdx-freetype` extension must be present in `core/build.gradle`:
```groovy
dependencies {
    api "com.badlogicgames.gdx:gdx-freetype:$gdxVersion"
}
```
And in `android/build.gradle`:
```groovy
natives "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a"
natives "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a"
natives "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86"
natives "com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64"
```

---

## 7. Game Mechanics Consistency — MANDATORY

### Coins / Currency — CRITICAL
If the game collects coins, gems, or any in-game currency:
- The player MUST be able to spend them → always implement a `ShopScreen`
- `ShopScreen` is reachable from `MainMenuScreen` and has a "Main Menu" back button
- Coin balance persists via SharedPreferences

If the GAME_SPEC does **not** define a shop, do **not** add coin collection.
Replace coin pickups with direct score bonuses instead:

```java
// WRONG — coins that go nowhere (no shop = meaningless mechanic)
collectibles.add(new Coin(x, y));

// CORRECT option B — no shop → score bonus instead
score += Constants.COIN_SCORE_VALUE; // direct score, no currency stored
```

### ShopScreen — MANDATORY content
Every `ShopScreen` MUST have **exactly these two categories**:

**1. Power-ups** (3 items — pure code, no extra assets needed):

| Item | Price | Effect |
|------|-------|--------|
| Shield | 20 coins | 5 seconds invincibility on next run |
| Coin Magnet | 30 coins | Auto-collects nearby coins for 10s |
| 2× Score | 50 coins | Double all points for the next run |

**2. Character Skins** (for runner/platformer/jetpack/space games):

For **runner/jetpack/platformer** games — 3 skins using the sprite variants in `assets/sprites/`:

| Skin | Price | Sprites to use |
|------|-------|---------------|
| Blue Alien (default) | free | `player_idle.png`, `player_walk1.png`, `player_walk2.png`, `player_jump.png` |
| Green Alien | 100 coins | `player_idle_green.png`, `player_walk1_green.png`, etc. |
| Pink Alien | 150 coins | `player_idle_pink.png`, `player_walk1_pink.png`, etc. |

For **racing** games — 3 car skins:

| Skin | Price | Sprite |
|------|-------|--------|
| Yellow Car (default) | free | `car_player.png` |
| Red Car | 100 coins | `car_red.png` |
| Blue Car | 150 coins | `car_blue.png` |

For **space** games — 2 ship skins:

| Skin | Price | Sprite |
|------|-------|--------|
| Blue Ship (default) | free | `player_ship.png` |
| Green Ship | 100 coins | `player_ship_alt.png` |

### Implementing skins — SharedPreferences pattern

```java
// Save selected skin index (0 = default, 1 = skin2, 2 = skin3)
Preferences prefs = Gdx.app.getPreferences("GamePrefs");
prefs.putInteger(Constants.PREF_SKIN, selectedSkinIndex);
prefs.flush();

// In GameScreen constructor — load correct player sprites based on saved skin:
int skin = Gdx.app.getPreferences("GamePrefs").getInteger(Constants.PREF_SKIN, 0);
String[] idleFrames = {
    "sprites/player_idle.png",       // skin 0 — yellow (default)
    "sprites/player_idle_green.png", // skin 1 — green
    "sprites/player_idle_pink.png"   // skin 2 — pink
};
Texture playerTex = manager.get(idleFrames[skin], Texture.class);
```

### Collision Detection — CRITICAL
Every visual element that should kill/hurt the player **MUST** have an active collision `Rectangle`.
Background decorations (ambient shapes, parallax art, hexagons, panels) must **NEVER** have collision rectangles.

**Rule: if it should kill you → it has a Rectangle. If it is decoration → no Rectangle.**

```java
// Maintain two SEPARATE lists — never mix them
private Array<Rectangle> obstacles;    // COLLIDABLE — kills player on contact
private Array<ShapeData>  decorations; // VISUAL ONLY — never collision-tested

// In update():
for (Rectangle obs : obstacles) {
    if (playerRect.overlaps(obs)) {
        triggerGameOver();
    }
}
// decorations are only drawn — never checked for collision
```

Common mistake: drawing background shapes with `ShapeRenderer` and accidentally adding
them to the obstacle list. Always ask: "Is this meant to kill the player?"
- Yes → add to `obstacles` list
- No  → draw only, never in `obstacles`

---

## 8. Navigation Rules — MANDATORY

These are hard requirements based on real bugs. Violating them causes broken UX.

### Every screen MUST have a "Main Menu" button
- No screen is a dead end
- **Every** screen — `GameScreen`, `PauseScreen`, `GameOverScreen`, `LeaderboardScreen`,
  `SettingsScreen`, `ShopScreen`, level complete — must have a clearly visible button
  that returns to `MainMenuScreen`
- `LeaderboardScreen` is the most frequently forgotten — it **always** needs a back button
- The Android system back button must also navigate to `MainMenuScreen`, never exit the app

```java
// CORRECT — handle back button on every screen
@Override
public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.BACK) {
        game.setScreen(new MainMenuScreen(game));
        return true;
    }
    return false;
}
```

### Restart / Game Over mechanics — CRITICAL
- After game over, game state MUST be fully reset before restarting
- Always create a NEW `GameScreen` instance on retry — never reuse the old one
- Ball/player/projectile must start from initial spawn position with zero velocity

```java
// CORRECT — restart creates fresh screen
retryButton.addListener(new ChangeListener() {
    public void changed(ChangeEvent event, Actor actor) {
        game.setScreen(new GameScreen(game)); // NEW instance every time
    }
});

// WRONG — reusing old state causes ball stuck, input broken
retryButton.addListener(new ChangeListener() {
    public void changed(ChangeEvent event, Actor actor) {
        game.setScreen(gameScreen); // OLD instance — never do this
    }
});
```

### Pause screen — MANDATORY

- Every game screen must have a visible pause button during gameplay
- Pause screen must have: Resume, Restart, Main Menu buttons
- Navigate to a separate `PauseScreen(game, this)` — pass the current screen instance

#### CRITICAL BUG — Pause button stops working after first use

This is the #1 most common bug. Root cause: when returning from PauseScreen, LibGDX calls
`gameScreen.show()`, but if `show()` does not re-register the input processor, the Stage stops
receiving touch events and the pause button silently does nothing on subsequent presses.

**Fix 1 — always re-register input in `show()`:**
```java
@Override
public void show() {
    Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
        @Override public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.BACK) { game.setScreen(new MainMenuScreen(game)); return true; }
            return false;
        }
    }));
}
```

**Fix 2 — if you use an in-screen pause overlay (paused boolean), NEVER gate `stage.act()`:**
```java
@Override
public void render(float delta) {
    if (!paused) {
        update(delta);          // game logic only — skip when paused
    }
    stage.act(delta);           // ALWAYS — so pause/resume buttons fire even when paused
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    // ... draw ...
    stage.draw();
}
```
The mistake `if (!paused) { stage.act(delta); update(delta); }` means stage buttons never fire
while paused — resume button does nothing, pause button does nothing after first press.

**Fix 3 — PauseScreen resume button must go back to the original instance:**
```java
// PauseScreen constructor receives the game screen
public PauseScreen(MainGame game, Screen previousScreen) {
    this.previousScreen = previousScreen;
}
// Resume button:
resumeButton.addListener(new ChangeListener() {
    public void changed(ChangeEvent event, Actor actor) {
        game.setScreen(previousScreen); // same instance — calls previousScreen.show()
    }
});
```

---

## 9. Viewport — MANDATORY — No Black Bars

Every screen MUST use `StretchViewport` to fill the entire screen with no black bars.

```java
// In every screen — declare fields
private OrthographicCamera camera;
private Viewport viewport;
private Stage stage;

// In constructor
camera = new OrthographicCamera();
viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
stage = new Stage(viewport, game.batch);
Gdx.input.setInputProcessor(stage);

// resize() — MANDATORY in every screen
@Override
public void resize(int width, int height) {
    viewport.update(width, height, true);
}
```

Define in `Constants.java`:
```java
public static final float WORLD_WIDTH  = 480f;
public static final float WORLD_HEIGHT = 854f;  // 16:9 portrait
```

All game coordinates use world units (480x854), never raw pixel values.

---

## 10. Location / World Variants

If the game has multiple environments:
- Each gets its own `GameScreen` subclass
- Each has a unique background asset
- Each has at least one unique obstacle/hazard type
- Share base logic via a parent class (e.g. `BaseGameScreen extends ScreenAdapter`)

---

## 11. AndroidLauncher.java

Create `android/src/main/java/com/factory/GAME_SLUG/android/AndroidLauncher.java`
(replace `GAME_SLUG` with the actual slug, dots removed — same as the package name).

```java
package com.asocity.GAME_SLUG.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.asocity.GAME_SLUG.MainGame;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;
        initialize(new MainGame(), configuration);
    }
}
```

The applicationId in `android/build.gradle` must match the package name exactly.
Set it to the `packageName` from GAME_SPEC (format: `com.asocity.{slug}{6digits}`).

---

## 12. Audio — MANDATORY

Every game MUST have background music and sound effects. They are pre-copied to
`assets/sounds/` by the pipeline. Read `assets/sounds/SOUNDS.md` for the full file list.

### Fields in MainGame (shared state)

```java
public boolean musicEnabled = true;
public boolean sfxEnabled   = true;
public Music   currentMusic = null;
```

### Loading (in LoadingScreen or MainGame.create)

```java
// Music — exactly 3 files per game (pre-selected by pipeline for this archetype):
//   music_menu.ogg      — title/menu screen (loops)
//   music_gameplay.ogg  — in-game music (loops); style varies by archetype
//   music_game_over.ogg — game over jingle (plays once)
// NEVER reference any other music filename — only these 3 exist.
manager.load("sounds/music/music_menu.ogg",      Music.class);
manager.load("sounds/music/music_gameplay.ogg",  Music.class);
manager.load("sounds/music/music_game_over.ogg", Music.class);

// SFX — use Sound class (buffered)
manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
manager.load("sounds/sfx/sfx_coin.ogg",           Sound.class);
manager.load("sounds/sfx/sfx_jump.ogg",           Sound.class);
manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);
manager.load("sounds/sfx/sfx_shoot.ogg",          Sound.class);
```

### Playing music — helpers in MainGame

```java
// For menu and gameplay screens — loops forever
// CRITICAL: check if same track is already playing — do NOT restart it.
// This prevents music restarting from the beginning when navigating to
// SettingsScreen, LeaderboardScreen, ShopScreen, etc. that share the same track.
public void playMusic(String path) {
    Music requested = manager.get(path, Music.class);
    if (requested == currentMusic && currentMusic.isPlaying()) return; // already playing — do nothing
    if (currentMusic != null) currentMusic.stop();
    currentMusic = requested;
    currentMusic.setLooping(true);
    currentMusic.setVolume(0.7f);
    if (musicEnabled) currentMusic.play();
}

// For game over screen — plays ONCE then stops (never loops)
public void playMusicOnce(String path) {
    if (currentMusic != null) currentMusic.stop();
    currentMusic = manager.get(path, Music.class);
    currentMusic.setLooping(false);  // CRITICAL: game over must NOT loop
    currentMusic.setVolume(0.7f);
    if (musicEnabled) currentMusic.play();
}
```

Usage per screen:
```java
// MainMenuScreen constructor:
game.playMusic("sounds/music/music_menu.ogg");

// GameScreen constructor (all archetypes — always use music_gameplay.ogg):
game.playMusic("sounds/music/music_gameplay.ogg");

// GameOverScreen constructor — plays ONCE, does NOT loop:
game.playMusicOnce("sounds/music/music_game_over.ogg");
```

### Playing SFX

```java
// Helper — call wherever needed:
private void playSound(String path) {
    if (game.sfxEnabled)
        game.manager.get(path, Sound.class).play(1.0f);
}

// Examples:
playSound("sounds/sfx/sfx_button_click.ogg");  // every button listener
playSound("sounds/sfx/sfx_coin.ogg");           // on coin collect
playSound("sounds/sfx/sfx_jump.ogg");           // on jump/thrust
playSound("sounds/sfx/sfx_hit.ogg");            // on player hurt
playSound("sounds/sfx/sfx_game_over.ogg");      // on game over
```

### Settings screen — music/sfx toggles MUST actually work

```java
// Load saved state in every screen constructor:
Preferences prefs = Gdx.app.getPreferences("GamePrefs");
game.musicEnabled = prefs.getBoolean(Constants.PREF_MUSIC, true);
game.sfxEnabled   = prefs.getBoolean(Constants.PREF_SFX,   true);

// Toggle listener:
musicBtn.addListener(new ChangeListener() {
    public void changed(ChangeEvent e, Actor a) {
        game.musicEnabled = !game.musicEnabled;
        prefs.putBoolean(Constants.PREF_MUSIC, game.musicEnabled);
        prefs.flush();
        if (game.currentMusic != null) {
            if (game.musicEnabled) game.currentMusic.play();
            else game.currentMusic.pause();
        }
        playSound("sounds/sfx/sfx_toggle.ogg");
    }
});
```

### Constants required

```java
public static final String PREF_MUSIC = "musicEnabled";
public static final String PREF_SFX   = "sfxEnabled";
```

### Rules
- Always use `Music` for background tracks, `Sound` for SFX — never swap them
- Always load via `AssetManager` — never `Gdx.audio.newSound()` or `Gdx.audio.newMusic()`
- Menu / gameplay music must loop: `setLooping(true)` via `playMusic()`
- **Game over music must NOT loop**: use `playMusicOnce()` — `setLooping(false)`
- Stop current music before starting new: check `currentMusic != null`
- SFX volume: `play(1.0f)` — use `play(0.5f)` for quieter effects like toggle

### Music / rhythm games — MANDATORY note sounds

For **piano-tiles, drum-hero, rhythm** or any game where the player taps lanes in time with music,
each lane tap **must play a distinct musical note sound** — never a generic hit/click SFX.

Available musical note sets in `sounds/sfx/`:
- `sfx_jingle_hit_00.ogg` … `sfx_jingle_hit_16.ogg` — melodic hits (use for rock/pop lanes)
- `sfx_jingle_pizz_00.ogg` … `sfx_jingle_pizz_16.ogg` — pizzicato notes (use for jazz lanes)
- `sfx_jingle_8bit_00.ogg` … `sfx_jingle_8bit_16.ogg` — 8-bit tones (use for retro/chiptune lanes)

Pattern — assign one note per lane so each column sounds different:
```java
// 4-lane example: each lane plays a different note from the set
private static final String[] LANE_SOUNDS = {
    "sounds/sfx/sfx_jingle_hit_00.ogg",
    "sounds/sfx/sfx_jingle_hit_04.ogg",
    "sounds/sfx/sfx_jingle_hit_08.ogg",
    "sounds/sfx/sfx_jingle_hit_12.ogg"
};

// On successful hit:
game.manager.get(LANE_SOUNDS[lane], Sound.class).play(1.0f);
```

- Load all used note sounds in `loadAssets()` via `AssetManager`
- **Never** use `sfx_hit.ogg`, `sfx_click.ogg`, or `sfx_bong.ogg` as the tap sound in a rhythm game

---

## 13. Assets

```java
// CORRECT
manager.load("backgrounds/bg_desert.png", Texture.class);
manager.finishLoading();
Texture bg = manager.get("backgrounds/bg_desert.png", Texture.class);

// WRONG — never do this
Texture bg = new Texture("backgrounds/bg_desert.png");
```

- All filenames exactly as in GAME_SPEC
- If AssetManager owns it, use `manager.unload()` not `texture.dispose()`

### ⚠️ MANDATORY: Every screen must render a background image

Every screen (menu, gameplay, game over, pause, etc.) **MUST** load and draw a background image.
**Never** leave a screen with only `glClearColor` — that produces an ugly solid-colour screen.

```java
// In constructor — pick the first file from ls assets/backgrounds/menu/ or backgrounds/game/
private static final String BG = "backgrounds/menu/YOUR_BG_FILE.png"; // exact filename from ls

// Load in constructor (after manager is ready):
if (!game.manager.isLoaded(BG)) {
    game.manager.load(BG, Texture.class);
    game.manager.finishLoading();
}

// At the TOP of render(), before anything else:
game.batch.begin();
game.batch.draw(game.manager.get(BG, Texture.class),
        0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
game.batch.end();

// Then draw UI / game elements on top
```

Rules:
- Menu screens → use a file from `assets/backgrounds/menu/`
- Game screens → use a file from `assets/backgrounds/game/`
- Game over / pause screens → reuse the menu background or game background
- Always `ls` the directory first — never invent filenames
- Stretch the image to fill the full world (0, 0, WORLD_WIDTH, WORLD_HEIGHT)
- The `glClearColor` call can remain but set it to black `(0,0,0,1)` as a safe fallback

### ⚠️ MANDATORY: Use generated UI screen images as backgrounds

Every game has pre-generated PNG backgrounds for each UI screen (AI image pipeline output).
Before writing any Screen class, run:
```bash
cat IMAGES_MANIFEST.json
```
The `generated` array lists image paths (relative to `assets/`). Load them as screen backgrounds.

**TIP:** Run `/check-ui-positions` to scan all `assets/ui/*.png` files and get
exact panel positions — use these instead of estimating from FIGMA_BRIEF.

**IMPORTANT: Images are BACKGROUNDS ONLY — no buttons or text are baked into them.**
The AI generates rich atmospheric art with decorative empty panel frames.
ALL buttons, labels, titles, and text are drawn by LibGDX code on top.

**Mapping rule** — match filename to screen class:
- `ui/main_menu.png` → `MainMenuScreen`
- `ui/kitchen_select.png` → `KitchenSelectScreen`
- `ui/game_over.png` → `GameOverScreen`

**When a generated image exists for a screen:**

```java
private static final String BG = "ui/main_menu.png";  // exact path from IMAGES_MANIFEST.json

@Override public void show() {
    if (!game.manager.isLoaded(BG)) {
        game.manager.load(BG, Texture.class);
        game.manager.finishLoading();
    }
    // ... build stage actors (buttons, labels) here
    Gdx.input.setInputProcessor(stage);
}

@Override public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    // 1. Draw background first
    game.batch.begin();
    game.batch.draw(game.manager.get(BG, Texture.class),
            0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
    game.batch.end();
    // 2. Draw stage (buttons + labels) on top
    stage.act(delta);
    stage.draw();
}
```

**All buttons and text drawn by code — use UiFactory or styled TextButton:**

```java
// Use UiFactory for styled buttons (consistent look across all screens)
TextButton playBtn = UiFactory.makeButton("PLAY", skin);
playBtn.setSize(280, 56);
playBtn.setPosition((Constants.WORLD_WIDTH - 280) / 2f, 378f);  // from FIGMA_BRIEF Button Layout
playBtn.addListener(new ChangeListener() {
    @Override public void changed(ChangeEvent e, Actor a) {
        game.setScreen(new GameScreen(game));
    }
});
stage.addActor(playBtn);

// Static title text as a Label actor
Label title = new Label("GAME TITLE", titleStyle);
title.setPosition((Constants.WORLD_WIDTH - title.getPrefWidth()) / 2f, 720f);
stage.addActor(title);
```

**⚠️ Y-coordinate conversion** — FIGMA_BRIEF Button Layout uses top-down Y (0 = top).
LibGDX uses bottom-up Y (0 = bottom). Convert every coordinate:
```
libgdxY = WORLD_HEIGHT - figmaTopY - elementHeight
// Example: figmaTopY=420, height=56, WORLD_HEIGHT=854 → libgdxY = 854 - 420 - 56 = 378
```

**Read FIGMA_BRIEF.md Button Layout section for every screen** before placing any actor.
It lists every button and label with `top-Y`, alignment, and `size=WxH`.

**Also check `ui_positions.json`** (generated by the pipeline) — it contains MEASURED pixel
positions of background panels, verified against the actual PNG images:

```
// ui_positions.json structure (libgdxY: Y=0 at bottom, increases upward)
{
  "how_to_play": {
    "panels_visual": [
      { "index": 0, "libgdxBottom": 698, "libgdxTop": 812,  // title panel
        "centerY": 755, "innerBottom": 706, "innerTop": 804 },
      { "index": 1, "libgdxBottom": 515, "libgdxTop": 663,  // rule 1
        "centerY": 589, "innerBottom": 523, "innerTop": 655 },
      ...
    ]
  }
}
```

**Rule: place labels so their top ≤ panel.innerTop and bottom ≥ panel.innerBottom.**

```java
// Example — place header near top of panel, desc below it
float innerTop    = 655f;  // from ui_positions.json panel[1].innerTop
float innerBottom = 523f;  // from ui_positions.json panel[1].innerBottom
float headerH     = 22f;   // effective font height at your fontScale

float headerY = innerTop - headerH - 5f;        // 5px gap from inner top
float descY   = innerBottom + 6f;               // 6px gap from inner bottom
float descH   = headerY - descY - 6f;           // remaining height for description
```

**If ui_positions.json is missing** or has no entry for this screen, fall back to FIGMA_BRIEF coordinates.

**If the generated image is missing** (in `failed` list or absent from `generated`),
fall back to a background from `assets/backgrounds/menu/` or `assets/backgrounds/game/`.

**NEVER** skip the background image and draw a solid colour screen when a PNG exists.
**NEVER** draw button text as static BitmapFont.draw() calls — use Stage Label/TextButton actors.

---

## 14. Constants.java

All magic numbers go here — speeds, sizes, timings, score values,
SharedPreferences keys, world dimensions. Never hardcode numbers inline.

```java
public class Constants {
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;
    // ... all other constants
}
```

---

## 15. Build

```bash
# Phase 1 — fix loop until debug passes:
./gradlew android:assembleDebug

# Phase 2 — once debug is GREEN, run release bundle:
./gradlew android:bundleRelease
```

- Zero errors, zero warnings
- Gradle wrapper only — never system gradle
- Fix ALL errors before finishing
- The `signingConfigs.release` block is already in `android/build.gradle` — do NOT modify it

---

## 16. Data Persistence

```java
Preferences prefs = Gdx.app.getPreferences("GamePrefs");
prefs.putInteger("highScore", score);
prefs.flush();
```

Keys defined in `Constants.java`. Save: high scores, unlocks, settings.

---

## 17. Reference Games

If reference games are provided in the prompt, they are at:
```
/home/kaliuzhnyi/asocity/reference/<game-folder>/
```

Study them BEFORE writing any code:
- Read their `core/src/` Java files to understand class structure
- Look at how screens are organized and navigation implemented
- Look at how `AssetManager` is loaded and disposed
- Look at how input is handled per screen
- Match their code quality, architecture, and scope

If no reference games provided — build clean, minimal implementation matching the GDD archetype.

---

## 18. Completion Checklist

- [ ] `./gradlew android:assembleDebug` → BUILD SUCCESSFUL
- [ ] `./gradlew android:bundleRelease` → BUILD SUCCESSFUL
- [ ] Exactly 8-10 screens implemented
- [ ] All screens in GAME_SPEC implemented
- [ ] All screens navigate correctly per GAME_SPEC screen flow
- [ ] Every screen has a visible "Main Menu" button
- [ ] Android back button navigates to MainMenuScreen on every screen — never exits app
- [ ] Game Over retry creates a NEW GameScreen instance — never reuses old state
- [ ] Ball/player starts from spawn position with zero velocity on restart
- [ ] Pause screen implemented with Resume / Restart / Main Menu
- [ ] Every screen uses StretchViewport — no black bars
- [ ] All assets in GAME_SPEC exist in `assets/`
- [ ] All assets loaded via AssetManager — zero `new Texture()` calls
- [ ] Fonts loaded from `assets/fonts/` — never from `assets/ui/` or anywhere else
- [ ] `game.fontBody` and `game.fontTitle` generated in `MainGame.create()` using the ASSIGNED FONTS filenames
- [ ] Button styles use `game.fontBody` — never `skin.getFont()`
- [ ] All `FreeTypeFontParameter` instances set `borderWidth` (1 for small, 2 for body, 3 for title) and `borderColor` — never generate fonts without outline
- [ ] `Constants.java` has WORLD_WIDTH, WORLD_HEIGHT and all magic numbers
- [ ] SharedPreferences saves/loads scores and settings
- [ ] No `System.out.println` — use `Gdx.app.log`
- [ ] `dispose()` on every screen
- [ ] Background music plays on every screen (menu, gameplay, game over)
- [ ] Every screen draws a background image (backgrounds/menu/ or backgrounds/game/) — never solid glClearColor only
- [ ] Rhythm/music games use sfx_jingle_hit/pizz/8bit note sounds per lane — not generic sfx_hit.ogg
- [ ] GameScreen uses `sounds/music/music_gameplay.ogg` (always — the pipeline selects the right track for this archetype)
- [ ] GameOverScreen uses `playMusicOnce()` — game over music plays once and stops, never loops
- [ ] SFX plays on button clicks, coin collect, jump, hit, game over
- [ ] Music/SFX toggles in SettingsScreen actually pause/resume audio
- [ ] `playMusic()` uses same-track guard — music does NOT restart when navigating between screens that share the same track
- [ ] `PREF_MUSIC` and `PREF_SFX` saved to SharedPreferences
- [ ] Package name matches `packageName` from GAME_SPEC (`com.asocity.{slug}{6digits}`)
- [ ] `applicationId` in `android/build.gradle` updated to match package name (com.asocity.*)
- [ ] android/res/values/strings.xml app_name updated to game title
- [ ] `assets/ASSETS_MANIFEST.json` was read before writing game object code
- [ ] Player, enemies, collectibles use sprites from `assets/sprites/` — no plain rectangles
- [ ] All buttons drawn via `UiFactory.drawButton()` — no unstyled `TextButton`
- [ ] Settings screen toggle buttons use icon PNGs from `sprites/ui/` (ls to find filenames) or UiFactory fallback
- [ ] If coins/currency collected → `ShopScreen` exists with spendable items
- [ ] If no `ShopScreen` → no coin collection; collectibles add score directly
- [ ] `LeaderboardScreen` has a visible "Main Menu" / "Back" button
- [ ] Every obstacle that can kill the player has a collision `Rectangle`
- [ ] Background decorations (ambient shapes, parallax art) have NO collision rectangles
- [ ] Collidable vs decorative objects kept in separate lists — never mixed


---

## 19. Vibration — MANDATORY when game uses haptic feedback

**Always add the VIBRATE permission to `android/AndroidManifest.xml`** before `<uses-feature>`:

```xml
<uses-permission android:name="android.permission.VIBRATE"/>
```

**Always wrap every vibrate call in try/catch** — some devices reject it even with permission:

```java
if (game.vibrationEnabled) {
    try { Gdx.input.vibrate(60); } catch (Exception e) { /* ignore */ }
}
```

Without the manifest permission `Gdx.input.vibrate()` throws `SecurityException` and crashes the game.

---

## 20. Star Counter — Use PNG Icon, Not Font Glyphs

The pipeline always copies `star.png` and `star_outline.png` to `assets/ui/buttons/`.
These are already loaded in AssetManager via `loadCoreAssets()`.

**ALWAYS use Image actor for the star icon** — never use ★ (U+2605) or any Unicode glyph,
because game fonts (pixel/retro/display) rarely contain those characters and show a broken box.

```java
// Correct pattern — PNG icon + plain number label
Image starIcon = new Image(game.manager.get("ui/buttons/star.png", Texture.class));
starIcon.setSize(26f, 26f);
starIcon.setPosition(x, y);
stage.addActor(starIcon);

Label starCountLabel = new Label(String.valueOf(starBalance), bodyStyle);
starCountLabel.setPosition(x + 30f, y);
stage.addActor(starCountLabel);

// In render / update:
starCountLabel.setText(String.valueOf(starBalance));  // plain number only
```

Apply this pattern in: GameScreen HUD, ShopScreen balance display, GameOverScreen star summary.

**IMPORTANT: always pre-load star.png in loadCoreAssets():**
```java
manager.load("ui/buttons/star.png",        Texture.class);
manager.load("ui/buttons/star_outline.png", Texture.class);
```
Without this, `game.manager.get("ui/buttons/star.png")` crashes at runtime.

**IMPORTANT: always pre-load star.png in loadCoreAssets():**

Without this,  crashes at runtime.

---



---

## 21. Snake Archetype — MANDATORY mechanics

These rules apply whenever `archetype == snake` (or any hybrid that uses snake movement).

### Smooth movement — sub-tile lerp interpolation

Snake movement MUST feel fluid, not robotic. Implement sub-tile lerp so segments
glide between cells instead of jumping.

**Required fields in GameScreen:**
```java
private float[] prevX;  // pixel X of each segment at start of last move tick
private float[] prevY;  // pixel Y of each segment at start of last move tick
private float   lerpT;  // 0 to 1 within current move interval
private static final int MAX_SNAKE = GRID_COLS * GRID_ROWS + 4;
```

**initGame() — initialise arrays:**
```java
prevX = new float[MAX_SNAKE];
prevY = new float[MAX_SNAKE];
for (int i = 0; i < snake.size; i++) {
    prevX[i] = PLAY_AREA_X + snake.get(i).col * CELL_SIZE;
    prevY[i] = PLAY_AREA_Y + snake.get(i).row * CELL_SIZE;
}
lerpT = 1f;
```

**moveSnake() — snapshot pixel positions BEFORE advancing:**
```java
private void moveSnake() {
    for (int i = 0; i < snake.size; i++) {
        prevX[i] = PLAY_AREA_X + snake.get(i).col * CELL_SIZE;
        prevY[i] = PLAY_AREA_Y + snake.get(i).row * CELL_SIZE;
    }
    lerpT = 0f;
    // ... rest of move logic
}
```

**update() — advance lerpT AFTER moveSnake fires:**
```java
moveTimer -= delta;
if (moveTimer <= 0f) {
    moveTimer = moveInterval;
    moveSnake();   // resets lerpT = 0
}
lerpT = Math.min(1f, lerpT + delta / moveInterval);
```

**render() — draw each segment at its lerped position:**
```java
// Body: gradient from bright (near head) to dark (tail)
for (int i = 1; i < snake.size; i++) {
    float tx = PLAY_AREA_X + snake.get(i).col * CELL_SIZE;
    float ty = PLAY_AREA_Y + snake.get(i).row * CELL_SIZE;
    float rx = (prevX != null && i < prevX.length) ? MathUtils.lerp(prevX[i], tx, lerpT) : tx;
    float ry = (prevY != null && i < prevY.length) ? MathUtils.lerp(prevY[i], ty, lerpT) : ty;
    float fade = MathUtils.lerp(0.85f, 0.30f, (float)i / Math.max(snake.size - 1, 1));
    shapeRenderer.setColor(0f, 1f * fade, 0.698f * fade, 1f);
    shapeRenderer.rect(rx + 2f, ry + 2f, CELL_SIZE - 4f, CELL_SIZE - 4f);
}
// Head: white circle with accent-colored core
float tx = PLAY_AREA_X + snake.get(0).col * CELL_SIZE;
float ty = PLAY_AREA_Y + snake.get(0).row * CELL_SIZE;
float hx = (prevX != null) ? MathUtils.lerp(prevX[0], tx, lerpT) : tx;
float hy = (prevY != null) ? MathUtils.lerp(prevY[0], ty, lerpT) : ty;
shapeRenderer.setColor(Color.WHITE);
shapeRenderer.circle(hx + CELL_SIZE * 0.5f, hy + CELL_SIZE * 0.5f, CELL_SIZE * 0.44f, 14);
shapeRenderer.setColor(Color.valueOf(Constants.COLOR_PRIMARY));
shapeRenderer.circle(hx + CELL_SIZE * 0.5f, hy + CELL_SIZE * 0.5f, CELL_SIZE * 0.20f, 10);
```

---

### Instant direction response — MANDATORY

On every valid direction input (swipe or key), immediately snap the lerp and fire the
next move tick. Without this the player waits up to `moveInterval` ms before the snake turns.

**In swipe/fling handler — add after setting nextDir:**
```java
// Snap lerp and fire move immediately — eliminates input lag
lerpT = 1f;
moveTimer = 0f;
```

**In key handler:**
```java
case Input.Keys.UP:    if (dirY != -1) { nextDirX=0;  nextDirY=1;  lerpT=1f; moveTimer=0f; } break;
case Input.Keys.DOWN:  if (dirY !=  1) { nextDirX=0;  nextDirY=-1; lerpT=1f; moveTimer=0f; } break;
case Input.Keys.LEFT:  if (dirX !=  1) { nextDirX=-1; nextDirY=0;  lerpT=1f; moveTimer=0f; } break;
case Input.Keys.RIGHT: if (dirX != -1) { nextDirX=1;  nextDirY=0;  lerpT=1f; moveTimer=0f; } break;
```

---

### Grid visibility — MANDATORY

Always add a dark overlay before drawing grid dots so the background art does not obscure them:

```java
// Dark overlay suppresses busy background image
shapeRenderer.setColor(0f, 0f, 0.04f, 0.72f);
shapeRenderer.rect(PLAY_AREA_X, PLAY_AREA_Y, PLAY_AREA_W, PLAY_AREA_H);

// Grid dots: bright white, subtle alpha, radius 2
shapeRenderer.setColor(1f, 1f, 1f, 0.22f);
for (int c = 0; c < GRID_COLS; c++) {
    for (int r = 0; r < GRID_ROWS; r++) {
        float gx = PLAY_AREA_X + c * CELL_SIZE + CELL_SIZE * 0.5f;
        float gy = PLAY_AREA_Y + r * CELL_SIZE + CELL_SIZE * 0.5f;
        shapeRenderer.circle(gx, gy, 2f, 6);
    }
}
```
