package com.fruitslash000900.app9751;

import com.badlogic.gdx.math.MathUtils;

public class BombObject {

    public float x, y;
    public float vx, vy;
    public float radius;
    public boolean active;
    public boolean exploded;
    public float rotation;
    public float rotationSpeed;

    public BombObject(float x, float y, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = Constants.FRUIT_RADIUS * 0.85f;
        this.active = true;
        this.exploded = false;
        this.rotation = MathUtils.random(0f, 360f);
        this.rotationSpeed = MathUtils.random(-90f, 90f);
    }

    public void update(float delta) {
        vy -= Constants.GRAVITY * delta;
        x += vx * delta;
        y += vy * delta;
        rotation += rotationSpeed * delta;
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
