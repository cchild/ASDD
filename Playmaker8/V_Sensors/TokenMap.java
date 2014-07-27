/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Sensors;

import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class TokenMap {
    
    public ArrayList <ArrayList <String> > TokenTypes;
    public int id;
    
    public TokenMap()
    {
        //String [][] a = {{""}};
        this.id = 0;
        this.TokenTypes = new ArrayList <> ();
    }
    
    
    public int fromFile () {
        
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                
                String line = scanner.nextLine();
                
                //System.out.println("Adding Line " + i + " (" + line + ")");
                
                for (int j = 0; j < (line.length()); j++) {
                    setToken(String.valueOf(line.charAt(j)),j);
                }
                

                
                //System.out.println("TokenMap is : " + this.TokenTypes.toString());
                //faites ici votre traitement
                i++;
            }
            
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }
    
    
    
    public int setToken (String str, int position) {
        
        if (this.TokenTypes.isEmpty())
        {  
                //System.out.println("Is Empty");
                
                for (int j = 0 ; j < position ; j++)
                {
                    ArrayList a = new ArrayList();
                    this.TokenTypes.add(j,a);
                }

                ArrayList b = new ArrayList();
                b.add(str);
                this.TokenTypes.add(position, b);
                return 0;
        }
        else {
            if (this.TokenTypes.size() < position)
            {
                //System.out.println("Position " + position + " is not reachable, Index max : " + (this.TokenTypes.size()-1));
                
                for (int i = (this.TokenTypes.size()) ; i < position ; i++)
                {
                    ArrayList a = new ArrayList();
                    this.TokenTypes.add(i,a);
                }
                
                ArrayList d = new ArrayList();
                d.add(str);
                this.TokenTypes.add(position, d);
                return 2;
            }

            if (this.TokenTypes.size() > position)
            
            {
                if ((this.TokenTypes.get(position).contains(str)))
                {
                    //System.out.println(str + " found at index " + position + "! Not adding ");
                    return 3;
                }
                else
                {
                    this.TokenTypes.get(position).add(str);
                    return 4;
                    
                }
            
            }
            ArrayList test = new ArrayList();
            this.TokenTypes.add(position, test);
            this.TokenTypes.get(position).add(str);
                       
        }
    return 0;
        
    }
    
    
     public ArrayList getTokenList (int position) {
         
         return this.TokenTypes.get(position);
     }
     
//     public Token getToken (int position,int reference) {
//         
//         return this.TokenTypes.get(position).get(reference);
//     }
     
     
     public List getPosition (String str)  {
         if (this.TokenTypes.isEmpty())
         {
             List m = new ArrayList();
             return m;
         }
         
         
         List s = new ArrayList();
         
         for (int i = 0; (i < this.TokenTypes.size()-1); i++)
         {
           if (this.TokenTypes.get(i).contains(str))  
           {
             
             s.add(i);
           }
         }
         
         return s;
     }
    
     
     public int getReference (int position, String str) {
         
         // Returns the spot of the String, at the position specified
         // NOTICE the value is the index + 1. 0 reserved to Wildcards
         // If not found, returns (0)
         
         if (this.TokenTypes.isEmpty())
         {
             
             return -1;
         }
         
         if (position >= this.TokenTypes.size()) { 
             return -1;
         }
         
         int res = this.TokenTypes.get(position).indexOf(str);
         
         if (res != -1)
         {
             return res+1;
         }
         return res;
         
     }
     
     
     
      public int getRefMax (int position) {
          return this.TokenTypes.get(position).size();
      }
      
      
      public String getToken (int position, int reference) {
          //if ((position >= 0) && (position <= this.TokenTypes.size()))
          return this.TokenTypes.get(position).get(reference);
          
          
            //return "LOL";
      }
    
 }
     
     
     
    

