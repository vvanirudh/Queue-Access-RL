import java.lang.Math;
import java.util.Vector;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.rlcommunity.rlglue.codec.AgentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.util.AgentLoader;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;


public class AccessAgent implements AgentInterface
{
	private Random randGenerator = new Random();
	private Action lastaction;
	private Observation lastobservation;
	private double[][] valuefunction = null;
	private double alpha_stepsize = 0.1;
	private double beta_stepsize = 0.01;
	private double epsilon = 0.1;

	private int numstates = 0;
	private int numactions = 0;

	private double average_reward = 0.0;


	public void agent_init(String taskspec)
	{
		TaskSpec spec = new TaskSpec(taskspec);


		assert (spec.getNumDiscreteObsDims() == 1);
		assert (spec.getNumContinuousObsDims() == 0);
		assert (!spec.getDiscreteObservationRange(0).hasSpecialMinStatus());
        assert (!spec.getDiscreteObservationRange(0).hasSpecialMaxStatus());

        numstates = spec.getDiscreteObservationRange(0).getMax() + 1;

        assert (spec.getNumDiscreteActionDims() == 1);
        assert (spec.getNumContinuousActionDims() == 0);
        assert (!spec.getDiscreteActionRange(0).hasSpecialMinStatus());
        assert (!spec.getDiscreteActionRange(0).hasSpecialMaxStatus());

        numactions = spec.getDiscreteActionRange(0).getMax() + 1;

        valuefunction = new double[numactions][numstates];
	}

	public Action agent_start(Observation observation)
	{
		int newactionint = egreedy(observation.getInt(0));

		Action returnaction = new Action(1,0,0);
		returnaction.intArray[0] = newactionint;

		lastaction = returnaction.duplicate();
		lastobservation = observation.duplicate();
	}

	public Action agent_step(double reward, Observation observation)
	{
		int newstateint = observation.getInt(0);
		int laststateint = lastobservation.getInt(0);	
		int lastactionint = lastaction.getInt(0);

		int newactionint = egreedy(newstateint);

		int maxactionint;
		if(valuefunction[0][newstateint]>valuefunction[1][newstateint]) maxactionint = 0;
		else maxactionint = 1;

		double Q_sa = valuefunction[lastactionint][laststateint];
		double Q_sprime_aprime = valuefunction[maxactionint][newstateint];


		double new_Q_sa = Q_sa + alpha_stepsize*(reward - average_reward + Q_sprime_aprime - Q_sa);
		valuefunction[lastactionint][laststateint]= new_Q_sa;

		if(new_Q_sa > valuefunction[1-lastactionint][laststateint]) average_reward = average_reward + beta_stepsize * (reward - average_reward + Q_sprime_aprime - new_Q_sa);

		Action returnAction = new Action();
        returnAction.intArray = new int[]{newactionint};

        lastaction = returnAction.duplicate();
        lastobservation = observation.duplicate();

        return returnAction;
	}

	public void agent_end(double reward) {
        int lastStateInt = lastobservation.getInt(0);
        int lastActionInt = lastaction.getInt(0);

        double Q_sa = valuefunction[lastActionInt][lastStateInt];
        double new_Q_sa = Q_sa + alpha_stepsize * (reward - Q_sa + average_reward);

        /*	Only update the value function if the policy is not frozen */
        
        valuefunction[lastActionInt][lastStateInt] = new_Q_sa;
        
        if(new_Q_sa > valuefunction[1 - lastActionInt][lastStateInt]) average_reward = average_reward + beta_stepsize * (reward - average_reward - new_Q_sa);


        lastobservation = null;
        lastaction = null;
    }

    public void agent_cleanup() {
        lastaction = null;
        lastobservation = null;
        valuefunction = null;
    }

    private int egreedy(int state)
    {

            if (randGenerator.nextDouble() <= epsilon) {
                return randGenerator.nextInt(numactions);
            }


            /*otherwise choose the greedy action*/
            int maxIndex = 0;
            if(valuefunction[1][state] > valuefunction[0][state]) maxIndex = 1;
            return maxIndex;
    }

    public static void main(String[] args) {
        AgentLoader theLoader = new AgentLoader(new AccessAgent());
        theLoader.run();
    }


}