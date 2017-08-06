import java.io.File;

public class CheckSignDir 
{
	private static int okCount = 0, badCount = 0;
	
	public static void main(String[] args) 
	{
		if(args.length == 1)
		{
			try
			{
				checkDir(args[0]);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		else
		{
			System.out.println("Usage: param 1: directory to check");
		}
	}
	
	private static void checkDir(String dir) throws Throwable
	{
		File f = new File(dir);
		
		if(!f.exists())
		{
			System.out.println("Does not exist");
			return;
		}
		
		if(f.isFile())
		{
			System.out.println("Is a file");
			return;
		}
		
		File[] files = f.listFiles();

		for(File file : files)
		{
			if(file.getName().toLowerCase().endsWith(".asc"))
			{
				checkSign(file);
			}
		}
		
		System.out.println("OK: " + okCount + " - BAD: " + badCount);
	}
	
	private static void checkSign(File ascFile) throws Throwable
	{
		System.out.print(ascFile.getName());
		ProcessBuilder builder = new ProcessBuilder("gpg", "--verify", ascFile.getAbsolutePath());
		Process process = builder.start();
		int exitCode = process.waitFor();
		if(exitCode == 0)
		{
			System.out.println(" Good signature");
			okCount++;
		}
		else
		{
			System.out.println(" BAD Signature");
			badCount++;
		}
	}
}
