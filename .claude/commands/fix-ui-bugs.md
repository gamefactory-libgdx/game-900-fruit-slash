# Fix UI Bugs

Scan every screen in this game for common UI bugs and fix them all. Work through each issue type systematically.

---

## Step 1 — Discover all screen files and constants

```
find core/src -name "*.java" | sort
cat core/src/main/java/*/app/Constants.java
```

Note WORLD_WIDTH, WORLD_HEIGHT, BUTTON_WIDTH_*, BUTTON_HEIGHT, font size constants.

---

## Step 2 — Check every screen for missing background image

For each *Screen.java file, read it and check:

**PROBLEM:** Screen uses only `glClearColor(...)` with no `batch.draw(...)` of a background texture — produces a flat solid-colour screen.

**HOW TO DETECT:** `glClearColor` present but no `manager.get(..., Texture.class)` call for a background path.

**HOW TO FIX:**
1. Run `ls assets/backgrounds/menu/` and `ls assets/backgrounds/game/` to discover available files
2. Menu/settings/leaderboard/gameover screens → pick first file from `assets/backgrounds/menu/`
3. Gameplay screens → pick first file from `assets/backgrounds/game/`
4. Add to constructor — load the background via `manager` if not already loaded, then `manager.finishLoading()`
5. At the **very top** of `render()`, before any ShapeRenderer or other drawing:

```java
game.batch.begin();
game.batch.draw(game.manager.get(BG, Texture.class),
        0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
game.batch.end();
```

6. Add `import com.badlogic.gdx.graphics.Texture;` if missing
7. In `dispose()`, unload: `if (game.manager.isLoaded(BG)) game.manager.unload(BG);`

---

## Step 3 — Check font generation for missing outline

Read `MainGame.java`. Find every `FreeTypeFontParameter` block.

**PROBLEM:** `param.borderWidth` is never set — text renders flat with no outline, looks poor on varied backgrounds.

**HOW TO FIX:** For each font generation block, add before `generateFont()`:

```java
param.borderWidth = 2;                              // 1 for sizes <=16, 2 for 17-36, 3 for >=37
param.borderColor = new Color(0f, 0f, 0f, 0.85f);
```

Add `import com.badlogic.gdx.graphics.Color;` to MainGame.java if missing.

---

## Step 4 — Check for text drawn outside world bounds

Read every screen `render()` method. Find every `fontX.draw(game.batch, text, x, y)` call.

**PROBLEM:** Hardcoded Y > WORLD_HEIGHT or < 0, or X > WORLD_WIDTH — text is invisible or clipped.

**HOW TO FIX:** Use GlyphLayout for centred text and proportional positions:

```java
layout.setText(game.fontBody, label);
float x = (Constants.WORLD_WIDTH - layout.width) / 2f;
float y = Constants.WORLD_HEIGHT * 0.75f;
```

---

## Step 5 — Check for buttons drawn outside world bounds

Find every `UiFactory.drawButton(...)` call and every manual button rectangle.

**PROBLEM:** Button `y + height > WORLD_HEIGHT` or `x + width > WORLD_WIDTH` — button clipped off screen.

**HOW TO FIX:** Use proportional positions — never raw pixel values like `800f` on an 854px world:

```java
float btnX = (Constants.WORLD_WIDTH  - btnW) / 2f;
float btnY = Constants.WORLD_HEIGHT  * 0.5f;
```

---

## Step 6 — Check resize() in every screen

Every screen must call `viewport.update(width, height, true)` in `resize()`.

**HOW TO FIX:**
```java
@Override
public void resize(int width, int height) {
    viewport.update(width, height, true);
}
```

---

## Step 7 — Build and verify

```
./gradlew android:assembleDebug --no-daemon 2>&1 | tail -30
```

Fix any compilation errors. Loop until BUILD SUCCESSFUL.

---

## Step 8 — Report

List every fix applied: which screen, which issue, what was changed.
List any issues found but NOT fixed with explanation.
Report final build status.
