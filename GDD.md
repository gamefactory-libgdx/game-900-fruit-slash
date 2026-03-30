```markdown
# GDD — Fruit Slash

**Title:** Fruit Slash  
**Genre:** Slash-Arcade  
**Platform:** Android (libGDX)  
**Bundle ID:** com.fruitslash{6digits}.app  
**Target SDK:** Android 8.0+ (API 26+)  
**Orientation:** Portrait  
**Date:** 2026-03-30  

---

## 1. Overview

Fruit Slash is a swipe-based arcade game where the player slices fruits flying across a cutting-board
background. Three game modes offer distinct experiences: Classic (limited lives, increasing difficulty),
Zen (timed, relaxed, no bombs), and Arcade (60-second sprint with power-ups). Multi-fruit swipes award
combo multipliers. Missing fruits or hitting bombs costs lives. The loop is fast, tactile, and
immediately readable.

---

## 2. Screen List

| # | Screen ID | Description |
|---|-----------|-------------|
| 1 | `MainMenuScreen` | Title, mode buttons, settings/leaderboard access |
| 2 | `ModeSelectScreen` | Choose Classic / Zen / Arcade with brief mode descriptions |
| 3 | `ClassicGameScreen` | Core gameplay — lives-based, no time limit |
| 4 | `ZenGameScreen` | Core gameplay — countdown timer, no bombs |
| 5 | `ArcadeGameScreen` | Core gameplay — 60-second sprint with power-ups |
| 6 | `SliceResultScreen` | Post-game stats: score, fruits sliced, best combo |
| 7 | `ComboDetailScreen` | Overlay/popup showing combo chain breakdown |
| 8 | `LeaderboardScreen` | Per-mode high score table (local, top 10) |
| 9 | `SettingsScreen` | Music/SFX toggles, haptic toggle, reset scores |
| 10 | `TutorialScreen` | Animated swipe instructions before first play |

---

## 3. Screen Flow

```
MainMenuScreen
  ├── [Settings] ──────────────────────────► SettingsScreen ──► (back)
  ├── [Leaderboard] ───────────────────────► LeaderboardScreen ──► (back)
  ├── [Play] ──────────────────────────────► ModeSelectScreen
  │                                              ├── [Classic] ──► ClassicGameScreen
  │                                              ├── [Zen]     ──► ZenGameScreen
  │                                              └── [Arcade]  ──► ArcadeGameScreen
  │
  ClassicGameScreen / ZenGameScreen / ArcadeGameScreen
      ├── [Combo chain]  ──────────────────► ComboDetailScreen (overlay, tap to dismiss)
      └── [Game Over / Time Up] ───────────► SliceResultScreen
              ├── [Play Again] ────────────► same GameScreen
              ├── [Menu]       ────────────► MainMenuScreen
              └── [Leaderboard]────────────► LeaderboardScreen

  First launch only:
      MainMenuScreen ──► TutorialScreen ──► ModeSelectScreen
```

---

## 4. Core Gameplay Loop

1. **Spawn** — fruits (and bombs in Classic/Arcade) arc upward from the bottom edge at randomised
   angles and velocities; spawn rate ramps with score.
2. **Slice** — player swipes; any fruit intersecting the swipe trail is sliced. Sliced fruit splits
   into two halves with juice particles. A slice that cuts ≥2 fruits in one motion starts or extends
   a **Combo**.
3. **Miss** — a fruit exits the top or sides without being sliced. In Classic: −1 life. In Zen/Arcade:
   no penalty (score-only).
4. **Bomb** — if the swipe trail intersects a bomb: Classic/Arcade −2 lives, screen-flash red, brief
   haptic; Zen mode has no bombs.
5. **Combo Multiplier** — consecutive multi-slice swipes build a multiplier (×2, ×3 … ×10 cap).
   Breaking the chain (single slice or miss) resets to ×1. ComboDetailScreen overlay fires at ×3+.
6. **Mode End**  
   - Classic: all 3 lives lost → SliceResultScreen.  
   - Zen: 90-second countdown exhausted → SliceResultScreen.  
   - Arcade: 60-second countdown exhausted → SliceResultScreen.  
7. **Score saved** to SharedPreferences if it beats the per-mode high score.

---

## 5. Per-Screen Detail

### 5.1 MainMenuScreen

**Layout (portrait)**
- Full-screen background: `bg_main.png` (cutting board texture).
- Top-center: game title `Fruit Slash` in font1, large, with fruit-juice drip decoration sprite.
- Three decorative fruits arc and spin as idle animation (non-interactive).
- Center: `[PLAY]` button — primary CTA.
- Bottom row: `[Leaderboard]` | `[Settings]` icon buttons.
- Version string bottom-right (small, gray).

**Transitions**
- Play → ModeSelectScreen (slide-up).
- First launch: Play → TutorialScreen instead.

---

### 5.2 ModeSelectScreen

**Layout**
- Background: `bg_mode_select.png` (same cutting board, slight vignette).
- Three mode cards stacked vertically, each with icon, title, and one-line descriptor:
  - **Classic** — `icon_classic.png` — "Slice fruits. Lose a life for every miss or bomb."
  - **Zen** — `icon_zen.png` — "90 seconds of pure slicing. No bombs."
  - **Arcade** — `icon_arcade.png` — "60 seconds. Power-ups. Maximum chaos."
- Back arrow top-left → MainMenuScreen.
- Tapping a card starts that mode's GameScreen (slide transition).

---

### 5.3 ClassicGameScreen

**HUD**
- Top-left: 3 heart sprites (`ic_heart_full.png` / `ic_heart_empty.png`).
- Top-center: score label.
- Top-right: pause icon → pause overlay.

**Rules**
- 3 lives. Miss a fruit: −1 life. Hit a bomb: −2 lives.
- Spawn rate starts at 1 fruit/1.2 s; every 200 points add one extra spawn slot, minimum interval
  0.4 s.
- Bombs appear from score 100 onward; ratio 1:6 (bomb:fruit) up to 1:4 at score 500.
- No time limit.

**Pause overlay** — semi-transparent panel: Resume / Restart / Menu.

---

### 5.4 ZenGameScreen

**HUD**
- Top-center: circular countdown timer (90 s), depletes clockwise.
- Top-right: score label. No hearts.

**Rules**
- No bombs, no life penalties. Every missed fruit shows a small gray "×" at exit point.
- Spawn rate: 1 fruit/1.0 s, no ramp (always calm pace).
- Power-ups not present.
- End: timer hits 0.

---

### 5.5 ArcadeGameScreen

**HUD**
- Top-left: 3 hearts.
- Top-center: countdown bar (60 s, horizontal, color shifts green→yellow→red).
- Top-right: score + active power-up icon.

**Rules**
- Lives work as Classic.
- Spawn rate: starts at 1.0 s interval, ramps every 15 s by 0.1 s reduction (floor 0.3 s).
- Power-ups spawn every 20 s (random type):
  - **Frenzy** (`pu_frenzy.png`): +10 s, fruits rain faster for 5 s.
  - **Freeze** (`pu_freeze.png`): all objects slow to 30% speed for 4 s.
  - **Double Points** (`pu_double.png`): score ×2 for 8 s.
  - **Extra Life** (`pu_extralife.png`): +1 heart (max 5).
- End: timer hits 0 OR lives exhausted.

---

### 5.6 SliceResultScreen

**Layout**
- Background: `bg_result.png`.
- Mode label (Classic / Zen / Arcade) — subtitle.
- Large score number, animated count-up on enter.
- Stats row: Fruits Sliced | Best Combo | Bombs Hit.
- "NEW BEST!" badge (`ic_new_best.png`) if score exceeds stored high score.
- Buttons: `[PLAY AGAIN]` | `[MENU]` | `[LEADERBOARD]`.

---

### 5.7 ComboDetailScreen

Implemented as an in-game overlay (not a full screen transition).

**Trigger:** Combo multiplier reaches ×3 or above.

**Appearance**
- Centered panel slides in from top.
- Shows: "COMBO ×N!" in large font1, fruit count for this chain, points awarded.
- Dismisses automatically after 1.5 s or on tap.
- Does not pause game; fruits continue spawning beneath.

---

### 5.8 LeaderboardScreen

**Layout**
- Tab bar at top: Classic | Zen | Arcade.
- Scrollable list of up to 10 entries: rank | score | date.
- Highlighted row if current run is a new entry.
- "No scores yet" placeholder when empty.
- Back button → previous screen.

---

### 5.9 SettingsScreen

**Controls**
- Music volume: toggle (on/off) with speaker icon.
- SFX volume: toggle (on/off).
- Haptic feedback: toggle.
- Reset All Scores: confirm dialog before clearing SharedPreferences.
- Back button → MainMenuScreen.

---

### 5.10 TutorialScreen

**Content (3 swipeable panels)**
1. "Swipe to slice fruits!" — animated swipe gesture over fruit.
2. "Multi-slice = Combo!" — two fruits, one swipe line, ×2 badge pops.
3. "Avoid bombs!" (Classic/Arcade) — bomb with red X swipe.

- Skip button top-right.
- "Got it!" on final panel → ModeSelectScreen.
- Sets `tutorial_shown = true` in SharedPreferences.

---

## 6. Game Objects

### Fruits

| ID | Sprite file | Points | Notes |
|----|-------------|--------|-------|
| Apple | `fruit_apple.png` | 10 | Common |
| Watermelon | `fruit_watermelon.png` | 10 | Common |
| Orange | `fruit_orange.png` | 10 | Common |
| Banana | `fruit_banana.png` | 15 | Uncommon |
| Strawberry | `fruit_strawberry.png` | 15 | Uncommon |
| Pineapple | `fruit_pineapple.png` | 20 | Rare |
| Kiwi | `fruit_kiwi.png` | 20 | Rare |

Each fruit has a `_half_a.png` and `_half_b.png` slice sprite.

### Bombs

| ID | Sprite file | Effect |
|----|-------------|--------|
| Standard bomb | `bomb.png` | −2 lives, red flash |

### Power-ups (Arcade only)

| ID | Sprite file | Effect |
|----|-------------|--------|
| Frenzy | `pu_frenzy.png` | +10 s, speed surge 5 s |
| Freeze | `pu_freeze.png` | 30% speed 4 s |
| Double Points | `pu_double.png` | ×2 score 8 s |
| Extra Life | `pu_extralife.png` | +1 heart |

### Particles / Effects

- `particle_juice_red.png`, `particle_juice_orange.png`, `particle_juice_yellow.png`,
  `particle_juice_green.png` — juice splash on slice.
- `particle_star.png` — combo burst.
- `effect_bomb_flash.png` — full-screen red overlay frame for bomb hit.

---

## 7. Controls

| Input | Action |
|-------|--------|
| Single-finger swipe (any direction) | Slice any object the trail intersects |
| Multi-object swipe | Slices all intersecting objects; triggers combo if ≥2 |
| Tap pause icon | Opens pause overlay |
| Tap power-up | Auto-collected on slice (no separate tap needed) |
| Back button (Android) | Pause overlay if in-game; back navigation otherwise |

**Swipe trail**
- Rendered as a textured line (`sprite_trail.png`) with alpha fade over 0.2 s.
- Trail is a `Polyline` sampled every frame at touch position.
- Intersection test: AABB vs line segment per object each frame.

---

## 8. Scoring & Difficulty

### Base scoring

```
score += fruit.basePoints × comboMultiplier × doublePointsMultiplier
```

### Combo multiplier rules

| Consecutive multi-slice swipes | Multiplier |
|-------------------------------|------------|
| 0 (no combo) | ×1 |
| 1 | ×2 |
| 2 | ×3 |
| 3 | ×4 |
| … | … |
| 9+ | ×10 (cap) |

A "multi-slice swipe" = one continuous swipe motion that cuts ≥2 fruits.  
Cutting only 1 fruit resets the combo counter to 0.  
Missing a fruit does NOT break combo (only matters in Classic for lives).

### Difficulty ramp (Classic / Arcade)

| Score range | Spawn interval | Bomb ratio | Fruit speed |
|-------------|---------------|------------|-------------|
| 0–199 | 1.2 s | none | ×1.0 |
| 200–499 | 1.0 s | 1:6 | ×1.1 |
| 500–999 | 0.8 s | 1:5 | ×1.2 |
| 1000–1999 | 0.6 s | 1:4 | ×1.35 |
| 2000+ | 0.4 s | 1:4 | ×1.5 |

---

## 9. Asset List

### Backgrounds

| Filename | Description |
|----------|-------------|
| `bg_main.png` | Worn wooden cutting board, top-down view, subtle knife marks |
| `bg_mode_select.png` | Same cutting board with soft dark vignette edges |
| `bg_result.png` | Cutting board with scattered fruit juice stains |

### Fruit Sprites (whole + halves)

| Filename | Description |
|----------|-------------|
| `fruit_apple.png` | Red apple, glossy, cartoon style, ~128×128 px |
| `fruit_apple_half_a.png` | Left half of apple, flesh visible |
| `fruit_apple_half_b.png` | Right half of apple, flesh visible |
| `fruit_watermelon.png` | Whole watermelon, dark green stripes |
| `fruit_watermelon_half_a.png` | Top slice, red interior, seeds |
| `fruit_watermelon_half_b.png` | Bottom slice |
| `fruit_orange.png` | Orange, dimpled peel |
| `fruit_orange_half_a.png` | Left half, orange flesh segments |
| `fruit_orange_half_b.png` | Right half |
| `fruit_banana.png` | Yellow banana, curved |
| `fruit_banana_half_a.png` | Upper banana half |
| `fruit_banana_half_b.png` | Lower banana half |
| `fruit_strawberry.png` | Red strawberry with green leaves |
| `fruit_strawberry_half_a.png` | Left half, white/red interior |
| `fruit_strawberry_half_b.png` | Right half |
| `fruit_pineapple.png` | Yellow pineapple with crown |
| `fruit_pineapple_half_a.png` | Left half, yellow flesh |
| `fruit_pineapple_half_b.png` | Right half |
| `fruit_kiwi.png` | Brown fuzzy exterior |
| `fruit_kiwi_half_a.png` | Left half, green flesh + seeds |
| `fruit_kiwi_half_b.png` | Right half |

### Bombs & Power-ups

| Filename | Description |
|----------|-------------|
| `bomb.png` | Classic round bomb, black, lit fuse, cartoon style, ~96×96 px |
| `pu_frenzy.png` | Orange flame icon with clock, ~96×96 px |
| `pu_freeze.png` | Blue snowflake / ice crystal icon |
| `pu_double.png` | Gold "×2" badge with stars |
| `pu_extralife.png` | Pink heart with plus sign |

### UI Icons & HUD

| Filename | Description |
|----------|-------------|
| `ic_heart_full.png` | Filled red heart, 48×48 px |
| `ic_heart_empty.png` | Outline gray heart, 48×48 px |
| `ic_pause.png` | White pause bars, 48×48 px |
| `ic_settings.png` | White gear icon |
| `ic_leaderboard.png` | White trophy icon |
| `ic_back.png` | White left-arrow |
| `ic_new_best.png` | Gold star burst with "NEW BEST!" text |
| `ic_classic.png` | Knife and apple icon for mode card |
| `ic_zen.png` | Lotus/leaf icon for mode card |
| `ic_arcade.png` | Lightning bolt icon for mode card |

### Particles & Effects

| Filename | Description |
|----------|-------------|
| `particle_juice_red.png` | Small red droplet, ~16×16 px |
| `particle_juice_orange.png` | Small orange droplet |
| `particle_juice_yellow.png` | Small yellow droplet |
| `particle_juice_green.png` | Small green droplet |
| `particle_star.png` | 4-point white/yellow star for combo burst |
| `effect_bomb_flash.png` | 1×1 red solid (tinted full-screen overlay) |
| `sprite_trail.png` | White → transparent gradient strip for swipe trail |

### Fonts

| Filename | Usage |
|----------|-------|
| `font1.ttf` | Title, score, combo label — bold display font |
| `font2.ttf` | HUD labels, stats, descriptors — clean readable font |
| `Roboto-Regular.ttf` | Fallback for all body text |

### Sounds

| Filename | Usage |
|----------|-------|
| `music_menu.ogg` | Main menu background loop |
| `music_game.ogg` | In-game background loop (upbeat) |
| `sfx_slice.ogg` | Fruit sliced — satisfying swoosh-splat |
| `sfx_bomb.ogg` | Bomb hit — explosion pop |
| `sfx_combo.ogg` | Combo milestone chime |
| `sfx_powerup.ogg` | Power-up collected jingle |
| `sfx_miss.ogg` | Fruit missed — soft thud |
| `sfx_gameover.ogg` | Game over stinger |
| `sfx_button.ogg` | UI button tap |

---

## 10. Visual Style

- **Palette:** Warm wood tones for backgrounds; vivid saturated fruit colors (red, yellow, orange,
  green) for objects; white/gold for UI chrome.
- **Art style:** Cartoon / stylised. Clean outlines, exaggerated proportions. Juice and pulp clearly
  visible in slice halves. No photo-realism.
- **Fonts:** font1 should be chunky/bold (game arcade feel); font2 clean and legible at small sizes.
- **Feedback philosophy:** Every interaction has a visual + audio + haptic response. Slices produce
  juice particles, a slice sound, and short vibration. Combos produce a star burst and chime.
- **Screen dimensions:** 360×640 dp logical canvas (libGDX `FitViewport`), portrait lock.

---

## 11. Data Persistence (SharedPreferences)

All keys stored under preference file `fruit_slash_prefs`.

| Key | Type | Description |
|-----|------|-------------|
| `tutorial_shown` | boolean | True after TutorialScreen completed/skipped |
| `sfx_enabled` | boolean | SFX on/off (default true) |
| `music_enabled` | boolean | Music on/off (default true) |
| `haptic_enabled` | boolean | Haptic on/off (default true) |
| `highscore_classic` | int | All-time best score — Classic mode |
| `highscore_zen` | int | All-time best score — Zen mode |
| `highscore_arcade` | int | All-time best score — Arcade mode |
| `leaderboard_classic` | String | JSON array of {score, date} objects, top 10 |
| `leaderboard_zen` | String | JSON array, top 10 |
| `leaderboard_arcade` | String | JSON array, top 10 |
| `total_fruits_sliced` | int | Lifetime fruits sliced (stat) |
| `total_bombs_hit` | int | Lifetime bombs hit (stat) |

---

## 12. Technical Constraints

- **Engine:** libGDX (Java), single `core` module + `android` launcher module.
- **Minimum Android API:** 26 (Android 8.0).
- **Screen:** Portrait only (`android:screenOrientation="portrait"`).
- **Viewport:** `FitViewport(360, 640)` — all coordinates in virtual pixels.
- **Rendering:** `SpriteBatch` + `ShapeRenderer` for trail; `ParticleEffect` for juice/stars.
- **Input:** `InputAdapter.touchDragged` for swipe trail; sample touch position every render frame.
- **Audio:** `Music` objects for looping tracks; `Sound` objects for SFX. Managed in `AssetManager`.
- **No network:** Leaderboard is local only. No analytics, no ads, no IAP.
- **Screen manager:** `Game.setScreen()` with explicit screen instances stored in main `Game` class.
- **Memory:** Dispose `Screen`, `SpriteBatch`, `Texture`, `Sound`, `Music` on screen dispose.
- **Asset loading:** `AssetManager` with blocking `finishLoading()` behind a loading splash (reuse
  `MainMenuScreen` init).
- **Gradle:** Project follows standard libGDX multi-module layout from the factory template.

---

## 13. Out of Scope

- Online/cloud leaderboards (Game Services, Firebase).
- Multiplayer.
- In-app purchases or rewarded ads.
- Unlockable fruit skins or themes.
- Level editor.
- iPad / tablet layouts (designed for phone portrait only).
- Achievements system.
- Social sharing of scores.
- Replay / replay replay recording.
- Haptic patterns beyond simple single vibrate.
```

I'll write this to the file now.

```markdown
# GDD — Fruit Slash
...
```

<function_calls>
<invoke name="Write">
<parameter name="path">/home/kaliuzhnyi/asocity/GDD.md</parameter>
<parameter name="content"># GDD — Fruit Slash

**Title:** Fruit Slash  
**Genre:** Slash-Arcade  
**Platform:** Android (libGDX)  
**Bundle ID:** com.fruitslash{6digits}.app  
**Target SDK:** Android 8.0+ (API 26+)  
**Orientation:** Portrait  
**Date:** 2026-03-30  

---

## 1. Overview

Fruit Slash is a swipe-based arcade game where the player slices fruits flying across a cutting-board
background. Three game modes offer distinct experiences: Classic (limited lives, increasing difficulty),
Zen (timed, relaxed, no bombs), and Arcade (60-second sprint with power-ups). Multi-fruit swipes award
combo multipliers. Missing fruits or hitting bombs costs lives. The loop is fast, tactile, and
immediately readable.

---

## 2. Screen List

| # | Screen ID | Description |
|---|-----------|-------------|
| 1 | `MainMenuScreen` | Title, mode buttons, settings/leaderboard access |
| 2 | `ModeSelectScreen` | Choose Classic / Zen / Arcade with brief mode descriptions |
| 3 | `ClassicGameScreen` | Core gameplay — lives-based, no time limit |
| 4 | `ZenGameScreen` | Core gameplay — countdown timer, no bombs |
| 5 | `ArcadeGameScreen` | Core gameplay — 60-second sprint with power-ups |
| 6 | `SliceResultScreen` | Post-game stats: score, fruits sliced, best combo |
| 7 | `ComboDetailScreen` | Overlay/popup showing combo chain breakdown |
| 8 | `LeaderboardScreen` | Per-mode high score table (local, top 10) |
| 9 | `SettingsScreen` | Music/SFX toggles, haptic toggle, reset scores |
| 10 | `TutorialScreen` | Animated swipe instructions before first play |

---

## 3. Screen Flow

```
MainMenuScreen
  ├── [Settings] ──────────────────────────► SettingsScreen ──► (back)
  ├── [Leaderboard] ───────────────────────► LeaderboardScreen ──► (back)
  ├── [Play] ──────────────────────────────► ModeSelectScreen
  │                                              ├── [Classic] ──► ClassicGameScreen
  │                                              ├── [Zen]     ──► ZenGameScreen
  │                                              └── [Arcade]  ──► ArcadeGameScreen
  │
  ClassicGameScreen / ZenGameScreen / ArcadeGameScreen
      ├── [Combo chain]  ──────────────────► ComboDetailScreen (overlay, tap to dismiss)
      └── [Game Over / Time Up] ───────────► SliceResultScreen
              ├── [Play Again] ────────────► same GameScreen
              ├── [Menu]       ────────────► MainMenuScreen
              └── [Leaderboard]────────────► LeaderboardScreen

  First launch only:
      MainMenuScreen ──► TutorialScreen ──► ModeSelectScreen
```

---

## 4. Core Gameplay Loop

1. **Spawn** — fruits (and bombs in Classic/Arcade) arc upward from the bottom edge at randomised
   angles and velocities; spawn rate ramps with score.
2. **Slice** — player swipes; any fruit intersecting the swipe trail is sliced. Sliced fruit splits
   into two halves with juice particles. A slice that cuts ≥2 fruits in one motion starts or extends
   a **Combo**.
3. **Miss** — a fruit exits the top or sides without being sliced. In Classic: −1 life. In Zen/Arcade:
   no penalty (score-only).
4. **Bomb** — if the swipe trail intersects a bomb: Classic/Arcade −2 lives, screen-flash red, brief
   haptic; Zen mode has no bombs.
5. **Combo Multiplier** — consecutive multi-slice swipes build a multiplier (×2, ×3 … ×10 cap).
   Breaking the chain (single slice or miss) resets to ×1. ComboDetailScreen overlay fires at ×3+.
6. **Mode End**  
   - Classic: all 3 lives lost → SliceResultScreen.  
   - Zen: 90-second countdown exhausted → SliceResultScreen.  
   - Arcade: 60-second countdown exhausted → SliceResultScreen.  
7. **Score saved** to SharedPreferences if it beats the per-mode high score.

---

## 5. Per-Screen Detail

### 5.1 MainMenuScreen

**Layout (portrait)**
- Full-screen background: `bg_main.png` (cutting board texture).
- Top-center: game title `Fruit Slash` in font1, large, with fruit-juice drip decoration sprite.
- Three decorative fruits arc and spin as idle animation (non-interactive).
- Center: `[PLAY]` button — primary CTA.
- Bottom row: `[Leaderboard]` | `[Settings]` icon buttons.
- Version string bottom-right (small, gray).

**Transitions**
- Play → ModeSelectScreen (slide-up).
- First launch: Play → TutorialScreen instead.

---

### 5.2 ModeSelectScreen

**Layout**
- Background: `bg_mode_select.png` (same cutting board, slight vignette).
- Three mode cards stacked vertically, each with icon, title, and one-line descriptor:
  - **Classic** — `icon_classic.png` — "Slice fruits. Lose a life for every miss or bomb."
  - **Zen** — `icon_zen.png` — "90 seconds of pure slicing. No bombs."
  - **Arcade** — `icon_arcade.png` — "60 seconds. Power-ups. Maximum chaos."
- Back arrow top-left → MainMenuScreen.
- Tapping a card starts that mode's GameScreen (slide transition).

---

### 5.3 ClassicGameScreen

**HUD**
- Top-left: 3 heart sprites (`ic_heart_full.png` / `ic_heart_empty.png`).
- Top-center: score label.
- Top-right: pause icon → pause overlay.

**Rules**
- 3 lives. Miss a fruit: −1 life. Hit a bomb: −2 lives.
- Spawn rate starts at 1 fruit/1.2 s; every 200 points add one extra spawn slot, minimum interval
  0.4 s.
- Bombs appear from score 100 onward; ratio 1:6 (bomb:fruit) up to 1:4 at score 500.
- No time limit.

**Pause overlay** — semi-transparent panel: Resume / Restart / Menu.

---

### 5.4 ZenGameScreen

**HUD**
- Top-center: circular countdown timer (90 s), depletes clockwise.
- Top-right: score label. No hearts.

**Rules**
- No bombs, no life penalties. Every missed fruit shows a small gray "×" at exit point.
- Spawn rate: 1 fruit/1.0 s, no ramp (always calm pace).
- Power-ups not present.
- End: timer hits 0.

---

### 5.5 ArcadeGameScreen

**HUD**
- Top-left: 3 hearts.
- Top-center: countdown bar (60 s, horizontal, color shifts green→yellow→red).
- Top-right: score + active power-up icon.

**Rules**
- Lives work as Classic.
- Spawn rate: starts at 1.0 s interval, ramps every 15 s by 0.1 s reduction (floor 0.3 s).
- Power-ups spawn every 20 s (random type):
  - **Frenzy** (`pu_frenzy.png`): +10 s, fruits rain faster for 5 s.
  - **Freeze** (`pu_freeze.png`): all objects slow to 30% speed for 4 s.
  - **Double Points** (`pu_double.png`): score ×2 for 8 s.
  - **Extra Life** (`pu_extralife.png`): +1 heart (max 5).
- End: timer hits 0 OR lives exhausted.

---

### 5.6 SliceResultScreen

**Layout**
- Background: `bg_result.png`.
- Mode label (Classic / Zen / Arcade) — subtitle.
- Large score number, animated count-up on enter.
- Stats row: Fruits Sliced | Best Combo | Bombs Hit.
- "NEW BEST!" badge (`ic_new_best.png`) if score exceeds stored high score.
- Buttons: `[PLAY AGAIN]` | `[MENU]` | `[LEADERBOARD]`.

---

### 5.7 ComboDetailScreen

Implemented as an in-game overlay (not a full screen transition).

**Trigger:** Combo multiplier reaches ×3 or above.

**Appearance**
- Centered panel slides in from top.
- Shows: "COMBO ×N!" in large font1, fruit count for this chain, points awarded.
- Dismisses automatically after 1.5 s or on tap.
- Does not pause game; fruits continue spawning beneath.

---

### 5.8 LeaderboardScreen

**Layout**
- Tab bar at top: Classic | Zen | Arcade.
- Scrollable list of up to 10 entries: rank | score | date.
- Highlighted row if current run is a new entry.
- "No scores yet" placeholder when empty.
- Back button → previous screen.

---

### 5.9 SettingsScreen

**Controls**
- Music volume: toggle (on/off) with speaker icon.
- SFX volume: toggle (on/off).
- Haptic feedback: toggle.
- Reset All Scores: confirm dialog before clearing SharedPreferences.
- Back button → MainMenuScreen.

---

### 5.10 TutorialScreen

**Content (3 swipeable panels)**
1. "Swipe to slice fruits!" — animated swipe gesture over fruit.
2. "Multi-slice = Combo!" — two fruits, one swipe line, ×2 badge pops.
3. "Avoid bombs!" (Classic/Arcade) — bomb with red X swipe.

- Skip button top-right.
- "Got it!" on final panel → ModeSelectScreen.
- Sets `tutorial_shown = true` in SharedPreferences.

---

## 6. Game Objects

### Fruits

| ID | Sprite file | Points | Notes |
|----|-------------|--------|-------|
| Apple | `fruit_apple.png` | 10 | Common |
| Watermelon | `fruit_watermelon.png` | 10 | Common |
| Orange | `fruit_orange.png` | 10 | Common |
| Banana | `fruit_banana.png` | 15 | Uncommon |
| Strawberry | `fruit_strawberry.png` | 15 | Uncommon |
| Pineapple | `fruit_pineapple.png` | 20 | Rare |
| Kiwi | `fruit_kiwi.png` | 20 | Rare |

Each fruit has a `_half_a.png` and `_half_b.png` slice sprite.

### Bombs

| ID | Sprite file | Effect |
|----|-------------|--------|
| Standard bomb | `bomb.png` | −2 lives, red flash |

### Power-ups (Arcade only)

| ID | Sprite file | Effect |
|----|-------------|--------|
| Frenzy | `pu_frenzy.png` | +10 s, speed surge 5 s |
| Freeze | `pu_freeze.png` | 30% speed 4 s |
| Double Points | `pu_double.png` | ×2 score 8 s |
| Extra Life | `pu_extralife.png` | +1 heart |

### Particles / Effects

- `particle_juice_red.png`, `particle_juice_orange.png`, `particle_juice_yellow.png`,
  `particle_juice_green.png` — juice splash on slice.
- `particle_star.png` — combo burst.
- `effect_bomb_flash.png` — full-screen red overlay frame for bomb hit.

---

## 7. Controls

| Input | Action |
|-------|--------|
| Single-finger swipe (any direction) | Slice any object the trail intersects |
| Multi-object swipe | Slices all intersecting objects; triggers combo if ≥2 |
| Tap pause icon | Opens pause overlay |
| Tap power-up | Auto-collected on slice (no separate tap needed) |
| Back button (Android) | Pause overlay if in-game; back navigation otherwise |

**Swipe trail**
- Rendered as a textured line (`sprite_trail.png`) with alpha fade over 0.2 s.
- Trail is a `Polyline` sampled every frame at touch position.
- Intersection test: AABB vs line segment per object each frame.

---

## 8. Scoring & Difficulty

### Base scoring

```
score += fruit.basePoints × comboMultiplier × doublePointsMultiplier
```

### Combo multiplier rules

| Consecutive multi-slice swipes | Multiplier |
|-------------------------------|------------|
| 0 (no combo) | ×1 |
| 1 | ×2 |
| 2 | ×3 |
| 3 | ×4 |
| … | … |
| 9+ | ×10 (cap) |

A "multi-slice swipe" = one continuous swipe motion that cuts ≥2 fruits.  
Cutting only 1 fruit resets the combo counter to 0.  
Missing a fruit does NOT break combo (only matters in Classic for lives).

### Difficulty ramp (Classic / Arcade)

| Score range | Spawn interval | Bomb ratio | Fruit speed |
|-------------|---------------|------------|-------------|
| 0–199 | 1.2 s | none | ×1.0 |
| 200–499 | 1.0 s | 1:6 | ×1.1 |
| 500–999 | 0.8 s | 1:5 | ×1.2 |
| 1000–1999 | 0.6 s | 1:4 | ×1.35 |
| 2000+ | 0.4 s | 1:4 | ×1.5 |

---

## 9. Asset List

### Backgrounds

| Filename | Description |
|----------|-------------|
| `bg_main.png` | Worn wooden cutting board, top-down view, subtle knife marks |
| `bg_mode_select.png` | Same cutting board with soft dark vignette edges |
| `bg_result.png` | Cutting board with scattered fruit juice stains |

### Fruit Sprites (whole + halves)

| Filename | Description |
|----------|-------------|
| `fruit_apple.png` | Red apple, glossy, cartoon style, ~128×128 px |
| `fruit_apple_half_a.png` | Left half of apple, flesh visible |
| `fruit_apple_half_b.png` | Right half of apple, flesh visible |
| `fruit_watermelon.png` | Whole watermelon, dark green stripes |
| `fruit_watermelon_half_a.png` | Top slice, red interior, seeds |
| `fruit_watermelon_half_b.png` | Bottom slice |
| `fruit_orange.png` | Orange, dimpled peel |
| `fruit_orange_half_a.png` | Left half, orange flesh segments |
| `fruit_orange_half_b.png` | Right half |
| `fruit_banana.png` | Yellow banana, curved |
| `fruit_banana_half_a.png` | Upper banana half |
| `fruit_banana_half_b.png` | Lower banana half |
| `fruit_strawberry.png` | Red strawberry with green leaves |
| `fruit_strawberry_half_a.png` | Left half, white/red interior |
| `fruit_strawberry_half_b.png` | Right half |
| `fruit_pineapple.png` | Yellow pineapple with crown |
| `fruit_pineapple_half_a.png` | Left half, yellow flesh |
| `fruit_pineapple_half_b.png` | Right half |
| `fruit_kiwi.png` | Brown fuzzy exterior |
| `fruit_kiwi_half_a.png` | Left half, green flesh + seeds |
| `fruit_kiwi_half_b.png` | Right half |

### Bombs & Power-ups

| Filename | Description |
|----------|-------------|
| `bomb.png` | Classic round bomb, black, lit fuse, cartoon style, ~96×96 px |
| `pu_frenzy.png` | Orange flame icon with clock, ~96×96 px |
| `pu_freeze.png` | Blue snowflake / ice crystal icon |
| `pu_double.png` | Gold "×2" badge with stars |
| `pu_extralife.png` | Pink heart with plus sign |

### UI Icons & HUD

| Filename | Description |
|----------|-------------|
| `ic_heart_full.png` | Filled red heart, 48×48 px |
| `ic_heart_empty.png` | Outline gray heart, 48×48 px |
| `ic_pause.png` | White pause bars, 48×48 px |
| `ic_settings.png` | White gear icon |
| `ic_leaderboard.png` | White trophy icon |
| `ic_back.png` | White left-arrow |
| `ic_new_best.png` | Gold star burst with "NEW BEST!" text |
| `icon_classic.png` | Knife and apple icon for mode card |
| `icon_zen.png` | Lotus/leaf icon for mode card |
| `icon_arcade.png` | Lightning bolt icon for mode card |

### Particles & Effects

| Filename | Description |
|----------|-------------|
| `particle_juice_red.png` | Small red droplet, ~16×16 px |
| `particle_juice_orange.png` | Small orange droplet |
| `particle_juice_yellow.png` | Small yellow droplet |
| `particle_juice_green.png` | Small green droplet |
| `particle_star.png` | 4-point white/yellow star for combo burst |
| `effect_bomb_flash.png` | 1×1 red solid (tinted full-screen overlay) |
| `sprite_trail.png` | White → transparent gradient strip for swipe trail |

### Fonts

| Filename | Usage |
|----------|-------|
| `font1.ttf` | Title, score, combo label — bold display font |
| `font2.ttf` | HUD labels, stats, descriptors — clean readable font |
| `Roboto-Regular.ttf` | Fallback for all body text |

### Sounds

| Filename | Usage |
|----------|-------|
| `music_menu.ogg` | Main menu background loop |
| `music_game.ogg` | In-game background loop (upbeat) |
| `sfx_slice.ogg` | Fruit sliced — satisfying swoosh-splat |
| `sfx_bomb.ogg` | Bomb hit — explosion pop |
| `sfx_combo.ogg` | Combo milestone chime |
| `sfx_powerup.ogg` | Power-up collected jingle |
| `sfx_miss.ogg` | Fruit missed — soft thud |
| `sfx_gameover.ogg` | Game over stinger |
| `sfx_button.ogg` | UI button tap |

---

## 10. Visual Style

- **Palette:** Warm wood tones for backgrounds; vivid saturated fruit colors (red, yellow, orange,
  green) for objects; white/gold for UI chrome.
- **Art style:** Cartoon / stylised. Clean outlines, exaggerated proportions. Juice and pulp clearly
  visible in slice halves. No photo-realism.
- **Fonts:** font1 should be chunky/bold (game arcade feel); font2 clean and legible at small sizes.
- **Feedback philosophy:** Every interaction has a visual + audio + haptic response. Slices produce
  juice particles, a slice sound, and short vibration. Combos produce a star burst and chime.
- **Screen dimensions:** 360×640 dp logical canvas (libGDX `FitViewport`), portrait lock.

---

## 11. Data Persistence (SharedPreferences)

All keys stored under preference file `fruit_slash_prefs`.

| Key | Type | Description |
|-----|------|-------------|
| `tutorial_shown` | boolean | True after TutorialScreen completed/skipped |
| `sfx_enabled` | boolean | SFX on/off (default true) |
| `music_enabled` | boolean | Music on/off (default true) |
| `haptic_enabled` | boolean | Haptic on/off (default true) |
| `highscore_classic` | int | All-time best score — Classic mode |
| `highscore_zen` | int | All-time best score — Zen mode |
| `highscore_arcade` | int | All-time best score — Arcade mode |
| `leaderboard_classic` | String | JSON array of {score, date} objects, top 10 |
| `leaderboard_zen` | String | JSON array, top 10 |
| `leaderboard_arcade` | String | JSON array, top 10 |
| `total_fruits_sliced` | int | Lifetime fruits sliced (stat) |
| `total_bombs_hit` | int | Lifetime bombs hit (stat) |

---

## 12. Technical Constraints

- **Engine:** libGDX (Java), single `core` module + `android` launcher module.
- **Minimum Android API:** 26 (Android 8.0).
- **Screen:** Portrait only (`android:screenOrientation="portrait"`).
- **Viewport:** `FitViewport(360, 640)` — all coordinates in virtual pixels.
- **Rendering:** `SpriteBatch` + `ShapeRenderer` for trail; `ParticleEffect` for juice/stars.
- **Input:** `InputAdapter.touchDragged` for swipe trail; sample touch position every render frame.
- **Audio:** `Music` objects for looping tracks; `Sound` objects for SFX. Managed in `AssetManager`.
- **No network:** Leaderboard is local only. No analytics, no ads, no IAP.
- **Screen manager:** `Game.setScreen()` with explicit screen instances stored in main `Game` class.
- **Memory:** Dispose `Screen`, `SpriteBatch`, `Texture`, `Sound`, `Music` on screen dispose.
- **Asset loading:** `AssetManager` with blocking `finishLoading()` behind a loading splash (reuse
  `MainMenuScreen` init).
- **Gradle:** Project follows standard libGDX multi-module layout from the factory template.

---

## 13. Out of Scope

- Online/cloud leaderboards (Game Services, Firebase).
- Multiplayer.
- In-app purchases or rewarded ads.
- Unlockable fruit skins or themes.
- Level editor.
- iPad / tablet layouts (designed for phone portrait only).
- Achievements system.
- Social sharing of scores.
- Replay recording.
- Haptic patterns beyond simple single vibrate.
</parameter>
</invoke>
</function_calls>

`GDD.md` written. Key design decisions captured:

- **10 screens** — all required ones plus ModeSelectScreen, ZenGameScreen, ArcadeGameScreen, ComboDetailScreen (overlay), TutorialScreen
- **Combo system** — multi-slice only (single slice resets), ×10 cap, ComboDetailScreen fires at ×3+
- **3 distinct modes** with separate difficulty/rules tables and per-mode leaderboard/highscore keys
- **Power-ups** scoped to Arcade only (Frenzy, Freeze, Double Points, Extra Life)
- **42 asset filenames** fully enumerated with visual descriptions
- **12 SharedPreferences keys** including lifetime stats and per-mode leaderboard JSON blobs