/*
 * NodeList.java
 *
 * Created on 20 November 2002, 18:34
 */

package EnvAgent.RuleLearner;

import EnvModel.*;

import java.util.*;
import java.io.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class NodeList implements Serializable {

    private ArrayList nodeList;
    
    private boolean listSorted;
    /** Creates new NodeList */
    public NodeList() {
        nodeList = new ArrayList();
        listSorted = false;
    }
    
    public ArrayList getArrayList() {
        listSorted = false;
        return nodeList;
    }
    
    public RuleNode get(int which) {
        return (RuleNode)nodeList.get(which);
    }
    
    public void set(int which, RuleNode val) {
        listSorted = false;
        nodeList.set(which, val);
    }
    
    public int size() {
        return nodeList.size();
    }
    
    public void remove(RuleNode node) {
        nodeList.remove(node);
    }
    
    public void remove(int which) {
        nodeList.remove(which);
    }
    
    public void add(RuleNode node) {
        listSorted = false;
        nodeList.add(node);
    }
    
    public void addAt(int pos, RuleNode node) {
        listSorted = false;
        nodeList.add(pos, node);
    }
    
    public void addAll(NodeList otherList) {
        listSorted = false;
        nodeList.addAll(otherList.getArrayList());
    }
    
    public void sortNonIncreasingGenerality() {
        for (int i =0; i < nodeList.size() -1; i++) {
            for (int j = 0; j < nodeList.size() -1; j++) {
                if (get(j).countWildcards() < get(j+1).countWildcards()) {
                    RuleNode temp = get(j);
                    nodeList.set(j, get(j+1));
                    nodeList.set(j+1, temp);
                }
            }
        } 
        
        listSorted = true;
    }
    
    /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        
        s.writeBoolean(listSorted);
        s.writeObject(nodeList);
    }
  
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        
        listSorted = s.readBoolean();
        nodeList = (ArrayList)s.readObject();
    }
    
    public ArrayList rulesWithSameOutputFluent() {
        ArrayList outLists = new ArrayList();
        
        for (int i = 0; i < get(0).getSuccessor().size() - 1; i ++) {
            outLists.add(new NodeList());
        }
        
        //Go through each rule adding the successor to which it refers to the list
        for (int i = 0; i < size(); i++) {
            RuleElements successor = get(i).getSuccessor();
            ((NodeList)outLists.get(successor.getFirstNonWildcardPosition() -1)).add(get(i));
        }
        
        return outLists;
    }
    
    public NodeList filterByPrecedence() {
        ArrayList sameOutputFluentRules = rulesWithSameOutputFluent();
        
        NodeList finalRuleSet = new NodeList();
        
        for (int outFluentLoop = 0; outFluentLoop < sameOutputFluentRules.size(); outFluentLoop ++) {
            NodeList rulesWithSameOutput = (NodeList)sameOutputFluentRules.get(outFluentLoop);
            for (int sameOutputLoop = rulesWithSameOutput.size() -1; sameOutputLoop >=0; sameOutputLoop --) {
                RuleNode removableRule = rulesWithSameOutput.get(sameOutputLoop);                
                for (int checkAgainstLoop = rulesWithSameOutput.size() -1; checkAgainstLoop >=0; checkAgainstLoop --) {   
                    RuleNode checkAgainstRule = rulesWithSameOutput.get(checkAgainstLoop);
                    if (removableRule.defersTo(checkAgainstRule)) {
                        rulesWithSameOutput.remove(sameOutputLoop);
                        checkAgainstLoop = -1;
                    } else {
                        //doesn' defer to, but add an extra check to make sure precedence has been set
                        if (removableRule != checkAgainstRule) {
                            if (!(removableRule.getRuleSet() == checkAgainstRule.getRuleSet())) {
                                if (!checkAgainstRule.defersTo(removableRule)) {
                                    //Thiese rules have never met, so keep the most general one
                                    if (removableRule.getPrecursorequals() <= checkAgainstRule.getPrecursorequals())
                                    {
                                        //System.out.print("\nRemoved never met rule:" + removableRule.toString() + " by " + checkAgainstRule.toString());
                                        rulesWithSameOutput.remove(sameOutputLoop);
                                        checkAgainstLoop = -1;
                                    }
                                }
                            }
                        }
                    }
                }                 
            }
            
            finalRuleSet.addAll(rulesWithSameOutput);
        }
        
        return finalRuleSet;
    }
    
    
    public void removeNodesWithPrecursorAndSuccessorPos(RuleElements precursor, int successorPos) {
        for (int i = size() -1; i >= 0; i--) {
            if (get(i).getPrecursor().isEqualTo(precursor)) {
                if (get(i).getSuccessor().getFirstNonWildcardPosition() == successorPos) {
                    if (LogFile.OUTPUT_LOG0) {
                        LogFile logfile = new LogFile();
                        logfile.print("Removed because covered by rule with less fluents\n");
                        logfile.print(get(i).toString());
                        logfile.close();
                    }
                    nodeList.remove(i);
                }
            }
        }
    }
    
    public boolean ruleAlreadyExists(RuleNode testNode) {
        for (int i = 0; i < size(); i++) {
            if (testNode.isEqualTo(get(i)))
                return true;
        }
        return false;
    }
    
    /*get a list of nodes whos precursor's match the current state from the complete node list*/
    public NodeList matchingNodes(RuleElements currentState) {
        NodeList matchingNodes = new NodeList();
        for (int i = 0; i < size(); i++) {
            if (get(i).getPrecursor().equals(currentState))
                matchingNodes.add(get(i));
        }
        
        return matchingNodes;
    }
    
    public String toString() {
        String str = new String();
        for (int i = 0; i < size(); i ++) {
            str += "\n" + get(i).toString(); 
        }
        return str;
    }

    
}
