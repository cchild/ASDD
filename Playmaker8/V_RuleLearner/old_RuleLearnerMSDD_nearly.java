package V_RuleLearner;

//
//
//package RuleLearner2;
//
//
//import Logging.*;
//import Token.*;
//import java.util.ArrayList;
//import java.util.Collections;
//
//
///**
// *
// * @author virgile
// */
//public class RuleLearnerMSDD_nearly {
//    
//    public TokenMap tokenMap;
//    public SensorList sensorMap;
//   
//    
//    public RuleLearnerMSDD_nearly(TokenMap t, SensorList s) {
//        this.tokenMap = t;
//        this.sensorMap = s;
//    }
//    
//    
//public static int learnRulesMSDD()
//    {
//
//        
//        // INIT
//        // INIT
//        // INIT
//        TokenMap t = new TokenMap();
//        SensorList slist = new SensorList();
//        RuleList rulelist = new RuleList();
//        
//        t.fromFile();
//        slist.fromFile(t);
//        slist.printList();
//        
//        int totalrules = 0;
//        int relevantrules = 0;
//        int maxrules = 900000;
//        boolean pruning = true;
//        boolean sort_rulelist = true;
//        int maxLevel = 1;
//        
//        
//        // PROBABILITIES INIT
//        double ruleOcc = 0;
//        double rulePrecOcc = 0;
//        ArrayList <Double> prob = new ArrayList();
//        
//        
//        Sensor rootsensor = new Sensor("******",t);
//        Rule rootnode = new Rule(rootsensor, rootsensor);
//        rootnode.occurrencies = slist.size();
//        rulelist.addRule(rootnode);
//        
//
//  
//        
//        
//        
//        // GENERATING LEVEL Y RULES
//        // GENERATING LEVEL Y RULES
//        // GENERATING LEVEL Y RULES
//
//        for (int y=1; y<= maxLevel; y++) {
//        RuleList pullList = rulelist.copy();
//        pullList.sort();
//        
//        //System.out.println("HERE : " + rulelist.printList());
//        //pullList = pullList.removeUnexpandable();
//        
//        
//        
//        for (int i=0; i< pullList.size(); i++) {
//            SensorList pre_children = pullList.getRule(i).precondition.expand(pullList.getRule(i).precondition.isExpandable());
//            SensorList post_children = pullList.getRule(i).postcondition.expand(pullList.getRule(i).postcondition.isExpandable());
//            //System.out.println(pullList.getRule(i).postcondition);
//            pre_children.printList();
//            post_children.printList();
//            
//            pullList.printList();
//            //pre_children.printList();
//            //post_children.printList();
//            for (int j=0; j< pre_children.size(); j++) {
//                for(int h=0; h<post_children.size();h++) {
//                    Rule rule = new Rule(pre_children.getSensor(j+1),post_children.getSensor(h+1));
//                    
//                    //System.out.println("TRY " + pullList.getRule(j).precondition + pullList.size());
//                    //System.out.println("POST " + post_children.getSensor(h+1));
//                    Rule rule2 = new Rule(pullList.getRule(j).precondition,post_children.getSensor(h+1));
//                    //Rule rule3 = new Rule(pre_children.getSensor(j+1),pullList.getRule(h).postcondition);
//                    
//                    rule.occurrencies = slist.indexesOfRule(rule).size();
//                    rule2.occurrencies = slist.indexesOfRule(rule2).size();
//                    //rule3.occurrencies = slist.indexesOfRule(rule3).size();
//                    
//                    totalrules = totalrules + 3;
//                    if ((pruning && rule.occurrencies > 0) || (!pruning))  {
//                            
//                            if (!rulelist.containsRule(rule)) {
//                            // RULE 1
//                            
//                            rulelist.addRule(rule);
//                            ruleOcc = rule.occurrencies;
//                                    
//                            if (ruleOcc != 0) {
//                            
//                            rulePrecOcc = slist.indexesOfSensor(rule.precondition).size();
//                            
//                            
//                            double proba = ruleOcc / rulePrecOcc;
//                            proba = Math.round(proba * 1000);
//                            proba = proba/1000;
//                            prob.add(proba);
//                            if (proba > 0)
//                                relevantrules++;
//                            }
//                            else { prob.add(0.); }
//                            
//                            }
//                    }
//                    if ((pruning && rule2.occurrencies > 0) || (!pruning))  {
//                            
//                        if (!rulelist.containsRule(rule2)) {
//                            // RULE 2
//                            rulelist.addRule(rule2);
//                            
//                            ruleOcc = rule2.occurrencies;
//                            if (ruleOcc != 0) {
//                            
//                            rulePrecOcc = slist.indexesOfSensor(rule2.precondition).size();
//                            
//                            
//                            double proba = ruleOcc / rulePrecOcc;
//                            proba = Math.round(proba * 1000);
//                            proba = proba/1000;
//                            prob.add(proba);
//                            if (proba > 0)
//                                relevantrules++;
//                            }
//                            else { prob.add(0.); }
//                            
//                            
//                        }
//                    }
//                    /*
//                    if ((pruning && rule3.occurrencies > 0) || (!pruning)) {
//                            
//                            if (!rulelist.containsRule(rule3)) {
//
//                            // RULE 3
//                            rulelist.addRule(rule3);
//                            
//                            ruleOcc = rule3.occurrencies;
//                            if (ruleOcc != 0) {
//                            rulePrecOcc = slist.indexesOfSensor(rule3.precondition).size();
//                            
//                            
//                            double proba = ruleOcc / rulePrecOcc;
//                            proba = Math.round(proba * 1000);
//                            proba = proba/1000;
//                            prob.add(proba);
//                            if (proba > 0)
//                                relevantrules++;
//                               }
//                            else { prob.add(0.); }
//                            }
//                    }*/
//                    }
//                }
//            }
//        }
//        
//        //rulelist.printList("LEVEL "+y,prob);
//        
//       //rulelist = rulelist.removeUnseenRules(slist);
//        if (sort_rulelist) {
//            // ONLY FOR PRESENTATION, RULELIST ALREADY SORTED IN LEVELS GENERATION
//            rulelist = rulelist.sort();
//        }
//        
//        rulelist.printList("LEVEL "+ maxLevel);
//       
//       
//       System.out.println(totalrules + " rules were explored, " + relevantrules + " were relevant."); 
//
//       System.out.println(rulelist.size());
//       System.out.println(prob.size());
////       Sensor he = new Sensor("A***ES",t);
////       Sensor ho = new Sensor("E*****",t);
////       Rule rule1 = new Rule(he,ho);
////       System.out.println(rulelist.containsRule(rule1));
//        
//        return 0;
//    }
//
//
//
//
//
//public static void main (String[] args) {
//
//
//    
//    //learnRulesMSDD();
//
//     TokenMap t = new TokenMap();
//     t.fromFile();
//     
//     SensorList sList = new SensorList();
//     sList.fromFile(t);
//     //sList.printList();
//    
//     String str_pre = "******";
//     String str_post = "******";
//     
//     int maxnodes = 900000;
//     int totalnodes = 0;
//     boolean nodes_empty = false;
//     boolean pruning = true;
//     
//     Sensor pre = new Sensor(str_pre,t);
//     Sensor post = new Sensor(str_post,t);
//     Rule rootRule = new Rule(pre,post);
//     
//     RuleList children = new RuleList();
//     
//     children = rootRule.getChildren(sList).sort();
//     totalnodes = children.size();
//     
//     //while ((totalnodes < maxnodes) && (!nodes_empty)) {
//     for (int i=0; i<children.size(); i++) {
//        
//         if (totalnodes >= maxnodes) {
//                break;   
//            }
//         
//        RuleList children2 = new RuleList();
//        
//        children2 = children.getRule(i).getChildren(sList).sort();
//        
//        for (int j=0; j<children2.size(); j++) {
//            if ((!pruning) || (children2.getRule(j).occurrencies >0)) {
//            children.addRule(children2.getRule(j));
//            totalnodes++;
//            
//            if ((totalnodes % 1000) == 0 ) {
//            //System.out.println("Totalnodes : " + totalnodes);
//            }
//            //System.out.println("Totalnodes : " + totalnodes);
//            
//            if (totalnodes >= maxnodes) {
//                System.out.println("MAXNODES REACHED");
//                break;
//                
//            }
//            if ((i == children.size()-1) && (j == children2.size()-1)) {
//                nodes_empty = true;
//                System.out.println("EMPTY_NODE REACHED");
//                break;
//            }
//        }
//     }
//     //}
//     }
//     
//     //System.out.println("MSDD Rules (" + totalnodes + " entries) learned. ");
//     children.printList();
//    
////     children = rootRule.getChildren(sList);
////     
////     children.printList();
//     
//    
//}  
//
//
//}
//
//  
//
//
