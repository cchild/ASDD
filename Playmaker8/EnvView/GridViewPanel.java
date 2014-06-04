/*
 * GridView.java
 *
 * Created on May 29, 2002, 1:15 PM
 */

package EnvView;

import EnvModel.*;

import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


/**
 *
 * @author  eu779
 * @version 
 */
public class GridViewPanel extends EnvViewPanel {
  
    Dimension preferredSize;
    int rectWidth = 50;
    int rectHeight = 50;
    
    /** Creates new form GridViewPanel */
    public GridViewPanel(State state) {
       super(state);
       initMyComponents();
    }
    
    protected int getRectWidth() {
        return rectWidth;
    }
    
    protected int getRectHeight() {
        return rectHeight;
    }
    
    protected GridState getGridState() {
        return (GridState)state;
    }

    protected void initMyComponents() {
        int xPref = getGridState().getXSize() * rectWidth;
        int yPref = getGridState().getYSize() * rectHeight;
        if (xPref >= 300)
            xPref = 300;
        if (yPref >= 300)
            yPref = 300;
            
        preferredSize = new Dimension(xPref,yPref);
        
        setPreferredSize(preferredSize);
   }
    
    
    public void setRectWidth(int width) {
        rectWidth = width;
    }
    
    public void setRectHeight(int height) {
        rectHeight = height;
    }
    
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  //paint background
  
        drawGridSquares(g);
        
        /* First painting occurs at (x,y), where x is at least
          insets.left, and y is at least insets.height. */
        drawGridContents(g);
    }
    
    protected void drawGridSquares(Graphics g) {
        /* First painting occurs at (x,y), where x is at least
          insets.left, and y is at least insets.height. */
        Insets insets = getInsets();
        int currentWidth = getWidth() - insets.left - insets.right;
        int currentHeight = getHeight() - insets.top - insets.bottom;
        int startX = insets.left;
        int startY = insets.top;
        int width = getGridState().getXSize() * rectWidth;
        int height = getGridState().getYSize() * rectHeight;
        
        //draw column lines
        for (int i = 0; i < getGridState().getXSize()+1; i++) {
            g.drawLine(startX + rectWidth * i, startY, startX + rectWidth * i, startY + height);
        }
        
        //draw Y Lines
        for (int j =0; j < getGridState().getYSize()+1; j++) {
            g.drawLine(startX, startY + rectHeight *j, startX + width, startY + rectHeight*j);
        }
    }
     
    protected void drawGridContents(Graphics g) {
         for (int x = 0; x < getGridState().getXSize(); x++) {
            for (int y = 0; y < getGridState().getYSize(); y++) {
                drawSquareContents(g, x, y);
            }
        }   
    }
    
    protected void drawSquareContents(Graphics g, int xSquare, int ySquare) {
        //Get the contents of this square (list of environment objects)
        
        SquareContents squareContents = getGridState().getSquareContents(xSquare, ySquare);
        for (int itemNumber = 0; itemNumber < squareContents.size(); itemNumber ++) {
            drawEnvObject(g, xSquare, ySquare, squareContents.getEnvironmentObject(itemNumber), itemNumber);
        }
    }
    
    protected void drawEnvObject(Graphics g, int xSquare, int ySquare, EnvironmentObject environmentObject, int itemNumber) {
        int xDrawStart = getXDrawStart (xSquare);
        int yDrawStart = getYDrawStart (ySquare);
        
        int xSquareInset = getXSquareInset(itemNumber);
        int ySquareInset = getYSquareInset(itemNumber);
        
        Color oldColor = g.getColor();
        
        /*default environment object colour*/
        g.setColor(Color.yellow);
        
        g.draw3DRect(xDrawStart + xSquareInset, yDrawStart + ySquareInset,
                     rectWidth / getMaxItemsPerRow(), //width of square
                     rectHeight / getMaxRows(), //height of square
                     true); //raised
        g.setColor(oldColor);
    }
    
    protected int getXDrawStart(int xSquare) {   
        return getInsets().left + rectWidth * xSquare;
    }
    
    protected int getYDrawStart(int ySquare) {
        return getInsets().top + rectHeight * ySquare;
    }
    
    protected int getMaxItemsPerRow() {
        /*This should be worked out, but constant at present*/
        return 2;
    }
    
    protected int getMaxRows() { 
        /*This should be worked out, but constant at present*/
        return 2;
    }
    
    protected int getXSquareInset(int itemNumber) {
        /*item number starts from 0. Integer divide by max items in a row
        * to get the item row for current item*/
        int row = itemNumber / getMaxItemsPerRow();
        int column = itemNumber - row * getMaxItemsPerRow();
        int pixelsPerColumn = rectWidth/getMaxItemsPerRow();
        return column * pixelsPerColumn;
    }
    
    protected int getYSquareInset(int itemNumber) {
        /*item number starts from 0. Integer divide by max items in a row
        * to get the item row for current item*/
        int row = itemNumber / getMaxItemsPerRow();
        int pixelsPerRow = rectHeight/getMaxRows();
        return row * pixelsPerRow;
    }
}
