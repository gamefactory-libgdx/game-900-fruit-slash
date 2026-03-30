# Audit Screens

Read-only scan of all screens. Report every issue found — do NOT fix anything. Use this before `/fix-ui-bugs` to understand the scope of problems.

---

## Step 1 — Discover files

```
find core/src -name "*.java" | sort
cat core/src/main/java/*/app/Constants.java
cat assets/ASSETS_MANIFEST.json
```

---

## Step 2 — For each screen file, check and report

Read every `*Screen.java` and report findings under these categories:

### Background rendering
- Does it call `glClearColor` without drawing a background image? → **MISSING BACKGROUND**
- Does it draw a background image that doesn't exist in `assets/backgrounds/`? → **WRONG FILENAME**
- Does it draw the background AFTER UI elements (wrong draw order)? → **WRONG DRAW ORDER**

### Fonts
- Does `MainGame.java` set `param.borderWidth` before each `generateFont()`? → **NO FONT OUTLINE** if missing
- Are any font sizes smaller than 14? → **FONT TOO SMALL**
- Is `game.fontBody` used for button labels? (correct) or `skin.getFont()`? (wrong) → **WRONG FONT SOURCE**

### Layout / bounds
- Any `fontX.draw(batch, text, x, y)` where Y > WORLD_HEIGHT? → **TEXT OFF TOP**
- Any button whose `y + height > WORLD_HEIGHT`? → **BUTTON OFF TOP**
- Any button whose `x + width > WORLD_WIDTH`? → **BUTTON OFF RIGHT**
- Any hardcoded pixel values instead of Constants? → **MAGIC NUMBERS**

### Lifecycle
- Does `resize()` call `viewport.update(width, height, true)`? → **MISSING RESIZE** if not
- Does `show()` call `Gdx.input.setInputProcessor(...)`? → **INPUT NOT REGISTERED** if not
- Does `dispose()` unload textures it loaded? → **TEXTURE LEAK** if not

### Audio (check archetype in ASSETS_MANIFEST.json)
- For rhythm/music archetype: does it use `sfx_hit.ogg` for lane taps? → **WRONG HIT SOUND**
- Does every screen transition play a button click sound? → **MISSING CLICK SFX** if not
- Does game-over screen use `playMusicOnce` (not loop)? → **WRONG GAME OVER MUSIC** if not

---

## Step 3 — Summary table

Output a table:

| Screen | Missing BG | No Outline | Text OOB | Button OOB | Missing Resize | Other |
|--------|-----------|------------|----------|------------|----------------|-------|
| MainMenuScreen | yes/no | yes/no | yes/no | yes/no | yes/no | — |

Then list the top 3 most impactful issues to fix first.
