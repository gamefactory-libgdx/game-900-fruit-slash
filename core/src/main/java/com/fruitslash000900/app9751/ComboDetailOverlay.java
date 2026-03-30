package com.fruitslash000900.app9751;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Transient overlay shown when the player slices 3+ fruits in one swipe.
 * Drawn directly on top of the game using batch + shapeRenderer — no extra Stage needed.
 */
public class ComboDetailOverlay {

    private int comboCount = 0;
    private float timeLeft = 0f;
    private boolean visible = false;
    private final GlyphLayout layout = new GlyphLayout();

    public void show(int count) {
        this.comboCount = Math.min(count, Constants.COMBO_MAX_MULTIPLIER);
        this.timeLeft = Constants.COMBO_OVERLAY_DURATION;
        this.visible = true;
    }

    public boolean isVisible() { return visible; }

    public void update(float delta) {
        if (!visible) return;
        timeLeft -= delta;
        if (timeLeft <= 0f) { visible = false; timeLeft = 0f; }
    }

    /**
     * Render the overlay. Call between batch.begin() / batch.end() brackets.
     * The shapeRenderer is expected to be outside begin()/end() here — we manage its own begin/end.
     */
    public void render(SpriteBatch batch, ShapeRenderer sr, BitmapFont font,
                       float worldW, float worldH) {
        if (!visible) return;

        float fadeAlpha = Math.min(1f, timeLeft / 0.3f);
        float panelW = 260f;
        float panelH = 110f;
        float panelX = (worldW - panelW) * 0.5f;
        float panelY = (worldH - panelH) * 0.5f + 60f; // slightly above center

        // Panel background
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.72f * fadeAlpha);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();

        // Gold border
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1f, 0.84f, 0f, fadeAlpha);
        sr.rect(panelX, panelY, panelW, panelH);
        sr.end();

        // Combo text
        batch.begin();
        Color prev = new Color(font.getColor());
        font.setColor(1f, 0.87f, 0f, fadeAlpha);
        String line1 = comboCount + "x COMBO!";
        layout.setText(font, line1);
        font.draw(batch, line1, panelX + (panelW - layout.width) * 0.5f, panelY + panelH - 18f);
        font.setColor(1f, 1f, 1f, fadeAlpha * 0.85f);
        String line2 = "+" + (comboCount * 10) + " pts bonus";
        layout.setText(font, line2);
        font.draw(batch, line2, panelX + (panelW - layout.width) * 0.5f, panelY + panelH - 55f);
        font.setColor(prev);
        batch.end();
    }
}
