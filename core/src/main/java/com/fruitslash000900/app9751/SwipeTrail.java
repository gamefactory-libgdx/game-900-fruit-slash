package com.fruitslash000900.app9751;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Tracks swipe trail points and renders a fading arc.
 * Points are recorded during a touch-drag gesture.
 */
public class SwipeTrail {

    private static final int MAX_POINTS = 32;

    private final Array<Vector2> points = new Array<>(MAX_POINTS);
    private float idleTime = 0f;
    private boolean active = false;

    /** Start a new swipe at the given world position. */
    public void begin(float x, float y) {
        points.clear();
        points.add(new Vector2(x, y));
        idleTime = 0f;
        active = true;
    }

    /** Extend the swipe to this world position. */
    public void addPoint(float x, float y) {
        if (points.size >= MAX_POINTS) points.removeIndex(0);
        points.add(new Vector2(x, y));
        idleTime = 0f;
        active = true;
    }

    /** End the swipe (finger lifted). */
    public void end() {
        active = false;
        idleTime = Constants.TRAIL_FADE;
    }

    public void update(float delta) {
        if (!active) {
            idleTime -= delta;
            if (idleTime <= 0f) points.clear();
        }
    }

    public boolean hasPoints() { return points.size >= 2; }

    /** Last recorded point (world coordinates). */
    public Vector2 lastPoint() {
        return points.size > 0 ? points.get(points.size - 1) : null;
    }

    /** Second-to-last point — the start of the current micro-segment. */
    public Vector2 prevPoint() {
        return points.size > 1 ? points.get(points.size - 2) : null;
    }

    public void render(ShapeRenderer sr) {
        if (points.size < 2) return;
        int n = points.size;
        for (int i = 1; i < n; i++) {
            float frac = (float) i / n;
            float alpha = frac * (active ? 0.85f : Math.max(0f, idleTime / Constants.TRAIL_FADE) * 0.5f);
            float width = Constants.TRAIL_WIDTH_MIN
                    + frac * (Constants.TRAIL_WIDTH_MAX - Constants.TRAIL_WIDTH_MIN);
            Vector2 a = points.get(i - 1);
            Vector2 b = points.get(i);
            sr.setColor(1f, 1f, 1f, alpha);
            sr.rectLine(a.x, a.y, b.x, b.y, width);
        }
    }
}
