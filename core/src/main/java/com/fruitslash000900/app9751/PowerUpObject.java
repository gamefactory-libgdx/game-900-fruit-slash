package com.fruitslash000900.app9751;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class PowerUpObject {

    public enum PowerUpType {
        FRENZY, FREEZE, DOUBLE_POINTS, EXTRA_LIFE;

        public Color getColor() {
            switch (this) {
                case FRENZY:        return new Color(1.00f, 0.30f, 0.00f, 1f);
                case FREEZE:        return new Color(0.40f, 0.82f, 1.00f, 1f);
                case DOUBLE_POINTS: return new Color(1.00f, 0.87f, 0.00f, 1f);
                default:            return new Color(0.40f, 1.00f, 0.40f, 1f); // EXTRA_LIFE
            }
        }

        public String getLabel() {
            switch (this) {
                case FRENZY:        return "FRENZY";
                case FREEZE:        return "FREEZE";
                case DOUBLE_POINTS: return "2X";
                default:            return "+LIFE";
            }
        }

        public static PowerUpType random() {
            PowerUpType[] values = values();
            return values[MathUtils.random(values.length - 1)];
        }
    }

    public float x, y;
    public float vx, vy;
    public float radius;
    public PowerUpType type;
    public boolean active;
    public float pulse;

    public PowerUpObject(float x, float y, float vx, float vy, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.type = type;
        this.radius = Constants.FRUIT_RADIUS;
        this.active = true;
        this.pulse = 0f;
    }

    public void update(float delta) {
        vy -= Constants.GRAVITY * delta;
        x += vx * delta;
        y += vy * delta;
        pulse += delta * 4f;
    }

    public boolean isOffScreen() {
        return y < -radius * 3f && vy < 0;
    }

    public boolean intersectsSegment(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float lenSq = dx * dx + dy * dy;
        if (lenSq < 0.001f) return false;
        float t = MathUtils.clamp(((x - x1) * dx + (y - y1) * dy) / lenSq, 0f, 1f);
        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;
        float distSq = (x - closestX) * (x - closestX) + (y - closestY) * (y - closestY);
        return distSq <= radius * radius;
    }
}
