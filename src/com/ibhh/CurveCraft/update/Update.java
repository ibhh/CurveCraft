package com.ibhh.CurveCraft.update;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import com.ibhh.CurveCraft.update.Updater.UpdateResult;
import com.ibhh.CurveCraft.update.Updater.UpdateType;

public class Update
{

	/**
     *
     */
	public static final long serialVersionUID = 1L;
	transient int i; // transient: not stored
	private final CurveCraft plugin;

	public Update(CurveCraft up)
	{
		plugin = up;
	}

	public void startUpdateTimer()
	{
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if(plugin.getConfigHandler().getConfig().getBoolean("updateCheck"))
				{
					try
					{
						plugin.getLoggerUtility().log("Searching update for CurveCraft!", LoggerUtility.Level.DEBUG);
						Updater updater = new Updater(plugin, 81737, plugin.getPluginFile(), UpdateType.NO_DOWNLOAD, true);
						if(updater.getResult() == UpdateResult.UPDATE_AVAILABLE)
						{
							plugin.getLoggerUtility().log("New version: " + updater.getLatestName() + " found!", LoggerUtility.Level.WARNING);
							plugin.getLoggerUtility().log("******************************************", LoggerUtility.Level.WARNING);
							plugin.getLoggerUtility().log("*********** Please update!!!! ************", LoggerUtility.Level.WARNING);
							plugin.getLoggerUtility().log("http://dev.bukkit.org/server-mods/CurveCraft", LoggerUtility.Level.WARNING);
							plugin.getLoggerUtility().log("******************************************", LoggerUtility.Level.WARNING);
						}
						else
						{
							plugin.getLoggerUtility().log("No update found!", LoggerUtility.Level.DEBUG);
						}
					}
					catch(Exception e)
					{
						plugin.getLoggerUtility().log("Error on doing update check! Message: " + e.getMessage(), LoggerUtility.Level.ERROR);
						plugin.getLoggerUtility().log("may the mainserver is down!", LoggerUtility.Level.ERROR);
						// plugin.getReportHandler().report(335, "Checking for update failed", e.getMessage(), "CurveCraft", e);
					}
				}
			}
		}, 400L, 50000L);
	}

}
