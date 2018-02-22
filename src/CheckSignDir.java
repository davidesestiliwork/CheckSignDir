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

import it.dsestili.jhashcode.core.DirectoryInfo;
import it.dsestili.jhashcode.core.DirectoryScanner;
import it.dsestili.jhashcode.core.DirectoryScannerNotRecursive;
import it.dsestili.jhashcode.core.DirectoryScannerRecursive;
import it.dsestili.jhashcode.core.IScanProgressListener;
import it.dsestili.jhashcode.core.ProgressEvent;

public class CheckSignDir 
{
	private static int okCount = 0, badCount = 0;
	private static String modeParam;
	private static boolean recursive;
	
	public static void main(String[] args) 
	{
		if(args.length == 2)
		{	
			try
			{
				modeParam = args[1];
				checkDir(args[0]);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		else
		{
			System.out.println("Usage: param 1: directory to check, param2: modeParam (not-recursive, recursive, no-subfolders)");
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
		
		File[] files = getFiles(f);
		if(files == null)
		{
			return;
		}

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
	
	private static File[] getFiles(File directory) throws Throwable
	{
		File[] result = null;
		DirectoryScanner scanner = null;
		
		if(modeParam != null && modeParam.trim().equals("not-recursive"))
		{
			recursive = true;
			scanner = new DirectoryScannerNotRecursive(directory, recursive);
		}
		else if(modeParam != null && modeParam.trim().equals("recursive"))
		{
			recursive = true;
			scanner = new DirectoryScannerRecursive(directory, recursive);
		}
		else if(modeParam != null && modeParam.trim().equals("no-subfolders"))
		{
			recursive = false;
			scanner = new DirectoryScannerNotRecursive(directory, recursive);
		}
		else
		{
			System.out.println("Mode error");
			return result;
		}
		
		scanner.addIScanProgressListener(new IScanProgressListener() {
			public void scanProgressEvent(ProgressEvent event)
			{
				System.out.println(event);
			}
		});
		
		DirectoryInfo di = scanner.getFiles();
		result = di.getFiles();
		long totalSize = di.getTotalSize();
		
		System.out.println("Scanning completed, " + result.length + " files found, " + totalSize + " bytes total size");
		
		if(di.getSymbolicLinksExcluded() > 0 || di.getHiddenFilesExcluded() > 0)
		{
			System.out.println(di.getSymbolicLinksExcluded() + " symlink excluded, " + di.getHiddenFilesExcluded() + " hidden files excluded");
		}
		
		return result;
	}
}
