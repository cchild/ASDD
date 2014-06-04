/*
 * AgentStore.java
 *
 * Created on June 19, 2002, 1:58 PM
 */

package EnvModel;

import java.util.*;
import EnvAgent.*;

/**
 *
 * @author  Chris Child
 * A store for agents. Currently implemeneted as ArrayList. Could
 * be changed to a Map when we need more agents. 
 */
public class AgentStore extends Object {

    ArrayList agents;
    ArrayList agentTurns;
    
    /** Creates new AgentStore */
    public AgentStore() {
        agents = new ArrayList();
        agentTurns = new ArrayList();
    }
    
    public Agent getAgent(int which) {
        return (Agent)agents.get(which);
    }
    
    public int size() {
        return agents.size();
    }
    
    public void addAgent(Agent agent) {
        agents.add(agent);
        agentTurns.add(new Boolean(true));
    }
    
    public void removeAgent(Agent agent) {
        for (int i = 0; i < agents.size(); i++) {
            if (agent == agents.get(i)) {
                agents.remove(i);
                agentTurns.remove(i);
                break;
            }
        }
    }
    
    /*get the ID of this agent. Currently just it's position in the
     *array*/
    private int getID(Agent agent) {
        for (int i = 0; i < agents.size(); i++) {
            if (agent == agents.get(i)) {
                return i;
            }
        }
        return -1;
    }
    
    /*Uses the agents object reference to set their turn*/
    public void setAgentTurn(Agent agent, boolean value) {
        int agentID = getID(agent);
        agentTurns.set(agentID, new Boolean(value));
    }
    
    
    /*Return true if it is currently this agent's turn*/
    protected boolean getAgentsTurn(Agent agent) {
        int agentID = getID(agent);
        return ((Boolean)agentTurns.get(agentID)).booleanValue();
    }
    
    /*Increments the turn counter for the specified agent. This version
     *is based on agents having alternate turns*/
    protected void updateAgentsTurn(Agent agent) {
        int agentID = getID(agent);
        Boolean turnValue = (Boolean)(agentTurns.get(agentID));
        agentTurns.set(agentID, new Boolean(!turnValue.booleanValue()));
    }

}
