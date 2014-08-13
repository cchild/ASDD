
package V_Sensors;

import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class TokenMap {
    
    public ArrayList <ArrayList <String> > TokenList;

    
    
    
    public TokenMap()
    {
        
        this.TokenList = new ArrayList <> ();
    }
    
    
    // BUILDS THE TOKENMAP FROM THE INPUT FILE
    //
    // FILE : LogFiles.INPUT_FILE
    public int fromFile () {
        
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));

            while (scanner.hasNextLine()) {
                
                String line = scanner.nextLine();
                
                for (int j = 0; j < (line.length()); j++) {
                    
                    setToken(String.valueOf(line.charAt(j)),j);
                }

            }
            
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }
    
    
    
    
    // SETS THE TOKEN AT "position" TO "str"
    public int setToken (String str, int position) {
        
        if (this.TokenList.isEmpty())
        {  

                for (int j = 0 ; j < position ; j++)
                {
                    ArrayList a = new ArrayList();
                    this.TokenList.add(j,a);
                }

                ArrayList b = new ArrayList();
                b.add(str);
                this.TokenList.add(position, b);
                return 0;
        }
        else {
            
            if (this.TokenList.size() < position) {
         
                for (int i = (this.TokenList.size()) ; i < position ; i++) {
                    
                    ArrayList a = new ArrayList();
                    this.TokenList.add(i,a);
                }
                
                ArrayList d = new ArrayList();
                d.add(str);
                this.TokenList.add(position, d);
                return 2;
            }

            if (this.TokenList.size() > position)
            
            {
                // CHECKS IF STR IS ALREADY IN THE TOKENMAP
                if ((this.TokenList.get(position).contains(str)))
                {
                   
                    return 3;
                }
                else
                {
                    this.TokenList.get(position).add(str);
                    return 4;
                    
                }
            
            }
            
            ArrayList test = new ArrayList();
            this.TokenList.add(position, test);
            this.TokenList.get(position).add(str);
                       
        }
        
        return 0;
        
    }
    
    
    // RETURNS POSSIBLE TOKENS AT POSITION "POSITION"
    public ArrayList getTokenList (int position) {
         
        return this.TokenList.get(position);
    }
     

    // Returns the reference of String "str", at the position "position"
    //
    // NOTICE the value is the index + 1. 
    // 0 reserved to Wildcards
    // If not found, returns -1 
    public int getReference (int position, String str) {
         
         
         
         if (this.TokenList.isEmpty())
         {
             
             return -1;
         }
         
         if (position >= this.TokenList.size()) { 
             return -1;
         }
         
         int res = this.TokenList.get(position).indexOf(str);
         
         if (res != -1)
         {
             return res+1;
         }
         return res;
         
     }
     
     
     
    public int getRefMax (int position) {
          
        return this.TokenList.get(position).size();
    }
      
      
    // RETURNS THE TOKEN AT POSITION "position" AND REF "reference"
    public String getToken (int position, int reference) {
          
        return this.TokenList.get(position).get(reference);

    }
    
 }
     
     
     
    

