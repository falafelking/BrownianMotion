/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brownianmotion;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author dkozloff11
 */
public class Slider {
    
    private double min;
    private double max;
    private double sliderloc;
    private Point drawloc;
    private Point dimension;
    private int sidespace = 15;
    private Point bardimensions;
    private boolean clicked;
    
    public Slider(double min, double max, Point dimension, Point drawloc) {
        this.min = min;
        this.max = max;
        this.dimension = dimension;
        this.drawloc = drawloc;
        sliderloc = 0;
        bardimensions = new Point(15,dimension.y*.6);
    }
    
    public boolean clicked(Point mouse) {
        return (mouse.x < drawloc.x + dimension.x && mouse.x > drawloc.x
                && mouse.y < drawloc.y + dimension.y && mouse.y > drawloc.y); 
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect((int)drawloc.x,(int)drawloc.y,(int)dimension.x,(int)dimension.y);
        g.drawLine((int) (drawloc.x + sidespace),(int) (drawloc.y + dimension.y/2),
                (int) (drawloc.x + dimension.x - sidespace), (int) (drawloc.y + dimension.y/2));
        g.fillRect((int) ((sliderloc/(max - min)) * (dimension.x-2*sidespace) + drawloc.x + sidespace - bardimensions.x/2),
                (int) (drawloc.y + dimension.y/2 - bardimensions.y/2),(int)bardimensions.x,(int)bardimensions.y);
    }
    
    public void dragged(Point mouse) {
        if (mouse.x <= drawloc.x + sidespace) {sliderloc = 0;} 
        else if (mouse.x >= drawloc.x + dimension.x - sidespace) {sliderloc = max-min;}
        else {sliderloc = ((mouse.x - drawloc.x - sidespace)/(dimension.x - 2*sidespace)) * (max - min); }
    }
    
    public double getValue() {
        return sliderloc + min;
    }
    
}
