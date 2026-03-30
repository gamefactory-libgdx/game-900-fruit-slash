# Figma AI Design Brief — Fruit Slash

---

## 1. Art Style & Color Palette

Fruit Slash uses a **vibrant tropical kitchen aesthetic** — bold, saturated, and immediately appetizing. The visual language draws from illustrated food packaging and classic fruit-ninja-style arcade: clean outlines, juicy color fills, and ink-splatter juice effects. Backgrounds evoke a worn wooden cutting board with knife-score marks and fruit stains, giving the game tactile physicality. Typography is chunky and rounded — playful but legible at a glance.

**Primary palette:**
- `#2D1A0E` — Deep mahogany (cutting board shadow, dark UI base)
- `#C8621A` — Amber wood grain (mid-tone cutting board, panel borders)
- `#F5E6C8` — Pale cream (cutting board surface, card fills)
- `#1A9E3F` — Tropical green (Zen mode accent, foliage details)

**Accent palette:**
- `#FF3B2F` — Watermelon red (danger, bombs, lives lost, Arcade energy)
- `#FFD600` — Citrus yellow (score highlights, combo flash, Zen timer glow)

**Font mood:** Chunky rounded display face (e.g., Skeleboom or OrangeKid) for titles and scores; clean sans (Roboto-Regular) for body/labels. Letter spacing tight, tracking 0.

---

## 2. App Icon — `icon_512.png` (512×512 px)

**Canvas:** Full 512×512, no transparent edges, no text, no rounded clipping (OS handles shape).

**Description:** A deep mahogany `#2D1A0E` to amber `#C8621A` radial gradient fills the background, suggesting a close-up of a cutting board lit from above. Dead center: a stylized watermelon half mid-slice — the top half flying upward, the bottom half tilting, with a spray of bright red `#FF3B2F` and green `#1A9E3F` juice droplets arcing outward. A subtle motion-blur slash arc in warm white `#FFFBE8` cuts diagonally through the fruit at 45°. The watermelon has a thick dark outline (`#2D1A0E`, 6–8 px stroke equivalent) and a glossy highlight on its rind. A soft drop-shadow beneath the bottom half grounds it on the board surface. The overall mood is energetic, fresh, and immediately readable at small sizes.

---

## 3. UI Screens (480×854 portrait)

---

### Screen 1 — `MainMenuScreen`

**A) BACKGROUND IMAGE (`ui/main_menu.png`)**

The full canvas is a close-up overhead view of a worn wooden cutting board texture in `#C8621A`–`#F5E6C8` tones, with natural wood grain lines running vertically and scattered knife-score scratches. Several whole and halved fruits (oranges, limes, strawberries, kiwi) are arranged artfully around the edges — fully illustrated, not interactive, acting as decorative props. Juice splash ink-blot shapes in semi-transparent `#FF3B2F` and `#FFD600` dot the board surface. A large empty rectangular banner shape (rounded corners 16 px, semi-transparent dark wood `#2D1A0E` at 70% opacity, faint inner border `#C8621A`) sits centered horizontally between y=180–340 px — this is the title zone. Two more empty rounded-rect card outlines (same style, smaller, 280×56 px) are stacked vertically centered at approximately y=420, 500, 580 — purely decorative blank button frames. A warm vignette darkens all four corners. No text. No icons.

**B) BUTTON LAYOUT (code-drawn)**

```
FRUIT SLASH (title label)  | top-Y=200px | x=centered | size=360x80
PLAY                        | top-Y=420px | x=centered | size=280x56
LEADERBOARD                 | top-Y=500px | x=centered | size=280x56
SETTINGS                    | top-Y=580px | x=centered | size=280x56
```

---

### Screen 2 — `ModeSelectScreen`

**A) BACKGROUND IMAGE (`ui/mode_select.png`)**

Same cutting board base as MainMenuScreen but tighter crop — the wood grain fills the canvas with richer amber saturation (`#B8561A`). Three large empty card-frame panels dominate the center of the screen, stacked vertically: each is a rounded-rect outline (280×160 px, border `#C8621A` 3 px, fill `#2D1A0E` at 65%) positioned at y≈160, 360, 560. Each card frame has a subtle inner glow in a distinct color — `#FF3B2F` for the top card (Classic), `#1A9E3F` for the middle (Zen), `#FFD600` for the bottom (Arcade) — suggesting mode identity without containing any text. Small decorative fruit silhouettes float near card edges. A translucent dark strip at the top (y=0–90) provides contrast for a back button. No text, no labels, no icons.

**B) BUTTON LAYOUT (code-drawn)**

```
BACK                  | top-Y=20px  | x=left@20px  | size=100x44
SELECT MODE (label)   | top-Y=95px  | x=centered   | size=300x40
CLASSIC               | top-Y=180px | x=centered   | size=280x56
  (sub-label: Lives · Increasing speed)
ZEN                   | top-Y=370px | x=centered   | size=280x56
  (sub-label: Timed · No bombs · Relax)
ARCADE                | top-Y=560px | x=centered   | size=280x56
  (sub-label: 60 sec · Power-ups · Rush)
```

---

### Screen 3 — `ClassicGameScreen`

**A) BACKGROUND IMAGE (`ui/classic_game.png`)**

Full cutting board surface fills the canvas — pale cream `#F5E6C8` with dense parallel grain lines in `#C8621A`. The board surface dominates roughly 85% of the screen height, leaving a dark mahogany strip at top (y=0–80) and bottom (y=780–854) for HUD decoration. Scattered across the board are dry juice stain rings and partial fruit-flesh impressions — fully static, non-interactive art. A faint radial light bloom at center-screen in warm white suggests the main play area. In the top strip: three small empty circular frames (32×32 px, `#FF3B2F` border, `#2D1A0E` fill) aligned left at y≈24, suggesting life-icon slots without containing icons. A thin decorative banner outline (full-width, height 64 px, dark wood) spans the top — blank inside. No text, no interactive shapes.

**B) BUTTON LAYOUT (code-drawn)**

```
❤ ❤ ❤ (lives icons, drawn as filled hearts)  | top-Y=18px | x=left@16px   | size=120x40
SCORE: 0 (label)                               | top-Y=18px | x=centered    | size=200x40
PAUSE                                          | top-Y=18px | x=right@16px  | size=80x40
[game play area — fruits spawn here]           | top-Y=80px | full width     | height=700px
```

---

### Screen 4 — `ZenGameScreen`

**A) BACKGROUND IMAGE (`ui/zen_game.png`)**

The cutting board surface shifts to a cooler, slightly muted tone — the cream base `#F5E6C8` washed with a soft green-teal tint gradient from bottom (pale sage `#D4EBD0`) to top (warm cream). Wood grain is lighter and more evenly spaced, giving a calmer feel than Classic mode. Tropical leaf silhouettes (monstera, banana leaf) appear in dark green `#1A4A2E` at partial opacity along the bottom-left and top-right corners, overlapping the board edge. Juice stains are fewer and smaller. A wide decorative rounded-rect frame (460×64 px, `#1A9E3F` border at 50% opacity) sits at the top, empty — timer area. The overall mood is serene and open. No text, no buttons.

**B) BUTTON LAYOUT (code-drawn)**

```
ZEN (mode label)                              | top-Y=10px  | x=left@16px  | size=80x30
TIMER: 2:00 (countdown label)                 | top-Y=18px  | x=centered   | size=200x40
PAUSE                                         | top-Y=18px  | x=right@16px | size=80x40
SCORE: 0 (label)                              | top-Y=70px  | x=centered   | size=200x36
[game play area]                              | top-Y=100px | full width    | height=680px
```

---

### Screen 5 — `ArcadeGameScreen`

**A) BACKGROUND IMAGE (`ui/arcade_game.png`)**

The cutting board base is the same cream–amber, but electrified with energy: diagonal speed-line streaks in semi-transparent `#FFD600` (10% opacity) sweep across the canvas at 30° suggesting momentum. Along both vertical edges, a pulsing neon border glow bleeds inward — left edge `#FF3B2F`, right edge `#FFD600` — like a race track boundary. The top strip (y=0–90) is a dark `#2D1A0E` banner with a bold empty horizontal progress-bar frame (440×20 px, rounded, `#FFD600` border) centered at y≈55. Fruit juice splatters are more numerous and larger than Classic mode. A subtle scanline texture at 4% opacity adds arcade CRT flavor. No text, no UI controls.

**B) BUTTON LAYOUT (code-drawn)**

```
TIMER: 60 (large countdown)                   | top-Y=10px  | x=left@16px  | size=100x40
SCORE: 0 (label)                              | top-Y=10px  | x=centered   | size=200x40
PAUSE                                         | top-Y=10px  | x=right@16px | size=80x40
[timer progress bar]                          | top-Y=56px  | x=centered   | size=440x18
[active power-up label, if any]               | top-Y=80px  | x=centered   | size=280x28
[game play area]                              | top-Y=100px | full width    | height=680px
```

---

### Screen 6 — `SliceResultScreen`

**A) BACKGROUND IMAGE (`ui/slice_result.png`)**

A slightly blurred and darkened version of the cutting board fills the canvas (dark overlay `#2D1A0E` at 55% opacity). A large central results card dominates: a rounded-rect panel (440×480 px, centered, top at y≈150) in `#F5E6C8` with a thick `#C8621A` border (4 px) and a warm drop shadow. Inside the card — the panel is **completely empty** (just the fill color `#F5E6C8`), providing a blank canvas for the code to write score stats. At the top of the card frame, a decorative horizontal fruit-slice strip motif (5–6 small fruit cross-section illustrations in a row) sits along the card's top edge as a divider ornament. Confetti-like juice droplet particles scatter above the card. Three empty rounded-rect button frames (280×52 px, dark wood fill, `#C8621A` border) stack inside the card's lower third. No text anywhere.

**B) BUTTON LAYOUT (code-drawn)**

```
RESULT (section label)          | top-Y=160px | x=centered  | size=260x36
SCORE: 0 (large score display)  | top-Y=215px | x=centered  | size=360x60
FRUITS SLICED: 0 (label)        | top-Y=295px | x=centered  | size=300x36
BEST COMBO: x0 (label)          | top-Y=345px | x=centered  | size=300x36
ACCURACY: 0% (label)            | top-Y=395px | x=centered  | size=300x36
PLAY AGAIN                      | top-Y=470px | x=centered  | size=280x52
MENU                            | top-Y=540px | x=centered  | size=280x52
LEADERBOARD                     | top-Y=610px | x=centered  | size=280x52
```

---

### Screen 7 — `ComboDetailScreen`

**A) BACKGROUND IMAGE (`ui/combo_detail.png`)**

This is an **overlay** — the background is fully transparent black (`#000000` at 0%, canvas is empty / alpha-only art). The visual content is a single floating card: a rounded-rect (400×300 px, centered, top at y≈260) in deep wood `#2D1A0E` at 95% opacity with a glowing `#FFD600` border (3 px) and an outer glow halo in `#FFD600` at 30% spread. Along the card's top edge, a decorative slash-arc motif (three diagonal speed lines in `#FFD600`) signals the combo theme. The card interior is completely blank — ready for code to render combo chain text. No text, no buttons in the image.

**B) BUTTON LAYOUT (code-drawn)**

```
COMBO! (title label, large)                   | top-Y=275px | x=centered | size=300x48
[combo chain list — dynamic text rows]        | top-Y=340px | x=centered | size=360x140
TAP TO CONTINUE (small label)                 | top-Y=510px | x=centered | size=260x32
```

---

### Screen 8 — `LeaderboardScreen`

**A) BACKGROUND IMAGE (`ui/leaderboard.png`)**

The cutting board fills the canvas with a slightly cooler amber tone. A tall central panel (440×600 px, centered, top at y≈120) has a `#2D1A0E` fill at 80% and a `#C8621A` beveled border with a subtle inner highlight on the top edge. Inside the panel, 10 evenly spaced horizontal row dividers (440×1 px lines in `#C8621A` at 30% opacity) divide the leaderboard rows — each row is blank. At the panel's top, a trophy laurel illustration (decorative only, no text) marks the header zone. Three tab-shaped frame outlines sit at the very top of the panel (120×36 px each, Classic / Zen / Arcade tabs), styled but empty. The top strip (y=0–80) is dark for the back button. No text, no icons in the image.

**B) BUTTON LAYOUT (code-drawn)**

```
BACK                            | top-Y=20px  | x=left@20px  | size=100x44
LEADERBOARD (title)             | top-Y=22px  | x=centered   | size=260x40
CLASSIC tab                     | top-Y=125px | x=left@20px  | size=130x36
ZEN tab                         | top-Y=125px | x=centered   | size=130x36
ARCADE tab                      | top-Y=125px | x=right@20px | size=130x36
[rows 1–10: RANK · NAME · SCORE]| top-Y=175px | x=centered   | size=440x360  (10×36px rows)
```

---

### Screen 9 — `SettingsScreen`

**A) BACKGROUND IMAGE (`ui/settings.png`)**

Same cutting board base, warmed with amber. A single centered panel (440×480 px, top at y≈150) in `#2D1A0E` at 85% opacity with `#C8621A` border. Inside the panel, four horizontal row bands (440×64 px, separated by 1 px `#C8621A` dividers) are completely blank — code renders toggle labels and switch controls. A small gear/cogwheel decorative motif (illustrated, not a UI icon) sits in the top-center of the panel as a header ornament. The top strip is dark for the back button. No text, no toggles, no icons.

**B) BUTTON LAYOUT (code-drawn)**

```
BACK                            | top-Y=20px  | x=left@20px  | size=100x44
SETTINGS (title)                | top-Y=22px  | x=centered   | size=200x40
MUSIC (label + toggle)          | top-Y=180px | x=centered   | size=400x56
SFX (label + toggle)            | top-Y=248px | x=centered   | size=400x56
HAPTIC (label + toggle)         | top-Y=316px | x=centered   | size=400x56
RESET SCORES                    | top-Y=440px | x=centered   | size=280x52
```

---

### Screen 10 — `TutorialScreen`

**A) BACKGROUND IMAGE (`ui/tutorial.png`)**

Bright, clean version of the cutting board — maximum cream saturation `#F5E6C8` with minimal stains (fresh board, first use). A large central empty rounded-rect card (440×520 px, top at y≈120) in `#F5E6C8` at 95% with `#1A9E3F` border (3 px) — fresh green suggests "beginner/welcome." Inside, a single large decorative swipe-arc illustration (a bold curved arrow in `#1A9E3F` sweeping from bottom-left to top-right, with motion lines) fills the upper two-thirds of the card — purely decorative, conveys "swipe" action visually without text. Below the arc illustration in the card's lower third, a blank text area is left for code labels. A single bottom-centered button frame (280×52, dark wood) sits at the card's foot. No text, no step counters.

**B) BUTTON LAYOUT (code-drawn)**

```
SKIP                            | top-Y=20px  | x=right@20px | size=100x40
HOW TO PLAY (title)             | top-Y=130px | x=centered   | size=300x40
[step illustration area]        | top-Y=180px | x=centered   | size=400x280
[step description label]        | top-Y=470px | x=centered   | size=400x60
NEXT / GOT IT                   | top-Y=560px | x=centered   | size=280x52
[step dots: ● ○ ○]              | top-Y=624px | x=centered   | size=120x20
```

---

## 4. Export Checklist

```
- icon_512.png (512x512)
- ui/main_menu.png (480x854)
- ui/mode_select.png (480x854)
- ui/classic_game.png (480x854)
- ui/zen_game.png (480x854)
- ui/arcade_game.png (480x854)
- ui/slice_result.png (480x854)
- ui/combo_detail.png (480x854)
- ui/leaderboard.png (480x854)
- ui/settings.png (480x854)
- ui/tutorial.png (480x854)
- feature_banner.png (1024x500)
```

---

## 5. Feature Banner — `feature_banner.png` (1024×500 landscape)

A sweeping overhead kitchen-stadium scene rendered in wide landscape: the entire canvas is a dramatic aerial close-up of a giant cutting board lit by a single spotlight from above-center, creating a warm elliptical pool of light (`#F5E6C8`) surrounded by deep mahogany shadows. From the left third of the canvas, a cascade of colorful fruits — watermelons, oranges, pineapples, kiwis, strawberries — fly upward in a curved arc as if launched into the air, each trailing a juice-splash comet tail in their respective colors. A bold diagonal slash line in electric white with a chromatic aberration fringe bisects the canvas from lower-left to upper-right, cutting directly through a watermelon that explodes mid-slice in a shower of red droplets. The game title **FRUIT SLASH** is set in a massive chunky rounded display typeface, positioned center-right, with each letter in `#FFD600` outlined in `#2D1A0E` (6 px stroke), a juice-wet gloss highlight on each letterform, and a tight red drop shadow. Below the title, a smaller subtitle line reads **"Slice · Combo · Survive"** in clean white Roboto-Bold. The color palette stays true: `#2D1A0E` deep shadows, `#C8621A` board mid-tones, `#FF3B2F` and `#FFD600` as energy accents. Full bleed, no device frame, no rounded corners, no white bars.