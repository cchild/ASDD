package Logging;

import java.io.*;
import java.util.Scanner;

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
    
   private  static final String FILE_NAME_1 = "c:\\JAVA Projects\\playmaker8\\LogFiles.txt";
   private  static final String FILE_NAME_2 = "c:\\JAVA Projects\\playmaker8\\LogFilesResults.txt";
   public  static final String FILE_NAME_3 = "c:\\JAVA Projects\\playmaker8\\RSList_MSDD.txt";
   private  static final String FILE_NAME_4 = "c:\\JAVA Projects\\playmaker8\\LogFilesOutput.txt";
   public  static final String FILE_NAME_5 = "c:\\JAVA Projects\\playmaker8\\Rules_MSDD.txt";
   public  static final String FILE_NAME_6 = "c:\\JAVA Projects\\playmaker8\\StateTable.txt";   
   public static String INPUT_FILE = "c:\\JAVA Projects\\playmaker8\\InputFile.txt";
   private  PrintWriter outfile1, outfile2, outfile3, outfile4, outfile5, outfile6;
   public static final boolean OUTPUT_LOG0 = true;
   public static final boolean OUTPUT_LOG1 = true;
   public static final boolean OUTPUT_LOG2 = true;
   public static final boolean ERASE_FILES = false;

   private static LogFiles instance;
   
   
   protected LogFiles() {
      
       
       FileWriter w1 = null;
       FileWriter w2 = null;
       FileWriter w3 = null;
       FileWriter w4 = null;
       FileWriter w5 = null;
       FileWriter w6 = null;
       
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
       
       try {           
                w5 = new FileWriter(FILE_NAME_5, true);
                
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILE 5");
            }       

       try {           
                w6 = new FileWriter(FILE_NAME_6, true);
                
                
        } catch (IOException e) {
                System.out.print("\nCANNOT OPEN LOGFILE 6");
            } 
       
       outfile1 = new PrintWriter(w1);
       outfile2 = new PrintWriter(w2);
       outfile3 = new PrintWriter(w3);
       outfile4 = new PrintWriter(w4);
       outfile5 = new PrintWriter(w5);
       outfile6 = new PrintWriter(w6);
       
   }
   

   
   public static LogFiles getInstance() {
      
      
            if(instance == null) {
                if (ERASE_FILES) {
                    eraseFiles();
                }
                instance = new LogFiles();
            }           
            return instance;    
       
      }
   
    public static LogFiles getInstance(int number) {
      
            eraseFile(number);
            
            instance = new LogFiles();
            
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
        if (number == 5)
            outfile5.print(line);    
        if (number == 6)
            outfile6.print(line);
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
        if (number == 5)
            outfile5.close();
        if (number == 6)
            outfile6.close();
    }
    
    public void closeall() {
        
        
            outfile1.close();
        
            outfile2.close();
        
            outfile3.close();
            
            outfile4.close();
            
            outfile5.close();
            
            outfile6.close();
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
        if (number == 5)
            outfile5.println(line);       
        if (number == 6)
            outfile6.println(line); 
    }
    
    
    public static void eraseFile (int number) {
        
        String str = "";
        
        if (number == 1)
            str = FILE_NAME_1;
        if (number == 2)
            str = FILE_NAME_2;
        if (number == 3)
            str = FILE_NAME_3;
        if (number == 4)
            str = FILE_NAME_4;
        if (number == 5)
            str = FILE_NAME_5;
        if (number == 6)
            str = FILE_NAME_6;
  
        
        try{
                
    		File file = new File(str);
 
                //System.out.println("Erasing file : " + number);
    		file.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE FAILED");
 
    	}

        
        
    }
        
        
    public static int eraseFiles () {
        try{
 
    		File file = new File(FILE_NAME_1);
 
    		file.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE 1 FAILED");
                return 1;
 
    	}
        
        try{
 
    		File file2 = new File(FILE_NAME_2);
 
    		file2.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE 2 FAILED");
                return 2;
 
    	}
        
        try{
 
    		File file3 = new File(FILE_NAME_3);
 
    		file3.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE 3 FAILED");
                return 3;
 
    	}
        
        try{
 
    		File file4 = new File(FILE_NAME_4);
 
    		file4.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE 4 FAILED");
                return 4;
 
    	}
        
        try{
 
    		File file5 = new File(FILE_NAME_5);
 
    		file5.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE 5 FAILED");
                return 5;
 
    	}        

        try{
 
    		File file6 = new File(FILE_NAME_6);
 
    		file6.delete();
 
    	}catch(Exception e){
 
    		System.out.println("ERASING LOGFILE 6 FAILED");
                return 6;
 
    	}          
        
        return 0;
    }
    
    // Counts the number of lines in a file
    public static int getLines () {
        String filePath = Logging.LogFiles.INPUT_FILE;
        int lines = 1;
         try {
        
            Scanner scanner=new Scanner(new File(filePath));
            
            
            while (scanner.hasNextLine()) {
                lines++;
                scanner.nextLine();
            }
            
            
            scanner.close();
            return lines;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
         return 0;
    }
}
