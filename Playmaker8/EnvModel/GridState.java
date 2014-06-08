/*
 * GridState.java
 *
 * Created on May 29, 2002, 12:41 PM
 */

package EnvModel;

import java.util.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * This class defines a state representation for a grid based environment
 * in which each environment object is to be contained within a square
 */
public abstract class GridState extends State {

    //private member variables
    ArrayList squares;
    int xSize;    /**The number of columns in the grid world*/
    int ySize;   /**The number of rows in the grid world*/
    int worldSize; /**Total number of squares in the grid*/
    
    private static int DEFAULT_XSIZE = 4;
    private static int DEFAULT_YSIZE = 4;
    
    public static final int
        NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3, BELOW = 4;

    /** create new grid state with default size*/
    public GridState() {
        this(DEFAULT_XSIZE, DEFAULT_YSIZE);
    }
    
    /**The number of columns in the grid world*/
    public int getXSize() {
        return xSize;
    }
  
    /**The number of rows in the grid world*/
    public int getYSize() {
        return ySize;
    }
    
    /** Creates new GridState with xSize and ySize*/
    public GridState(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        worldSize = xSize * ySize;
        squares = new ArrayList(worldSize);
      
        for (int currentSquare = 0; currentSquare < worldSize; currentSquare ++) {
            squares.add(currentSquare, new SquareContents(0));
         }
    }
    
    /**Convert x,y to square number in the arrayList*/
    private int coordinatesToSquare(int xCoordinate, int yCoordinate) {
        int square = xCoordinate + yCoordinate * xSize;
        if ((xCoordinate < 0) || (xCoordinate >= xSize))
            return -1;
        if ((yCoordinate < 0) || (yCoordinate >= ySize))
            return -1;
        return square;
    }
    
    /**convert coordinates to square number in the arrayList*/
    private int coordinatesToSquare(Coordinates coordinates) {
        return coordinatesToSquare(coordinates.getX(), coordinates.getY());
    }
    
    /**returns the coordinates of the square in the arrayList*/
    private Coordinates squareToCoordinates(int square) {
        Coordinates position = new Coordinates();
        position.setY(square/xSize);
        position.setX(square - position.getY()*xSize);
        
        return position;
    }
    
    
    /*advance for step based agent environment*/
    /*Go through all Environment Objects and advance them
     *by a one step.*/
    public void advanceStep() {
        
        //get a list of environment objects 
        //advance the environment object by one step
        ArrayList environmentObjects = this.getEnvironmentObjects();
        
        for (int i = 0; i < environmentObjects.size(); i ++) {
            EnvironmentObject envObject =  (EnvironmentObject)environmentObjects.get(i);
            envObject.advanceStep();
        }
    }   
    
    /*Go through all Environment Objects and advance them
     *by a timestep.*/
    public void advance(float time) {
        
        //get the list of environment objects
        //advance the environment objects by time
        ArrayList environmentObjects = this.getEnvironmentObjects();
       
        for (int i = 0; i < environmentObjects.size(); i ++) {
            EnvironmentObject envObject =  (EnvironmentObject)environmentObjects.get(i);
            envObject.advance(time);
        }
    }
   
    /**Return a SquareContents object specified by
     *its position in the total list of squares. Create the SquareContents
     *object if this square is empty*/
    private SquareContents getCreateSquareContents(int whichSquare) {
        if (squares.get(whichSquare) == null) {
            squares.set(whichSquare, new SquareContents(0));
        }
  
        java.lang.Object obj = squares.get(whichSquare);
        return (SquareContents)obj;
    }
    
    /**Return a SquareContents object specified by
     *its coordinates.*/
    public SquareContents getCreateSquareContents(Coordinates position) {
        int whichSquare = coordinatesToSquare(position.getX(), position.getY());
        return getCreateSquareContents(whichSquare);
    }
    
     /**Return a SquareContents object specified by
     *its x and y coordinates.*/
    public SquareContents getCreateSquareContents(int x, int y) {
        int whichSquare = coordinatesToSquare(x, y);
        return getCreateSquareContents(whichSquare);
    }
    
     /**Return a SquareContents object of square specified
     **by its co-ordinates.*/
    public SquareContents getSquareContents(Coordinates position) {
        return getSquareContents(position.getX(), position.getY());
    }
    
    /**Return a SquareContents object of square specified
     **by its x, y co-ordinates.*/
    public SquareContents getSquareContents(int x, int y) {
        int whichSquare = coordinatesToSquare(x, y);
        if (whichSquare != -1) //-1 indicates outside the world (wall)
            return  getSquareContents(whichSquare);
        else
            return null;
    }
    
    /*Returns true if the requested grid square is inside the grid*/
    public boolean inBounds(int x, int y) {
        if ((x < 0) || (x >= xSize))
            return false;
        if ((y < 0) || (y >= ySize))
           return false;
        
        return true;
    }
    
    /**Return a SquareContents object of all contents of square specified by
    *its position in the total list of squares.*/
    private SquareContents getSquareContents(int whichSquare) {
        if (whichSquare != -1) //-1 indicates outside the world (wall)
            return (SquareContents)squares.get(whichSquare);
        else
            return null;
    }
    
    /**find the position in the world of a certain object
     *in coordinates. Returns (-1,-1) if the object is not present*/
    public Coordinates whereIs(EnvironmentObject object)
    {
        //for the moment just do an exhastive search. We could easily
        //index the world to make it easier
        for (int i = 0; i < worldSize; i++)
        {
            SquareContents contents = getSquareContents(i);
            if (contents != null) {
                for (int j = 0; j < contents.size(); j++) {
                    if (contents.getEnvironmentObject(j) == object)
                        return squareToCoordinates(i);
                }
            }
        }
        
        //return -1 if object was not found
        return new Coordinates(-1,-1);
    }
    
    /*remove object from square given by position*/
    public void remove(EnvironmentObject object, Coordinates position)
    {
        SquareContents contents = getSquareContents(position);
        contents.removeEnvironmentObject(object);
    }
    
    /*add object to square given by position*/
    public void add(EnvironmentObject object, Coordinates position)
    {
        SquareContents contents = getCreateSquareContents(position);
        contents.addEnvironmentObject(object);
    }
    
    /**Move object in direction NORTH, SOUTH, EAST or WEST;
     */
    public boolean move(EnvironmentObject object, int direction)
    {
        /*stores where the object will move to*/
        Coordinates coordinates = whereIs(object);
        /*stores where the object has moved from*/
        Coordinates oldCoordinates = new Coordinates(coordinates);
         
        switch (direction)
        {
            case NORTH: {
                if (coordinates.getY() > 0)
                    coordinates.setY(coordinates.getY() -1);
                break;
            }
            case SOUTH: {
                if (coordinates.getY() < ySize -1)
                    coordinates.setY(coordinates.getY() + 1);
                break;
            }
            case EAST: {
                if (coordinates.getX() < xSize -1)
                    coordinates.setX(coordinates.getX() +1);
                break;
            }
            case WEST: {
                if (coordinates.getX() > 0)
                    coordinates.setX(coordinates.getX() - 1);
                break;
            }
        }
        
        int newPosition = coordinatesToSquare(coordinates);
        
        if (newPosition != -1) {
            if (newPosition  > worldSize)
                return false;
            else {
                remove(object, oldCoordinates);
                add(object, coordinates);
                return true;
            }
        }
        else
            return false;
    }
    
    /*Output text strings of all square contents to output*/
    public void outputState() {
        super.outputState();
        LogFiles logfile = LogFiles.getInstance();
        for (int i = 0; i <squares.size(); i++) {
            SquareContents squareContents = getSquareContents(i);
            if ((squareContents != null) && (squareContents.size() > 0)) {   
                logfile.print ("\nContents of square " + i + ": ",1);
                for (int j = 0; j < squareContents.size(); j++) {
                    (squareContents.getEnvironmentObject(j)).output();
                }
            }
        }
        
    }
    
    /*Returns a list of all objects contained in the environment, including
     *agent bodies*/
    public ArrayList getEnvironmentObjects() {
        //Perhaps add this if we thnk the superclass should have objects
        //ArrayList envObjects = super.getEnvironmentObjects();
        //otherwise
        ArrayList envObjects = new ArrayList();
        /*Itterate through the contents of all squares adding objects
        * which are contents of the squares to the ArrayList*/
        for (int i = 0; i <squares.size(); i++) {
            SquareContents squareContents = getSquareContents(i);
            if ((squareContents != null) && (squareContents.size() > 0)) {   
                for (int j = 0; j < squareContents.size(); j++) {
                    envObjects.add(squareContents.getEnvironmentObject(j));
                }
            }
        }
        
        return envObjects;
    }

    /*all objects must be created using this function to give them
     *an individual object ID*/
    protected abstract EnvironmentObject createObject(String objectType);

}
