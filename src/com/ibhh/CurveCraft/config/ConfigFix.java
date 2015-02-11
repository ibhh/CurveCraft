package com.ibhh.CurveCraft.config;

import com.ibhh.CurveCraft.CurveCraft;

public class ConfigFix
{
	public static void fixConfig(CurveCraft plugin)
	{
		if(plugin.getConfig().getString("language").equalsIgnoreCase("en"))
		{
			plugin.getConfig().set("language", "en_CA");
		}
		if(plugin.getConfig().getString("language").equalsIgnoreCase("de"))
		{
			plugin.getConfig().set("language", "de_DE");
		}
		plugin.saveConfig();
		plugin.reloadConfig();
	}
}
