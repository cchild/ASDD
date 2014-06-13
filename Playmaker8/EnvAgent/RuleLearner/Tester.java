/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EnvAgent.RuleLearner;

import Token.*;
import EnvAgent.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class Tester {
    
    
    
    public static int readFile (TokenMap t) {
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                Sensor s = new Sensor(line,t);

            System.out.println("Line " + i + " :  " + line + "  >>  " + "Sensor " + i + s.getString());
                
                //faites ici votre traitement
            
            }
            
            Sensor x = new Sensor("HESESN",t);
            System.out.println("Sensor " + "X" + x.getString());
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        
        
        return 1;
    }
    
    
    
    /////////////////////////////////////////////////////
    ////////////////  MAIN ///////////////
    /////////////////////////////////////////////////////
    
    
    public static void main (String[] args) {
        
        
        
        System.out.println("\n\nCREATING TOKENMAP FROM FILE\n");
        
        TokenMap t = new TokenMap();
        t.fromFile();
        
        
        
        
        
        
        System.out.println("\n\nCREATING SENSORS FROM FILE\n");
        readFile(t);
        
        

        
        
    }
    
    
    
}
