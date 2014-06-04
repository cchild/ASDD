package EnvModel;


import EnvAgent.*;

//This version of the environment works on a timer. Agents are updated by a float
//timer in seconds.
//In this version of the environment agents are able to act directly on the
//state of the environment because it makes for much nicer code.
//In this way agents can now directly influence their bodies rather
//than waiting for the environment to do it.
//The dissadvantage is that you cannot resolve simultaneous actions any more,
//but its worth it
//We can then also remove the list of actions which the environment has and allow
//the agent to do things like act directly on its body.

import java.util.*;

public abstract class TimedEnvironment extends Environment
{  
    /**The length of a single time step in*/
    protected float timeStep;
    /**Time left over from a single time step in last advance*/
    protected float timeLeftOver;
    
    public static final float DEFAULT_TIME_STEP = 0.1f;
    
    /**Constructor for the environment*/
    public TimedEnvironment() {
        initEnvironment();
    }
    
    
    protected void initEnvironment() {
        super.initEnvironment();
        timeLeftOver = 0.0f;
        timeStep = DEFAULT_TIME_STEP;
    }
    
    /**return the time in seconds for a complete step in this
     *this environment*/
    public float getTimeStep() {return timeStep;}
    
  
    /*add an agent (as opposed to an agent body) to the environment*/
    public void addAgent (Agent agent) { 
        agents.addAgent(agent);
    }
    
    /*remove an agent from the environment using their object address*/
    public void removeAgent (Agent agent) {
        agents.removeAgent(agent);   
    }
    
    /*move everything in the environment along by "time" given
     *in seconds. All agents, objects and environment actions are
     *advanced*/
    public void updateEnvironment(float time) {
        float timeSoFar = 0.0f;
        
        //add on the time less than one time step that we didn't complete last time
        time += timeLeftOver;
        timeLeftOver = 0;
        while (timeSoFar < time) {
            timeSoFar += timeStep;
            for (int i = 0; i < agents.size(); i ++) {
                Agent current = (Agent)agents.getAgent(i);
                current.perceive();
                Action action = current.deliberate();
                current.act(action);
            }
        
            for (int i = 0; i < envActions.size(); i++) {
                EnvironmentAction current = (EnvironmentAction)envActions.getAction(i);
                current.act(timeStep, state);
            }
            
            state.advance(timeStep);
            //state.outputState();
        }
    }
    
    public boolean getTurnBased() {
        return false;
    }
}
      

