package com.fruitslash000900.app9751;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class FruitObject {

    public enum FruitType {
        APPLE, WATERMELON, ORANGE, BANANA, STRAWBERRY, PINEAPPLE, KIWI;

        public Color getColor() {
            switch (this) {
                case APPLE:      return new Color(0.98f, 0.20f, 0.14f, 1f);
                case WATERMELON: return new Color(0.10f, 0.62f, 0.25f, 1f);
                case ORANGE:     return new Color(1.00f, 0.50f, 0.05f, 1f);
                case BANANA:     return new Color(1.00f, 0.87f, 0.10f, 1f);
                case STRAWBERRY: return new Color(0.85f, 0.08f, 0.22f, 1f);
                case PINEAPPLE:  return new Color(1.00f, 0.72f, 0.05f, 1f);
                default:         return new Color(0.42f, 0.65f, 0.14f, 1f); // KIWI
            }
        }

        public Color getInnerColor() {
            switch (this) {
                case APPLE:      return new Color(1.00f, 0.75f, 0.70f, 1f);
                case WATERMELON: return new Color(0.98f, 0.30f, 0.30f, 1f);
                case ORANGE:     return new Color(1.00f, 0.85f, 0.55f, 1f);
                case BANANA:     return new Color(1.00f, 0.97f, 0.70f, 1f);
                case STRAWBERRY: return new Color(1.00f, 0.65f, 0.70f, 1f);
                case PINEAPPLE:  return new Color(1.00f, 0.95f, 0.60f, 1f);
                default:         return new Color(0.80f, 0.90f, 0.40f, 1f); // KIWI
            }
        }

        public int getScore() {
            switch (this) {
                case WATERMELON: case KIWI: return 20;
                case STRAWBERRY: case PINEAPPLE: return 15;
                default: return 10;
            }
        }

        public static FruitType random() {
            FruitType[] values = values();
            return values[MathUtils.random(values.length - 1)];
        }
    }

    public float x, y;
    public float vx, vy;
    public float radius;
    public FruitType type;
    public boolean active;
    public boolean sliced;
    public float rotation;
    public float rotationSpeed;

    public FruitObject(float x, float y, float vx, float vy, FruitType type) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.type = type;
        this.radius = Constants.FRUIT_RADIUS;
        this.active = true;
        this.sliced = false;
        this.rotation = MathUtils.random(0f, 360f);
        this.rotationSpeed = MathUtils.random(-Constants.FRUIT_ROTATION_SPEED,
                Constants.FRUIT_ROTATION_SPEED);
    }

    public void update(float delta) {
        vy -= Constants.GRAVITY * delta;
        x += vx * delta;
        y += vy * delta;
        rotation += rotationSpeed * delta;
    }

    /** True when the fruit has fallen below the screen and is still falling. */
    public boolean isOffScreen() {
        return y < -radius * 3f && vy < 0;
    }

    /** Segment–circle intersection test for swipe detection. */
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
