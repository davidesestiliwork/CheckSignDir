/*
CheckSignDir a digital signature checker for gpg
Copyright (C) 2017-2018 Davide Sestili

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
			if(file.getName().toLowerCase().endsWith(".asc") || file.getName().toLowerCase().endsWith(".gpg"))
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
