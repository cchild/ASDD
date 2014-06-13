package Logging;

import java.io.*;

/*
 * LogFiles.java
 *
 * Created on June 04, 2012, 11:55 PM
 */

/**
 *
 * @author  virgile
 * @version 
 */
public class LogFiles extends java.lang.Object {
    
   private  final String FILE_NAME_1 = "c:\\JAVA Projects\\playmaker8\\LogFiles.txt";
   private  final String FILE_NAME_2 = "c:\\JAVA Projects\\playmaker8\\LogFilesResults.txt";
   private  final String FILE_NAME_3 = "c:\\JAVA Projects\\playmaker8\\LogFilesValueTable.txt";
   private  final String FILE_NAME_4 = "c:\\JAVA Projects\\playmaker8\\LogFilesOutput.txt";
   public static String INPUT_FILE = "c:\\JAVA Projects\\playmaker8\\LogFilesInput.txt";
   private  PrintWriter outfile1;
   private  PrintWriter outfile2;
   private  PrintWriter outfile3;
   private  PrintWriter outfile4;
   public static final boolean OUTPUT_LOG0 = true;
   public static final boolean OUTPUT_LOG1 = true;
   public static final boolean OUTPUT_LOG2 = true;

   private static LogFiles instance;
   
   
   protected LogFiles() {
      
       FileWriter w1 = null;
       FileWriter w2 = null;
       FileWriter w3 = null;
       FileWriter w4 = null;
       try {           
                w1 = new FileWriter(FILE_NAME_1, true);
                
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILE 1");
            }
       
       try {           
                w2 = new FileWriter(FILE_NAME_2, true);
                
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILE 2");
            }
       
       try {           
                w3 = new FileWriter(FILE_NAME_3, true);
                
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILE 3");
            }
       
       try {           
                w4 = new FileWriter(FILE_NAME_4, true);
                
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILE 4");
            }
       
       outfile1 = new PrintWriter(w1);
       outfile2 = new PrintWriter(w2);
       outfile3 = new PrintWriter(w3);
       outfile4 = new PrintWriter(w4);
       
       
   }
   
//   public ~LogFiles() {
//       
//       outfile1 = null;
//       outfile2 = null;
//       outfile3 = null;
//       
//       
//       
//   }
   
   protected void finalize ()  {
        
       outfile1 = null;
       outfile2 = null;
       outfile3 = null;
       
        }
   
   public static LogFiles getInstance() {
      
      
            if(instance == null) {
                instance = new LogFiles();
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
        if (number == 4)
            outfile4.print(line);
    }
    
    
    public void flush(int number) {
        
        if (number == 1)
            outfile1.flush();
        if (number == 2)
            outfile2.flush();
        if (number == 3)
            outfile3.flush();
        if (number == 4)
            outfile4.flush();
    }
    
    
    public void close(int number) {
        
        if (number == 1)
            outfile1.close();
        if (number == 2)
            outfile2.close();
        if (number == 3)
            outfile3.close();
        if (number == 4)
            outfile4.close();
    }
    
    public void closeall() {
        
        
            outfile1.close();
        
            outfile2.close();
        
            outfile3.close();
            
            outfile4.close();
    }
    
    
    public void println(String line, int number) {
        
        if (number == 1)
            outfile1.println(line);
        if (number == 2)
            outfile2.println(line);
        if (number == 3)
            outfile3.println(line);
        if (number == 4)
            outfile4.println(line);
    }
}
