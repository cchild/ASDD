package Logging;

import java.io.*;

/*
 * Singleton.java
 *
 * Created on June 04, 2012, 11:55 PM
 */

/**
 *
 * @author  virgile
 * @version 
 */
public class Singleton extends java.lang.Object {
    
   private static final String FILE_NAME_1 = "c:\\JAVA Projects\\playmaker8\\LogFile.txt";
   private static final String FILE_NAME_2 = "c:\\JAVA Projects\\playmaker8\\LogFileResults.txt";
   private static final String FILE_NAME_3 = "c:\\JAVA Projects\\playmaker8\\LogFileValueTable.txt";
   private PrintWriter outfile1;
   private PrintWriter outfile2;
   private PrintWriter outfile3;
   public static final boolean OUTPUT_LOG0 = true;
   public static final boolean OUTPUT_LOG1 = true;
   public static final boolean OUTPUT_LOG2 = true;

   private static Singleton instance;
   
   
   protected Singleton() {
      
       FileWriter w1 = null;
       FileWriter w2 = null;
       FileWriter w3 = null;
       try {           
                w1 = new FileWriter(FILE_NAME_1, true);
                w2 = new FileWriter(FILE_NAME_2, true);
                w3 = new FileWriter(FILE_NAME_3, true);
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILES");
            }
       
       outfile1 = new PrintWriter(w1);
       outfile2 = new PrintWriter(w2);
       outfile3 = new PrintWriter(w3);
       
   }
   public static Singleton getInstance() {
      
      
            if(instance == null) {
                instance = new Singleton();
            }           
            return instance;    
       
      }
      
    public void print(String line, int number) {
        
        if (number == 1)
            outfile1.print(line);
        if (number == 2)
            outfile2.print(line);
        if (number == 3)
            outfile3.print(line);
        
    }
    
    
    public void flush(int number) {
        
        if (number == 1)
            outfile1.flush();
        if (number == 2)
            outfile2.flush();
        if (number == 3)
            outfile3.flush();
    }
    
    
    public void close(int number) {
        
        if (number == 1)
            outfile1.close();
        if (number == 2)
            outfile2.close();
        if (number == 3)
            outfile3.close();
    }
    
    
    public void println(String line, int number) {
        
        if (number == 1)
            outfile1.println(line);
        if (number == 2)
            outfile2.println(line);
        if (number == 3)
            outfile3.println(line);
    }
}
