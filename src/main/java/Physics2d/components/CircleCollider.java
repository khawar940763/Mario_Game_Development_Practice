package Physics2d.components;

import components.Component;

public class CircleCollider extends Collider {
    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private float radius = 1f;
}
