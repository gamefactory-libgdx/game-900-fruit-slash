package com.fruitslash000900.app9751;

import com.badlogic.gdx.math.MathUtils;

public class FruitHalf {

    public float x, y;
    public float vx, vy;
    public float radius;
    public FruitObject.FruitType type;
    public boolean isLeft;
    public float rotation;
    public float rotationSpeed;
    public float alpha;
    public boolean active;

    public FruitHalf(float x, float y, float vx, float vy,
                     FruitObject.FruitType type, boolean isLeft) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.type = type;
        this.isLeft = isLeft;
        this.radius = Constants.FRUIT_RADIUS * 0.85f;
        this.alpha = 1f;
        this.active = true;
        this.rotation = MathUtils.random(0f, 360f);
        this.rotationSpeed = MathUtils.random(-180f, 180f);
    }

    public void update(float delta) {
        vy -= Constants.GRAVITY * 0.55f * delta;
        x += vx * delta;
        y += vy * delta;
        rotation += rotationSpeed * delta;
        alpha -= delta / Constants.HALF_FADE_DURATION;
        if (alpha <= 0f) { alpha = 0f; active = false; }
    }
}
