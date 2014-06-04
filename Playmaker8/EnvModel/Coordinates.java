package EnvModel;

/*
 * Coordinates.java
 *
 * Created on August 2, 2001, 4:02 PM
 */


/**
 *
 * @author  eu779
 * @version 
 */
public class Coordinates extends java.lang.Object {

    private int x, y;
    
    /** Creates new Coordinates */
    public Coordinates() {
        x = 0;
        y = 0;
    }
    
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public  Coordinates(Coordinates coordinates) {
        x = coordinates.getX();
        y = coordinates.getY();
    }
    
    public int getX() {return x;}
    public int getY() {return y;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

}
