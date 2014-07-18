package com.ibhh.CurveCraft.config;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author ibhh
 *
 */
public class ConfigurationHandler {

    private YamlConfiguration language_config;
    private final CurveCraft plugin;

    /**
     * Creates a new ConfigurationHandler
     *
     * @param plugin Needed for saving configs
     */
    public ConfigurationHandler(CurveCraft plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the current language configuration
     *
     * @return YamlConfiguration
     */
    public YamlConfiguration getLanguage_config() {
        return language_config;
    }

    /**
     *
     * @return plugin.getConifg();
     */
    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    /**
     * Called on start
     *
     * @return true if config was successfully loaded, false if it failed;
     */
    public boolean onStart() {
        // loading main config
        try {
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
            plugin.reloadConfig();
            plugin.getLoggerUtility().log("Config loaded", LoggerUtility.Level.DEBUG);
        } catch (Exception e) {
            plugin.getLoggerUtility().log("Cannot create config!", LoggerUtility.Level.ERROR);
            e.printStackTrace();
            plugin.onDisable();
        }
        createLanguageConfig();
        return true;
    }

    /**
     * Creates the language config and added defaults
     */
    private void createLanguageConfig() {
        for (int i = 0; i < 2; i++) {
            String a = "";
            if (i == 0) {
                a = "de";
            } else {
                a = "en";
            }
            File folder = new File(plugin.getDataFolder() + File.separator);
            folder.mkdirs();
            File configl = new File(plugin.getDataFolder() + File.separator + "language_" + a + ".yml");
            if (!configl.exists()) {
                try {
                    configl.createNewFile();
                } catch (IOException ex) {
                    plugin.getLoggerUtility().log("Couldnt create new config file!", LoggerUtility.Level.ERROR);
                }
            }
            language_config = YamlConfiguration.loadConfiguration(configl);
            if (i == 0) {
                this.language_config.addDefault("permission.error", "Wir haben ein Problem! Dies darfst Du nicht machen!");

                this.language_config.addDefault("privacy.notification.1", "Dieses plugin speichert nutzerbezogene Daten in eine Datei");
                this.language_config.addDefault("privacy.notification.2", "\"/cc allowtracking\" um dem Plugin dies zu erlauben");
                this.language_config.addDefault("privacy.notification.3", "\"/cc denytracking\" um deine Daten zu anonymisieren");

                this.language_config.addDefault("privacy.notification.denied", "Das Plugin speichert nun keine nutzerbezogene Daten mehr");
                this.language_config.addDefault("privacy.notification.allowed", "Das Plugin speichert deine Daten, wende Dich an einen Admin um diese z.B. loeschen zu lassen");

                this.language_config.addDefault("create.notfinished.start", "Du musst einen Namen und zwei Ecken fuer die Arena setzen! Benutze /cc setname NAME und /cc setcorner1 /cc setcorner2 um deine aktuelle Position als Ecke zu setzen!");
                this.language_config.addDefault("create.notfinished.start2", "Und eine Position für die Lobby /cc setlobby und eine Position fuer ausgeschieden Spieler /cc setend and eine exit Position mit /cc setexit");

                this.language_config.addDefault("create.notfinished.name", "Name gesetzt");
                this.language_config.addDefault("create.finished", "Abgeschlossen");
                this.language_config.addDefault("create.already", "Es wird bereits eine Arena erstellt. /cc cancel um Abzubrechen");
                this.language_config.addDefault("create.noarena", "Starte erst eine Arena! /cc create");

                this.language_config.addDefault("create.notfinished.corner", "Ecke gesetzt");
                this.language_config.addDefault("create.notfinished.lobby", "Lobby Position gesetzt.");
                this.language_config.addDefault("create.notfinished.end", "End Position gesetzt.");
                this.language_config.addDefault("create.notfinished.exit", "Exit position set.");
                this.language_config.addDefault("create.notfinished.nameexist", "Eine Arena mit diesem Namen besteht bereits, setzte bitte einen Neuen (/cc setname)");
                this.language_config.addDefault("create.notfinished.noname", "Du musst einen Namen für die Arena definieren! /cc setname NAME");
                this.language_config.addDefault("create.notfinished.nocorner", "Du musst die zwei gegenueberliegende Ecken für die Arena definieren! /cc setcorner1 NAME und /cc setcorner2 NAME");
                this.language_config.addDefault("create.notfinished.wrongY", "Beide Ecken muessen die gleiche Y-Koordinate haben.");
                this.language_config.addDefault("create.notfinished.lobbyloc", "Es muss eine Lobby-Position mit /cc setlobby gestetzt werden.");
                this.language_config.addDefault("create.notfinished.endloc", "Es muss eine End-Position mit /cc setend gesetzt werden");
                this.language_config.addDefault("create.notfinished.exitloc", "Es muss eine Exit-Position mit /cc setexit gesetzt werden");

                this.language_config.addDefault("commands.setexit.name", "setexit");
                this.language_config.addDefault("commands.setexit.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setexit.description", "Setzt die Position für den Exit Punkt.");
                this.language_config.addDefault("commands.setexit.usage", "/cc setexit");

                this.language_config.addDefault("commands.setlobby.name", "setlobby");
                this.language_config.addDefault("commands.setlobby.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setlobby.description", "Setzt die Position für die Lobby.");
                this.language_config.addDefault("commands.setlobby.usage", "/cc setlobby");

                this.language_config.addDefault("commands.setend.name", "setend");
                this.language_config.addDefault("commands.setend.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setend.description", "Setzt die End-Position.");
                this.language_config.addDefault("commands.setend.usage", "/cc setend");

                this.language_config.addDefault("create.cancel.message", "Du hast die Erstellung abgebrochen!");
                this.language_config.addDefault("create.already", "Es wird schon eine Arena erstellt /cc cancel um abzubrechen");
                this.language_config.addDefault("create.noarena", "Keine Arena ausgewaehlt. Starte mit die Erstellung mit /cc create");

                this.language_config.addDefault("lobby.join.message1", "Du betritts nun die Arena \"%s\".");
                this.language_config.addDefault("lobby.join.message2", "Es sind jetzt %d Spieler in der Lobby.");
                this.language_config.addDefault("lobby.join.message3", "Das Spiel kann ab %d Spielern gestartet werden, wenn alle /cc start eingeben.");
                this.language_config.addDefault("lobby.join.message4", "Das Spiel startet bei %d Spielern.");
                this.language_config.addDefault("lobby.join.playerjoin", "%s ist der Lobby beigetreten.");
                this.language_config.addDefault("lobby.join.gamerunning", "Das Spiel laeuft bereits.");
                this.language_config.addDefault("lobby.join.full", "Die Lobby ist bereits voll.");
                this.language_config.addDefault("lobby.join.noarena", "Keine Arena mit dem Namen: %s");
                this.language_config.addDefault("lobby.join.already", "Du bist schon in einer Lobby/Game");

                this.language_config.addDefault("lobby.exit.message", "Du hast die Lobby verlassen.");
                this.language_config.addDefault("lobby.exit.playerexit", "%s hat die Lobby verlassen.");
                this.language_config.addDefault("lobby.exit.noarena", "Du bist in keiner Lobby / Spiel");

                this.language_config.addDefault("lobby.players", "Diese Player sind in der Lobby:");

                this.language_config.addDefault("commands.lobby.name", "lobby");
                this.language_config.addDefault("commands.lobby.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.lobby.description", "Zeigt alle Spieler, die in der Lobby sind");
                this.language_config.addDefault("commands.lobby.usage", "/cc lobby");

                this.language_config.addDefault("commands.exit.name", "exit");
                this.language_config.addDefault("commands.exit.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.exit.description", "Verlaesst Lobby/Spiel");
                this.language_config.addDefault("commands.exit.usage", "/cc exit");

                this.language_config.addDefault("commands.join.name", "join");
                this.language_config.addDefault("commands.join.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.join.description", "Ermoeglicht das betreten einer Arena");
                this.language_config.addDefault("commands.join.usage", "/cc join ARENA");

                this.language_config.addDefault("commands.start.name", "start");
                this.language_config.addDefault("commands.start.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.start.description", "Ermoeglicht das vorzeitige starten eines Spiels");
                this.language_config.addDefault("commands.start.usage", "/cc start");
                
                this.language_config.addDefault("commands.forcestart.name", "forcestart");
                this.language_config.addDefault("commands.forcestart.permission", "CurveCraft.forcestart");
                this.language_config.addDefault("commands.forcestart.description", "startet Spiel frueher.");
                this.language_config.addDefault("commands.forcestart.usage", "/cc forcestart");

                this.language_config.addDefault("game.exit.message", "Du hast das Spiel verlassen.");
                this.language_config.addDefault("game.exit.playerexit", "%s hat das Spiel verlassen.");
                this.language_config.addDefault("game.crashed", "Du bist gecrasht!");
                this.language_config.addDefault("game.crash.wand", "Du bist gegen die Begrenzung gefahren!");
                this.language_config.addDefault("game.crash.plane", "Du bist gegen eine Spur gefahren!");
                this.language_config.addDefault("game.color", "Du hast die Farbe: %s");

                this.language_config.addDefault("scoreboard.name", "Punkte");

                this.language_config.addDefault("start.alreadyvoted", "Du hast schon gevoted!");
                this.language_config.addDefault("start.voted", "Du hast erfolgreich abgestimmt!");
                this.language_config.addDefault("start.votingnotenabled", "Das Spiel kann nicht vorzeitig gestartet werden!");
                this.language_config.addDefault("start.starting", "Das Spiel startet demnaechst!");
                this.language_config.addDefault("start.round", "Neue Runde startet demnaechst!");
                this.language_config.addDefault("start.forcestart.success", "Ausgefuehrt!");
                this.language_config.addDefault("start.forcestart.fail", "Keine 2 Spieler in Arena!");

                this.language_config.addDefault("start.starting_round", "Neu Runde started in %d Sekunden!");
                this.language_config.addDefault("start.starting_game", "Spiel startet in %d Sekunden!");
                this.language_config.addDefault("start.starting0", "Go go go!");

                this.language_config.addDefault("game.winner", "Der Sieger von Arena %s ist: %s");

                this.language_config.addDefault("round.winner", "Der Sieger dieser Runde ist: %s");

                this.language_config.addDefault("commands.help.name", "help");
                this.language_config.addDefault("commands.help.permission", "CurveCraft.help");
                this.language_config.addDefault("commands.help.description", "Zeigt die Hilfe an");
                this.language_config.addDefault("commands.help.usage", "/cc help");

                this.language_config.addDefault("commands.create.name", "create");
                this.language_config.addDefault("commands.create.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.create.description", "Erzeugt eine neue CF Arena");
                this.language_config.addDefault("commands.create.usage", "/cc create");

                this.language_config.addDefault("commands.finish.name", "finish");
                this.language_config.addDefault("commands.finish.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.finish.description", "Schließt create ab");
                this.language_config.addDefault("commands.finish.usage", "/cc finish");

                this.language_config.addDefault("commands.cancel.name", "cancel");
                this.language_config.addDefault("commands.cancel.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.cancel.description", "Cancels a new CF arena");
                this.language_config.addDefault("commands.cancel.usage", "/cc cancel");

                this.language_config.addDefault("commands.setname.name", "setname");
                this.language_config.addDefault("commands.setname.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setname.description", "Setzt den Namen einer Arena waehrend der Spieler diese erstellt.");
                this.language_config.addDefault("commands.setname.usage", "/cc setname");

                this.language_config.addDefault("commands.setcorner1.name", "setcorner1");
                this.language_config.addDefault("commands.setcorner1.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setcorner1.description", "Setzt Ecke 1.");
                this.language_config.addDefault("commands.setcorner1.usage", "/cc setcorner1");

                this.language_config.addDefault("commands.setcorner2.name", "setcorner2");
                this.language_config.addDefault("commands.setcorner2.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setcorner2.description", "Setzt Ecke 2.");
                this.language_config.addDefault("commands.setcorner2.usage", "/cc setcorner2");

                this.language_config.addDefault("commands.version.name", "version");
                this.language_config.addDefault("commands.version.permission", "CurveCraft.version");
                this.language_config.addDefault("commands.version.description", "Zeigt die Version an");
                this.language_config.addDefault("commands.version.usage", "/cc version");

                this.language_config.addDefault("commands.denytracking.name", "denytracking");
                this.language_config.addDefault("commands.denytracking.permission", "CurveCraft.user");
                this.language_config.addDefault("commands.denytracking.description", "Zwingt das Plugin deine Daten zu anonymisieren");
                this.language_config.addDefault("commands.denytracking.usage", "/cc denytracking");

                this.language_config.addDefault("commands.allowtracking.name", "allowtracking");
                this.language_config.addDefault("commands.allowtracking.permission", "CurveCraft.user");
                this.language_config.addDefault("commands.allowtracking.description", "Erlaubt dem Plugin deine Daten zu speichern");
                this.language_config.addDefault("commands.allowtracking.usage", "/cc allowtracking");
            } else {
                this.language_config.addDefault("permission.error", "we have a problem! You musnt do this!");

                this.language_config.addDefault("privacy.notification.1", "this plugin saves your interact events to a log");
                this.language_config.addDefault("privacy.notification.2", "\"/cc allowtracking\" to allow the plugin to save your data");
                this.language_config.addDefault("privacy.notification.3", "\"/cc denytracking\" to anonymise your data");

                this.language_config.addDefault("privacy.notification.denied", "The plugin anonymises your data now");
                this.language_config.addDefault("privacy.notification.allowed", "The plugin saves your data now, to delete the data, please tell an admin");

                this.language_config.addDefault("create.notfinished.start", "You must set a name and two corners for the arena! Use /cc setname NAME and /cc setcorner1 /cc setcorner2 to set your position as corner!");
                this.language_config.addDefault("create.notfinished.start2", "And a position for the lobby /cc setlobby and a position for eleminated player /cc setend and an exit position /cc setexit");

                this.language_config.addDefault("create.notfinished.name", "Name set");
                this.language_config.addDefault("create.finished", "Finished!");
                this.language_config.addDefault("create.notfinished.corner", "Corner set");
                this.language_config.addDefault("create.notfinished.lobby", "Lobby position set.");
                this.language_config.addDefault("create.notfinished.end", "End position set.");
                this.language_config.addDefault("create.notfinished.exit", "Exit position set.");
                this.language_config.addDefault("create.notfinished.nameexist", "Arena already exit with this name, choose another one (/cc setname)");
                this.language_config.addDefault("create.notfinished.noname", "You must set a name for the arena! /cc setname NAME");
                this.language_config.addDefault("create.notfinished.nocorner", "You must define two corners of the arena! /cc setcorner1 NAME und /cc setcorner2 NAME");
                this.language_config.addDefault("create.notfinished.wrongY", "Both corners must have the same Y-Coordinate");

                this.language_config.addDefault("create.notfinished.lobbyloc", "You must set a lobby position (/cc setlobby)");
                this.language_config.addDefault("create.notfinished.endloc", "You must set an end position(/cc setend)");
                this.language_config.addDefault("create.notfinished.exitloc", "You must set an exit position(/cc setexit)");

                this.language_config.addDefault("commands.setexit.name", "setexit");
                this.language_config.addDefault("commands.setexit.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setexit.description", "Setzt die Position für den Exit Punkt.");
                this.language_config.addDefault("commands.setexit.usage", "/cc setexit");

                this.language_config.addDefault("commands.setlobby.name", "setlobby");
                this.language_config.addDefault("commands.setlobby.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setlobby.description", "Setzt die Position für die Lobby.");
                this.language_config.addDefault("commands.setlobby.usage", "/cc setlobby");

                this.language_config.addDefault("commands.setend.name", "setend");
                this.language_config.addDefault("commands.setend.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setend.description", "Setzt die End-Position.");
                this.language_config.addDefault("commands.setend.usage", "/cc setend");

                this.language_config.addDefault("create.cancel.message", "You cancelled the creation of the arena!");
                this.language_config.addDefault("create.already", "An arena is in creation /cc cancel to cancel");
                this.language_config.addDefault("create.noarena", "Start a creation prozess first! /cc create");

                this.language_config.addDefault("lobby.join.message1", "You are joining the arena \"%s\".");
                this.language_config.addDefault("lobby.join.message2", "There are %d players in the lobby now.");
                this.language_config.addDefault("lobby.join.message3", "The game can start if there are %d players and all typed /cc start to start the game.");
                this.language_config.addDefault("lobby.join.message4", "The game starts at %d players.");
                this.language_config.addDefault("lobby.join.playerjoin", "%s is joining the lobby.");
                this.language_config.addDefault("lobby.join.gamerunning", "The game is already running. Sorry.");
                this.language_config.addDefault("lobby.join.full", "The lobby is already full. Sorry.");
                this.language_config.addDefault("lobby.join.noarena", "No arena with name \"%s\"");
                this.language_config.addDefault("lobby.join.already", "You already in an arena/game");

                this.language_config.addDefault("lobby.exit.message", "You left the lobby.");
                this.language_config.addDefault("lobby.exit.playerexit", "%s left the lobby.");
                this.language_config.addDefault("lobby.exit.noarena", "You are not in a lobby/game");

                this.language_config.addDefault("lobby.players", "This players are in your lobby:");

                this.language_config.addDefault("scoreboard.name", "Points");

                this.language_config.addDefault("start.alreadyvoted", "You have already voted!");
                this.language_config.addDefault("start.voted", "Sucessfully voted!");
                this.language_config.addDefault("start.votingnotenabled", "The game can't be started without the maximum of players.");
                this.language_config.addDefault("start.starting", "Game is starting!");
                this.language_config.addDefault("start.round", "New round is starting!");
                this.language_config.addDefault("start.starting_round", "New round is starting in %d seconds!");
                this.language_config.addDefault("start.starting_game", "Game is starting in %d seconds!");
                this.language_config.addDefault("start.starting0", "Go go go!");
                this.language_config.addDefault("start.forcestart.success", "Executed!");
                this.language_config.addDefault("start.forcestart.fail", "There must be two players in the arena");

                this.language_config.addDefault("round.winner", "The winner of this round is: %s");
                this.language_config.addDefault("game.winner", "The winner of arena %s is: %s");
                this.language_config.addDefault("game.crashed", "You crashed!");
                this.language_config.addDefault("game.crash.wand", "You crashed into a wand!");
                this.language_config.addDefault("game.crash.plane", "You crashed into plane!");
                this.language_config.addDefault("game.color", "Your colour ist: %s");

                this.language_config.addDefault("commands.lobby.name", "lobby");
                this.language_config.addDefault("commands.lobby.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.lobby.description", "Shows players of lobby");
                this.language_config.addDefault("commands.lobby.usage", "/cc lobby");

                this.language_config.addDefault("commands.exit.name", "exit");
                this.language_config.addDefault("commands.exit.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.exit.description", "Exit lobby/game");
                this.language_config.addDefault("commands.exit.usage", "/cc exit");

                this.language_config.addDefault("game.exit.message", "You left the game.");
                this.language_config.addDefault("game.exit.playerexit", "%s left the game.");

                this.language_config.addDefault("commands.join.name", "join");
                this.language_config.addDefault("commands.join.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.join.description", "Joins an arena");
                this.language_config.addDefault("commands.join.usage", "/cc join ARENA");
                
                this.language_config.addDefault("commands.forcestart.name", "forcestart");
                this.language_config.addDefault("commands.forcestart.permission", "CurveCraft.forcestart");
                this.language_config.addDefault("commands.forcestart.description", "start game earlier.");
                this.language_config.addDefault("commands.forcestart.usage", "/cc forcestart");

                this.language_config.addDefault("commands.start.name", "start");
                this.language_config.addDefault("commands.start.permission", "CurveCraft.play");
                this.language_config.addDefault("commands.start.description", "Vote to start game earlier.");
                this.language_config.addDefault("commands.start.usage", "/cc start");

                this.language_config.addDefault("commands.help.name", "help");
                this.language_config.addDefault("commands.help.permission", "CurveCraft.help");
                this.language_config.addDefault("commands.help.description", "Shows help");
                this.language_config.addDefault("commands.help.usage", "/cc help");

                this.language_config.addDefault("commands.create.name", "create");
                this.language_config.addDefault("commands.create.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.create.description", "Creates a new CF arena");
                this.language_config.addDefault("commands.create.usage", "/cc create");

                this.language_config.addDefault("commands.finish.name", "finish");
                this.language_config.addDefault("commands.finish.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.finish.description", "Finishes the creation prozess");
                this.language_config.addDefault("commands.finish.usage", "/cc finish");

                this.language_config.addDefault("commands.cancel.name", "cancel");
                this.language_config.addDefault("commands.cancel.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.cancel.description", "Cancels a new CF arena");
                this.language_config.addDefault("commands.cancel.usage", "/cc cancel");

                this.language_config.addDefault("commands.setname.name", "setname");
                this.language_config.addDefault("commands.setname.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setname.description", "Sets a name for a arena while the player creates it.");
                this.language_config.addDefault("commands.setname.usage", "/cc setname");

                this.language_config.addDefault("commands.setcorner1.name", "setcorner1");
                this.language_config.addDefault("commands.setcorner1.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setcorner1.description", "Sets corner 1.");
                this.language_config.addDefault("commands.setcorner1.usage", "/cc setcorner1");

                this.language_config.addDefault("commands.setcorner2.name", "setcorner2");
                this.language_config.addDefault("commands.setcorner2.permission", "CurveCraft.create");
                this.language_config.addDefault("commands.setcorner2.description", "Sets corner 2.");
                this.language_config.addDefault("commands.setcorner2.usage", "/cc setcorner2");

                this.language_config.addDefault("commands.version.name", "version");
                this.language_config.addDefault("commands.version.permission", "CurveCraft.version");
                this.language_config.addDefault("commands.version.description", "Shows current version");
                this.language_config.addDefault("commands.version.usage", "/cc version");

                this.language_config.addDefault("commands.denytracking.name", "denytracking");
                this.language_config.addDefault("commands.denytracking.permission", "CurveCraft.user");
                this.language_config.addDefault("commands.denytracking.description", "forces the plugin to anonymise your data");
                this.language_config.addDefault("commands.denytracking.usage", "/cc denytracking");

                this.language_config.addDefault("commands.allowtracking.name", "allowtracking");
                this.language_config.addDefault("commands.allowtracking.permission", "CurveCraft.user");
                this.language_config.addDefault("commands.allowtracking.description", "Allows the plugin to save userdata");
                this.language_config.addDefault("commands.allowtracking.usage", "/cc allowtracking");
            }
            try {
                language_config.options().copyDefaults(true);
                language_config.save(configl);
            } catch (IOException ex) {
                ex.printStackTrace();
                plugin.getLoggerUtility().log("Couldnt save language config!", LoggerUtility.Level.ERROR);
            }
        }
        File configl = new File(plugin.getDataFolder() + File.separator + "language_" + plugin.getConfig().getString("language") + ".yml");
        try {
            language_config = YamlConfiguration.loadConfiguration(configl);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLoggerUtility().log("Couldnt load language config!", LoggerUtility.Level.ERROR);
            plugin.getConfig().set("language", "en");
            plugin.saveConfig();
            plugin.onDisable();
            return;
        }
        plugin.getLoggerUtility().log("language config loaded", LoggerUtility.Level.DEBUG);
    }
}
