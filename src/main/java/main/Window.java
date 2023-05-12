package main;

import DTO.VerletObject;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class Window extends JFrame implements Runnable {
    public static int frame = 0;
    private boolean running = false;
    private Solver solver;
    private BufferedImage buffer;
    private Graphics2D g2d;
    public final Integer SCREEN_WIDTH = 1000;
    public final Integer SCREEN_HEIGHT = 1000;
    public final float fps = 30f;

    public Window() {
        super("Verlet sym");
        this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        solver = new Solver(1 / fps);
        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) buffer.getGraphics();
        new Thread(this).start();
        this.setVisible(true);
    }

    private void update() {
        frame++;
        solver.update();
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        g2d.setColor(Color.darkGray);
        g2d.fillRect(0,0,1000,1000);
        g2d.setColor(Color.green);
        g2d.drawString("Cuadros: " + frame, 20, 60);
        g2d.drawString("Pelotas: " + solver.getObjects(), 20, 75);
        Vector3f constraint = solver.getConstraint();
        Ellipse2D constraint_background = new Ellipse2D.Float(
                constraint.x - constraint.z,
                constraint.y - constraint.z,
                2 * constraint.z,
                2 * constraint.z
        );
        g2d.setColor(Color.black);
        g2d.fill(constraint_background);
        solver.paint(g2d);
        g.drawImage(buffer, 0, 0, null);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            update();
            try {
                Thread.sleep((long) (1000.0/fps));
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
