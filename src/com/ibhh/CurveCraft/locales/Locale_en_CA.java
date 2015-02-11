package com.ibhh.CurveCraft.locales;

import java.io.File;
import java.io.IOException;

public class Locale_en_CA extends PluginLocale implements PredefinedLocale
{

	public Locale_en_CA(String path)
	{
		super("en_CA", Localizer.getByCode("en_CA").getName());
		createFile(path);
	}

	@Override
	public void createFile(String path)
	{
		File folder = new File(path + File.separator);
		folder.mkdirs();
		File configl = new File(path + File.separator + "language." + getCode() + ".yml");
		if(!configl.exists())
		{
			try
			{
				configl.createNewFile();
			}
			catch(IOException ex)
			{
				System.err.println("Couldnt create new arenaconfig file!");
			}
		}

		super.loadConfiguration(configl);

		addDefault("configuration.language.pathnotfound", "In the configuration file \"language.%s.yml\" is no path \"%s\"! The default language is used: ");
		addDefault("configuration.language.languagenotfound", "The configuration file of the language \"%s\" does not exist!");
		addDefault("configuration.language.list", "Following languages are installed and can be selected with: ");

		addDefault("help.head", "CurveCraft Help ");
		addDefault("help.commandsallowed", "You are allowed to run following commands:");
		
		addDefault("permission.error", "we have a problem! You musnt do this!");

		addDefault("privacy.notification.1", "this plugin saves your interact events to a log");
		addDefault("privacy.notification.2", "\"/cc allowtracking\" to allow the plugin to save your data");
		addDefault("privacy.notification.3", "\"/cc denytracking\" to anonymise your data");

		addDefault("privacy.notification.denied", "The plugin anonymises your data now");
		addDefault("privacy.notification.allowed", "The plugin saves your data now, to delete the data, please tell an admin");

		addDefault("create.notfinished.start", "You must set a name and two corners for the arena! Use /cc setname NAME and /cc setcorner1 /cc setcorner2 to set your position as corner!");
		addDefault("create.notfinished.start2", "And a position for the lobby /cc setlobby and a position for eleminated player /cc setend and an exit position /cc setexit. Finish the process with /cc finish");

		addDefault("create.notfinished.name", "Name set");
		addDefault("create.finished", "Finished!");
		addDefault("create.notfinished.corner", "Corner set");
		addDefault("create.notfinished.lobby", "Lobby position set.");
		addDefault("create.notfinished.end", "End position set.");
		addDefault("create.notfinished.exit", "Exit position set.");
		addDefault("create.notfinished.nameexist", "Arena already exit with this name, choose another one (/cc setname)");
		addDefault("create.notfinished.noname", "You must set a name for the arena! /cc setname NAME");
		addDefault("create.notfinished.nocorner", "You must define two corners of the arena! /cc setcorner1 NAME und /cc setcorner2 NAME");
		addDefault("create.notfinished.wrongY", "Both corners must have the same Y-Coordinate");

		addDefault("create.notfinished.lobbyloc", "You must set a lobby position (/cc setlobby)");
		addDefault("create.notfinished.endloc", "You must set an end position(/cc setend)");
		addDefault("create.notfinished.exitloc", "You must set an exit position(/cc setexit)");

		addDefault("commands.notallowed", "This command isn't allowed in the arena! Use /cc exit");

		addDefault("commands.setexit.name", "setexit");
		addDefault("commands.setexit.permission", "CurveCraft.create");
		addDefault("commands.setexit.description", "Setzt die Position für den Exit Punkt.");
		addDefault("commands.setexit.usage", "/cc setexit");

		addDefault("commands.setlobby.name", "setlobby");
		addDefault("commands.setlobby.permission", "CurveCraft.create");
		addDefault("commands.setlobby.description", "Setzt die Position für die Lobby.");
		addDefault("commands.setlobby.usage", "/cc setlobby");

		addDefault("commands.setend.name", "setend");
		addDefault("commands.setend.permission", "CurveCraft.create");
		addDefault("commands.setend.description", "Setzt die End-Position.");
		addDefault("commands.setend.usage", "/cc setend");
		
		/* changelanguage Command */
		addDefault("commands.changelanguage.name", "changelanguage");
		addDefault("commands.changelanguage.permission", "WoolRun.changelanguage");
		addDefault("commands.changelanguage.description", "changes the language");
		addDefault("commands.changelanguage.usage", "/wr changelanguage [language]");
		addDefault("commands.changelanguage.return", "The language has been changed.");

		/* sprachliste Command */
		addDefault("commands.languagelist.name", "languagelist");
		addDefault("commands.languagelist.permission", "WoolRun.changelanguage");
		addDefault("commands.languagelist.description", "Shows all languages");
		addDefault("commands.languagelist.usage", "/wr languagelist");

		addDefault("create.cancel.message", "You cancelled the creation of the arena!");
		addDefault("create.already", "An arena is in creation /cc cancel to cancel");
		addDefault("create.noarena", "Start a creation prozess first! /cc create");

		addDefault("lobby.join.message1", "You are joining the arena \"%s\".");
		addDefault("lobby.join.message2", "There are %d players in the lobby now.");
		addDefault("lobby.join.message3", "The game can start if there are %d players and all typed /cc start to start the game.");
		addDefault("lobby.join.message4", "The game starts at %d players.");
		addDefault("lobby.join.playerjoin", "%s is joining the lobby.");
		addDefault("lobby.join.gamerunning", "The game is already running. Sorry.");
		addDefault("lobby.join.full", "The lobby is already full. Sorry.");
		addDefault("lobby.join.noarena", "No arena with name \"%s\"");
		addDefault("lobby.join.already", "You already in an arena/game");
		addDefault("lobby.join.disabled", "Arena disabled!");

		addDefault("lobby.exit.message", "You left the lobby.");
		addDefault("lobby.exit.playerexit", "%s left the lobby.");
		addDefault("lobby.exit.noarena", "You are not in a lobby/game");

		addDefault("lobby.players", "This players are in your lobby:");

		addDefault("scoreboard.name", "Points");

		addDefault("start.alreadyvoted", "You have already voted!");
		addDefault("start.voted", "Sucessfully voted!");
		addDefault("start.votingnotenabled", "The game can't be started without the maximum of players.");
		addDefault("start.starting", "Game is starting!");
		addDefault("start.round", "New round is starting!");
		addDefault("start.starting_round", "New round is starting in %d seconds!");
		addDefault("start.starting_game", "Game is starting in %d seconds!");
		addDefault("start.starting0", "Go go go!");
		addDefault("start.forcestart.success", "Executed!");
		addDefault("start.forcestart.fail", "There must be two players in the arena");

		addDefault("round.winner", "The winner of this round is: %s");
		addDefault("game.winner", "The winner of arena %s is: %s");
		addDefault("game.crashed", "You crashed!");
		addDefault("game.crash.wand", "You crashed into a limitation!");
		addDefault("game.crash.plane", "You crashed into plane!");
		addDefault("game.color", "Your colour ist: %s");

		addDefault("arena.change", "Successfully changed. Please do /cc resetarena [ARENA] at the end of your operation.");

		addDefault("commands.lobby.name", "lobby");
		addDefault("commands.lobby.permission", "CurveCraft.play");
		addDefault("commands.lobby.description", "Shows players of lobby");
		addDefault("commands.lobby.usage", "/cc lobby");

		addDefault("commands.exit.name", "exit");
		addDefault("commands.exit.permission", "CurveCraft.play");
		addDefault("commands.exit.description", "Exit lobby/game");
		addDefault("commands.exit.usage", "/cc exit");

		addDefault("game.exit.message", "You left the game.");
		addDefault("game.exit.playerexit", "%s left the game.");

		addDefault("commands.list.name", "list");
		addDefault("commands.list.permission", "CurveCraft.play");
		addDefault("commands.list.description", "Lists all loaded arenas");
		addDefault("commands.list.usage", "/cc list");

		addDefault("commands.join.name", "join");
		addDefault("commands.join.permission", "CurveCraft.play");
		addDefault("commands.join.description", "Joins an arena");
		addDefault("commands.join.usage", "/cc join ARENA");

		addDefault("commands.forcestart.name", "forcestart");
		addDefault("commands.forcestart.permission", "CurveCraft.forcestart");
		addDefault("commands.forcestart.description", "start game earlier.");
		addDefault("commands.forcestart.usage", "/cc forcestart ARENANAME");

		addDefault("commands.start.name", "start");
		addDefault("commands.start.permission", "CurveCraft.play");
		addDefault("commands.start.description", "Vote to start game earlier.");
		addDefault("commands.start.usage", "/cc start");

		addDefault("commands.help.name", "help");
		addDefault("commands.help.permission", "CurveCraft.help");
		addDefault("commands.help.description", "Shows help");
		addDefault("commands.help.usage", "/cc help");

		addDefault("commands.create.name", "create");
		addDefault("commands.create.permission", "CurveCraft.create");
		addDefault("commands.create.description", "Creates a new CF arena");
		addDefault("commands.create.usage", "/cc create");

		addDefault("commands.finish.name", "finish");
		addDefault("commands.finish.permission", "CurveCraft.create");
		addDefault("commands.finish.description", "Finishes the creation prozess");
		addDefault("commands.finish.usage", "/cc finish");

		addDefault("commands.cancel.name", "cancel");
		addDefault("commands.cancel.permission", "CurveCraft.create");
		addDefault("commands.cancel.description", "Cancels a new CF arena");
		addDefault("commands.cancel.usage", "/cc cancel");

		addDefault("commands.setname.name", "setname");
		addDefault("commands.setname.permission", "CurveCraft.create");
		addDefault("commands.setname.description", "Sets a name for a arena while the player creates it.");
		addDefault("commands.setname.usage", "/cc setname");

		addDefault("commands.setcorner1.name", "setcorner1");
		addDefault("commands.setcorner1.permission", "CurveCraft.create");
		addDefault("commands.setcorner1.description", "Sets corner 1.");
		addDefault("commands.setcorner1.usage", "/cc setcorner1");

		addDefault("commands.setcorner2.name", "setcorner2");
		addDefault("commands.setcorner2.permission", "CurveCraft.create");
		addDefault("commands.setcorner2.description", "Sets corner 2.");
		addDefault("commands.setcorner2.usage", "/cc setcorner2");

		addDefault("commands.changecorner1.name", "changecorner1");
		addDefault("commands.changecorner1.permission", "CurveCraft.changecorner");
		addDefault("commands.changecorner1.description", "Sets new corner 1");
		addDefault("commands.changecorner1.usage", "/cc changecorner1 [ARENA]");

		addDefault("commands.changecorner2.name", "changecorner2");
		addDefault("commands.changecorner2.permission", "CurveCraft.changecorner");
		addDefault("commands.changecorner2.description", "Sets new corner 2");
		addDefault("commands.changecorner2.usage", "/cc changecorner2 [ARENA]");

		addDefault("commands.enable.name", "enable");
		addDefault("commands.enable.permission", "CurveCraft.changestatus");
		addDefault("commands.enable.description", "Enables a arena");
		addDefault("commands.enable.usage", "/cc enable [ARENA]");

		addDefault("commands.disable.name", "disable");
		addDefault("commands.disable.permission", "CurveCraft.changestatus");
		addDefault("commands.disable.description", "Disables a arena");
		addDefault("commands.disable.usage", "/cc disable [ARENA]");

		addDefault("commands.deletearena.name", "deletearena");
		addDefault("commands.deletearena.permission", "CurveCraft.deletearena");
		addDefault("commands.deletearena.description", "Deletes the arena permanently.");
		addDefault("commands.deletearena.usage", "/cc deletearena [ARENA]");

		addDefault("commands.resetarena.name", "resetarena");
		addDefault("commands.resetarena.permission", "CurveCraft.resetarena");
		addDefault("commands.resetarena.description", "Resets the arena");
		addDefault("commands.resetarena.usage", "/cc resetarena [ARENA]");

		addDefault("commands.version.name", "version");
		addDefault("commands.version.permission", "CurveCraft.version");
		addDefault("commands.version.description", "Shows current version");
		addDefault("commands.version.usage", "/cc version");

		addDefault("commands.denytracking.name", "denytracking");
		addDefault("commands.denytracking.permission", "CurveCraft.user");
		addDefault("commands.denytracking.description", "forces the plugin to anonymise your data");
		addDefault("commands.denytracking.usage", "/cc denytracking");

		addDefault("commands.allowtracking.name", "allowtracking");
		addDefault("commands.allowtracking.permission", "CurveCraft.user");
		addDefault("commands.allowtracking.description", "Allows the plugin to save userdata");
		addDefault("commands.allowtracking.usage", "/cc allowtracking");

		try
		{
			options().copyDefaults(true);
			save(configl);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			System.err.println("Couldnt save language arenaconfig!");
		}
	}
}
