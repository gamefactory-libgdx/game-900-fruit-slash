package com.fruitslash000900.app9751;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Factory for styled Scene2D buttons backed by the pre-copied button PNGs in assets/ui/buttons/.
 *
 * <p>Always call {@code makeRectStyle} or {@code makeRoundStyle} rather than constructing
 * raw {@link TextButton} instances, so every screen shares a consistent visual language.</p>
 */
public final class UiFactory {

    private UiFactory() {}

    // ── Styles ────────────────────────────────────────────────────────────────

    /** Rectangle button style — use for primary / secondary action buttons. */
    public static TextButton.TextButtonStyle makeRectStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = font;
        s.up   = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_rectangle_depth_gradient.png", Texture.class)));
        s.down = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_rectangle_depth_flat.png", Texture.class)));
        return s;
    }

    /** Round button style — use for small icon / action buttons. */
    public static TextButton.TextButtonStyle makeRoundStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font = font;
        s.up   = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_round_depth_gradient.png", Texture.class)));
        s.down = new TextureRegionDrawable(new TextureRegion(
                     mgr.get("ui/buttons/button_round_depth_flat.png", Texture.class)));
        return s;
    }

    // ── Convenience constructors ──────────────────────────────────────────────

    /** Create and size a rectangle button in one call. */
    public static TextButton makeButton(String label, TextButton.TextButtonStyle style,
                                        float w, float h) {
        TextButton btn = new TextButton(label, style);
        btn.setSize(w, h);
        return btn;
    }

    /** Create a primary-size (280×56) rectangle button. */
    public static TextButton makePrimaryButton(String label, TextButton.TextButtonStyle style) {
        return makeButton(label, style, Constants.BTN_W_PRIMARY, Constants.BTN_H_PRIMARY);
    }

    /** Create a secondary-size (220×50) rectangle button. */
    public static TextButton makeSecondaryButton(String label, TextButton.TextButtonStyle style) {
        return makeButton(label, style, Constants.BTN_W_SECONDARY, Constants.BTN_H_SECONDARY);
    }

    /** Create a small (160×44) rectangle button. */
    public static TextButton makeSmallButton(String label, TextButton.TextButtonStyle style) {
        return makeButton(label, style, Constants.BTN_W_SMALL, Constants.BTN_H_SMALL);
    }

    /** Create a round icon button (56×56). */
    public static TextButton makeRoundButton(String label, TextButton.TextButtonStyle style) {
        return makeButton(label, style, Constants.BTN_ROUND_SIZE, Constants.BTN_ROUND_SIZE);
    }
}
