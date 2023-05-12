package DTO;

import org.joml.Vector2f;

import java.awt.*;

public class VerletObject {

    public Vector2f position_current;
    public Vector2f position_old;
    public Vector2f acceleration;
    public Vector2f friction;
    private Vector2f velocity;
    public static final float radius = 15;
    public final Color color;

    public VerletObject(Vector2f position_current, Color color) {
        this.position_current = position_current;
        this.position_old = new Vector2f(position_current.x(), position_current.y());
        this.acceleration = new Vector2f(0,0);
        this.friction = new Vector2f();
        this.velocity = new Vector2f();
        this.color = color;
    }

    public void updatePosition(float dt) {
        velocity.set(position_current);
        velocity.sub(position_old);
        position_old.set(position_current);
        acceleration.mul(dt*dt*0.90f);
        position_current.add(velocity).add(acceleration);
        if(velocity.length() > 0) {
            friction.set(velocity).mul(-0.005f);
            position_current.add(friction);
        }
        acceleration.set(0,0);
    }

    public void accelerate(Vector2f acc) {
        acceleration.add(acc);
    }
}
