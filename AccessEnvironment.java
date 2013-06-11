import java.util.Random;
import java.util.Vector;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;
import org.rlcommunity.rlglue.codec.types.Reward_observation_terminal;
import org.rlcommunity.rlglue.codec.util.EnvironmentLoader;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpecVRLGLUE3;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.taskspec.ranges.IntRange;
import org.rlcommunity.rlglue.codec.taskspec.ranges.DoubleRange;


public class AccessEnvironment implements EnvironmentInterface {

	int startstate;

	SetupWorldDescription theWorld;

	public String env_init()
	{
		theWorld = new SetupWorldDescription();

		TaskSpecVRLGLUE3 theTaskSpecObject = new TaskSpecVRLGLUE3();
        //theTaskSpecObject.setEpisodic();
        //theTaskSpecObject.setDiscountFactor(1.0d);
        //Specify that there will be an integer observation [0,107] for the state
        theTaskSpecObject.addDiscreteObservation(new IntRange(0, 14));
        //Specify that there will be an integer action [0,3]
        theTaskSpecObject.addDiscreteAction(new IntRange(0, 1));
        //Specify the reward range [-100,10]
        theTaskSpecObject.setRewardRange(new DoubleRange(1, 4));

        String taskSpecString = theTaskSpecObject.toTaskSpec();
		TaskSpec.checkTaskSpec(taskSpecString);
		return taskSpecString;
	}

	public Observation env_start()
	{
		Observation theObservation = new Observation(1, 0, 0);
        //theObservation.setInt(0, 1);
        //System.out.println("Initial state is " + theWorld.getState()+"");
        theObservation.setInt(0, theWorld.getState());
        return theObservation;
	}

	public Reward_observation_terminal env_step(Action thisAction)
	{
		theWorld.updateState(thisAction.getInt(0));
        Observation theObservation = new Observation(1, 0, 0);
        theObservation.setInt(0, theWorld.getState());
        Reward_observation_terminal RewardObs = new Reward_observation_terminal();
        RewardObs.setObservation(theObservation);
        RewardObs.setTerminal(theWorld.isTerminal());
        RewardObs.setReward(theWorld.getReward());
        return RewardObs;
	}

	public void env_cleanup(){}

	public String env_message(String message) { return "None";}

    public static void main(String[] args) {
        EnvironmentLoader theLoader = new EnvironmentLoader(new SampleMinesEnvironment());
        theLoader.run();
    }
}


class SetupWorldDescription
{
	private Random randGenerator = new Random();

	private int numOfServers = 4;
	private int numOfPriorities = 3;

	private int lastAction;
	private int lastPriority;
	private int currentPriority;
	private int numOfFreeServers;

	private double probOfFree = 0.1;
	private double proportionOfHigh = 0.5;

	//private Vector<boolean> servers;



	public SetupWorldDescription()
	{

		currentPriority = randGenerator.nextInt(numOfPriorities);
		numOfFreeServers = 4;

		

		this.lastPriority = currentPriority;
	}

	public boolean isTerminal()
	{
		return false;
	}

	public int getState()
	{
		int state = (numOfServers+1)*currentPriority + numOfFreeServers;
		return state;
	}

	public int getNumStates()
	{
		return (numOfServers+1)*numOfPriorities;
	}

	public int getReward()
	{
		int reward = 0;
		if(lastAction==1)
		{
			if(lastPriority==0) reward = 1;
			else if(lastPriority==1) reward = 2;
			else if(lastPriority==2) reward = 4;
		}
		
		return reward;
	}

	public void updateState(int action)
	{
		lastAction = action;
		lastPriority = currentPriority;

		int numOfBusyServers = numOfServers - numOfFreeServers;
		for(int i=0; i<numOfBusyServers; i++)
		{
			double pick = randGenerator.nextDouble();
			if(pick<probOfFree)
			{
				if(numOfFreeServers!=numOfServers) numOfFreeServers= numOfFreeServers+1;
			}
		}

		if(randGenerator.nextDouble()<proportionOfHigh) currentPriority = 2;
		else currentPriority = randGenerator.nextInt(2);

		if(action==1) if(numOfFreeServers!=0) numOfFreeServers = numOfFreeServers-1;
		
	}

}

