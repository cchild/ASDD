/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Token;

import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class TokenMap {
    
    protected ArrayList <ArrayList> TokenTypes;
    
    public TokenMap()
    {
        //String [][] a = {{""}};
        this.TokenTypes = new ArrayList <ArrayList> ();
    }
    
    
    
    public int setToken (String str, int position) {
        
        if (this.TokenTypes.isEmpty())
        {    
        ArrayList a = new ArrayList();
        a.add(str);
        this.TokenTypes.add(a);
        return 1;
        }
        else {
            if (this.TokenTypes.size() < position)
            {
//                if (!this.TokenTypes.get(position).contains(str))
//                {
//                    this.TokenTypes.get(position).add(str);
//                }
                System.out.println("Position " + position + " is not reachable, Index max : " + this.TokenTypes.size());
                return 2;
            }
//            if (this.TokenTypes.get(position-1).contains(str))
//            {
//                System.out.println("Hahaha");
//            }
//            try { this.TokenTypes.get(position); }
//            catch (Exception e) 
//            {
//                System.out.println("Zoub");
//            }
            if (this.TokenTypes.size() > position)
            {
            if ((this.TokenTypes.get(position).contains(str)))
            {
                System.out.println(str + " found at index " + position + "! Not adding ");
                return 3;
            }
            else {
                this.TokenTypes.get(position).add(str);
                return 4;
            }
            
            }
            
            
            //System.out.println("Token at index : " + (position-1) + " : " + this.TokenTypes.get(position-1));
            ArrayList a = new ArrayList();
            a.add(str);
            this.TokenTypes.add(position, a );
            //System.out.println("Size is now : " + this.TokenTypes.size());
        }
    return 0;
        
    }
    
    
}
