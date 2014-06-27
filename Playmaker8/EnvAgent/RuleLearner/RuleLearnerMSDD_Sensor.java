/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EnvAgent.RuleLearner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import Token.*;
import EnvModel.*;
import Logging.*;

import java.util.*;
import java.sql.Time;
import EnvAgent.*;

/**
 *
 * @author virgile
 */
public class RuleLearnerMSDD_Sensor extends RuleLearner {
    
    public TokenMap tokenMap;
    public SensorList sensorMap;
   
    
    public RuleLearnerMSDD_Sensor(PercepRecord percepRecord, ActionRecord actionRecord) {
        super(percepRecord, actionRecord);
    }
    
    
public NodeList learnRules()
    {
    return null;
    }


public static void main (String[] args) {

    TokenMap t = new TokenMap();
    
    t.fromFile();
    
    SensorList s = new SensorList();
    
    s.fromFile(t);
    
    
    System.out.println(s.getSensor(1).getString());
    
    System.out.println(s.getSensor(1).getActionString());
    
    //  ACTION IS ABSTRACT CANNOT BE INSTANTIATED
    //Action a = new Action();
    
    
    
}    
}

