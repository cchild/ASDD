/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EnvAgent.RuleLearner;

import V_Sensors.TokenMap;
import V_Sensors.SensorList;
import EnvAgent.*;
import EnvAgent.PredatorAgent.PredatorAgent;
import EnvModel.PredatorModel.PredatorEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import EnvModel.*;
import StateGenerator.*;

/**
 *
 * @author virgile
 */
public class Tester {

    
    /////////////////////////////////////////////////////
    ////////////////  MAIN ///////////////
    /////////////////////////////////////////////////////
    
    
    public static void main (String[] args) {
        
        
        
        System.out.println("\n\nCREATING TOKENMAP FROM FILE\n");
        
        TokenMap t = new TokenMap();
        t.fromFile();
        
        
        
        
        
        
        //System.out.println("\n\nCREATING SENSORS FROM FILE\n");
        //readFile(t);
        
        SensorList s = new SensorList();
        s.fromFile(t);
        
        System.out.println("Sensor 7 : " + s.getSensor(7).getString().toString());
        // SET WILDCARD
        s.getSensor(7).setToken("*", 3);
        System.out.println("Sensor 7 : " + s.getSensor(7).getString().toString());
        
//        PredatorEnvironment predatorEnvironment = new PredatorEnvironment();
//        PredatorAgent pred = (PredatorAgent)predatorEnvironment.addPredatorAgent(0,0,PredatorAgent.PREDATOR);
//        PredatorAgent prey = (PredatorAgent)predatorEnvironment.addPredatorAgent(3,3,PredatorAgent.PREY);
//        
//        Percep a = pred.getPercep();
//        System.out.println(a);

       
        //Percep b = (StateGenerator)generateNextState(a,pred.getActionRecord().getAction(0));
    }
    
    
    
}
