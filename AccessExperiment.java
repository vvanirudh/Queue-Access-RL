import java.lang.Integer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.rlcommunity.rlglue.codec.RLGlue;


public class AccessExperiment
{
	public void offlineDemo()
	{
		
		RLGlue.RL_episode(1000000);
		//double thisReturn = RLGlue.RL_return();
		//System.out.println(thisReturn);	
	}

	public void evaluateAgent()
	{
		RLGlue.RL_agent_message("freeze learning");
		RLGlue.RL_episode(1000000);
		double thisReturn = RLGlue.RL_return();
		System.out.println("The total Return is "+ thisReturn);
		RLGlue.RL_agent_message("unfreeze learning");
	}

	public void runExperiment()
	{
		RLGlue.RL_init();
		offlineDemo();
		evaluateAgent();
		RLGlue.RL_agent_message("print-state");
		RLGlue.RL_agent_message("print-value-function");
		RLGlue.RL_agent_message("print-average-reward");
		RLGlue.RL_cleanup();
	}

	public static void main(String[] args) {
        AccessExperiment theExperiment = new AccessExperiment();
        theExperiment.runExperiment();
    }
}