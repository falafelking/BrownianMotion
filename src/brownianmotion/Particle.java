/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brownianmotion;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author dkozloff11
 */
public class Particle {

    public Point pos;
    public Point vel;
    public Point force;
    private Point dimension;
    private Point drawloc;
    public double mass;
    public double damp=1;
    public Color color;
    public double radius;
    private double maxmass = 50;
    private double minmass = 1;

    public Particle(Point pos, Point dimension, Point drawloc, double mass, int radius) {
        this.mass = mass;
        this.pos = pos;
        this.color = Color.YELLOW;
        this.radius = radius;
        this.dimension = dimension;
        this.drawloc = drawloc;
        vel = new Point(0, 0);
        force = new Point(0, 0);
    }

    public Particle(Point pos, Point dimension, Point drawloc, double radius) {
        this.pos = pos;
        this.radius = radius;
        this.dimension = dimension;
        this.drawloc = drawloc;
        vel = new Point(0, 0);
        force = new Point(0, 0);
        init();
    }

    private void init() {
        Random r = new Random();
        mass = minmass + (maxmass - minmass) * r.nextDouble();
        chooseColor();
    }

    private void chooseColor() {
        if (mass < minmass + (maxmass - minmass) / 5) {
            color = Color.GREEN;
        } else if (mass < minmass + 2 * (maxmass - minmass) / 5) {
            color = Color.CYAN;
        } else if (mass < minmass + 3 * (maxmass - minmass) / 5) {
            color = Color.BLUE;
        } else if (mass < minmass + 4 * (maxmass - minmass) / 5) {
            color = Color.MAGENTA;
        } else if (mass < minmass + 5 * (maxmass - minmass) / 5) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }
    }

    public void update() {
        collidewall();
        pos.x = pos.x + vel.x;
        pos.y = pos.y + vel.y;
        vel.x = vel.x + force.x/mass;
        vel.y = vel.y + force.y/mass;
    }

    public void player(boolean x) {
        if (x) {
            color = Color.YELLOW;
        } else {
            init();
        }
    }

    public boolean clicked(Point mouse) {
        return Math.sqrt(Math.pow(mouse.x - drawloc.x - pos.x, 2) + Math.pow(mouse.y - drawloc.y - pos.y, 2)) <= radius;
    }

    public void collision(Particle other) {
        if (againstwall()) {
            collisionagainstwall(other);
        } else {
            Point newvel = vel.minus(pos.minus(other.pos).
                    scalar(((2 * other.mass) / (other.mass + mass)) * (vel.minus(other.vel).dot(pos.minus(other.pos)))
                    / (pos.minus(other.pos).dot(pos.minus(other.pos))))).scalar(damp);
            Point othernewvel = other.vel.minus(other.pos.minus(pos).
                    scalar(((2 * mass) / (mass + other.mass)) * (other.vel.minus(vel).dot(other.pos.minus(pos)))
                    / (other.pos.minus(pos).dot(other.pos.minus(pos))))).scalar(damp);
            vel = newvel;
            other.vel = othernewvel;
        }
    }

    public void collisionagainstwall(Particle other) {
        
    }

    public boolean againstwall() {
        if (pos.x + vel.x - radius < 0) {
            return true;
        }
        if (pos.x + vel.x + radius > dimension.x) {
            return true;
        }
        if (pos.y + vel.y - radius < 0) {
            return true;
        }
        if (pos.y + vel.y + radius > dimension.y) {
            return true;
        } else {
            return false;
        }
    }

    public boolean collide(Particle other) {
        if(other.pos.x - pos.x > radius + other.radius) {return false;}
        if(other.pos.y - pos.y > radius + other.radius) {return false;}
        return Math.sqrt(Math.pow(pos.x + vel.x - other.pos.x - other.vel.x, 2)
                + Math.pow(pos.y + vel.y - other.pos.y - other.vel.y, 2))
                <= other.radius + radius;
    }

    public boolean pointcollide(Point other, double radius) {
        return Math.sqrt(Math.pow(pos.x + vel.x - other.x, 2)
                + Math.pow(pos.y + vel.y - other.y, 2))
                <= radius + this.radius;
    }

    public void collidewall() {
        if (pos.x + vel.x - radius < 0) {
            pos.x = radius;
            vel.x = damp*Math.abs(vel.x);
        } else if (pos.x + vel.x + radius > dimension.x) {
            pos.x = dimension.x - radius;
            vel.x = -damp*Math.abs(vel.x);
        }
        if (pos.y + vel.y - radius < 0) {
            pos.y = radius;
            vel.y = damp*Math.abs(vel.y);
        } else if (pos.y + vel.y + radius > dimension.y) {
            pos.y = dimension.y - radius;
            vel.y = -damp*Math.abs(vel.y);
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int) (drawloc.x + pos.x - radius), (int) (drawloc.y + pos.y - radius), (int) radius * 2, (int) radius * 2);
        g.setColor(Color.BLACK);
        g.drawOval((int) (drawloc.x + pos.x - radius), (int) (drawloc.y + pos.y - radius), (int) radius * 2, (int) radius * 2);
    }
}
