/*
 * Class.java
 *
 * Created on June 10, 2002, 2:28 PM
 */


package EnvController.PredatorEnvController;

import EnvController.*;
import EnvModel.*;
import EnvModel.PredatorModel.*;
import EnvView.PredatorEnvView.*;
import EnvAgent.PredatorAgent.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 *
 * @author  eu779
 * @version 
 */
public class PredatorEnvApp extends EnvApp {

 
    /** Creates new Class */
    public PredatorEnvApp(Environment environment) {
        super(environment);
        initMyComponents();
        PredatorEnvironment predatorEnvironment = getPredatorEnvironment();
        predatorEnvironment.addPredatorAgent(0,0, PredatorAgent.PREDATOR);
        predatorEnvironment.addPredatorAgent(3,3, PredatorAgent.PREY);
        //predatorEnvironment.addSpanner(2,2);
       
        
        setEnvViewPanel(new PredatorViewPanel(environment.getState()));
        getContentPane().add(getEnvViewPanel(), java.awt.BorderLayout.CENTER);
        
        predatorRunPanel = new PredatorRunPanel(this);
        getContentPane().add(predatorRunPanel, java.awt.BorderLayout.EAST);
        
        pack();
    }
    
    protected PredatorEnvironment getPredatorEnvironment() {
        return (PredatorEnvironment)getEnvironment();
    }
 
    
   public void run1Cycle() {
       
       // OLD VERSION
        //getPredatorEnvironment().updateEnvironment();
        
        
        // 2nd VERSION USING ACTION VALUE TABLE IN STATETABLE.TXT
        //getPredatorEnvironment().updateEnvironmentFromStateActionValueTable();
      
        // 3rd VERSION USING VALUE TABLE IN STATETABLE.TXT
        //getPredatorEnvironment().updateEnvironmentFromStateValueTable();
       
        
        // 4th VERSION USING DECISION TABLE IN STATETABLE.TXT
        getPredatorEnvironment().updateEnvironmentFromDecisionTable();
        
        
        
        
        /*Force a repaint*/      
        getEnvViewPanel().repaint();
    }
     
    public void startRunning() {
        /*Start the app running here*/
        timer = new Timer(10, this);
        timer.setInitialDelay(10);
        timer.start();
    }
      
    public void stopRunning() {
        /*Stop the app running here*/
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        run1Cycle();
    }

 
    private void run10cyclesActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        for (int i = 0; i < 10; i++) {
            getPredatorEnvironment().updateEnvironment();
         
        }
           
        /*Force a repaint*/
        getEnvViewPanel().repaint();
    }
    
    private void initMyComponents() {
        runMenu = new javax.swing.JMenu();
        run10cycles = new javax.swing.JMenuItem();
        
   
    
        runMenu.setText("Run");
        runMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMenuActionPerformed(evt);
            }
        });
        
        run10cycles.setText("run10cycles");
        run10cycles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                run10cyclesActionPerformed(evt);
            }
        });
        
        runMenu.add(run10cycles);
        getJMenuBar().add(runMenu);
        
        pack();
    }
    
    private void runMenuActionPerformed(java.awt.event.ActionEvent evt) {
        ;
    }
    
      /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //create a new preator environment with turn based updates
        
        new PredatorEnvApp(new PredatorEnvironment()).show();
    }
    
    private javax.swing.JMenu runMenu;
    private javax.swing.JMenuItem run10cycles;
     
    PredatorRunPanel predatorRunPanel;
}
