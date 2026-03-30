package com.fruitslash000900.app9751;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class JuiceParticle {

    public float x, y;
    public float vx, vy;
    public float radius;
    public float alpha;
    public boolean active;
    public Color color;

    public JuiceParticle(float x, float y, Color baseColor) {
        this.x = x;
        this.y = y;
        this.color = new Color(baseColor);

        float angle = MathUtils.random(0f, MathUtils.PI2);
        float speed = MathUtils.random(Constants.JUICE_SPEED_MIN, Constants.JUICE_SPEED_MAX);
        this.vx = MathUtils.cos(angle) * speed;
        this.vy = MathUtils.sin(angle) * speed;
        this.radius = MathUtils.random(4f, 9f);
        this.alpha = 1f;
        this.active = true;
    }

    public void update(float delta) {
        vy -= Constants.GRAVITY * 0.4f * delta;
        x += vx * delta;
        y += vy * delta;
        vx *= Math.max(0f, 1f - delta * 2.5f);
        alpha -= delta / Constants.PARTICLE_LIFETIME;
        if (alpha <= 0f) { alpha = 0f; active = false; }
    }
}
