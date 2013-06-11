import java.util.Random;

public class Try
{
	public static void main(String[] args)
	{
		Random randgenerator = new Random();
		for(int i=0;i<10;i++)
		{
			System.out.println("number is "+ randgenerator.nextInt(4) + "");
		}
	}
}