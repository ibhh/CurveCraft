package com.ibhh.CurveCraft.commandwhitelist;

import com.ibhh.CurveCraft.CurveCraft;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author ibhh
 */
public class GlobalCommandWhiteList
{

	private final ArrayList<String> whitelistarray = new ArrayList<>();

	public GlobalCommandWhiteList(final CurveCraft plugin) throws IOException
	{

		File file = new File(plugin.getDataFolder() + File.separator + "globalcommandwhitelist.txt");

		if(!file.exists())
		{
			file.createNewFile();
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true))))
			{
				out.println("#Example (only the command without /): tell");
				out.println("cc");
				out.println("tell");
				out.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			for(String line; (line = br.readLine()) != null;)
			{
				line = line.trim();
				if(!line.startsWith("#"))
				{
					whitelistarray.add(line);
				}
			}
		}

	}

	public boolean allowed(String s)
	{
		return whitelistarray.contains(s);
	}

}
