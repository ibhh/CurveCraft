package com.ibhh.CurveCraft;

import com.ibhh.CurveCraft.Listeners.PlayerListener;
import com.ibhh.CurveCraft.Permissions.PermissionsUtility;
import com.ibhh.CurveCraft.Report.ReportToHost;
import com.ibhh.CurveCraft.arena.AlreadyVotedException;
import com.ibhh.CurveCraft.arena.ArenaHandler;
import com.ibhh.CurveCraft.arena.CCArena;
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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class CurveCraft extends JavaPlugin {

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
    private final String[] commands = {"help", "version", "denytracking", "allowtracking", "create", "setname", "setcorner1", "setcorner2", "join", "forcestart", "start", "setlobby", "setend", "lobby", "exit"};

    private final HashMap<String, ArenaCreationProzess> arena = new HashMap();
    private ArenaHandler arenaHandler;
    private static boolean a = false;

    public String[] getCommands() {
        return this.commands;
    }

    public File getPluginFile() {
        return getFile();
    }

    public ReportToHost getReportHandler() {
        if (this.report == null) {
            this.report = new ReportToHost(this);
        }
        return this.report;
    }

    public MetricsHandler getMetricsHandler() {
        if (this.metricsHandler == null) {
            this.metricsHandler = new MetricsHandler(this);
        }
        return this.metricsHandler;
    }

    public Help getHelp() {
        if (this.help == null) {
            this.help = new Help(this);
        }
        return this.help;
    }

    public IConomyHandler getiConomyHandler() {
        if (this.iConomyHandler == null) {
            this.iConomyHandler = new IConomyHandler(this);
        }
        return this.iConomyHandler;
    }

    public ArenaHandler getArenaHandler() {
        if (this.arenaHandler == null) {
            this.arenaHandler = new ArenaHandler(this);
        }
        return this.arenaHandler;
    }

    public PlayerListener getListener() {
        if (this.listener == null) {
            this.listener = new PlayerListener(this);
        }
        return this.listener;
    }

    public Utilities getPluginManager() {
        if (this.pluginmanager == null) {
            this.pluginmanager = new Utilities(this);
        }
        return this.pluginmanager;
    }

    public Privacy getPrivacy() {
        if (this.privacy == null) {
            this.privacy = new Privacy(this);
        }
        return this.privacy;
    }

    public Update getUpdate() {
        if (this.update == null) {
            this.update = new Update(this);
        }
        return this.update;
    }

    public PermissionsUtility getPermissions() {
        if (this.permissions == null) {
            this.permissions = new PermissionsUtility(this);
        }
        return this.permissions;
    }

    public ConfigurationHandler getConfigHandler() {
        if (this.config == null) {
            this.config = new ConfigurationHandler(this);
        }
        return this.config;
    }

    public LoggerUtility getLoggerUtility() {
        if (this.logger == null) {
            this.logger = new LoggerUtility(this);
        }
        return this.logger;
    }

    public boolean isStarted() {
        return isEnabled();
    }

    @Override
    public void onDisable() {
        setEnabled(false);
        getPrivacy().savePrivacyFiles();
        long time = System.nanoTime();
        System.out.println(new StringBuilder().append("CurveCraft disabled in ").append((System.nanoTime() - time) / 1000000L).append(" ms").toString());
    }

    public static boolean toggletest() {
        a = !a;
        return a;
    }

    public static boolean test() {
        return a;
    }

    @Override
    public void onEnable() {
        long time = System.nanoTime();
        getConfigHandler().onStart();
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
        try {
            getMetricsHandler().start();
        } catch (IOException ex) {
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
        if (getConfigHandler().getConfig().getBoolean("senderrorreport")) {
            getLoggerUtility().log("This plugin collects error reports and send them to the developer.", LoggerUtility.Level.INFO);
            getLoggerUtility().log("To change this, change the value \"senderrorreport\" to false in the config.", LoggerUtility.Level.INFO);
            if (getConfigHandler().getConfig().getBoolean("senddebugfile")) {
                getLoggerUtility().log("This plugin collects debugfiles and send them to the developer.", LoggerUtility.Level.INFO);
                getLoggerUtility().log("To change this, change the value \"senddebugfile\" to false in the config.", LoggerUtility.Level.INFO);
            }
        }
        getLoggerUtility().log(new StringBuilder().append("CurveCraft enabled in ").append((System.nanoTime() - time) / 1000000L).append(" ms").toString(), LoggerUtility.Level.INFO);
        setEnabled(true);
    }

    public float getVersion() {
        try {
            return Float.parseFloat(getDescription().getVersion());
        } catch (NumberFormatException e) {
            getLoggerUtility().log("Could not parse version in float", LoggerUtility.Level.INFO);
            getLoggerUtility().log(new StringBuilder().append("Error getting version of ").append(getName()).append("! Message: ").append(e.getMessage()).toString(), LoggerUtility.Level.ERROR);
            this.report.report(3310, new StringBuilder().append("Error getting version of ").append(getName()).append("!").toString(), e.getMessage(), "CurveCraft", e);
            getLoggerUtility().log("Uncatched Exeption!", LoggerUtility.Level.ERROR);
        }
        return 0.0F;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isEnabled()) {
            getLoggerUtility().log("CurveCraft plugin is NOT enabled!", LoggerUtility.Level.ERROR);
            return true;
        }
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("cc")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("horse")) {
                        Horse h = (Horse) getServer().getWorld("world").spawnEntity(player.getLocation(), EntityType.HORSE);
                        h.setAdult();
                        h.setOwner(player);
                        h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        h.setPassenger(player);
                        h.setVelocity(new Vector(0.0D, 0.0D, 0.3D));
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.cancel.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.cancel.permission"))) {
                            if (!this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.cancel.message"), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.exit.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.exit.permission"))) {
                            try {
                                CCArena aren = getArenaHandler().getArenaOfPlayer(player);
                                if (aren == null) {
                                    getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"), LoggerUtility.Level.ERROR);
                                } else {
                                    aren.removePlayer(this, player);
                                }
                            } catch (NotInLobbyorGameException ex) {
                                getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
                            }
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.start.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.start.permission"))) {
                            try {
                                CCArena are = getArenaHandler().getArenaOfPlayer(player);
                                if (are == null) {
                                    getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"), LoggerUtility.Level.ERROR);
                                } else {
                                    are.voteForStart(this, player);
                                }
                            } catch (NotInLobbyorGameException | AlreadyVotedException | VotingNotEnabledException ex) {
                                getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
                            }
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.lobby.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.lobby.permission"))) {
                            CCArena ar = getArenaHandler().getArenaOfPlayer(player);
                            if (ar == null) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"), LoggerUtility.Level.ERROR);
                            } else {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("lobby.players"), LoggerUtility.Level.INFO);
                                StringBuilder st = new StringBuilder();
                                for (int i = 0; i < ar.getLobby().size(); i++) {
                                    st.append(((Player) ar.getLobby().get(i)).getName());
                                    if (i + 1 < ar.getLobby().size()) {
                                        st.append(", ");
                                    }
                                }
                                getLoggerUtility().log(player, st.toString(), LoggerUtility.Level.INFO);
                            }
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.create.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.create.permission"))) {
                            if (this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.already"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.start"), LoggerUtility.Level.INFO);
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.start2"), LoggerUtility.Level.INFO);

                            this.arena.put(player.getName(), new ArenaCreationProzess(this));
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.finish.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.finish.permission"))) {
                            if (!this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            if (this.arenaHandler.getArenaByName(((ArenaCreationProzess) this.arena.get(player.getName())).getName()) != null) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.nameexist"), LoggerUtility.Level.ERROR);
                                return true;
                            }
                            try {
                                ((ArenaCreationProzess) this.arena.get(player.getName())).finishProzess();
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.finished"), LoggerUtility.Level.INFO);
                            } catch (NotFinishedYetException ex) {
                                getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
                                return true;
                            }
                            this.arena.remove(player.getName());
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.setcorner1.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.setcorner1.permission"))) {
                            if (!this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.corner"), LoggerUtility.Level.INFO);
                            Location l = player.getLocation().add(0.0D, -1.0D, 0.0D);
                            ((ArenaCreationProzess) this.arena.get(player.getName())).setCorner1(l);
                            getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.setlobby.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.setlobby.permission"))) {
                            if (!this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.lobby"), LoggerUtility.Level.INFO);
                            Location l = player.getLocation();
                            ((ArenaCreationProzess) this.arena.get(player.getName())).setLobbyloc(l);
                            getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.setexit.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.setexit.permission"))) {
                            if (!this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.exit"), LoggerUtility.Level.INFO);
                            Location l = player.getLocation();
                            ((ArenaCreationProzess) this.arena.get(player.getName())).setExitloc(l);
                            getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.setend.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.setend.permission"))) {
                            if (!this.arena.containsKey(player.getName())) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                                return true;
                            }
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.end"), LoggerUtility.Level.INFO);
                            Location l = player.getLocation();
                            ((ArenaCreationProzess) this.arena.get(player.getName())).setEndloc(l);
                            getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.setcorner2.name"))) {
                        if (!this.arena.containsKey(player.getName())) {
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.noarena"), LoggerUtility.Level.INFO);
                            return true;
                        }
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.setcorner2.permission"))) {
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.corner"), LoggerUtility.Level.INFO);
                            Location l = player.getLocation().add(0.0D, -1.0D, 0.0D);
                            ((ArenaCreationProzess) this.arena.get(player.getName())).setCorner2(l);
                            getLoggerUtility().log(player, new StringBuilder().append(l.getBlockX()).append(" ").append(l.getBlockY()).append(" ").append(l.getBlockZ()).toString(), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if ((args[0].equalsIgnoreCase("info")) || (args[0].equalsIgnoreCase("version"))) {
                        player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("[CurveCraft]").append(ChatColor.DARK_AQUA).append(" CurveCraft Status:").append(ChatColor.GREEN).append("Working!").toString());
                        player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("[CurveCraft]").append(ChatColor.DARK_AQUA).append(" CurveCraft Version:").append(getVersion()).toString());
                        player.sendMessage(new StringBuilder().append(ChatColor.GRAY).append("[CurveCraft]").append(ChatColor.DARK_AQUA).append(" Further information: http://dev.bukkit.org/server-mods/curvefever").toString());
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.denytracking.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.denytracking.permission"))) {
                            if (getPrivacy().getConfig().containsKey(player.getName())) {
                                getPrivacy().getConfig().remove(player.getName());
                            }
                            getPrivacy().getConfig().put(player.getName(), Boolean.FALSE);
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("privacy.notification.denied"), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.allowtracking.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.allowtracking.permission"))) {
                            if (getPrivacy().getConfig().containsKey(player.getName())) {
                                getPrivacy().getConfig().remove(player.getName());
                            }
                            getPrivacy().getConfig().put(player.getName(), Boolean.TRUE);
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("privacy.notification.allowed"), LoggerUtility.Level.INFO);
                        }
                        return true;
                    }
                    getHelp().help(player, args);
                    return true;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.setname.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.setname.permission"))) {
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("create.notfinished.name"), LoggerUtility.Level.INFO);
                            ((ArenaCreationProzess) this.arena.get(player.getName())).setName(args[1]);
                        }
                        return true;
                    }

                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.forcestart.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.forcestart.permission"))) {
                            CCArena are = getArenaHandler().getArenaByName(args[1]);
                            if (are != null) {
                                try {
                                    are.forcestart(this);
                                    getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("start.forcestart.success"), LoggerUtility.Level.INFO);
                                } catch (StartGameException ex) {
                                    getLoggerUtility().log(player, ex.getMessage(), LoggerUtility.Level.ERROR);
                                }
                            } else {
                                getLoggerUtility().log(player, String.format(getConfigHandler().getLanguage_config().getString("lobby.join.noarena"), new Object[]{args[1]}), LoggerUtility.Level.ERROR);

                            }
                        }
                        return true;
                    }
                    if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.join.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.join.permission"))) {
                            if (getArenaHandler().getArenaOfPlayer(player) != null) {
                                getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("lobby.join.already"), LoggerUtility.Level.ERROR);
                            } else {
                                CCArena a = getArenaHandler().getArenaByName(args[1]);
                                if (a == null) {
                                    getLoggerUtility().log(player, String.format(getConfigHandler().getLanguage_config().getString("lobby.join.noarena"), new Object[]{args[1]}), LoggerUtility.Level.ERROR);
                                } else if (((getConfigHandler().getConfig().getBoolean("everyArenaOwnJoinPermission")) && (getPermissions().checkpermissions(player, new StringBuilder().append("CurveFerver.join.").append(args[1]).toString()))) || (!getConfigHandler().getConfig().getBoolean("everyArenaOwnJoinPermission"))) {
                                    try {
                                        a.addPlayerToLobby(this, player);
                                    } catch (LobbyJoinException ex) {
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
}
