package main;

import DTO.VerletObject;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Solver {
    public List<VerletObject> objects;

    private final Vector2f gravity;
    private final Vector2f screenCenter;

    private Vector2f to_obj;
    private Vector2f collision_axis = new Vector2f();
    public int amount_of_objects;
    private final int radius = 250;
    private final int sub_steps = 16;
    private final float x_coordinate_center = 500;
    private final float y_coordinate_center = 400;
    private final float dt;
    private final float sub_dt;

    public Solver(float dt) {
        this.amount_of_objects = 200;
        this.to_obj = new Vector2f();
        this.screenCenter = new Vector2f(x_coordinate_center, y_coordinate_center);
        this.gravity = new Vector2f(0, 2000);
        this.objects = new ArrayList<>();
        this.dt = dt;
        this.sub_dt = dt/sub_steps;
    }

    private void generateObjects() {
        if(objects.size() < (int) (Window.frame * 4 * dt) && objects.size() < amount_of_objects) {
            Vector2f vector = new Vector2f(x_coordinate_center + radius - VerletObject.radius, y_coordinate_center);
            /* generate color */
            to_obj.set(vector.x(), vector.y());
            to_obj.sub(screenCenter);
            Color color = new Color(
                    (float) Math.random(),
                    (float) Math.random(),
                    (float) Math.random()
            );

            VerletObject obj = new VerletObject(vector, color);
            AtomicBoolean canRender = new AtomicBoolean(true);
            objects.forEach(object -> {
                to_obj.set(object.position_current).sub(obj.position_current);
                if(to_obj.length() < 2 * VerletObject.radius) canRender.set(false);
            });
            if(canRender.get()) objects.add(obj);
        }
    }

    private void updatePositions(float dt) {
        objects.forEach(verletObject -> verletObject.updatePosition(dt));
    }

    private void applyGravity() {
        objects.forEach(verletObject -> verletObject.accelerate(gravity));
    }

    private void applyConstraint(List<VerletObject> objects) {
        objects.forEach(verletObject -> {
            to_obj.set(verletObject.position_current.x(), verletObject.position_current.y());
            to_obj.sub(screenCenter);
            float dist = to_obj.length();
            if(dist >= radius - VerletObject.radius) {
                Vector2f n = new Vector2f(to_obj);
                n.normalize().mul(radius - VerletObject.radius);
                verletObject.position_current = new Vector2f(screenCenter);
                verletObject.position_current.add(n);
            }
        });
    }

    private void solveCollisions() {
        for (int i = 0; i < objects.size(); ++i) {
            VerletObject object_1 = objects.get(i);
            for (int k = i+1; k < objects.size(); ++k) {
                VerletObject object_2 = objects.get(k);
                collision_axis.set(object_1.position_current).sub(object_2.position_current);
                float dist = collision_axis.length();
                float min_dist = object_1.radius + object_2.radius;
                if (dist <= min_dist) {
                    Vector2f n = collision_axis.normalize();
                    float delta = min_dist - dist;
                    object_1.position_current.add(n.mul(0.5f * delta));
                    object_2.position_current.sub(n.mul(0.5f * delta));
                }
            }
        }
    }

    public void paint(Graphics2D g2d) {
        Ellipse2D circle = new Ellipse2D.Float();
        for(VerletObject obj : objects) {
            circle.setFrame(
                    obj.position_current.x - obj.radius,
                    obj.position_current.y - obj.radius,
                    2 * obj.radius,
                    2 * obj.radius
            );
            g2d.setColor(obj.color);
            g2d.fill(circle);
        }
    }

    public Vector3f getConstraint() {
        return new Vector3f(x_coordinate_center, y_coordinate_center, radius);
    }

    public int getObjects() {
        return objects.size();
    }

    public void update() {
        for(int i = 0; i<sub_steps; ++i) {
            generateObjects();
            applyGravity();
            applyConstraint(objects);
            solveCollisions();
            updatePositions(sub_dt);
        }
    }
}
