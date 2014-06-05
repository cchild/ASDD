package Logging;

import java.io.*;

/*
 * LogFile.java
 *
 * Created on January 22, 2003, 3:14 PM
 */

/**
 *
 * @author  eu779
 * @version 
 */
public class LogFile extends java.lang.Object {

    private static final String FILE_NAME_1 = "c:\\JAVA Projects\\playmaker8\\LogFile.txt";
    private static final String FILE_NAME_2 = "c:\\JAVA Projects\\playmaker8\\LogFileResults.txt";
    private static final String FILE_NAME_3 = "c:\\JAVA Projects\\playmaker8\\LogFileValueTable.txt";
    private PrintWriter outfile;
    public static final boolean OUTPUT_LOG0 = true;
    public static final boolean OUTPUT_LOG1 = true;
     public static final boolean OUTPUT_LOG2 = true;
    
    /** Creates new LogFile */
  /*  public LogFile(1) {
        //if logFile does not already exist then create it
        FileWriter w = null;
        try {
             w = new FileWriter(FILE_NAME, true);
        } catch (IOException e) {
            System.out.print("\nCANNOT OPEN " + FILE_NAME + e.toString());
        }
        outfile = new PrintWriter(w);
    } */
    
    public LogFile(int number) {
        //if logFile does not already exist then create it
        FileWriter w = null;
        try {
            if (number == 1)
                w = new FileWriter(FILE_NAME_1, true);
            if (number == 2)
                w = new FileWriter(FILE_NAME_3, true);
            if (number == 3)
                w = new FileWriter(FILE_NAME_3, true);
        } catch (IOException e) {
            if (number == 1)
                System.out.print("\nCANNOT OPEN " + FILE_NAME_1);
            if (number == 2)
                System.out.print("\nCANNOT OPEN " + FILE_NAME_2);
            if (number == 3)
                System.out.print("\nCANNOT OPEN " + FILE_NAME_3);
            else
                System.out.print("\nUnknown Error ");
        }
        outfile = new PrintWriter(w);
    }
 
    public void print(String line) {
        outfile.print(line);
    }
    
    public void flush() {
        outfile.flush();
    }
    
    public void close() {
        outfile.close();
    }
    
    public void println(String line) {
        outfile.println(line);
    }

}
