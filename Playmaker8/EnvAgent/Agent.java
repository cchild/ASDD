package EnvAgent;

/*
 * EnvironmentObject.java
 *
 * Created on 26 February 2001, 15:18
 */

/**
 *
 * @author  Chris Child
 * @version 
 */

import EnvModel.*;
import StateGenerator.*;
import EnvAgent.ClauseLearner.*;
import EnvAgent.RuleLearner.*;
import ReinforcementLearner.*;
import EnvAgent.StateActionMap.*;
import Logging.*;


import java.util.*;
import java.io.*;

//an agents has state which is internal to itself and can perform actions through
//an agent body

public abstract class Agent extends Object {

    protected AgentBody body;
    State agentState;
    protected Action action;
    protected boolean realThinker;
  
    /*These will store records of percpts and actions*/
    protected PercepRecord percepRecord;
    protected ActionRecord actionRecord;
    
    /*set these variables if we are recording percets and action*/
    protected boolean recordActions;
    protected boolean recordPercepts;
    
    protected boolean USE_SAVED_PERCEP_ACTION_RECORD;
    protected String PERCEP_FILE_NAME;        
    protected String ACTION_FILE_NAME;        
    
    
    public boolean USE_REINFORCEMENT_POLICY;
    public boolean USE_BELLMAN;
    public boolean USE_CLAUSE_APRIORI_VALUES;
    public boolean USE_RULE_APRIORI_VALUES;
    public boolean USE_CLAUSE_VALUES;
    public boolean USE_STATE_ACTION_LOTS;

    public int REFINE_MAP_STEPS;
    public int REFINE_MAP_STEPS_RVRL;
        
    //CHANGE THIS NEXT LINE BACK in the code!!
    //USE_SAVED_CLAUSE_APRIORI_VALUES
    
   protected boolean USE_SAVED_APRIORI_VALUES;
   protected String VALUE_MAP_APRIORI_FILENAME;
     
   protected boolean USE_SAVED_MSDD_VALUES;
   protected String VALUE_MAP_MSDD_FILENAME;
     
   protected boolean USE_SAVED_STATE_ACTION_VALUES;
   protected String VALUE_MAP_STATE_ACTION_FILENAME;
    
   protected boolean USE_SAVED_STATE_ACTION_STATE_ACTION_VALUES;
   protected String STATE_ACTION_VALUE_MAP_STATE_ACTION_FILENAME;
    
   protected boolean USE_SAVED_CLAUSE_APRIORI_VALUES;
   protected String VALUE_MAP_CLAUSE_APRIORI_FILENAME;

    
    /********SAVED RULES*/
   protected boolean USE_SAVED_MSDD_RULES;
   protected String MSDD_RULES_FILE_NAME;        
   protected boolean USE_SAVED_APRIORI_RULES;
   protected String APRIORI_RULES_FILE_NAME;        
  
   public boolean USE_SAVED_STATE_ACTION_MAP;
   protected String STATE_ACTION_FILE_NAME;        
    
   protected String STATE_ACTION_LOTS_FILE_NAME;        
  
   protected boolean USE_SAVED_APRIORI_CLAUSES;
   protected String APRIORI_CLAUSES_FILE_NAME;        
    
   public boolean LEARN_RULES_MSDD;
   public boolean LEARN_RULES_APRIORI;
   
    
   public boolean LEARN_CLAUSES_ASDD;
    protected ClauseLearner clauseLearnerASDD;
    
    protected RuleLearner ruleLearnerMSDD;
    protected RuleLearner ruleLearnerApriori;
  
    //int role;
    
    protected ReinforcementLearner reinforcementLearner;
    protected StateActionMap stateActionMap;
    
    protected ClauseList learnedClausesASDD;
    
    /** Creates new Agent */
    public Agent() {
        body = null;
        agentState = null;
        action = null;
        realThinker = true;
        REFINE_MAP_STEPS = 1000; //Number of iterations for dynamic programming or RL.
        REFINE_MAP_STEPS_RVRL = 1000; //Number of iterations of RVRL value learning
    }
    
    public void initialise(Percep thePercep, Action theAction) {
            
   
      if (USE_SAVED_PERCEP_ACTION_RECORD) {
        //Open serialisation file
        try {
            /* Open the file and set to read objects from it. */
            FileInputStream istream = new FileInputStream(PERCEP_FILE_NAME);
            ObjectInputStream q = new ObjectInputStream(istream);

            /* Read the learned rules object */
            percepRecord = (PercepRecord)q.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Open serialisation file
        try {
            /* Open the file and set to read objects from it. */
            FileInputStream istream = new FileInputStream(ACTION_FILE_NAME);
            ObjectInputStream q = new ObjectInputStream(istream);

            /* Read the learned rules object */
            actionRecord = (ActionRecord)q.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            recordActions = true;
            recordPercepts = true;
        
            if (recordPercepts)
                percepRecord = new PercepRecord();
            if (recordActions)
                actionRecord = new ActionRecord();
        }
        
        ruleLearnerMSDD = null;
        ruleLearnerApriori = null;
        learnedClausesASDD = null;
    
        /*we create a new predator agent percep which will be discarded
         *so that we know the percep's format*/
        if (LEARN_RULES_MSDD)
            ruleLearnerMSDD = new RuleLearnerMSDD ((Percep)thePercep.clone(), (Action)theAction.clone(), percepRecord, actionRecord);
        
        if (LEARN_RULES_APRIORI)
            ruleLearnerApriori = new RuleLearnerApriori((Percep)thePercep.clone(), (Action)theAction.clone(), percepRecord, actionRecord);
               
        if (LEARN_CLAUSES_ASDD)  
            clauseLearnerASDD = new ClauseLearnerASDD((Percep)thePercep.clone(), (Action)theAction.clone(), percepRecord, actionRecord);
    
        action = (Action)theAction.clone();
    }
    
    public boolean getUseSavedPercepActionRecord() {
        return USE_SAVED_PERCEP_ACTION_RECORD;
    }
    
    public void savePrecepActionRecord() {

        if (LEARN_CLAUSES_ASDD || LEARN_RULES_MSDD) {
            //only save the percept record if we're not using the big one
         if (!USE_SAVED_PERCEP_ACTION_RECORD) {
            try { 
                /* Create a file to write the serialized tree to. */ 
                FileOutputStream ostream = new FileOutputStream(PERCEP_FILE_NAME); 
                /* Create the output stream */ 
                ObjectOutputStream p = new ObjectOutputStream(ostream); 
                p.writeObject(percepRecord);
            } catch (Exception ex) { 
                ex.printStackTrace();
            }
        }
         
        if (!USE_SAVED_PERCEP_ACTION_RECORD) {
            try { 
                /* Create a file to write the serialized tree to. */ 
                FileOutputStream ostream = new FileOutputStream(ACTION_FILE_NAME); 
                /* Create the output stream */ 
                ObjectOutputStream p = new ObjectOutputStream(ostream); 
                p.writeObject(actionRecord);
            } catch (Exception ex) { 
                ex.printStackTrace();
            }
        }
        }
    }
    
    public void setRecordActions(boolean recordActions) {
        this.recordActions = recordActions;
    }
    
    public void setRecordPercepts(boolean recordPercepts) {
        this.recordPercepts = recordPercepts;
    }
    
    //public Action deliberate() {return null;}
   
  
    public final void perceive()
    {
        if (body != null) {
            body.setPercep();
            
            if (recordPercepts) {
                percepRecord.addPercep(body.getPercep());
            }
        }
    }
    
    public Percep getPercep() {
        return body.getPercep();
    }
    
    public boolean getRealThinker() {
        return realThinker;
    }
    
    public void act(Action action)
    {
        if (body != null) {
            body.setAction((Action)action.clone());
            if (recordActions) {
                if (realThinker) {
                    actionRecord.addAction((Action)action.clone());
                }
            }
        }
    }
    
    public PercepRecord getPercepRecord() {
        return percepRecord;
    }
    
    public ActionRecord getActionRecord() {
        return actionRecord;
    }


    /*Learn rules relatuing actions and state varibales (fluents) to next states*/
    public void learnRules() {
        
        StateActionMap stateActionLots = null;
        
        try {   
            /* Open the file and set to read objects from it. */ 
            FileInputStream istream = new FileInputStream(STATE_ACTION_LOTS_FILE_NAME); 
            ObjectInputStream q = new ObjectInputStream(istream); 

            /* Read the learned rules object */ 
            stateActionLots = (StateActionMap)q.readObject();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }   
        
        if (!USE_SAVED_STATE_ACTION_MAP) {
            try { 
                /* Create a file to write the serialized tree to. */ 
                FileOutputStream ostream = new FileOutputStream(STATE_ACTION_FILE_NAME); 
                /* Create the output stream */ 
                ObjectOutputStream p = new ObjectOutputStream(ostream); 
                //open serialization file
                Date start = new Date();
                long startTime = start.getTime();


                stateActionMap = new StateActionMap();
                stateActionMap.generateFromPercepActionList(percepRecord, actionRecord);
                Date finish = new Date();
                long elapsedTime = finish.getTime() - startTime;

                LogFiles logfile2 = LogFiles.getInstance();
                logfile2.print("\n State action model learned in: " + elapsedTime + " MILLISECONDS.",2);
                System.out.print("\n State action model learned in:" + elapsedTime + " MILLISECONDS.");
                 
                
                p.writeObject(stateActionMap);
            } catch (Exception ex) { 
                ex.printStackTrace();
            }
        } else {
            //Open serialisation file
            try {   
                /* Open the file and set to read objects from it. */ 
                FileInputStream istream = new FileInputStream(STATE_ACTION_FILE_NAME); 
                ObjectInputStream q = new ObjectInputStream(istream); 
             
                /* Read the learned rules object */ 
                stateActionMap = (StateActionMap)q.readObject();
            } catch (Exception ex) { 
                ex.printStackTrace(); 
            } 
        }
        
        NodeList learnedRulesMSDD = null;
   
     
        NodeList learnedRulesApriori = null;
        
        if (LEARN_RULES_APRIORI) {
            if (!USE_SAVED_APRIORI_RULES) {
                try { 
                    /* Create a file to write the serialized tree to. */ 
                    FileOutputStream ostream = new FileOutputStream(APRIORI_RULES_FILE_NAME); 
                    /* Create the output stream */ 
                    ObjectOutputStream p = new ObjectOutputStream(ostream); 
                    //open serialization file
                    learnedRulesApriori = ruleLearnerApriori.learnRules();
                    p.writeObject(learnedRulesApriori);
                } catch (Exception ex) { 
                    ex.printStackTrace();
                }
            } else {
                //Open serialisation file
                try {   
                    /* Open the file and set to read objects from it. */ 
                    FileInputStream istream = new FileInputStream(APRIORI_RULES_FILE_NAME); 
                    ObjectInputStream q = new ObjectInputStream(istream); 

                    /* Read the learned rules object */ 
                    learnedRulesApriori = (NodeList)q.readObject();
                } catch (Exception ex) { 
                    ex.printStackTrace(); 
                } 
            }
        }
        
           if (LEARN_RULES_MSDD) {
            if (!USE_SAVED_MSDD_RULES) {
                try { 
                    /* Create a file to write the serialized tree to. */ 
                    FileOutputStream ostream = new FileOutputStream(MSDD_RULES_FILE_NAME); 
                    /* Create the output stream */ 
                    ObjectOutputStream p = new ObjectOutputStream(ostream); 
                    //open serialization file
                    learnedRulesMSDD = ruleLearnerMSDD.learnRules();
                    p.writeObject(learnedRulesMSDD);
                } catch (Exception ex) { 
                    ex.printStackTrace();
                }
            } else {
                //Open serialisation file
                try {   
                    /* Open the file and set to read objects from it. */ 
                    FileInputStream istream = new FileInputStream(MSDD_RULES_FILE_NAME); 
                    ObjectInputStream q = new ObjectInputStream(istream); 

                    /* Read the learned rules object */ 
                    learnedRulesMSDD = (NodeList)q.readObject();
                } catch (Exception ex) { 
                    ex.printStackTrace(); 
                } 
            }
        }
      
        if (!USE_REINFORCEMENT_POLICY)
            compairStateGenerators(learnedRulesApriori, learnedRulesMSDD);

        if (false) {
            if (!USE_SAVED_APRIORI_VALUES) {
                StateValueMap valueMapApriori = generateRuleValueMap(learnedRulesApriori);
                valueMapApriori.writeTo(VALUE_MAP_APRIORI_FILENAME);
            }

            if (!USE_SAVED_MSDD_VALUES) {
                StateValueMap valueMapMSDD = generateRuleValueMap(learnedRulesMSDD);
                valueMapMSDD.writeTo(VALUE_MAP_MSDD_FILENAME);
            }

            if (!USE_SAVED_STATE_ACTION_VALUES) {
                StateValueMap valueMapStateAction = generateStateActionValueMap(stateActionMap);
                valueMapStateAction.writeTo(VALUE_MAP_STATE_ACTION_FILENAME);
            }
            
            if (!USE_SAVED_STATE_ACTION_STATE_ACTION_VALUES) {
                StateActionValueMap stateActionValueMapStateAction = generateStateActionStateActionValueMap(stateActionMap);
                stateActionValueMapStateAction.writeTo(STATE_ACTION_VALUE_MAP_STATE_ACTION_FILENAME);
            }
        }
        reinforcementLearner.getValueMap().output();
    }
    
       /*Learn rules relatuing actions and state varibales (Variables) to next states*/
    public void learnClauses() {
       
        
       StateActionMap stateActionLots = null;
        
       try {   
            // Open the file and set to read objects from it.
            FileInputStream istream = new FileInputStream(STATE_ACTION_LOTS_FILE_NAME); 
            ObjectInputStream q = new ObjectInputStream(istream); 

            // Read the learned rules object
            stateActionLots = (StateActionMap)q.readObject();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }

       if (USE_REINFORCEMENT_POLICY) { //this bit now handled in learn rules
            if (!USE_SAVED_STATE_ACTION_MAP) {
                try {
                    /* Create a file to write the serialized tree to. */
                    FileOutputStream ostream = new FileOutputStream(STATE_ACTION_FILE_NAME);
                    /* Create the output stream */
                    ObjectOutputStream p = new ObjectOutputStream(ostream);
                    //open serialization file
                    stateActionMap = new StateActionMap();
                    stateActionMap.generateFromPercepActionList(percepRecord, actionRecord);
                    p.writeObject(stateActionMap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                //Open serialisation file
                try {
                    /* Open the file and set to read objects from it. */
                    FileInputStream istream = new FileInputStream(STATE_ACTION_FILE_NAME);
                    ObjectInputStream q = new ObjectInputStream(istream);

                    /* Read the learned rules object */
                    stateActionMap = (StateActionMap)q.readObject();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        if (!USE_SAVED_CLAUSE_APRIORI_VALUES) {
            if (LEARN_CLAUSES_ASDD) {
                try { 
                    /* Create a file to write the serialized tree to. */ 
                    FileOutputStream ostream = new FileOutputStream(APRIORI_CLAUSES_FILE_NAME); 
                    /* Create the output stream */ 
                    ObjectOutputStream p = new ObjectOutputStream(ostream); 
                    //open serialization file
                    learnedClausesASDD = clauseLearnerASDD.learnClauses();
                    System.runFinalization();
                    System.gc();
                    p.writeObject(learnedClausesASDD);
                  /*                      
                    ArrayList storeTypes = new ArrayList(); 
                    ArrayList storeValues = new ArrayList();

                    Clause cl = (Clause)((ClauseNode)learnedClausesASDD.getArrayList().get(0)).getClauseHead();
                    Variable vr = (Variable)cl.getPredicate(0);
                    
                    for (int i = 0; i < vr.getAllTypes().size(); i++) {
                        storeTypes.add(vr.getAllTypes().get(i));
                        storeValues.add(vr.getMaxValues().get(i));
                    }

                    p.writeObject(storeTypes);
                    p.writeObject(storeValues);
                    */
    
                } catch (Exception ex) { 
                    ex.printStackTrace();
                }
       
            } 
        } else {
            //Open serialisation file
            try {   
                /* Open the file and set to read objects from it. */ 
                FileInputStream istream = new FileInputStream(APRIORI_CLAUSES_FILE_NAME); 
                ObjectInputStream q = new ObjectInputStream(istream); 

                /* Read the learned rules object */ 
                System.runFinalization();
                System.gc();
                learnedClausesASDD = (ClauseList)q.readObject();
                
                /*
                ArrayList storeTypes = null; 
                ArrayList storeValues = null;

                storeTypes = (ArrayList)q.readObject();
                storeValues = (ArrayList)q.readObject();


                Clause cl = (Clause)((ClauseNode)learnedClausesASDD.getArrayList().get(0)).getClauseHead();
                Variable vr = (Variable)cl.getPredicate(0);

                vr.setMaxValues(storeValues);
                vr.setAllTypes(storeTypes);
           */

                   
            } catch (Exception ex) { 
                ex.printStackTrace(); 
            } 
        }
        
        System.runFinalization();
        System.gc();
        
        //if (!USE_SAVED_CLAUSE_APRIORI_VALUES)
         //   compairClauseStateGenerators(learnedClausesASDD);
        
        StateValueMap valueMapClauseApriori = null;
        StateValueMap valueMapStateAction = null; 
        
        if (USE_CLAUSE_APRIORI_VALUES) { //generate the clause value map
            if (!USE_SAVED_CLAUSE_APRIORI_VALUES) {

                if (USE_SAVED_CLAUSE_APRIORI_VALUES) {
                    System.out.print("WARNING: Change line above back to !USE_SAVED_CLAUSE_APRIORI_VALUES");
                }

                valueMapClauseApriori = generateClauseValueMap(learnedClausesASDD);
                valueMapClauseApriori.writeTo(VALUE_MAP_CLAUSE_APRIORI_FILENAME);
                LogFiles logfile = LogFiles.getInstance();
                logfile.print ("\n\nClause Value Map: \n",1);
                logfile.print(valueMapClauseApriori.toString(),1);
                logfile.flush(1);
             
            } else {
                 try {   
                    /* Open the file and set to read objects from it. */ 
                    FileInputStream istream = new FileInputStream(VALUE_MAP_CLAUSE_APRIORI_FILENAME); 
                    ObjectInputStream q = new ObjectInputStream(istream); 

                    /* Read the learned rules object */ 
                    valueMapClauseApriori = (StateValueMap)q.readObject();
                } catch (Exception ex) { 
                    if (false) {
                        //for the moment generate one because we didn't have one
                        valueMapClauseApriori = generateClauseValueMap(learnedClausesASDD);
                        valueMapClauseApriori.writeTo(VALUE_MAP_CLAUSE_APRIORI_FILENAME);
                        LogFiles logfile = LogFiles.getInstance();
                        logfile.print ("\n\nClause Value Map: \n",1);
                        logfile.print(valueMapClauseApriori.toString(),1);
                        logfile.flush(1);
                    }
                    ex.printStackTrace(); 
                } 
            }
        }
        
        if (USE_REINFORCEMENT_POLICY) {      
           if (!USE_CLAUSE_VALUES) {
               if (USE_BELLMAN) {
                   if (!USE_CLAUSE_APRIORI_VALUES && !USE_RULE_APRIORI_VALUES) {
                       StateActionMap stateActionMapTemp = null;
                       if (USE_STATE_ACTION_LOTS)  { //use normal rather than lots
                           System.out.print("WARNING: Using Bellman with state action lots map");
                           System.out.flush();
                           stateActionMapTemp = stateActionLots;
                                
                       } else {
                           System.out.print("WARNING: Using Bellman with state action normal map");
                           System.out.flush();
                           stateActionMapTemp = stateActionMap;
                       }
                      
                       reinforcementLearner.setStateGenerator(new StateActionMapStateGenerator(stateActionMapTemp));
                       StateValueMap valueMapStateActionTemp = generateStateActionValueMap(stateActionMapTemp);
                       reinforcementLearner.setValueMap(valueMapStateActionTemp);
                       LogFiles logfile = LogFiles.getInstance();
                       logfile.print ("\n\nState Action Lots Value Map: \n",1);
                       logfile.print(valueMapStateActionTemp.toString(),1);
                       logfile.flush(1);
                                  
                       
                   } else {// (USE_CLAUSE_APRIORI_VALUES) // note there is no q-version yet
                       if (USE_CLAUSE_APRIORI_VALUES) {
                           //This uses value map with full MSDD rules
                           System.out.print("WARNING: Using clause Value Map");
                           System.out.flush();
                           reinforcementLearner.setValueMap(valueMapClauseApriori);
                           learnedClausesASDD.findLevel1Clauses();
                           StateGenerator stateGeneratorAprioriClauses = new ClauseStateGenerator(learnedClausesASDD);
                           reinforcementLearner.setStateGenerator(stateGeneratorAprioriClauses);
                       } else { // USE_RULE_APRIORI_VALUES
                           if (LEARN_RULES_MSDD) {
                             NodeList learnedRulesMSDD = null;
                             try {
                                /* Open the file and set to read objects from it. */
                                FileInputStream istream = new FileInputStream(MSDD_RULES_FILE_NAME);
                                ObjectInputStream q = new ObjectInputStream(istream);

                                /* Read the learned rules object */
                                learnedRulesMSDD = (NodeList)q.readObject();
                                } catch (Exception ex) {
                                ex.printStackTrace();
                              }
                             StateGenerator stateGeneratorMSDDRules = new RuleStateGenerator(learnedRulesMSDD);
                             reinforcementLearner.setStateGenerator(stateGeneratorMSDDRules);
                             StateValueMap stateValueMapStateActionTemp = this.generateRuleValueMap(learnedRulesMSDD);
                           reinforcementLearner.setValueMap(stateValueMapStateActionTemp);
                           } else { //LEARN_RULES_ASDD
                                NodeList learnedRulesASDD = null;
                                try {
                                /* Open the file and set to read objects from it. */
                                FileInputStream istream = new FileInputStream(APRIORI_RULES_FILE_NAME);
                                ObjectInputStream q = new ObjectInputStream(istream);

                                /* Read the learned rules object */
                                learnedRulesASDD = (NodeList)q.readObject();
                                } catch (Exception ex) {
                                ex.printStackTrace();
                                }
                                StateGenerator stateGeneratorASDDRules = new RuleStateGenerator(learnedRulesASDD);
                                reinforcementLearner.setStateGenerator(stateGeneratorASDDRules);
                                 StateValueMap stateValueMapStateActionTemp = this.generateRuleValueMap(learnedRulesASDD);
                                reinforcementLearner.setValueMap(stateValueMapStateActionTemp);
                           }
                          
                       }
                   }

                   reinforcementLearner.getValueMap().output();

               } else { //not USE_BELLMAN so using q learning Note that there is no Apriori version yet
                   //Looks like I'll ignore this completely for the test, but just in case
                   StateActionMap stateActionMapTemp = null;
                   if (!USE_CLAUSE_APRIORI_VALUES) {
                       if (USE_STATE_ACTION_LOTS)  { //use normal rather than lots
                           System.out.print("WARNING: Using Q-Learning with state action lots map");
                           System.out.flush();
                           stateActionMapTemp = stateActionLots;

                       } else {
                           System.out.print("WARNING: Using Q-Learning with state action normal map");
                           System.out.flush();
                           stateActionMapTemp = stateActionMap;
                       }
                       reinforcementLearner.setStateGenerator(new StateActionMapStateGenerator(stateActionMapTemp));
                   } else { // if (USE_CLAUSE_APRIORI_VALUES) {
                       if (LEARN_RULES_MSDD) {
                         NodeList learnedRulesMSDD = null;
                         try {
                            /* Open the file and set to read objects from it. */
                            FileInputStream istream = new FileInputStream(MSDD_RULES_FILE_NAME);
                            ObjectInputStream q = new ObjectInputStream(istream);

                            /* Read the learned rules object */
                            learnedRulesMSDD = (NodeList)q.readObject();
                            } catch (Exception ex) {
                            ex.printStackTrace();
                            }
                            StateGenerator stateGeneratorMSDDRules = new RuleStateGenerator(learnedRulesMSDD);
                            reinforcementLearner.setStateGenerator(stateGeneratorMSDDRules);
                 
                        //StateGenerator stateGeneratorAprioriRules = new RuleStateGenerator(learnedRulesApriori);
                       } else { //LEARN_RULES_ASDD
                            NodeList learnedRulesASDD = null;
                         try {
                            /* Open the file and set to read objects from it. */
                            FileInputStream istream = new FileInputStream(APRIORI_RULES_FILE_NAME);
                            ObjectInputStream q = new ObjectInputStream(istream);

                            /* Read the learned rules object */
                            learnedRulesASDD = (NodeList)q.readObject();
                            } catch (Exception ex) {
                            ex.printStackTrace();
                            }
                            StateGenerator stateGeneratorASDDRules = new RuleStateGenerator(learnedRulesASDD);
                            reinforcementLearner.setStateGenerator(stateGeneratorASDDRules);
                       }
                  }
                        
                   StateActionValueMap stateActionValueMapStateActionTemp = generateStateActionStateActionValueMap(stateActionMapTemp);
                   reinforcementLearner.setStateActionValueMap(stateActionValueMapStateActionTemp);
                   LogFiles logfile = LogFiles.getInstance();
                   logfile.print ("\n\nState Action Value Map When generated: \n",1);
                   for (int j = 0; j < stateActionValueMapStateActionTemp.getStateActionValues().size(); j++) {
                       logfile.print("\n" + j + " "+ stateActionValueMapStateActionTemp.getStateActionValues().get(j).toString(),1);
                   }
                   logfile.flush(1);
                   
               
               }
           }
        }
        
        
       //(Rule based -- change this back to USE_REINFORCEMENT_POLICY
       //if we're doing updating the old file
       if (USE_CLAUSE_VALUES && !USE_REINFORCEMENT_POLICY) {

           if (USE_REINFORCEMENT_POLICY) {
                System.out.print("\nWARNING: Generating a new rule clause values ");
                System.out.print("\nWARNING: Change this back to !USE_REINFORCEMENT_POLICY");
                System.out.flush();
           }

           System.runFinalization();
           System.gc();

           //Had to do this inline becuse Java has a dumb Serialize
           //learnedClausesASDD.clearRewardAndError();
           if (true) {
                for (int i = learnedClausesASDD.getClauseSetMap().size() -1; i > 0 ; i--) {
                     ClauseSet seti = learnedClausesASDD.getClauseSetMap().get(i);
                     seti.setError(0);
                     seti.restoreValueWithUpdateDefaults();
                }
           } else {
                System.out.print("\nWARNING: Not zeroing clause values  ");
                System.out.print("\nWARNING: Change this back to true");
                System.out.flush();
           }

           generateClauseValues (learnedClausesASDD);
           LogFiles logfile = LogFiles.getInstance();
           logfile.print ("\n\nLearned Clause Sets with Values: \n",1);
           logfile.print(learnedClausesASDD.getClauseSetMap().toString(),1);
           logfile.flush(1);
           

         

           boolean removeNonPrecedence = true;
           if (removeNonPrecedence) {
                ClauseSetMap clauseSetMap = learnedClausesASDD.buildClauseSetMap();

                for (int i = clauseSetMap.size() -1; i > 0 ; i--) {
                    ClauseSet seti = clauseSetMap.get(i);

                    if (!clauseSetMap.get(i).getUsedWithSpremacy()) {
                        learnedClausesASDD.removeAllWithClauseSet(seti);
                        clauseSetMap.remove(i);
                    }
                }
           }
           LogFiles logfile2 = LogFiles.getInstance();
           logfile2.print ("\n\nLearned Clause Sets with Values: \n",2);
           logfile2.print(learnedClausesASDD.getClauseSetMap().toStringOnlyWinning(),2);
           logfile2.flush(2);
           

           //Now save out the cluauses with value map
           try { 
                /* Create a file to write the serialized tree to. */ 
                FileOutputStream ostream = new FileOutputStream(APRIORI_CLAUSES_FILE_NAME); 
                /* Create the output stream */ 
                ObjectOutputStream p = new ObjectOutputStream(ostream); 
                //open serialization file
                System.runFinalization();
                System.gc();
                p.writeObject(learnedClausesASDD);
              /*                      
                ArrayList storeTypes = new ArrayList(); 
                ArrayList storeValues = new ArrayList();

                Clause cl = (Clause)((ClauseNode)learnedClausesASDD.getArrayList().get(0)).getClauseHead();
                Variable vr = (Variable)cl.getPredicate(0);

                for (int i = 0; i < vr.getAllTypes().size(); i++) {
                    storeTypes.add(vr.getAllTypes().get(i));
                    storeValues.add(vr.getMaxValues().get(i));
                }

                p.writeObject(storeTypes);
                p.writeObject(storeValues);
                */

            } catch (Exception ex) { 
                ex.printStackTrace();
            }
       }
        
        
    }

    
    public void compairStateGenerators(NodeList learnedRulesApriori, NodeList learnedRulesMSDD) {
        
        StateActionMap stateActionLots = null;
        
        try {   
            /* Open the file and set to read objects from it. */ 
            FileInputStream istream = new FileInputStream(STATE_ACTION_LOTS_FILE_NAME); 
            ObjectInputStream q = new ObjectInputStream(istream); 

            /* Read the learned rules object */ 
            stateActionLots = (StateActionMap)q.readObject();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }   
        
        StateGenerator stateGeneratorLotsActionMap = new StateActionMapStateGenerator(stateActionLots);
        StateGenerator stateGeneratorMSDDRules = new RuleStateGenerator(learnedRulesMSDD);
        StateGenerator stateGeneratorAprioriRules = new RuleStateGenerator(learnedRulesApriori);
        StateGenerator stateGeneratorStateActionMap = new StateActionMapStateGenerator(stateActionMap);
        
      
        double stateActionMapError = (double)0.0f;
        double MSDDError = (double)0.0f;
        double aprioriError = (double)0.0f;
        int totalTested = 0;
        int totalStateActionStates = 0;
        Integer extraStatesSAM  =0;
        Integer tooFewStatesSAM =0;
        Integer extraStatesMSDD =0;
        Integer tooFewStatesMSDD =0;
        Integer extraStatesAPR =0;
        Integer tooFewStatesAPR =0;
        
        for (int stateLoop = 0; stateLoop < stateActionLots.size(); stateLoop++) {
            StateActions stateActions = stateActionLots.getStateActions(stateLoop);
            Percep percep = stateActions.getState();
            for (int actionLoop = 0; actionLoop < stateActions.getNumActions(); actionLoop ++) {
                totalTested ++;
                Action action = stateActions.getAction(actionLoop);
                     
                LogFiles logfile = LogFiles.getInstance();
                logfile.print("\n Initial test percep act is:" + percep.toString() + "Action: " + action.toString(),1);
                 
                if ((learnedRulesMSDD != null) || (learnedRulesApriori != null)) {
                   
                   logfile.print("\n STATES GENERATED FROM 200000 STATE ACTION MAP: \n",1);
                    
                   ArrayList generatedStatesLots = stateGeneratorLotsActionMap.generateNextStates(percep, action);
                   totalStateActionStates += generatedStatesLots.size();
                   
                   ArrayList generatedStatesAprioriRules = null;
                   if (learnedRulesApriori != null) {
                       
                       logfile.print("\n STATES GENERATED FROM APRIORI RULES: \n",1);
                        
                       generatedStatesAprioriRules = stateGeneratorAprioriRules.generateNextStates(percep, action);
                    }

                   
                   logfile.print("\n STATES GENERATED FROM STATE ACTION MAP: \n",1);
                    
                   ArrayList generatedStatesActionMap = stateGeneratorStateActionMap.generateNextStates(percep, action);

                   ArrayList generatedStatesMSDDRules = null;
                   if (learnedRulesMSDD != null) {
                       
                       logfile.print("\n STATES GENERATED FROM MSDD RULES: \n",1);
                        
                       generatedStatesMSDDRules = stateGeneratorMSDDRules.generateNextStates(percep, action);
                    }
                    /*Now get the error measure for each of these against LOTS version
                    */
                    
                    logfile.print("\n State Errors initial test percep act is:" + percep.toString() + "Action: " + action.toString(),1);
                    logfile.print("\n Correct following states are:\n",1);
                    for (int i = 0; i < generatedStatesLots.size(); i++) {
                       logfile.print(((StateAndProb)generatedStatesLots.get(i)).getPercep().getString(),1);
                       logfile.println(" " + ((StateAndProb)generatedStatesLots.get(i)).getProbability(),1);
                       
                    }
                     
                    ArrayList stateFewMany = new ArrayList();
                    stateFewMany.add(0);
                    stateFewMany.add(0);
                    stateFewMany.set(0, tooFewStatesSAM);
                    stateFewMany.set(1, extraStatesSAM);
                    
                    logfile.print("\n State Action Map Errors: ",1);
                     
                    stateActionMapError += generatedStatesError(generatedStatesLots, generatedStatesActionMap, stateFewMany);
                    tooFewStatesSAM = (Integer)stateFewMany.get(0);
                    extraStatesSAM = (Integer)stateFewMany.get(1);
                   
                    if (learnedRulesMSDD != null) {
                        stateFewMany.set(0, tooFewStatesMSDD);
                        stateFewMany.set(1, extraStatesMSDD);
                        
                        logfile.print("\n MSDD rule Errors: ",1);
                         
                        MSDDError += generatedStatesError(generatedStatesLots, generatedStatesMSDDRules, stateFewMany);
                        tooFewStatesMSDD = (Integer)stateFewMany.get(0);
                        extraStatesMSDD = (Integer)stateFewMany.get(1);
                      
                    }

                    if (learnedRulesApriori != null) {
                        stateFewMany.set(0, tooFewStatesAPR);
                        stateFewMany.set(1, extraStatesAPR);
                        
                        logfile.print("\n Apriori rules Errors: ",1);
                         
                        aprioriError += generatedStatesError(generatedStatesLots, generatedStatesAprioriRules, stateFewMany);
                        tooFewStatesAPR = (Integer)stateFewMany.get(0);
                        extraStatesAPR = (Integer)stateFewMany.get(1);
                    }
                 
                }
            }
        }
        
         LogFiles logfile2 = LogFiles.getInstance();
         logfile2.print("\n State action Error: " + stateActionMapError,2);
         logfile2.print("\n MSDDError Error: " + MSDDError,2);
         logfile2.print("\n aprioriError Error: " + aprioriError,2);
         logfile2.print("\n total state action pairs tested: " + totalTested,2);
         logfile2.print("\n Total Stateaction States: " + totalStateActionStates,2);
         logfile2.print("\n Total Extra SAM States: " + extraStatesSAM,2);
         logfile2.print("\n Total to few SAM States: " + tooFewStatesSAM,2);
         logfile2.print("\n Total Extra MSDD States: " + extraStatesMSDD,2);
         logfile2.print("\n Total to few MSDD States: " + tooFewStatesMSDD,2);
         logfile2.print("\n Total Extra APR States: " + extraStatesAPR,2);
         logfile2.print("\n Total to few APR States: " + tooFewStatesAPR,2);
        
          
    }
    
      public void compairClauseStateGenerators(ClauseList learnedClausesApriori) {
        
        StateActionMap stateActionLots = null;
        
        try {   
            /* Open the file and set to read objects from it. */ 
            FileInputStream istream = new FileInputStream(STATE_ACTION_LOTS_FILE_NAME); 
            ObjectInputStream q = new ObjectInputStream(istream); 

            /* Read the learned rules object */ 
            stateActionLots = (StateActionMap)q.readObject();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }   
        
        StateGenerator stateGeneratorLotsActionMap = new StateActionMapStateGenerator(stateActionLots);
        StateGenerator stateGeneratorAprioriClauses = new ClauseStateGenerator(learnedClausesApriori);
        StateGenerator stateGeneratorStateActionMap = new StateActionMapStateGenerator(stateActionMap);
        
     

        double stateActionMapError = (double)0.0f;
        double aprioriError = (double)0.0f;
        int totalTested = 0;
        int totalStateActionStates = 0;
        Integer extraStatesSAM  =0;
        Integer tooFewStatesSAM =0;
        Integer extraStatesAPR =0;
        Integer tooFewStatesAPR =0;
        
        for (int stateLoop = 0; stateLoop < stateActionLots.size(); stateLoop++) {
            StateActions stateActions = stateActionLots.getStateActions(stateLoop);
            Percep percep = stateActions.getState();
            for (int actionLoop = 0; actionLoop < stateActions.getNumActions(); actionLoop ++) {
                totalTested ++;
                Action action = stateActions.getAction(actionLoop);
                     
                LogFiles logfile = LogFiles.getInstance();
                logfile.print("\n Initial test state is:" + percep.toString() + " " + action.toString(),2);
                 
                if (learnedClausesApriori != null) {
                   
                   logfile.print("\n STATES GENERATED FROM 200000 STATE ACTION MAP: \n",1);
                    
                   ArrayList generatedStatesLots = stateGeneratorLotsActionMap.generateNextStates(percep, action);
                   totalStateActionStates += generatedStatesLots.size();
                   
                   
                   logfile.print("\n STATES GENERATED FROM APRIORI RULES: \n",1);
                    
                   ArrayList generatedStatesAprioriRules = stateGeneratorAprioriClauses.generateNextStates(percep, action);

                   
                   logfile.print("\n STATES GENERATED FROM STATE ACTION MAP: \n",1);
                    
                   ArrayList generatedStatesActionMap = stateGeneratorStateActionMap.generateNextStates(percep, action);
                   
           
                    /*Now get the error measure for each of these against LOTS version
                    */
                    ArrayList stateFewMany = new ArrayList();
                    stateFewMany.add(0);
                    stateFewMany.add(0);
                    stateFewMany.set(0, tooFewStatesSAM);
                    stateFewMany.set(1, extraStatesSAM);
                    
                    logfile.print("\n State action Map Errors: ",1);
                     
                    stateActionMapError += generatedStatesError(generatedStatesLots, generatedStatesActionMap, stateFewMany);
                    tooFewStatesSAM = (Integer)stateFewMany.get(0);
                    extraStatesSAM = (Integer)stateFewMany.get(1);
                    stateFewMany.set(0, tooFewStatesAPR);
                    stateFewMany.set(1, extraStatesAPR);
                    
                    logfile.print("\n Apriori Clause rules Errors: ",1);
                     
                    aprioriError += generatedStatesError(generatedStatesLots, generatedStatesAprioriRules, stateFewMany);
                    tooFewStatesAPR = (Integer)stateFewMany.get(0);
                    extraStatesAPR = (Integer)stateFewMany.get(1);
                    
                    
                }
            }
        }
        
         LogFiles logfile2 = LogFiles.getInstance();
         logfile2.print("\n State action Error: " + stateActionMapError,2);
         logfile2.print("\n aprioriError Error: " + aprioriError,2);
         logfile2.print("\n total state action pairs tested: " + totalTested,2);
         logfile2.print("\n Total Stateaction States: " + totalStateActionStates,2);
         logfile2.print("\n Total Extra SAM States: " + extraStatesSAM,2);
         logfile2.print("\n Total to few SAM States: " + tooFewStatesSAM,2);
         logfile2.print("\n Total Extra APR States: " + extraStatesAPR,2);
         logfile2.print("\n Total to few APR States: " + tooFewStatesAPR,2);
        
          
    }
    
    public double generatedStatesError(ArrayList trueStates, ArrayList generatedStates, ArrayList stateFewMany) {
        
        double error = (double)0.0f;
        
       
        if (trueStates != null) {
            for (int t = 0; t < trueStates.size(); t ++) {
                Percep tState = ((StateAndProb)trueStates.get(t)).getPercep();
                double tProb = (double)((StateAndProb)trueStates.get(t)).getProbability();
                boolean matching = false;
                if (generatedStates != null) {
                    for (int g = 0; g < generatedStates.size(); g++) {
                        Percep gState = ((StateAndProb)generatedStates.get(g)).getPercep();
                        double gProb = (double)((StateAndProb)generatedStates.get(g)).getProbability();
                        if (gState.isEqual(tState)) {
                            matching = true;
                            double er = gProb - tProb;
                            if (er < (double)0.0f)
                                er = -error;
                            error += er;
                            g = generatedStates.size();
                        }
                    }
                } else {
                    LogFiles logfile = LogFiles.getInstance();
                    logfile.print("\n Generated states are null in error measure.",1);
                     
                }
                if (!matching) {
                    LogFiles logfile = LogFiles.getInstance();
                    logfile.print("\n Missed this state: " + tState.toString(),1);
                     
                    error += (double)0.5f;
                    stateFewMany.set(0, ((Integer)stateFewMany.get(0)).intValue()+1);
                }
            }
        } else {
            LogFiles logfile = LogFiles.getInstance();
            logfile.print("\n Generated states are null in error measure.",1);
             
        }
            
          
        /*Now add 0.5 for all the generated states which don't match the real ones*/
        if (generatedStates == null)
            return error;
        
        for (int g = 0; g < generatedStates.size(); g ++) {
            Percep gState = ((StateAndProb)generatedStates.get(g)).getPercep();
            boolean matching = false;
            if (trueStates != null) {
                for (int t = 0; t < trueStates.size(); t++) {
                   Percep tState = ((StateAndProb)trueStates.get(t)).getPercep();
                    if (gState.isEqual(tState)) {
                        matching = true;
                        t = trueStates.size();
                    }
                }
            }
            if (!matching) {
                error += (double)0.5f;
                stateFewMany.set(1, ((Integer)stateFewMany.get(1)).intValue()+1);
                LogFiles logfile = LogFiles.getInstance();
                logfile.print("\n Extra state: " + gState.toString(),1);
                 
            }
        }
        
        return error;
    }


public StateValueMap generateRuleValueMap (NodeList learnedRules) {
          
        StateGenerator stateGenerator;
        ReinforcementLearner tempReinforcementLearner = new ReinforcementLearner();
       
        stateGenerator = new RuleStateGenerator(learnedRules);
       
        tempReinforcementLearner.setStateGenerator(stateGenerator);
        
        return refineValueMap(tempReinforcementLearner);
    }
    
    
    public StateValueMap generateClauseValueMap (ClauseList learnedClauses) {
          
        StateGenerator stateGenerator;
        ReinforcementLearner tempReinforcementLearner = new ReinforcementLearner();
       
        stateGenerator = new ClauseStateGenerator(learnedClauses);
        
        tempReinforcementLearner.setStateGenerator(stateGenerator);
        
        return refineValueMap(tempReinforcementLearner);
        
    }
    
      ///Start of state action map generation ***********************
    
    public StateActionValueMap generateStateActionStateActionValueMap(StateActionMap stateActionMap) {
        StateGenerator stateGenerator;
        
        stateGenerator = new StateActionMapStateGenerator(stateActionMap);
        
        ReinforcementLearner tempReinforcementLearner = new ReinforcementLearner();
        
        tempReinforcementLearner.setStateGenerator(stateGenerator);
        
        return refineStateActionValueMap(tempReinforcementLearner);
    }      
    
    public StateActionValueMap generateRuleStateActionValueMap (NodeList learnedRules) {
          
        StateGenerator stateGenerator;
        ReinforcementLearner tempReinforcementLearner = new ReinforcementLearner();
       
        stateGenerator = new RuleStateGenerator(learnedRules);
       
        tempReinforcementLearner.setStateGenerator(stateGenerator);
        
        return refineStateActionValueMap(tempReinforcementLearner);
    }
    
    
    public StateActionValueMap generateClauseStateActionValueMap (ClauseList learnedClauses) {
          
        StateGenerator stateGenerator;
        ReinforcementLearner tempReinforcementLearner = new ReinforcementLearner();
       
        stateGenerator = new ClauseStateGenerator(learnedClauses);
        
        tempReinforcementLearner.setStateGenerator(stateGenerator);
        
        return refineStateActionValueMap(tempReinforcementLearner);
        
    }
    
    public void generateClauseValues (ClauseList learnedClauses) {
          
        StateGenerator stateGenerator;
        ClauseReinforcementLearner clauseReinforcementLearner = new ClauseReinforcementLearner();
       
        stateGenerator = new ClauseStateGenerator(learnedClauses);
        
        clauseReinforcementLearner.setStateGenerator(stateGenerator);
        
        refineClauseValueMap(clauseReinforcementLearner, learnedClauses);
        
    }
    
    //End of state action map generation ***************************
    
      public StateValueMap generateStateActionValueMap(StateActionMap stateActionMap) {
        StateGenerator stateGenerator;
        
        stateGenerator = new StateActionMapStateGenerator(stateActionMap);
        
        ReinforcementLearner tempReinforcementLearner = new ReinforcementLearner();
        
        tempReinforcementLearner.setStateGenerator(stateGenerator);
        
        return refineValueMap(tempReinforcementLearner);
    }      
      
    //abstract public StateValueMap refineValueMap (ReinforcementLearner tempReinforcementLearner);
    //abstract public StateActionValueMap refineStateActionValueMap (ReinforcementLearner tempReinforcementLearner);
    //abstract public void refineClauseValueMap (ClauseReinforcementLearner tempReinforcementLearner, ClauseList clauseList);
    public StateValueMap refineValueMap (ReinforcementLearner tempReinforcementLearner) {
          
        RuleElements initialState = new RuleElements();
        
        Action action = defaultAction();
        Percep initialPercep = (Percep)body.getPercep().clone();
        Percep nextPercep = (Percep)body.getPercep().clone();
        
        LogFiles logfile = LogFiles.getInstance();
         
     
        System.out.println("\nWARNING (Predator Agnet): Reinforcement learner set to " + REFINE_MAP_STEPS + " itterations");
        for (int i = 0; i < REFINE_MAP_STEPS; i++) {
            //System.out.print("\nInitial state: " + initialState.toString() + "\n");
            tempReinforcementLearner.bellmanRefineValue(initialPercep, action);

            Percep percep = tempReinforcementLearner.pickRandomState();

            if (percep != null) {
                for (int j = 0; j < percep.getNumFluents(); j++) {
                    initialPercep.getFluent(j).setByValue(percep.getFluent(j).getValue());
                }
            }
        }

        return tempReinforcementLearner.getValueMap(); 
    }
    
    public StateActionValueMap refineStateActionValueMap (ReinforcementLearner tempReinforcementLearner) {
        
        Action action = defaultAction();
        
        
        Percep agentPercep = (Percep)body.getPercep().clone();
        Percep nextPercep = (Percep)body.getPercep().clone();

        
         
     
        System.out.println("\nWARNING (Predator Agnet): State Action Reinforcement learner set to " + REFINE_MAP_STEPS+ " itterations");
        for (int i = 0; i < 1; i++) {
            //System.out.print(i);
            //System.out.flush();
            
            int whatever = 0;
            
            for (int j = 0; j < REFINE_MAP_STEPS; j++) {
                
                //This is the standard q learning reinforcement 
                //nextPercep = (PredatorAgentPercep)tempReinforcementLearner.getStateGenerator().generateNextState((Percep)agentPercep, action);
                //tempReinforcementLearner.QLearningRefineValue(agentPercep, action, nextPercep);
                //System.out.print("Are we sure we want nextPercep and not random perep?");
                //agentPercep = (PredatorAgentPercep)tempReinforcementLearner.pickRandomState();
                //if (agentPercep == null)
                //    agentPercep = nextPercep;
                
                //This is the DynaQMethod
                ArrayList nextStatesAndProbs = tempReinforcementLearner.getStateGenerator().generateNextStates((Percep)agentPercep, action);
                if (nextStatesAndProbs != null) {
                    tempReinforcementLearner.DynaQLearningRefineValue(agentPercep, action, nextStatesAndProbs);
                    agentPercep = (Percep)tempReinforcementLearner.getStateGenerator().rouletteStatesAndProbs(nextStatesAndProbs);
                }

                //Need to do something here to pick a random action. Not sure where this should be done
                //but it must be done
                //Also need to add the generate random state things to the other types of state generator (only done 
                //for state action so far)
                 //action = tempReinforcementLearner.getStateGenerator().pickRandomAction();

                //***FRIG for random action****
                //if this is not an action the agent is allowed to take then go to the next action
                action.randomAction();
            }
        }

        return tempReinforcementLearner.getStateActionValueMap(); 
    }
    
    
    
       
    public void refineClauseValueMap (ClauseReinforcementLearner tempReinforcementLearner, ClauseList clauseList) {

        double ALPHA = 1.0f;
        double GAMMA = 0.9f;

        Action action = defaultAction();
        Percep agentPercep = (Percep)body.getPercep().clone();
        Percep nextPercep = (Percep)body.getPercep().clone();

        LogFiles logfile = LogFiles.getInstance();
         
     
        System.out.println("\nWARNING (Pedator Agnet): Clause (Rule based) \n Value Reinforcement learner set to " + REFINE_MAP_STEPS_RVRL+ " itterations");
        for (int i = 0; i < 1; i++) {
            //System.out.print(i);
            //System.out.flush();
            
            int whatever = 0;
            
            for (int j = 0; j < REFINE_MAP_STEPS_RVRL; j++) {
                
                if (j == REFINE_MAP_STEPS_RVRL - 10) {
                    int letsLook = 1;
                }

                 if (j > (int)(REFINE_MAP_STEPS_RVRL/4.0f*3.0f)) {
                    ALPHA = 0.1f;
                }
                
                //This is the DynaQMethod with clauses
                ArrayList nextStatesAndProbs = tempReinforcementLearner.getStateGenerator().generateNextStates((Percep)agentPercep, action);
                tempReinforcementLearner.DynaQClauseLearningRefineValue(agentPercep, action, nextStatesAndProbs, clauseList, ALPHA, GAMMA);



                //NOTE: Changed back to roulette to keep the approximations accurate
                //Percep newAgentPercep = (Percep)tempReinforcementLearner.getStateGenerator().rouletteStatesAndProbs(nextStatesAndProbs);
                //NOTE: CHANGEED FROM ROULETTE TO VISITING EACH RANDOMLY
                //if (j < (int)(REFINE_MAP_STEPS_RVRL/2.0f)){
                //    newAgentPercep = (Percep)tempReinforcementLearner.getStateGenerator().randomState(nextStatesAndProbs);
                //}

                Percep newAgentPercep = (Percep)tempReinforcementLearner.getStateGenerator().randomState(nextStatesAndProbs);

                //Itterate throught the rest of the actions to update the states as fairly as possible
                /*if (j < (int)(REFINE_MAP_STEPS_RVRL/2.0f)){
                    Action actionRefine = (Action)action.clone();
                    actionRefine.firstLegalAction();
                    while (actionRefine != null) {
                        nextStatesAndProbs = tempReinforcementLearner.getStateGenerator().generateNextStates((Percep)agentPercep, actionRefine);
                        tempReinforcementLearner.DynaQClauseLearningRefineValue(agentPercep, actionRefine, nextStatesAndProbs, clauseList, ALPHA, GAMMA);
                        actionRefine = actionRefine.nextLegalAction();
                    }
                }*/

                //Need to do something here to pick a random action. Not sure where this should be done
                //but it must be done
                //Also need to add the generate random state things to the other types of state generator (only done 
                //for state action so far)
                if (newAgentPercep != null) {
                    agentPercep = (Percep)newAgentPercep;
                    //action = tempReinforcementLearner.getStateGenerator().pickRandomAction();
                }

                if (true) {
                //***FRIG for random action****
                    action.randomAction();
                } else {
                    //if this is not an action the agent is allowed to take then go to the next action
                    //take a random policy to start with to build the map
                    if (j < (int)(REFINE_MAP_STEPS_RVRL/2.0f)) {
                        action.randomAction();   
                    } else {
                        //then refine it a bit using an e-greedy policy
                        if (j < (int)(REFINE_MAP_STEPS_RVRL/4.0f*3.0f)) {
                            if (Math.random() < 0.2f)
                                action.randomAction();   
                            else
                                action = clauseList.getBestAction(agentPercep, action);
                        } else {
                            //finally use a greedy policy to firm up th policy
                            action = clauseList.getBestAction(agentPercep, action);
                        }
                    }
                }
            }
        }

        //return tempReinforcementLearner.getStateActionValueMap(); 
    }
    
     //deliberate on the percept to create an action
    //should be possible to happed on different thread
    public Action deliberate() 
    {  
          
        if (!realThinker) {
            //this is a dumb random agent
            action.randomDumbAction();
            return action;
        }
 
        if (action == null) {
            action = defaultAction();
        }
        
        if (USE_REINFORCEMENT_POLICY) {
            Percep thePercep = (Percep)body.getPercep();

            if (!USE_CLAUSE_VALUES) {
                if (USE_BELLMAN) {
                    return (Action)reinforcementLearner.getBestBellmanAction(thePercep, action).clone();
                } else {

                    Action actionReturned = reinforcementLearner.getBestQAction(thePercep);

                    if (actionReturned != null)
                        return (Action)actionReturned.clone();
                    else {
                        System.out.print("Problem with getBestQAction");
                    }
                }
            } else { //USE_CLAUSE_VALUES
                Action actionReturned = learnedClausesASDD.getBestAction(thePercep, action);

                if (actionReturned != null)
                    return (Action)actionReturned.clone();
                else {
                    System.out.print("Problem with getBestQAction");
                }
            }
        }
    
      
        //not using reinforcement policy so pick a random action
        action.randomAction();
        
        return action;
    }
    
    abstract public Action defaultAction();
}
