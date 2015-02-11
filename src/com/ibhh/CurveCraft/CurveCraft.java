package com.ibhh.CurveCraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ibhh.CurveCraft.Listeners.PlayerListener;
import com.ibhh.CurveCraft.Permissions.PermissionsUtility;
import com.ibhh.CurveCraft.Report.ReportToHost;
import com.ibhh.CurveCraft.arena.AlreadyVotedException;
import com.ibhh.CurveCraft.arena.ArenaHandler;
import com.ibhh.CurveCraft.arena.CCArena;
import com.ibhh.CurveCraft.arena.DisableException;
import com.ibhh.CurveCraft.arena.EnableExeption;
import com.ibhh.CurveCraft.arena.LobbyJoinException;
import com.ibhh.CurveCraft.arena.NotInLobbyorGameException;
import com.ibhh.CurveCraft.arena.StartGameException;
import com.ibhh.CurveCraft.arena.VotingNotEnabledException;
import com.ibhh.CurveCraft.config.ConfigurationHandler;
import com.ibhh.CurveCraft.createArena.ArenaCreationProzess;
import com.ibhh.CurveCraft.createArena.NotFinishedYetException;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import com.ibhh.CurveCraft.metrics.MetricsHandler;
import com.ibhh.CurveCraft.update.Update;
import com.ibhh.CurveCraft.update.Utilities;

public class CurveCraft extends JavaPlugin
{

	private LoggerUtility logger;
	private ConfigurationHandler config;
	private ReportToHost report;
	private PermissionsUtility permissions;
	private Privacy privacy;
	private Update update;
	private Utilities pluginmanager;
	private PlayerListener listener;
	private IConomyHandler iConomyHandler;
	private MetricsHandler metricsHandler;
	private Help help;
	private final String[] commands = {"help", "changelanguage", "languagelist", "version", "denytracking", "allowtracking", "list", "create", "setname", "setcorner1", "setcorner2", "join", "forcestart", "start", "setlobby", "setend", "lobby", "exit", "resetarena", "deletearena", "changecorner1", "changecorner2", "enable", "disable"};

	private final HashMap<String, ArenaCreationProzess> arena = new HashMap<String, ArenaCreationProzess>();
	private ArenaHandler arenaHandler;
	private static boolean a = false;

	public String[] getCommands()
	{
		return this.commands;
	}

	public File getPluginFile()
	{
		return getFile();
	}

	public ReportToHost getReportHandler()
	{
		if(this.report == null)
		{
			this.report = new ReportToHost(this);
		}
		return this.report;
	}

	public MetricsHandler getMetricsHandler()
	{
		if(this.metricsHandler == null)
		{
			this.metricsHandler = new MetricsHandler(this);
		}
		return this.metricsHandler;
	}

	public Help getHelp()
	{
		if(this.help == null)
		{
			this.help = new Help(this);
		}
		return this.help;
	}

	public IConomyHandler getiConomyHandler()
	{
		if(this.iConomyHandler == null)
		{
			this.iConomyHandler = new IConomyHandler(this);
		}
		return this.iConomyHandler;
	}

	public ArenaHandler getArenaHandler()
	{
		if(this.arenaHandler == null)
		{
			this.arenaHandler = new ArenaHandler(this);
		}
		return this.arenaHandler;
	}

	public PlayerListener getListener()
	{
		if(this.listener == null)
		{
			this.listener = new PlayerListener(this);
		}
		return this.listener;
	}

	public Utilities getPluginManager()
	{
		if(this.pluginmanager == null)
		{
			this.pluginmanager = new Utilities(this);
		}
		return this.pluginmanager;
	}

	public Privacy getPrivacy()
	{
		if(this.privacy == null)
		{
			this.privacy = new Privacy(this);
		}
		return this.privacy;
	}

	public Update getUpdate()
	{
		if(this.update == null)
		{
			this.update = new Update(this);
		}
		return this.update;
	}

	public PermissionsUtility getPermissions()
	{
		if(this.permissions == null)
		{
			this.permissions = new PermissionsUtility(this);
		}
		return this.permissions;
	}

	public ConfigurationHandler getConfigHandler()
	{
		if(this.config == null)
		{
			this.config = new ConfigurationHandler(this);
		}
		return this.config;
	}

	public LoggerUtility getLoggerUtility()
	{
		if(this.logger == null)
		{
			this.logger = new LoggerUtility(this);
		}
		return this.logger;
	}

	public boolean isStarted()
	{
		return isEnabled();
	}

	@Override
	public void onDisable()
	{
		setEnabled(false);
		getPrivacy().savePrivacyFiles();
		long time = System.nanoTime();
		System.out.println(new StringBuilder().append("CurveCraft disabled in ").append((System.nanoTime() - time) / 1000000L).append(" ms").toString());
	}

	public static boolean toggletest()
	{
		a = !a;
		return a;
	}

	public static boolean test()
	{
		return a;
	}

	@Override
	public void onEnable()
	{
		long time = System.nanoTime();
		getConfigHandler();
		getLoggerUtility();
		getLoggerUtility().log("creating config!", LoggerUtility.Level.DEBUG);
		getLoggerUtility().log("init logger!", LoggerUtility.Level.DEBUG);
		getReportHandler();
		getLoggerUtility().log("init report!", LoggerUtility.Level.DEBUG);
		getPermissions();
		getLoggerUtility().log("init permissions!", LoggerUtility.Level.DEBUG);
		getLoggerUtility().log("init database!", LoggerUtility.Level.DEBUG);
		getPrivacy().loadData();
		getPrivacy().autoSave();
		try
		{
			getMetricsHandler().start();
		}
		catch(IOException ex)
		{
			Logger.getLogger(CurveCraft.class.getName()).log(Level.SEVERE, null, ex);
			getLoggerUtility().log("Cannot init metrics config", LoggerUtility.Level.ERROR);
		}
		getLoggerUtility().log("init privacy control!", LoggerUtility.Level.DEBUG);
		getUpdate().startUpdateTimer();
		getLoggerUtility().log("init update control!", LoggerUtility.Level.DEBUG);
		getArenaHandler().loadArenas();
		getLoggerUtility().log("arenas loaded!", LoggerUtility.Level.DEBUG);
		getListener();
		getLoggerUtility().log("init listeners!", LoggerUtility.Level.DEBUG);
		if(getConfigHandler().getConfig().getBoolean("senderrorreport"))
		{
			getLoggerUtility().log("This plugin collects error reports and send them to the developer.", LoggerUtility.Level.INFO);
			getLoggerUtility().log("To change this, change the value \"senderrorreport\" to false in the config.", LoggerUtility.Level.INFO);
			if(getConfigHandler().getConfig().getBoolean("senddebugfile"))
			{
				getLoggerUtility().log("This plugin collects debugfiles and send them to the developer.", LoggerUtility.Level.INFO);
				getLoggerUtility().log("To change this, change the value \"senddebugfile\" to false in the config.", LoggerUtility.Level.INFO);
			}
		}
		getLoggerUtility().log(new StringBuilder().append("CurveCraft enabled in ").append((System.nanoTime() - time) / 1000000L).append(" ms").toString(), LoggerUtility.Level.INFO);
		setEnabled(true);
	}

	public float getVersion()
	{
		try
		{
			return Float.parseFloat(getDescription().getVersion());
		}
		catch(NumberFormatException e)
		{
			getLoggerUtility().log("Could not parse version in float", LoggerUtility.Level.INFO);
			getLoggerUtility().log(new StringBuilder().append("Error getting version of ").append(getName()).append("! Message: ").append(e.getMessage()).toString(), LoggerUtility.Level.ERROR);
			this.report.report(3310, new StringBuilder().append("Error getting version of ").append(getName()).append("!").toString(), e.getMessage(), "CurveCraft", e);
			getLoggerUtility().log("Uncatched Exeption!", LoggerUtility.Level.ERROR);
		}
		return 0.0F;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!isEnabled())
		{
			getLoggerUtility().log("CurveCraft plugin is NOT enabled!", LoggerUtility.Level.ERROR);
			return true;
		}
		if((sender instanceof Player))
		{
			Player player = (Player) sender;

			if(command.getName().equalsIgnoreCase("cc"))
			{
				if(args.length == 1)
				{
					/* Language List Command languagelist */

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.languagelist.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.languagelist.name")))
					{
						return executeCommandLanguageList(player, args);
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.list.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.list.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.list.permission")))
						{
							getLoggerUtility().log(player, "Lobbys: ", LoggerUtility.Level.INFO);
							ArrayList<CCArena> ar = arenaHandler.getArena();
							for(CCArena next : ar)
							{
								getLoggerUtility().log(player, next.getName() + ": " + (next.isGameisrunning() ? "Running" : next.getLobby().size() + "/" + next.getMaxplayers()), LoggerUtility.Level.INFO);
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.cancel.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.cancel.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.cancel.permission")))
						{
							if(!this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
								return true;
							}
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.cancel.message"), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.exit.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.exit.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.exit.permission")))
						{
							try
							{
								CCArena aren = getArenaHandler().getArenaOfPlayer(player);
								if(aren == null)
								{
									getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "lobby.exit.noarena"), LoggerUtility.Level.ERROR);
								}
								else
								{
									aren.removePlayer(this, player);
								}
							}
							catch(NotInLobbyorGameException ex)
							{
								getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.start.name")) || args[0].equalsIgnoreCase("vote") || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.start.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.start.permission")))
						{
							try
							{
								CCArena are = getArenaHandler().getArenaOfPlayer(player);
								if(are == null)
								{
									getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "lobby.exit.noarena"), LoggerUtility.Level.ERROR);
								}
								else
								{
									are.voteForStart(this, player);
								}
							}
							catch(NotInLobbyorGameException | AlreadyVotedException | VotingNotEnabledException ex)
							{
								getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.lobby.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.lobby.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.lobby.permission")))
						{
							CCArena ar = getArenaHandler().getArenaOfPlayer(player);
							if(ar == null)
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "lobby.exit.noarena"), LoggerUtility.Level.ERROR);
							}
							else
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "lobby.players"), LoggerUtility.Level.INFO);
								StringBuilder st = new StringBuilder();
								for(int i = 0; i < ar.getLobby().size(); i++)
								{
									st.append(((Player) ar.getLobby().get(i)).getName());
									if(i + 1 < ar.getLobby().size())
									{
										st.append(", ");
									}
								}
								getLoggerUtility().log(player, st.toString(), LoggerUtility.Level.INFO);
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.create.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.create.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.create.permission")))
						{
							if(this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.already"), LoggerUtility.Level.INFO);
								return true;
							}
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.start"), LoggerUtility.Level.INFO);
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.start2"), LoggerUtility.Level.INFO);

							this.arena.put(player.getName(), new ArenaCreationProzess(this));
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.finish.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.finish.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.finish.permission")))
						{
							if(!this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
								return true;
							}
							if(this.arenaHandler.getArenaByName(((ArenaCreationProzess) this.arena.get(player.getName())).getName()) != null)
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.nameexist"), LoggerUtility.Level.ERROR);
								return true;
							}
							try
							{
								((ArenaCreationProzess) this.arena.get(player.getName())).finishProzess();
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.finished"), LoggerUtility.Level.INFO);
							}
							catch(NotFinishedYetException ex)
							{
								getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
								return true;
							}
							this.arena.remove(player.getName());
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.setcorner1.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.setcorner1.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.setcorner1.permission")))
						{
							if(!this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
								return true;
							}
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.corner"), LoggerUtility.Level.INFO);
							Location l = player.getLocation().add(0.0D, -1.0D, 0.0D);
							((ArenaCreationProzess) this.arena.get(player.getName())).setCorner1(l);
							getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.setlobby.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.setlobby.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.setlobby.permission")))
						{
							if(!this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
								return true;
							}
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.lobby"), LoggerUtility.Level.INFO);
							Location l = player.getLocation();
							((ArenaCreationProzess) this.arena.get(player.getName())).setLobbyloc(l);
							getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.setexit.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.setexit.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.setexit.permission")))
						{
							if(!this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
								return true;
							}
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.exit"), LoggerUtility.Level.INFO);
							Location l = player.getLocation();
							((ArenaCreationProzess) this.arena.get(player.getName())).setExitloc(l);
							getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.setend.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.setend.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.setend.permission")))
						{
							if(!this.arena.containsKey(player.getName()))
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
								return true;
							}
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.end"), LoggerUtility.Level.INFO);
							Location l = player.getLocation();
							((ArenaCreationProzess) this.arena.get(player.getName())).setEndloc(l);
							getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.setcorner2.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.setcorner2.name")))
					{
						if(!this.arena.containsKey(player.getName()))
						{
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.noarena"), LoggerUtility.Level.INFO);
							return true;
						}
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.setcorner2.permission")))
						{
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.corner"), LoggerUtility.Level.INFO);
							Location l = player.getLocation().add(0.0D, -1.0D, 0.0D);
							((ArenaCreationProzess) this.arena.get(player.getName())).setCorner2(l);
							getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if((args[0].equalsIgnoreCase("info")) || (args[0].equalsIgnoreCase("version")))
					{
						player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("[CurveCraft]").append(ChatColor.DARK_AQUA).append(" CurveCraft Status:").append(ChatColor.GREEN).append("Working!").toString());
						player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("[CurveCraft]").append(ChatColor.DARK_AQUA).append(" CurveCraft Version:").append(getVersion()).toString());
						player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("[CurveCraft]").append(ChatColor.DARK_AQUA).append(" Further information: http://dev.bukkit.org/server-mods/curvefever").toString());
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.denytracking.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.denytracking.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.denytracking.permission")))
						{
							if(getPrivacy().getConfig().containsKey(player.getName()))
							{
								getPrivacy().getConfig().remove(player.getName());
							}
							getPrivacy().getConfig().put(player.getName(), Boolean.FALSE);
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "privacy.notification.denied"), LoggerUtility.Level.INFO);
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.allowtracking.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.allowtracking.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.allowtracking.permission")))
						{
							if(getPrivacy().getConfig().containsKey(player.getName()))
							{
								getPrivacy().getConfig().remove(player.getName());
							}
							getPrivacy().getConfig().put(player.getName(), Boolean.TRUE);
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "privacy.notification.allowed"), LoggerUtility.Level.INFO);
						}
						return true;
					}
					getHelp().help(player, args);
					return true;
				}
				if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.changelanguage.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.changelanguage.name")))
					{
						return executeCommandChangeLanguage(player, args);
					}
					if(args[0].equalsIgnoreCase("checkarena"))
					{
						boolean ina = getArenaHandler().getArenaByName(args[1]).isInArena(player.getLocation());
						getLoggerUtility().log(player, "in Arena " + args[1] + ": " + ina, LoggerUtility.Level.INFO);
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.changecorner1.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.changecorner1.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.changecorner1.permission")))
						{
							CCArena a = arenaHandler.getArenaByName(args[1]);
							if(a != null)
							{
								a.setCorner1(player.getLocation().add(0.0D, -1.0D, 0.0D));
								a.saveToFolder(this);
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "arena.change"), LoggerUtility.Level.INFO);
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.changecorner2.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.changecorner2.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.changecorner2.permission")))
						{
							CCArena a = arenaHandler.getArenaByName(args[1]);
							if(a != null)
							{
								a.setCorner1(player.getLocation().add(0.0D, -1.0D, 0.0D));
								a.saveToFolder(this);
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "arena.change"), LoggerUtility.Level.INFO);
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.enable.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.enable.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.enable.permission")))
						{
							CCArena a = arenaHandler.getArenaByName(args[1]);
							if(a != null)
							{
								try
								{
									a.enable();
									a.saveToFolder(this);
									getLoggerUtility().log(player, "Status changed to enabled!", LoggerUtility.Level.INFO);
								}
								catch(EnableExeption ex)
								{
									getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
								}
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.disable.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.disable.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.disable.permission")))
						{
							CCArena a = arenaHandler.getArenaByName(args[1]);
							if(a != null)
							{
								try
								{
									a.disable();
									a.saveToFolder(this);
									getLoggerUtility().log(player, "Status changed to disabled!", LoggerUtility.Level.INFO);
								}
								catch(DisableException ex)
								{
									getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
								}
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.deletearena.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.deletearena.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.deletearena.permission")))
						{
							CCArena ar = arenaHandler.getArenaByName(args[1]);
							if(ar != null)
							{
								arenaHandler.delete(ar);
								getLoggerUtility().log(player, "Delete complete! Arena permanently deleted!", LoggerUtility.Level.INFO);
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.resetarena.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.resetarena.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.resetarena.permission")))
						{
							CCArena a = arenaHandler.getArenaByName(args[1]);
							if(a != null)
							{
								arenaHandler.reset(a);
								getLoggerUtility().log(player, "Reset complete!", LoggerUtility.Level.INFO);
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.setname.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.setname.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.setname.permission")))
						{
							getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "create.notfinished.name"), LoggerUtility.Level.INFO);
							((ArenaCreationProzess) this.arena.get(player.getName())).setName(args[1]);
						}
						return true;
					}

					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.forcestart.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.forcestart.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.forcestart.permission")))
						{
							CCArena are = getArenaHandler().getArenaByName(args[1]);
							if(are != null)
							{
								try
								{
									are.forcestart(this);
									getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "start.forcestart.success"), LoggerUtility.Level.INFO);
								}
								catch(StartGameException ex)
								{
									getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
								}
							}
							else
							{
								getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), args[1]), LoggerUtility.Level.ERROR);
							}
						}
						return true;
					}
					if(args[0].equalsIgnoreCase(getConfigHandler().getLanguageString(player, "commands.join.name")) || args[0].equalsIgnoreCase(getConfigHandler().getLanguageString("system", "commands.join.name")))
					{
						if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.join.permission")))
						{
							if(getArenaHandler().getArenaOfPlayer(player) != null)
							{
								getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "lobby.join.already"), LoggerUtility.Level.ERROR);
							}
							else
							{
								CCArena a = getArenaHandler().getArenaByName(args[1]);
								if(a == null)
								{
									getLoggerUtility().log(player, String.format(getConfigHandler().getLanguageString(player, "lobby.join.noarena"), new Object[] {args[1]}), LoggerUtility.Level.ERROR);
								}
								else if(((getConfigHandler().getConfig().getBoolean("everyArenaOwnJoinPermission")) && (getPermissions().checkpermissions(player, new StringBuilder().append("CurveFerver.join.").append(args[1]).toString()))) || (!getConfigHandler().getConfig().getBoolean("everyArenaOwnJoinPermission")))
								{
									try
									{
										a.addPlayerToLobby(this, player);
									}
									catch(LobbyJoinException ex)
									{
										getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
									}
								}
							}
						}

						return true;
					}

					getHelp().help(player, args);
					return true;
				}
				getHelp().help(player, args);
				return true;
			}
		}
		return false;
	}

	/**
	 * Change the language of a player
	 * @param player
	 * @param args
	 * @return
	 */
	private boolean executeCommandChangeLanguage(Player player, String... args)
	{
		if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.changelanguage.permission", false)))
		{
			try
			{
				getConfigHandler().setPlayerLanguage(player, args[1]);
			}
			catch(IllegalArgumentException e)
			{
				getLoggerUtility().log(player, e.getMessage(), LoggerUtility.Level.ERROR);
				return true;
			}
			catch(IOException e)
			{
				e.printStackTrace();
				getLoggerUtility().log(player, e.getMessage(), LoggerUtility.Level.ERROR);
				return true;
			}
			getLoggerUtility().log(player, getConfigHandler().getLanguageString(player, "commands.changelanguage.return"), LoggerUtility.Level.INFO);
		}
		return true;
	}

	private boolean executeCommandLanguageList(Player player, String... args)
	{
		if(getPermissions().checkpermissions(player, getConfigHandler().getLanguageString(player, "commands.languagelist.permission", false)))
		{
			getLoggerUtility().log(player, ChatColor.GREEN + getConfigHandler().getLanguageString(player, "configuration.language.list"), LoggerUtility.Level.INFO);
			getLoggerUtility().log(player, ChatColor.GREEN + getConfigHandler().getLanguageString(player, "commands.changelanguage.usage"), LoggerUtility.Level.INFO);

			for(com.ibhh.CurveCraft.locales.PluginLocale file : getConfigHandler().getLanguage_configs().values())
			{
				getLoggerUtility().log(player, file.getLocaleName() + " (" + file.getCode() + ")", LoggerUtility.Level.INFO);
			}
		}
		return true;
	}
}
