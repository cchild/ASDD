package EnvModel;


import EnvAgent.*;

//In this version of the environment agents are able to act directly on the
//state of the environment because it makes for much nicer code.
//In this way agents can now directly influence their bodies rather
//than waiting for the environment to do it.
//The dissadvantage is that you cannot resolve simultaneous actions any more,
//but its worth it
//We can then also remove the list of actions which the environment has and allow
//the agent to do things like act directly on its body.

import java.util.*;

public abstract class Environment extends Object
{
    /**The state of every physical thing in the environment*/
    protected State state;
    
    /**environment actions*/
    protected ActionStore envActions;
    /**agents, which can act on agent bodies only*/
    protected AgentStore agents;
  
    
    /**Constructor for the environment*/
    public Environment() {
        initEnvironment();
    }
    
    protected void initEnvironment() {
        agents = new AgentStore();
        envActions = new ActionStore();
    }
    
    /*return state. The state of the environment*/
    public State getState() {return state;}   
    
    /*add an agent (as opposed to an agent body) to the environment
     */
    public abstract void addAgent (Agent agent);
    
    public abstract void removeAgent (Agent agent);
    
    public AgentStore getAgents() {
        return agents;
    }
    
    /*add an environment action, which is an agentless action, to the
     *environment*/
    public void addEnvironmentAction (EnvironmentAction envAction) {
        envActions.addAction((Action)envAction); 
    } 
    
    public abstract boolean getTurnBased();
   
}
      

