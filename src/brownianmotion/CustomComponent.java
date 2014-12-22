/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brownianmotion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/**
 *
 * @author dkozloff11
 */
public class CustomComponent extends JPanel {

    private final int height = 700;
    private final int width = 1200;
    private final int sliderspace = 80;
    private final int sidespace = 20;
    private final Point partdimension = new Point(width - 2 * sidespace,
            height - sliderspace - sidespace);
    private final Point partdrawloc = new Point(sidespace, sliderspace);
    private final int buttonspace = 40;
    private final int title = 55;
    private static final int betweenspace = 5;
    private static final int sliderrows = 3;

    static private final double mindamp = 0;
    static private final double maxdamp = 1;
    static private final double maxforce = 6;
    static private final double minforce = .05;
    private final int maxrad = 20;
    private final int minrad = 5;
    private final double maxplayermass = 50;
    private final double minplayermass = 1;
    private final double mingravforce = .001;
    private final double maxgravforce = 1;
    private final int maxpart = 500;
    
    private final int updatedelay = 1;
    private Particle player;
    private ArrayList<Particle> particles;
    private int clicked = -1;
    private Random random;
    private ArrayList<Slider> sliders;

    private double damp;
    private double gravforce;
    boolean gravity;
    private double playerrad = 20;
    private double playermass;

    public CustomComponent() {
        damp = 1;
        gravforce = mingravforce;
        gravity = false;
        playermass = minplayermass;

        addKeyListener(new MyKeyListener());
        MyMouser m = new MyMouser();
        addMouseListener(m);
        addMouseMotionListener(m);
        player = new Particle(new Point(playerrad, playerrad), partdimension, partdrawloc, playermass, (int) playerrad);
        particles = new ArrayList<Particle>();

        sliders = new ArrayList<Slider>();
        sliders.add(new Slider(0, maxpart, new Point((width - sidespace * 3) / 2 - title, (sliderspace - betweenspace * 2) / sliderrows),
                new Point(sidespace + title, betweenspace)));
        random = new Random();
        sliders.add(new Slider(minforce, maxforce, new Point((width - sidespace * 3) / 2 - title, (sliderspace - betweenspace * 2) / sliderrows),
                new Point(sidespace + (width - sidespace * 3) / 2 + sidespace + title, betweenspace)));
        sliders.add(new Slider(mingravforce, maxgravforce, new Point((width - sidespace * 3) / 2 - title, (sliderspace - betweenspace * 2) / sliderrows),
                new Point(title + sidespace, betweenspace + (sliderspace - betweenspace * 2) / sliderrows)));
        sliders.add(new Slider(minplayermass, maxplayermass, new Point((width - sidespace * 3) / 2 - title, (sliderspace - betweenspace * 2) / sliderrows),
                new Point(title + sidespace + (width - sidespace * 3) / 2 + sidespace, betweenspace + (sliderspace - betweenspace * 2) / sliderrows)));
        sliders.add(new Slider(mindamp,maxdamp, new Point((width - sidespace * 3) / 2 - title, (sliderspace - betweenspace * 2) / sliderrows),
                new Point(title + sidespace, betweenspace + 2 * (sliderspace - betweenspace * 2) / sliderrows)));

        ActionListener update = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                checkSliders();
                collisionHandling();
                player.update();
                for (int i = 0; i < particles.size(); i++) {
                    particles.get(i).update();
                }
                repaint();
            }
        };
        new Timer(updatedelay, update).start();
    }

    private void collisionHandling() {
        for (int i = 0; i < particles.size(); i++) {
            if (player.collide(particles.get(i))) {
                player.collision(particles.get(i));
            }
            for (int j = i + 1; j < particles.size(); j++) {
                if (particles.get(i).collide(particles.get(j))) {
                    particles.get(i).collision(particles.get(j));
                }
            }
        }
    }

    private void checkSliders() {
        addParticles();
        updateGravity();
        updatePlayerMass();
        updateDamping();
    }

    private void addParticles() {
        //Add particles if necessary
        for (int i = particles.size(); i < (int) sliders.get(0).getValue(); i++) {
            makeNewParticle();
        }
        for (int i = particles.size(); i > (int) sliders.get(0).getValue(); i--) {
            particles.remove((int) ((particles.size() - 1) * random.nextDouble()));
        }
    }

    private void updateGravity() {
        //Set grav force
        if (gravforce != sliders.get(2).getValue()) {
            gravforce = sliders.get(2).getValue();
            setGravity();
        }
    }

    private void setGravity() {
        if (gravity) {
            player.force.y = gravforce * player.mass;
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).force.y = gravforce * particles.get(i).mass;
            }
        }
    }

    private void updatePlayerMass() {
        //Set mass
        if (player.mass != sliders.get(3).getValue()) {
            playermass = sliders.get(3).getValue();
            player.mass = playermass;
            if (gravity) {
                setGravity();
            }
        }
    }
    
    private void updateDamping() {
        if(damp != (double) 1-sliders.get(4).getValue()) {
            damp = 1-sliders.get(4).getValue();
            player.damp = damp;
            for(int i=0;i<particles.size();i++) {
                particles.get(i).damp = damp;
            }
        }
    }

    private void makeNewParticle() {
        Point check = new Point(0, 0);
        double rad = 0;
        boolean valid = false;
        while (!valid) {
            rad = minrad + (maxrad - minrad) * random.nextDouble();
            check.x = rad + (partdimension.x - 2 * rad) * random.nextDouble();
            check.y = rad + (partdimension.y - 2 * rad) * random.nextDouble();
            valid = true;
            if (player.pointcollide(check, rad)) {
                valid = false;
            }
            for (int i = 0; i < particles.size(); i++) {
                if (particles.get(i).pointcollide(check, rad)) {
                    valid = false;
                    break;
                }
            }
        }
        particles.add(new Particle(check, partdimension, partdrawloc, rad));
        particles.get(particles.size() - 1).damp = damp;
        if (gravity) {
            particles.get(particles.size() - 1).force.y = gravforce;
        }
    }
    

    public void kick() {
        double mass;
        double force = sliders.get(1).getValue();
        player.vel = new Point(Math.pow(-1, (int) (2 * random.nextDouble())) * force * player.mass * random.nextDouble(),
                Math.pow(-1, (int) (2 * random.nextDouble())) * force * player.mass * random.nextDouble());
        for (int i = 0; i < particles.size(); i++) {
            mass = particles.get(i).mass;
            particles.get(i).vel = new Point(Math.pow(-1, (int) (2 * random.nextDouble())) * force * mass * random.nextDouble(),
                    Math.pow(-1, (int) (2 * random.nextDouble())) * force * mass * random.nextDouble());
        }
    }

    public void gravity() {
        if (gravity) {
            gravity = false;
            player.force.y = 0;
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).force.y = 0;
            }
        } else {
            gravity = true;
            setGravity();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.drawRect(sidespace, sliderspace, width - 2 * sidespace, height - sliderspace - sidespace);
        player.draw(g);
        for (int j = 0; j < sliders.size(); j++) {
            sliders.get(j).draw(g);
        }
        for (int i = 0; i < particles.size(); i++) {
            particles.get(i).draw(g);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension a = new Dimension(width, height + buttonspace);
        return a;
    }

    private class MyKeyListener extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    player.force.y = sliders.get(1).getValue();
                    break;
                case KeyEvent.VK_UP:
                    player.force.y = -sliders.get(1).getValue();
                    break;
                case KeyEvent.VK_LEFT:
                    player.force.x = -sliders.get(1).getValue();
                    break;
                case KeyEvent.VK_RIGHT:
                    player.force.x = sliders.get(1).getValue();
                    break;
                case KeyEvent.VK_SPACE:
                    player.vel.x = 0;
                    player.vel.y = 0;
                    player.force.x = 0;
                    if (gravity) {
                        setGravity();
                    } else {
                        player.force.y = 0;
                        for (int i = 0; i < particles.size(); i++) {
                            particles.get(i).vel = new Point(0, 0);
                            particles.get(i).force = new Point(0, 0);

                        }
                    }
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    player.force.y = 0;
                    break;
                case KeyEvent.VK_UP:
                    player.force.y = 0;
                    break;
                case KeyEvent.VK_LEFT:
                    player.force.x = 0;
                    break;
                case KeyEvent.VK_RIGHT:
                    player.force.x = 0;
                    break;
            }
        }
    }

    class MyMouser extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            Point mouse = new Point(e.getX(), e.getY());
            for (int i = 0; i < sliders.size(); i++) {
                if (sliders.get(i).clicked(mouse)) {
                    clicked = i;
                    break;
                }
            }
            for (int i = 0; i < particles.size(); i++) {
                if (particles.get(i).clicked(mouse)) {
                    player.player(false);
                    particles.get(i).player(true);
                    particles.add(player);
                    player = particles.get(i);
                    particles.remove(i);
                    playerrad = player.radius;
                    break;
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (clicked >= 0) {
                sliders.get(clicked).dragged(new Point(e.getX(), e.getY()));
            }
        }

        public void mouseReleased(MouseEvent e) {
            clicked = -1;
        }
    }
}
