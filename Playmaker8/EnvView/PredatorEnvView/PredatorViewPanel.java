/*
 * PredatorViewClass.java
 *
 * Created on May 29, 2002, 5:06 PM
 */

package EnvView.PredatorEnvView;

import EnvModel.PredatorModel.*;
import EnvModel.*;
import EnvView.*;
import java.awt.*;

/**
 * Extension to Grid View Class with specifics for drawin
 * the agent, spanner and nut
 * @author  eu779
 * @version 
 */

public class PredatorViewPanel extends GridViewPanel {

    /** Creates new form GridViewPanel */
    public PredatorViewPanel(State state) {
       super(state);
       setBackground(java.awt.Color.green);
    }
    
    /**return the state as a predator state*/
    protected PredatorEnvState getPredatorState() {
        return (PredatorEnvState)getGridState();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  //paint background including grid squares
      
    }
    
    //over-ride drawing of environment objects for the predator world
    protected void drawEnvObject(Graphics g, int xSquare, int ySquare, EnvironmentObject environmentObject, int itemNumber) {
        Color oldColor = g.getColor();
        String itemType = environmentObject.getName();
        
        /*This is stand-in code. It will be replaced by a map object containing
         *environmentObjectDrawers identified by their object ID. If no drawer 
         *exists one will be crated for the object. Occasionaly it should go through
         *and check that all drawers are still being used. Id they are not they
         *should be destroyed. Simple way to do this is to keep a timer for
         *each drawer for when it was last draws. If not recently then destroy*/
        
        /*default environment object colour*/
        //if (itemType == "Spanner") {
        //    g.setColor(Color.orange);
        //}
        //else
        if (itemType == "Agent Body") {
            PredatorAgentBody predatorAgentBody = (PredatorAgentBody)environmentObject;
            int role = predatorAgentBody.getRole();
            if (role == PredatorAgentBody.PREDATOR) {
                g.setColor(Color.red);
            }
            else if (role == PredatorAgentBody.PREY) {
                g.setColor(Color.yellow);
            } else {
                g.setColor(Color.blue);
            }
        } 
        else {
            /*This is a default grid object type so go to default drawing*/
            super.drawEnvObject(g, xSquare, ySquare, environmentObject, itemNumber);
            return;
        }
 
        /*We will only get to here if it's a non default object type*/
        int xDrawStart = getXDrawStart (xSquare);
        int yDrawStart = getYDrawStart (ySquare);
        
        int xSquareInset = getXSquareInset(itemNumber);
        int ySquareInset = getYSquareInset(itemNumber);
            
        g.fill3DRect(xDrawStart + xSquareInset, yDrawStart + ySquareInset,
                     getRectWidth() / getMaxItemsPerRow(), //width of square
                     getRectHeight() / getMaxRows(), //height of square
                     true); //raised
        g.setColor(oldColor);
    }
}
