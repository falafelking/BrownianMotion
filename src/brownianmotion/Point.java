/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brownianmotion;

/**
 *
 * @author Sanfer D'souza
 */
public class Point {
  
    public double x;   
    public double y;
 
    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public boolean equals(Point other) {
      if(other.x == x && other.y == y){return true;}
      return false;
    }
    
    public Point minus(Point other) {
        return new Point(x - other.x,y-other.y);
    }
    
    public Point plus(Point other) {
        return new Point(x+other.x,y+other.y);
    }
    
    public double dot(Point other) {
        return x*other.x + y*other.y;
    }
    
    public Point scalar(double c) {
        return new Point(c*x,c*y);
    }
    
}
