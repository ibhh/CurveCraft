package com.ibhh.CurveCraft.locales;

import java.io.File;
import java.io.IOException;

public class Locale_de_DE extends PluginLocale implements PredefinedLocale
{

	public Locale_de_DE(String path)
	{
		super("de_DE", Localizer.getByCode("de_DE").getName());
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

		addDefault("configuration.language.pathnotfound", "In der Konfigurationsdatei \"language.%s.yml\" ist der String \"%s\" nicht vorhanden! Es wird die Standard Sprache verwendet: ");
		addDefault("configuration.language.languagenotfound", "Die Konfigurationsdatei zu der Sprache \"%s\" ist nicht vorhanden!");
		addDefault("configuration.language.list", "Folgende Sprachen sind installiert und köennen ausgewählt werden mit: ");

		addDefault("help.head", "CurveCraft Hilfe ");
		addDefault("help.commandsallowed", "Folgende Befehle darfst Du ausführen:");
		
		addDefault("permission.error", "Wir haben ein Problem! Dies darfst Du nicht machen!");

		addDefault("privacy.notification.1", "Dieses plugin speichert nutzerbezogene Daten in eine Datei");
		addDefault("privacy.notification.2", "\"/cc allowtracking\" um dem Plugin dies zu erlauben");
		addDefault("privacy.notification.3", "\"/cc denytracking\" um deine Daten zu anonymisieren");

		addDefault("privacy.notification.denied", "Das Plugin speichert nun keine nutzerbezogene Daten mehr.");
		addDefault("privacy.notification.allowed", "Das Plugin speichert deine Daten, wende Dich an einen Admin um diese z.B. löschen zu lassen");

		addDefault("create.notfinished.start", "Du musst einen Namen und zwei Ecken für die Arena setzen! Benutze /cc setname NAME und /cc setcorner1 /cc setcorner2 um deine aktuelle Position als Ecke zu setzen!");
		addDefault("create.notfinished.start2", "Und eine Position für die Lobby /cc setlobby und eine Position für ausgeschieden Spieler /cc setend and eine exit Position mit /cc setexit. Schließe den Prozess mit /cc finish ab");

		addDefault("create.notfinished.name", "Name gesetzt");
		addDefault("create.finished", "Abgeschlossen");
		addDefault("create.already", "Es wird bereits eine Arena erstellt. /cc cancel um Abzubrechen");
		addDefault("create.noarena", "Starte erst eine Arena! /cc create");

		addDefault("create.notfinished.corner", "Ecke gesetzt");
		addDefault("create.notfinished.lobby", "Lobby Position gesetzt.");
		addDefault("create.notfinished.end", "End Position gesetzt.");
		addDefault("create.notfinished.exit", "Exit position set.");
		addDefault("create.notfinished.nameexist", "Eine Arena mit diesem Namen besteht bereits, setzte bitte einen Neuen (/cc setname)");
		addDefault("create.notfinished.noname", "Du musst einen Namen für die Arena definieren! /cc setname NAME");
		addDefault("create.notfinished.nocorner", "Du musst die zwei gegenüberliegende Ecken für die Arena definieren! /cc setcorner1 NAME und /cc setcorner2 NAME");
		addDefault("create.notfinished.wrongY", "Beide Ecken müssen die gleiche Y-Koordinate haben.");
		addDefault("create.notfinished.lobbyloc", "Es muss eine Lobby-Position mit /cc setlobby gestetzt werden.");
		addDefault("create.notfinished.endloc", "Es muss eine End-Position mit /cc setend gesetzt werden");
		addDefault("create.notfinished.exitloc", "Es muss eine Exit-Position mit /cc setexit gesetzt werden");

		addDefault("commands.notallowed", "Dieser Befehl ist in der Arena nicht erlaubt. Benutze /cc exit");

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
		addDefault("commands.changelanguage.name", "#aenderesprache");
		addDefault("commands.changelanguage.permission", "WoolRun.changelanguage");
		addDefault("commands.changelanguage.description", "ändert die Sprache");
		addDefault("commands.changelanguage.usage", "/wr änderesprache [sprache]");
		addDefault("commands.changelanguage.return", "Die Sprache wurde geändert.");

		/* sprachliste Command */
		addDefault("commands.languagelist.name", "sprachliste");
		addDefault("commands.languagelist.permission", "WoolRun.changelanguage");
		addDefault("commands.languagelist.description", "Zeigt alle Sprachen");
		addDefault("commands.languagelist.usage", "/wr sprachliste");

		addDefault("create.cancel.message", "Du hast die Erstellung abgebrochen!");
		addDefault("create.already", "Es wird schon eine Arena erstellt /cc cancel um abzubrechen");
		addDefault("create.noarena", "Keine Arena ausgewählt. Starte mit die Erstellung mit /cc create");

		addDefault("lobby.join.message1", "Du betritts nun die Arena \"%s\".");
		addDefault("lobby.join.message2", "Es sind jetzt %d Spieler in der Lobby.");
		addDefault("lobby.join.message3", "Das Spiel kann ab %d Spielern gestartet werden, wenn alle /cc start eingeben.");
		addDefault("lobby.join.message4", "Das Spiel startet bei %d Spielern.");
		addDefault("lobby.join.playerjoin", "%s ist der Lobby beigetreten.");
		addDefault("lobby.join.gamerunning", "Das Spiel laeuft bereits.");
		addDefault("lobby.join.full", "Die Lobby ist bereits voll.");
		addDefault("lobby.join.noarena", "Keine Arena mit dem Namen: %s");
		addDefault("lobby.join.already", "Du bist schon in einer Lobby/Game");
		addDefault("lobby.join.disabled", "Arena ausgeschaltet!");

		addDefault("lobby.exit.message", "Du hast die Lobby verlassen.");
		addDefault("lobby.exit.playerexit", "%s hat die Lobby verlassen.");
		addDefault("lobby.exit.noarena", "Du bist in keiner Lobby / Spiel");

		addDefault("lobby.players", "Diese Player sind in der Lobby:");

		addDefault("commands.lobby.name", "lobby");
		addDefault("commands.lobby.permission", "CurveCraft.play");
		addDefault("commands.lobby.description", "Zeigt alle Spieler, die in der Lobby sind");
		addDefault("commands.lobby.usage", "/cc lobby");

		addDefault("commands.exit.name", "exit");
		addDefault("commands.exit.permission", "CurveCraft.play");
		addDefault("commands.exit.description", "Verlaesst Lobby/Spiel");
		addDefault("commands.exit.usage", "/cc exit");

		addDefault("commands.join.name", "join");
		addDefault("commands.join.permission", "CurveCraft.play");
		addDefault("commands.join.description", "Ermöglicht das betreten einer Arena");
		addDefault("commands.join.usage", "/cc join ARENA");

		addDefault("commands.start.name", "start");
		addDefault("commands.start.permission", "CurveCraft.play");
		addDefault("commands.start.description", "Ermöglicht das vorzeitige starten eines Spiels");
		addDefault("commands.start.usage", "/cc start");

		addDefault("commands.forcestart.name", "forcestart");
		addDefault("commands.forcestart.permission", "CurveCraft.forcestart");
		addDefault("commands.forcestart.description", "startet Spiel früher.");
		addDefault("commands.forcestart.usage", "/cc forcestart ARENANAME");

		addDefault("game.exit.message", "Du hast das Spiel verlassen.");
		addDefault("game.exit.playerexit", "%s hat das Spiel verlassen.");
		addDefault("game.crashed", "Du bist gecrasht!");
		addDefault("game.crash.wand", "Du bist gegen die Begrenzung gefahren!");
		addDefault("game.crash.plane", "Du bist gegen eine Spur gefahren!");
		addDefault("game.color", "Du hast die Farbe: %s");

		addDefault("scoreboard.name", "Punkte");

		addDefault("start.alreadyvoted", "Du hast schon gevoted!");
		addDefault("start.voted", "Du hast erfolgreich abgestimmt!");
		addDefault("start.votingnotenabled", "Das Spiel kann nicht vorzeitig gestartet werden!");
		addDefault("start.starting", "Das Spiel startet demnächst!");
		addDefault("start.round", "Neue Runde startet demnächst!");
		addDefault("start.forcestart.success", "Ausgefuehrt!");
		addDefault("start.forcestart.fail", "Keine 2 Spieler in Arena!");

		addDefault("start.starting_round", "Neu Runde started in %d Sekunden!");
		addDefault("start.starting_game", "Spiel startet in %d Sekunden!");
		addDefault("start.starting0", "Go go go!");

		addDefault("game.winner", "Der Sieger von Arena %s ist: %s");

		addDefault("round.winner", "Der Sieger dieser Runde ist: %s");

		addDefault("arena.change", "Erfolgreich geändert. Schließe deine ganze Operation am Ende mit /cc resetarena [ARENA] ab.");

		addDefault("commands.help.name", "help");
		addDefault("commands.help.permission", "CurveCraft.help");
		addDefault("commands.help.description", "Zeigt die Hilfe an");
		addDefault("commands.help.usage", "/cc help");

		addDefault("commands.list.name", "list");
		addDefault("commands.list.permission", "CurveCraft.play");
		addDefault("commands.list.description", "Listet alle geladenen Arenen auf.");
		addDefault("commands.list.usage", "/cc list");

		addDefault("commands.create.name", "create");
		addDefault("commands.create.permission", "CurveCraft.create");
		addDefault("commands.create.description", "Erzeugt eine neue CF Arena");
		addDefault("commands.create.usage", "/cc create");

		addDefault("commands.finish.name", "finish");
		addDefault("commands.finish.permission", "CurveCraft.create");
		addDefault("commands.finish.description", "Schließt create ab");
		addDefault("commands.finish.usage", "/cc finish");

		addDefault("commands.cancel.name", "cancel");
		addDefault("commands.cancel.permission", "CurveCraft.create");
		addDefault("commands.cancel.description", "Bricht den Erstell-Vorgang für eine Arena ab");
		addDefault("commands.cancel.usage", "/cc cancel");

		addDefault("commands.setname.name", "setname");
		addDefault("commands.setname.permission", "CurveCraft.create");
		addDefault("commands.setname.description", "Setzt den Namen einer Arena während der Spieler diese erstellt.");
		addDefault("commands.setname.usage", "/cc setname");

		addDefault("commands.setcorner1.name", "setcorner1");
		addDefault("commands.setcorner1.permission", "CurveCraft.create");
		addDefault("commands.setcorner1.description", "Setzt Ecke 1.");
		addDefault("commands.setcorner1.usage", "/cc setcorner1");

		addDefault("commands.setcorner2.name", "setcorner2");
		addDefault("commands.setcorner2.permission", "CurveCraft.create");
		addDefault("commands.setcorner2.description", "Setzt Ecke 2.");
		addDefault("commands.setcorner2.usage", "/cc setcorner2");

		addDefault("commands.changecorner1.name", "changecorner1");
		addDefault("commands.changecorner1.permission", "CurveCraft.changecorner");
		addDefault("commands.changecorner1.description", "Setzt Ecke 1 neu.");
		addDefault("commands.changecorner1.usage", "/cc changecorner1 [ARENA]");

		addDefault("commands.changecorner2.name", "changecorner2");
		addDefault("commands.changecorner2.permission", "CurveCraft.changecorner");
		addDefault("commands.changecorner2.description", "Setzt Ecke 2 neu.");
		addDefault("commands.changecorner2.usage", "/cc changecorner2 [ARENA]");

		addDefault("commands.enable.name", "enable");
		addDefault("commands.enable.permission", "CurveCraft.changestatus");
		addDefault("commands.enable.description", "Aktiviert eine Arena");
		addDefault("commands.enable.usage", "/cc enable [ARENA]");

		addDefault("commands.disable.name", "disable");
		addDefault("commands.disable.permission", "CurveCraft.changestatus");
		addDefault("commands.disable.description", "Deaktiviert eine Arena.");
		addDefault("commands.disable.usage", "/cc disable [ARENA]");

		addDefault("commands.deletearena.name", "deletearena");
		addDefault("commands.deletearena.permission", "CurveCraft.deletearena");
		addDefault("commands.deletearena.description", "Löscht die Arena für immer");
		addDefault("commands.deletearena.usage", "/cc deletearena [ARENA]");

		addDefault("commands.resetarena.name", "resetarena");
		addDefault("commands.resetarena.permission", "CurveCraft.resetarena");
		addDefault("commands.resetarena.description", "Setzt die Arena zurück");
		addDefault("commands.resetarena.usage", "/cc resetarena [ARENA]");

		addDefault("commands.version.name", "version");
		addDefault("commands.version.permission", "CurveCraft.version");
		addDefault("commands.version.description", "Zeigt die Version an");
		addDefault("commands.version.usage", "/cc version");

		addDefault("commands.denytracking.name", "denytracking");
		addDefault("commands.denytracking.permission", "CurveCraft.user");
		addDefault("commands.denytracking.description", "Zwingt das Plugin deine Daten zu anonymisieren");
		addDefault("commands.denytracking.usage", "/cc denytracking");

		addDefault("commands.allowtracking.name", "allowtracking");
		addDefault("commands.allowtracking.permission", "CurveCraft.user");
		addDefault("commands.allowtracking.description", "Erlaubt dem Plugin deine Daten zu speichern");
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
