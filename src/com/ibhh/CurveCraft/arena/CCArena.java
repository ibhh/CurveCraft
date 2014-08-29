package com.ibhh.CurveCraft.arena;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.commandwhitelist.CommandWhiteList;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

public class CCArena {

    private String name;
    private int maxplayers = 16;

    private int minplayers = 2;

    private int gap_length = 3;

    private int gap_distance = 20;

    private Location corner1 = null;
    private Location corner2 = null;
    private Location lobbyloc = null;
    private Location endloc = null;
    private Location exitloc = null;

    private boolean allowstartwithoutmaxplayers = true;

    private double speed = 0.5D;

    private boolean gameisrunning = false;
    private boolean roundrunning = false;

    private boolean lightning = true;

    private int pointsneeded = -1;

    private int timebeforegame = 10;
    private int timebeforeround = 5;

    private int invincible = 60;
    private int invincible_standard = 60;
    
    private boolean commandwhitelist = false;

    private final HashMap<Player, Integer> score = new HashMap<>();
    private final HashMap<Player, DyeColor> horses = new HashMap<>();
    private final ArrayList<Player> lobby = new ArrayList<>();
    private final ArrayList<Player> voted = new ArrayList<>();
    private final ArrayList<Player> alive = new ArrayList<>();

    private final HashMap<Player, Integer> gap = new HashMap();
    
    private CommandWhiteList commandwhitelistfile = null;

    public CCArena(String m, Location l1, Location l2, Location lobbyloc, Location endloc, Location exitloc) {
        this.name = m;
        this.corner1 = l1;
        this.corner2 = l2;
        this.lobbyloc = lobbyloc;
        this.endloc = endloc;
        this.exitloc = exitloc;
    }

    public CCArena(final CurveCraft plugin, File file, World world) {
        YamlConfiguration arena_save = prepareSave(plugin, file);

        this.minplayers = arena_save.getInt("player.minplayer");
        if (this.minplayers < 2) {
            this.minplayers = 2;
            plugin.getLoggerUtility().log("The value \"player.minplayer\" of the file \"" + file.getAbsolutePath() + "\" is invalid. Using default 2.", LoggerUtility.Level.ERROR);
        }

        this.maxplayers = arena_save.getInt("player.maxplayer");
        if (this.maxplayers > 16) {
            this.maxplayers = 16;
            plugin.getLoggerUtility().log("The value \"player.maxplayer\" of the file \"" + file.getAbsolutePath() + "\" is invalid. Using default 16.", LoggerUtility.Level.ERROR);
        }

        this.gap_length = arena_save.getInt("arena.gap_length");
        if (this.gap_length == 0) {
            this.gap_length = 3;
        }
        


        this.timebeforegame = arena_save.getInt("arena.timebeforegame");
        if (this.timebeforegame == 0) {
            this.timebeforegame = 10;
        }

        this.timebeforeround = arena_save.getInt("arena.timebeforeround");
        if (this.timebeforeround == 0) {
            this.timebeforeround = 5;
        }

        this.gap_distance = arena_save.getInt("arena.gap_distance");
        if (this.gap_distance == 0) {
            this.gap_distance = 20;
        }

        this.invincible = arena_save.getInt("arena.timeinvincible");
        if (this.invincible == 0) {
            this.invincible = 60;
            this.invincible_standard = 60;
        }

        if (!arena_save.contains("arena.lightning")) {
            this.lightning = true;
        } else {
            this.lightning = arena_save.getBoolean("arena.lightning");
        }

        this.invincible_standard = invincible;

        this.allowstartwithoutmaxplayers = arena_save.getBoolean("player.playersCanVoteForStart");

        this.name = arena_save.getString("arena.name");

        this.speed = arena_save.getDouble("arena.speed");
        if (this.speed == 0.0D) {
            this.speed = 0.5D;
        }
        String c1 = arena_save.getString("arena.corner1");
        String c2 = arena_save.getString("arena.corner2");
        String c3 = arena_save.getString("arena.lobbyloc");
        String c4 = arena_save.getString("arena.endloc");
        String c5 = arena_save.getString("arena.exitloc");

        String[] cc1 = c1.split("/");
        String[] cc2 = c2.split("/");
        String[] cc3 = c3.split("/");
        String[] cc4 = c4.split("/");
        String[] cc5 = c5.split("/");

        this.corner1 = new Location(world, Integer.parseInt(cc1[0]), Integer.parseInt(cc1[1]), Integer.parseInt(cc1[2]));
        this.corner2 = new Location(world, Integer.parseInt(cc2[0]), Integer.parseInt(cc2[1]), Integer.parseInt(cc2[2]));
        this.lobbyloc = new Location(world, Integer.parseInt(cc3[0]), Integer.parseInt(cc3[1]), Integer.parseInt(cc3[2]));
        this.endloc = new Location(world, Integer.parseInt(cc4[0]), Integer.parseInt(cc4[1]), Integer.parseInt(cc4[2]));
        this.exitloc = new Location(world, Integer.parseInt(cc5[0]), Integer.parseInt(cc5[1]), Integer.parseInt(cc5[2]));
        
        this.commandwhitelist = arena_save.getBoolean("arena.commandwhitelist");
        
        try {
            commandwhitelistfile = new CommandWhiteList(plugin, this);
        } catch (IOException ex) {
            Logger.getLogger(CCArena.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                CCArena.this.reset(plugin, true);
            }
        });
        plugin.getLoggerUtility().log("starting gametick thread!", LoggerUtility.Level.DEBUG);
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                CCArena.this.gametick(plugin);
            }
        }, 0L, 1L);
    }

    public boolean isInArena(Location l) {
        Location topLeftCorner;
        Location bottomRightCorner;
        if ((this.corner1.getBlockX() > this.corner2.getBlockX()) && (this.corner1.getBlockZ() > this.corner2.getBlockZ())) {
            bottomRightCorner = new Location(this.corner1.getWorld(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner2.getBlockZ());
            topLeftCorner = new Location(this.corner1.getWorld(), this.corner2.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ());
        } else {
            if ((this.corner1.getBlockX() > this.corner2.getBlockX()) && (this.corner1.getBlockZ() < this.corner2.getBlockZ())) {
                bottomRightCorner = this.corner1;
                topLeftCorner = this.corner2;
            } else {
                if ((this.corner1.getBlockX() < this.corner2.getBlockX()) && (this.corner1.getBlockZ() > this.corner2.getBlockZ())) {
                    bottomRightCorner = this.corner2;
                    topLeftCorner = this.corner1;
                } else {
                    bottomRightCorner = new Location(this.corner1.getWorld(), this.corner2.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ());
                    topLeftCorner = new Location(this.corner1.getWorld(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner2.getBlockZ());
                }
            }
        }
        return (l.getBlockX() > topLeftCorner.getBlockX()) && (l.getBlockX() < bottomRightCorner.getBlockX())
                && (l.getBlockZ() < topLeftCorner.getBlockZ()) && (l.getBlockZ() > bottomRightCorner.getBlockZ());
    }

    public void setCommandwhitelist(boolean commandwhitelist) {
        this.commandwhitelist = commandwhitelist;
    }

    public boolean isCommandwhitelist() {
        return commandwhitelist;
    }

    public boolean commandWhiteListed(String s) {
        if(commandwhitelist == false) {
            return true;
        } else {
            return commandwhitelistfile.allowed(s);
        }
    }
    
    
    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public void setMaxplayers(int maxplayers) {
        this.maxplayers = maxplayers;
    }

    public boolean isAllowstartwithoutmaxplayers() {
        return this.allowstartwithoutmaxplayers;
    }

    public void setMinplayers(int minplayers) {
        this.minplayers = minplayers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAllowstartwithoutmaxplayers(boolean allowstartwithoutmaxplayers) {
        this.allowstartwithoutmaxplayers = allowstartwithoutmaxplayers;
    }

    public Location getCorner1() {
        return this.corner1;
    }

    public Location getCorner2() {
        return this.corner2;
    }

    public int getGap_distance() {
        return this.gap_distance;
    }

    public int getGap_length() {
        return this.gap_length;
    }

    public int getPointsneeded() {
        return this.pointsneeded;
    }

    /**
     * HashMap with the score of each player inside this arena
     * @return Hashmap
     */
    public HashMap<Player, Integer> getScore() {
        return this.score;
    }

    public void setGap_distance(int gap_distance) {
        this.gap_distance = gap_distance;
    }

    public void setGap_length(int gap_length) {
        this.gap_length = gap_length;
    }

    public void setPointsneeded(int pointsneeded) {
        this.pointsneeded = pointsneeded;
    }

    public Location getEndloc() {
        return this.endloc;
    }

    public Location getLobbyloc() {
        return this.lobbyloc;
    }

    public void setEndloc(Location endloc) {
        this.endloc = endloc;
    }

    public void setLobbyloc(Location lobbyloc) {
        this.lobbyloc = lobbyloc;
    }

    /**
     * Get a ArrayList of players which are alive. Can be empty
     * @return ArrayList
     */
    public ArrayList<Player> getAlive() {
        return this.alive;
    }

    public Location getExitloc() {
        return this.exitloc;
    }

    
    /**
     * List contains all players which voted to start the game
     * @return ArrayList
     */
    public ArrayList<Player> getVoted() {
        return this.voted;
    }

    public void setExitloc(Location exitloc) {
        this.exitloc = exitloc;
    }

    public void setGameisrunning(boolean gameisrunning) {
        this.gameisrunning = gameisrunning;
    }

    /**
     * Maximum amount of players allowed in this arena
     * @return int
     */
    public int getMaxplayers() {
        return this.maxplayers;
    }

    /**
     * Required amount of players for a game in this arena
     * @return int
     */
    public int getMinplayers() {
        return this.minplayers;
    }

    /**
     * Name of the arena
     * @return String
     */
    public String getName() {
        return this.name;
    }

    
    /**
     * Get the speed of the horses of this arena
     * --- normalized vektor * speed = velocity
     * @return double
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * set the horse speed
     * @param speed 
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isGameisrunning() {
        return this.gameisrunning;
    }

    
    /**
     * Gives a ArrayList of players which are in this lobby / game
     * @return ArrayList
     */
    public ArrayList<Player> getLobby() {
        return this.lobby;
    }

    /**
     * Contains colors of the planes for each player
     * @return HashMap
     */
    public HashMap<Player, DyeColor> getHorses() {
        return this.horses;
    }

    private int getInvincible() {
        return this.invincible;
    }

    public int getTimebeforegame() {
        return this.timebeforegame;
    }

    public int getTimebeforeround() {
        return this.timebeforeround;
    }

    /**
     * Saves the arena to config file
     * @param plugin a CurveCraft instance
     * @return true on success
     */
    public boolean saveToFolder(CurveCraft plugin) {
        String world = this.corner1.getWorld().getName();
        File f1 = new File(plugin.getDataFolder() + File.separator + "arena-saves" + File.separator + world);
        f1.mkdirs();
        File configl = new File(plugin.getDataFolder() + File.separator + "arena-saves" + File.separator + world + File.separator + getName() + ".yml");
        YamlConfiguration arena_save = prepareSave(plugin, configl);

        arena_save.set("player.minplayer", getMinplayers());
        arena_save.set("player.maxplayer", getMaxplayers());
        arena_save.set("player.playersCanVoteForStart", isAllowstartwithoutmaxplayers());

        arena_save.set("arena.name", getName());
        arena_save.set("arena.speed", getSpeed());
        arena_save.set("arena.gap_length", getGap_length());
        arena_save.set("arena.gap_distance", getGap_distance());
        arena_save.set("arena.timebeforegame", getTimebeforegame());
        arena_save.set("arena.timebeforeround", getTimebeforeround());
        arena_save.set("arena.timeinvincible", getInvincible());
        arena_save.set("arena.corner1", getCorner1().getBlockX() + "/" + getCorner1().getBlockY() + "/" + getCorner1().getBlockZ());
        arena_save.set("arena.corner2", getCorner2().getBlockX() + "/" + getCorner2().getBlockY() + "/" + getCorner2().getBlockZ());
        arena_save.set("arena.lobbyloc", getLobbyloc().getBlockX() + "/" + getLobbyloc().getBlockY() + "/" + getLobbyloc().getBlockZ());
        arena_save.set("arena.endloc", getEndloc().getBlockX() + "/" + getEndloc().getBlockY() + "/" + getEndloc().getBlockZ());
        arena_save.set("arena.exitloc", getExitloc().getBlockX() + "/" + getExitloc().getBlockY() + "/" + getExitloc().getBlockZ());
        arena_save.set("arena.lightning", lightning);
        arena_save.set("arena.commandwhitelist", isCommandwhitelist());
        try {
            arena_save.options().copyDefaults(true);
            arena_save.save(configl);
        } catch (IOException ex) {
            ex.printStackTrace();
            plugin.getLoggerUtility().log("Couldnt save language config!", LoggerUtility.Level.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Used to add new default values, to update the config to new versions :D
     * @param plugin a CurveCraft instance (data folder and error logging)
     * @return true on success
     */
    public boolean updateSave(CurveCraft plugin) {
        String world = this.corner1.getWorld().getName();
        File f1 = new File(plugin.getDataFolder() + File.separator + "arena-saves" + File.separator + world);
        f1.mkdirs();
        File configl = new File(plugin.getDataFolder() + File.separator + "arena-saves" + File.separator + world + File.separator + getName() + ".yml");
        YamlConfiguration arena_save = prepareSave(plugin, configl);

        arena_save.addDefault("player.minplayer", "2");
        arena_save.addDefault("player.maxplayer", "8");
        arena_save.addDefault("player.playersCanVoteForStart", "true");

        arena_save.addDefault("arena.name", "Arena_Name");
        arena_save.addDefault("arena.speed", "0.5");
        arena_save.addDefault("arena.gap_length", "3");
        arena_save.addDefault("arena.gap_distance", "20");
        arena_save.addDefault("arena.timebeforegame", "10");
        arena_save.addDefault("arena.timebeforeround", "5");
        arena_save.addDefault("arena.timeinvincible", "60");
        arena_save.addDefault("arena.corner1", "0/0/0");
        arena_save.addDefault("arena.corner2", "25/0/25");
        arena_save.addDefault("arena.lobbyloc", "-1/0/-1");
        arena_save.addDefault("arena.endloc", "-10/0/-10");
        arena_save.addDefault("arena.exitloc", "-15/0/-15");
        arena_save.addDefault("arena.lightning", "true");
        arena_save.addDefault("arena.commandwhitelist", "false");
        try {
            arena_save.options().copyDefaults(true);
            arena_save.save(configl);
        } catch (IOException ex) {
            ex.printStackTrace();
            plugin.getLoggerUtility().log("Couldnt save language config!", LoggerUtility.Level.ERROR);
            return false;
        }
        return true;
    }

    /**
     * Create folders for config
     * @param plugin a CurveCraft instance
     * @param configl config file
     * @return  YamlConfiguration on success else null
     */
    private YamlConfiguration prepareSave(CurveCraft plugin, File configl) {
        File folder = new File(plugin.getDataFolder() + File.separator);
        folder.mkdirs();
        if (!configl.exists()) {
            try {
                configl.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                plugin.getLoggerUtility().log("Couldnt create new config file!", LoggerUtility.Level.ERROR);
                return null;
            }
        }
        return YamlConfiguration.loadConfiguration(configl);
    }

    /**
     * Manages voting
     * @param plugin a CurveCraft instance (used for language config)
     * @param player the player which votes
     * @throws NotInLobbyorGameException if the player is not in a lobby
     * @throws AlreadyVotedException if the player has already voted
     * @throws VotingNotEnabledException if voting is not enabled for this arena
     */
    public void voteForStart(CurveCraft plugin, Player player) throws NotInLobbyorGameException, AlreadyVotedException, VotingNotEnabledException {
        if (!this.allowstartwithoutmaxplayers) {
            throw new VotingNotEnabledException(plugin.getConfigHandler().getLanguage_config().getString("start.votingnotenabled"));
        }
        if (!this.lobby.contains(player)) {
            throw new NotInLobbyorGameException(plugin.getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"));
        }
        if (this.voted.contains(player)) {
            throw new AlreadyVotedException(plugin.getConfigHandler().getLanguage_config().getString("start.alreadyvoted"));
        }
        this.voted.add(player);
        plugin.getLoggerUtility().log(player, plugin.getConfigHandler().getLanguage_config().getString("start.voted"), LoggerUtility.Level.INFO);
        if (this.voted.size() == this.minplayers) {
            initGame(plugin, false);
        }
    }

    /**
     * Removes a player from a running round and teleports him to the endloc
     * @param plugin a CurveCraft insance
     * @param p the Player that should be removed.
     * @param crash if the reason for removal is a crash this have to be true (if he is the last player in the round this is true)
     */
    private void die(final CurveCraft plugin, final Player p, final boolean crash) {
        plugin.getLoggerUtility().log("player " + p.getName() + " died!", LoggerUtility.Level.DEBUG);

        Entity e = p.getVehicle();
        if ((e != null) && ((e instanceof Horse))) {
            Horse h = (Horse) e;
            h.eject();
            h.getInventory().setSaddle(null);
            h.damage(1000.0D);
        }
        int sco = (this.score.get(p));
        sco += this.lobby.size() - this.alive.size();
        this.score.remove(p);
        this.score.put(p, sco);
        this.alive.remove(p);

        if (crash && lightning) {
            corner1.getWorld().strikeLightningEffect(p.getLocation());
        }

        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 1), true);
        final ScoreboardManager manager = Bukkit.getScoreboardManager();

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                p.teleport(endloc);
            }
        }, 40L);

        for (Player pl : this.lobby) {
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("cfboard", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.GREEN + plugin.getConfigHandler().getLanguage_config().getString("scoreboard.name") + "(" + (this.lobby.size() - 1) * 10 + ")");
            for (Player pla : this.lobby) {
                objective.getScore(pla.getName()).setScore((this.score.get(pla)));
            }
            pl.setScoreboard(board);
        }

        /**
         * Fire Event PlayerCrashedEvent
         */
        if (this.alive.size() == 1) {
            plugin.getLoggerUtility().log("last player was: " + ((Player) this.alive.get(0)).getName(), LoggerUtility.Level.DEBUG);
            for (Player pl : this.lobby) {
                plugin.getLoggerUtility().log(pl, String.format(plugin.getConfigHandler().getLanguage_config().getString("round.winner"), this.alive.get(0).getName()), LoggerUtility.Level.INFO);
            }
            die(plugin, (Player) this.alive.get(0), false);
            roundrunning = false;
            if (hasWinner() != null) {
                /**
                 * Fire event PlayerGameWinEvent
                 */
                APIHandler.throwPlayerGameWinEvent(p, this, score, lobby);
            } else {
                /**
                 * Fire event PlayerRoundWinEvent
                 */
                APIHandler.throwPlayerRoundWinEvent(p, this);
            }

            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    if (hasWinner() != null) {

                        plugin.getMetricsHandler().addPlayersFinished(CCArena.this.lobby.size());
                        Player[] pla = plugin.getServer().getOnlinePlayers();
                        for (final Player s : pla) {
                            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                                @Override
                                public void run() {
                                    ScoreboardManager manager = Bukkit.getScoreboardManager();
                                    Scoreboard board = manager.getNewScoreboard();
                                    p.setScoreboard(board);
                                }
                            }, 200L);
                            plugin.getLoggerUtility().log(s, String.format(plugin.getConfigHandler().getLanguage_config().getString("game.winner"), getName(), hasWinner().getName()), LoggerUtility.Level.INFO);
                        }
                        plugin.getLoggerUtility().log("passed gameticks: " + (CCArena.this.invincible - 20), LoggerUtility.Level.DEBUG);

                        CCArena.this.reset(plugin, true);
                    } else {
                        CCArena.this.reset(plugin, false);

                        plugin.getLoggerUtility().log("starting new round!", LoggerUtility.Level.DEBUG);

                        CCArena.this.initGame(plugin, true);
                        plugin.getLoggerUtility().log("round initialized", LoggerUtility.Level.DEBUG);
                    }
                }
            }, 1L);
        }
    }
    
    /**
     * Starts a game without voting. 
     * @param plugin
     * @throws StartGameException if the game is already running, or if there are not two players in the lobby.
     */
    public void forcestart(final CurveCraft plugin) throws StartGameException {
        if (gameisrunning) {
            throw new StartGameException(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.gamerunning"));
        }
        if (lobby.size() >= 2) {
            initGame(plugin, false);
        } else {
            throw new StartGameException(plugin.getConfigHandler().getLanguage_config().getString("start.forcestart.fail"));
        }
    }

    /**
     * Checks if the game has already a winner
     * @return if yes, it returns the player object, else null
     */
    private Player hasWinner() {
        int i = -1;
        int i2 = -1;
        Player pi = null;
        for (Player p : this.lobby) {
            if ((i == -1) && ((this.score.get(p)) >= this.pointsneeded)) {
                i = (this.score.get(p));
                pi = p;
            }
            if ((i != -1) && ((this.score.get(p)) > i)) {
                i2 = i;
                i = (this.score.get(p));
                pi = p;
            }
        }
        if ((i - i2) < 2) {
            return null;
        }
        return pi;
    }

    
    /**
     * Manages game mechanics (like horse behavior or crashing)
     * @param plugin a CurveCraft instance
     */
    private void gametick(final CurveCraft plugin) {
        if (!gameisrunning || !roundrunning) {
            return;
        }
        ArrayList<Player> a = (ArrayList<Player>) alive.clone();
        for (final Player p : a) {
            if (!isInArena(p.getLocation()) && (invincible <= 0)) {
                plugin.getLoggerUtility().log(p, plugin.getConfigHandler().getLanguage_config().getString("game.crash.wand"), LoggerUtility.Level.INFO);
                plugin.getLoggerUtility().log("player " + p.getName() + " was outside the arena!", LoggerUtility.Level.DEBUG);

                die(plugin, p, true);
            }
            p.setSprinting(false);
            if ((p.getVehicle() instanceof Horse)) {
                Horse h = (Horse) p.getVehicle();
                plugin.getLoggerUtility().log("p: " + p.getName() + " velocity3 set: " + h.getVelocity().toString(), LoggerUtility.Level.DEBUG);
                plugin.getLoggerUtility().log("direction: " + p.getLocation().getDirection(), LoggerUtility.Level.DEBUG);
                if (h.isTamed()) {
                    final Block b2 = h.getLocation().getBlock();
                    final Block b = h.getLocation().add(0.0D, 0.0D, 0.0D).getBlock();
                    if ((this.gap.get(p)) == -1) {
                        this.gap.remove(p);
                        this.gap.put(p, this.gap_length);
                    }
                    if ((this.gap.get(p)) == 0) {
                        this.gap.remove(p);
                        this.gap.put(p, (int) (-(this.gap_distance)) - 1);
                    }
                    if ((this.gap.get(p)) > 0) {
                        int z = (this.gap.get(p));
                        this.gap.remove(p);
                        this.gap.put(p, --z);
                    } else {
                        int z = (this.gap.get(p));
                        this.gap.remove(p);
                        this.gap.put(p, ++z);
                        if (this.invincible <= 0) {
                            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    synchronized (horses) {
                                        if (CCArena.this.horses.containsKey(p)) {
                                            b2.setType(Material.STAINED_GLASS_PANE);
                                            b2.setData(new Wool((DyeColor) horses.get(p)).getData());
                                            b2.getState().update(true);
                                        }
                                    }
                                }
                            }, 4L);
                        }
                    }

                    if ((b.getType().equals(Material.STAINED_GLASS_PANE)) && (this.invincible <= 0)) {
                        plugin.getLoggerUtility().log(p, plugin.getConfigHandler().getLanguage_config().getString("game.crash.plane"), LoggerUtility.Level.INFO);
                        plugin.getLoggerUtility().log("player " + p.getName() + " crashed into plane!", LoggerUtility.Level.DEBUG);
                        die(plugin, p, true);
                    }
                    h.getLocation().setDirection(p.getLocation().getDirection());
                    Vector v = p.getLocation().getDirection();
                    v = v.setY(0).normalize();
                    h.setVelocity(v.multiply(this.speed));
                    p.getLocation().getDirection().setY(0);
                    plugin.getLoggerUtility().log("p: " + p.getName() + " velocity4 set: " + h.getVelocity().toString(), LoggerUtility.Level.DEBUG);
                    plugin.getLoggerUtility().log("direction: " + p.getLocation().getDirection(), LoggerUtility.Level.DEBUG);

                }
            }

        }
        invincible--;

    }

    /**
     * Does all what is needed to start the game
     * @param plugin a CurveCraft instance
     * @param round if a new round start this must be true, if a new game starts false
     */
    private void initGame(final CurveCraft plugin, final boolean round) {
        if (!round) {
            plugin.getMetricsHandler().addPlayersPlayed(this.lobby.size());
            plugin.getMetricsHandler().addMatchesStarted();
            gameisrunning = true;
        }
        for (Player player : this.lobby) {
            if (round) {
                plugin.getLoggerUtility().log(player, plugin.getConfigHandler().getLanguage_config().getString("start.round"), LoggerUtility.Level.INFO);
            } else {
                plugin.getLoggerUtility().log(player, plugin.getConfigHandler().getLanguage_config().getString("start.starting"), LoggerUtility.Level.INFO);
            }
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    int time = round ? CCArena.this.timebeforeround : CCArena.this.timebeforegame;
                    for (int i = 0; i < time; i++) {
                        Thread.sleep(1000L);
                        if (((time - i) % 5 == 0) || (time - i <= 3)) {
                            for (Player player : CCArena.this.lobby) {
                                if (round) {
                                    plugin.getLoggerUtility().log(player, String.format(plugin.getConfigHandler().getLanguage_config().getString("start.starting_round"), timebeforeround - i), LoggerUtility.Level.INFO);
                                } else {
                                    plugin.getLoggerUtility().log(player, String.format(plugin.getConfigHandler().getLanguage_config().getString("start.starting_game"), timebeforegame - i), LoggerUtility.Level.INFO);
                                }
                            }
                        }
                    }
                    for (Player player : CCArena.this.lobby) {
                        plugin.getLoggerUtility().log(player, plugin.getConfigHandler().getLanguage_config().getString("start.starting0"), LoggerUtility.Level.INFO);
                    }
                    if (round) {
                        /**
                         * Throw Event RoundStartEvent
                         */
                        APIHandler.throwRoundStartEvent(CCArena.this);
                    } else {
                        /**
                         * Throw Event GameStartEvent
                         */
                        APIHandler.throwGameStartEvent(CCArena.this);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(CCArena.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        });
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                CCArena.this.startGame(plugin);
                if (!round) {
                    for (Player player : CCArena.this.lobby) {
                        plugin.getLoggerUtility().log(player, String.format(plugin.getConfigHandler().getLanguage_config().getString("game.color"), horses.get(player).name()), LoggerUtility.Level.INFO);
                    }
                    pointsneeded = (CCArena.this.lobby.size() - 1) * 10;
                }
                roundrunning = true;
            }
        }, (round ? this.timebeforeround : this.timebeforegame) * 20);
    }

    private double minDistToNextPlayer(Location loc) {
        double min = -1.0D;
        for (Player p : this.lobby) {
            double temp = p.getLocation().distanceSquared(loc);
            if ((min == -1.0D) || (temp < min)) {
                min = temp;
            }
        }
        return min;
    }

    /**
     * Calculates player positions and manages teleport
     * @param plugin a CurveCraft instance
     */
    private void startGame(CurveCraft plugin) {
        for (Player player : this.lobby) {
            Location ploc = null;
            Location topLeftCorner;
            Location bottomRightCorner;
            if ((this.corner1.getBlockX() > this.corner2.getBlockX()) && (this.corner1.getBlockZ() > this.corner2.getBlockZ())) {
                bottomRightCorner = new Location(this.corner1.getWorld(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner2.getBlockZ());
                topLeftCorner = new Location(this.corner1.getWorld(), this.corner2.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ());
            } else {
                if ((this.corner1.getBlockX() > this.corner2.getBlockX()) && (this.corner1.getBlockZ() < this.corner2.getBlockZ())) {
                    bottomRightCorner = this.corner1;
                    topLeftCorner = this.corner2;
                } else {
                    if ((this.corner1.getBlockX() < this.corner2.getBlockX()) && (this.corner1.getBlockZ() > this.corner2.getBlockZ())) {
                        bottomRightCorner = this.corner2;
                        topLeftCorner = this.corner1;
                    } else {
                        bottomRightCorner = new Location(this.corner1.getWorld(), this.corner2.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ());
                        topLeftCorner = new Location(this.corner1.getWorld(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner2.getBlockZ());
                    }
                }
            }
            Location topRightCorner = new Location(this.corner1.getWorld(), bottomRightCorner.getBlockX(), bottomRightCorner.getBlockY(), topLeftCorner.getBlockZ());
            Location bottomLeftCorner = new Location(this.corner1.getWorld(), topLeftCorner.getBlockX(), bottomRightCorner.getBlockY(), bottomRightCorner.getBlockZ());
            plugin.getLoggerUtility().log("starting calculation for player positions", LoggerUtility.Level.DEBUG);

            int xrange = bottomRightCorner.getBlockX() - topLeftCorner.getBlockX();
            int zrange = topLeftCorner.getBlockZ() - bottomRightCorner.getBlockZ();
            int trys = 0;
            do {
                if (trys >= 10) {
                    break;
                }
                trys++;

                ploc = new Location(this.corner1.getWorld(), topLeftCorner.getBlockX() + 1 + (xrange - 2) * Math.random(), this.corner1.getBlockY() + 1, bottomRightCorner.getBlockZ() + 1 + (zrange - 2) * Math.random());
            } while (minDistToNextPlayer(ploc) < 100.0D);

            plugin.getLoggerUtility().log("finished calculation for player positions", LoggerUtility.Level.DEBUG);

            synchronized (player) {
                for (PotionEffect ef : player.getActivePotionEffects()) {
                    player.removePotionEffect(ef.getType());
                }
                plugin.getLoggerUtility().log("player teleported", LoggerUtility.Level.DEBUG);

                Horse h = (Horse) this.corner1.getWorld().spawnEntity(ploc, EntityType.HORSE);
                plugin.getLoggerUtility().log("horse spawned", LoggerUtility.Level.DEBUG);

                synchronized (h) {
                    h.setVariant(Horse.Variant.HORSE);
                    plugin.getLoggerUtility().log("horse set to horse", LoggerUtility.Level.DEBUG);

                    h.setAdult();
                    plugin.getLoggerUtility().log("horse set adult", LoggerUtility.Level.DEBUG);

                    synchronized (h.getInventory()) {
                        h.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        plugin.getLoggerUtility().log("saddle added", LoggerUtility.Level.DEBUG);
                    }

                    h.setOwner(player);
                    plugin.getLoggerUtility().log("owner set", LoggerUtility.Level.DEBUG);

                    h.setJumpStrength(0.0D);
                    plugin.getLoggerUtility().log("jump strength set to 0", LoggerUtility.Level.DEBUG);

                    double dist = -1.0D;
                    Location max = null;

                    dist = ploc.distance(bottomLeftCorner);
                    max = bottomLeftCorner;

                    if (ploc.distance(bottomRightCorner) > dist) {
                        dist = ploc.distance(bottomRightCorner);
                        max = bottomRightCorner;
                    }

                    if (ploc.distance(topLeftCorner) > dist) {
                        dist = ploc.distance(topLeftCorner);
                        max = topLeftCorner;
                    }

                    if (ploc.distance(topRightCorner) > dist) {
                        max = topRightCorner;
                    }

                    plugin.getLoggerUtility().log("max set: " + max, LoggerUtility.Level.DEBUG);

                    float yaw = getYaw(ploc, max);
                    Location ploc2 = new Location(ploc.getWorld(), ploc.getX(), ploc.getY(), ploc.getZ(), yaw, 0);
                    //Vector vect = new Vector(max.getX() - ploc.getX(), 0, max.getZ() - ploc.getZ()).normalize();
                    //h.setVelocity(vect.multiply(speed));
                    //player.setVelocity(vect.multiply(speed));
                    //plugin.getLoggerUtility().log("velocity set: " + h.getVelocity().toString(), LoggerUtility.Level.DEBUG);

                    plugin.getLoggerUtility().log("yaw: " + yaw, LoggerUtility.Level.DEBUG);

                    h.teleport(ploc2);
                    player.teleport(ploc2);

                    //player.getLocation().setDirection(vect);
                    //h.teleport(ploc);
                    //player.teleport(ploc);
                    h.setPassenger(player);

                    plugin.getLoggerUtility().log("passenger set", LoggerUtility.Level.DEBUG);
                    plugin.getLoggerUtility().log("velocity2 set: " + h.getVelocity().toString(), LoggerUtility.Level.DEBUG);
                    plugin.getLoggerUtility().log("direction: " + player.getLocation().getDirection(), LoggerUtility.Level.DEBUG);

                }
            }
            DyeColor dye = null;
            switch (this.horses.size()) {
                case 0:
                    dye = DyeColor.LIGHT_BLUE;
                    break;
                case 1:
                    dye = DyeColor.BLUE;
                    break;
                case 2:
                    dye = DyeColor.BROWN;
                    break;
                case 3:
                    dye = DyeColor.CYAN;
                    break;
                case 4:
                    dye = DyeColor.GRAY;
                    break;
                case 5:
                    dye = DyeColor.GREEN;
                    break;
                case 6:
                    dye = DyeColor.BLACK;
                    break;
                case 7:
                    dye = DyeColor.LIME;
                    break;
                case 8:
                    dye = DyeColor.MAGENTA;
                    break;
                case 9:
                    dye = DyeColor.ORANGE;
                    break;
                case 10:
                    dye = DyeColor.PINK;
                    break;
                case 11:
                    dye = DyeColor.PURPLE;
                    break;
                case 12:
                    dye = DyeColor.RED;
                    break;
                case 13:
                    dye = DyeColor.SILVER;
                    break;
                case 14:
                    dye = DyeColor.WHITE;
                    break;
                case 15:
                    dye = DyeColor.YELLOW;
                    break;
            }

            this.horses.put(player, dye);
            this.alive.add(player);
            this.gap.put(player, 0);
        }

    }

    private static float getLookAtYaw(Vector motion) {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0.0D;

        if (dx != 0.0D) {
            if (dx < 0.0D) {
                yaw = 4.71238898038469D;
            } else {
                yaw = 1.570796326794897D;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0.0D) {
            yaw = 3.141592653589793D;
        }
        return (float) (-yaw * 180.0D / 3.141592653589793D - 90.0D);
    }

    private float getYaw(Location source, Location target) {
        double disX = source.getX() - target.getX();
        double disY = source.getY() - target.getY();
        double disZ = source.getZ() - target.getZ();

        int X = (int) Math.abs(disX);
        int Y = (int) Math.abs(disY);
        int Z = (int) Math.abs(disZ);

        if (source.getX() <= target.getX()) {
            if (source.getZ() >= target.getZ()) {
                return getLookAtYaw(new Vector(X, Y, Z)) + 180.0F;
            }
            return getLookAtYaw(new Vector(X, Y, Z)) - 90.0F;
        }
        if (source.getX() >= target.getZ()) {
            return getLookAtYaw(new Vector(X, Y, Z)) + 90.0F;
        }
        return getLookAtYaw(new Vector(X, Y, Z));
    }

    /**
     * Adds a player to a game that isn't running yet
     * @param plugin a CurveCraft instance
     * @param p Player that should be added
     * @throws LobbyJoinException if the game is running, or the lobby has 16 players and is full
     */
    public void addPlayerToLobby(CurveCraft plugin, Player p) throws LobbyJoinException {
        if (this.gameisrunning) {
            throw new LobbyJoinException(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.gamerunning"));
        }
        if (this.lobby.size() >= this.maxplayers) {
            throw new LobbyJoinException(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.full"));
        }
        plugin.getLoggerUtility().log(p, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.message1"), new Object[]{getName()}), LoggerUtility.Level.INFO);
        if (this.allowstartwithoutmaxplayers) {
            plugin.getLoggerUtility().log(p, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.message3"), new Object[]{this.minplayers}), LoggerUtility.Level.INFO);
        }
        plugin.getLoggerUtility().log(p, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.message4"), new Object[]{this.maxplayers}), LoggerUtility.Level.INFO);
        plugin.getLoggerUtility().log(p, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.message2"), new Object[]{this.lobby.size() + 1}), LoggerUtility.Level.INFO);

        p.teleport(this.lobbyloc);
        for (Player pl : this.lobby) {
            plugin.getLoggerUtility().log(pl, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.playerjoin"), new Object[]{p.getName()}), LoggerUtility.Level.INFO);
            plugin.getLoggerUtility().log(pl, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.message2"), new Object[]{this.lobby.size() + 1}), LoggerUtility.Level.INFO);
        }
        this.lobby.add(p);
        this.score.put(p, 0);
        p.setFoodLevel(20);
        p.setSprinting(false);
        p.setAllowFlight(false);
        p.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect ef : p.getActivePotionEffects()) {
            p.removePotionEffect(ef.getType());
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        for (Player pl : this.lobby) {
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("cfboard", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.GREEN + plugin.getConfigHandler().getLanguage_config().getString("scoreboard.name") + "(" + (this.lobby.size() - 1) * 10 + ")");
            for (Player pla : this.lobby) {
                objective.getScore(pla.getName()).setScore(0);
            }
            pl.setScoreboard(board);
        }

        /**
         * Fire Event PlayerJoinEvent
         */
        APIHandler.throwPlayerJoinEvent(p, this);

        if (this.lobby.size() == this.maxplayers) {
            initGame(plugin, false);
        }
    }

    /**
     * Used if a player leaves game/lobby
     * @param plugin a CurveCraft instance
     * @param p the player
     * @throws NotInLobbyorGameException if the player is not in a lobby/game 
     */
    public void removePlayer(CurveCraft plugin, Player p)
            throws NotInLobbyorGameException {
        if (!this.lobby.contains(p)) {
            throw new NotInLobbyorGameException(plugin.getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"));
        }
        if (this.gameisrunning) {
            removePlayerFromGame(plugin, p);
        } else {
            removePlayerFromLobby(plugin, p);
        }
        this.score.remove(p);
        final ScoreboardManager manager = Bukkit.getScoreboardManager();
        for (Player pl : this.lobby) {
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("cfboard", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.GREEN + plugin.getConfigHandler().getLanguage_config().getString("scoreboard.name") + "(" + (this.lobby.size() - 1) * 10 + ")");
            for (Player pla : this.lobby) {
                objective.getScore(pla.getName()).setScore((this.score.get(pla)));
            }
            pl.setScoreboard(board);
        }
        final Scoreboard board = manager.getNewScoreboard();
        p.setScoreboard(board);
        /**
         * Fire Event PlayerLeaveEvent
         */
        APIHandler.throwPlayerLeaveEvent(p, this);
        if ((this.gameisrunning) && (this.lobby.size() == 1)) {
            Player[] pla = plugin.getServer().getOnlinePlayers();
            /**
             * Fire Event PlayerLeaveEvent
             */
            APIHandler.throwPlayerGameWinEvent(lobby.get(0), this, score, lobby);
            for (final Player s : pla) {

                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                    @Override
                    public void run() {
                        ScoreboardManager manager = Bukkit.getScoreboardManager();
                        Scoreboard board = manager.getNewScoreboard();
                        s.setScoreboard(board);
                    }
                }, 200L);
                plugin.getLoggerUtility().log(s, String.format(plugin.getConfigHandler().getLanguage_config().getString("game.winner"), new Object[]{this.name, ((Player) this.lobby.get(0)).getName()}), LoggerUtility.Level.INFO);
            }
            removePlayerFromGame(plugin, lobby.get(0));
            reset(plugin, true);
        }
    }

    private void removePlayerFromLobby(CurveCraft plugin, final Player p) {
        this.lobby.remove(p);
        this.alive.remove(p);
        for (Player pl : this.lobby) {
            plugin.getLoggerUtility().log(pl, String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.exit.playerexit"), new Object[]{p.getName()}), LoggerUtility.Level.INFO);
        }
        p.teleport(exitloc);
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                p.teleport(exitloc);
            }
        }, 20L);

        plugin.getLoggerUtility().log(p, plugin.getConfigHandler().getLanguage_config().getString("lobby.exit.message"), LoggerUtility.Level.INFO);
    }

    private void removePlayerFromGame(CurveCraft plugin, final Player p) {
        Entity e = p.getVehicle();
        if ((e != null) && ((e instanceof Horse))) {
            Horse h = (Horse) e;
            h.eject();
            h.getInventory().setSaddle(null);
            h.damage(1000.0D);
        }
        this.lobby.remove(p);
        this.horses.remove(p);
        this.alive.remove(p);
        for (PotionEffect ef : p.getActivePotionEffects()) {
            p.removePotionEffect(ef.getType());
        }
        p.teleport(exitloc);
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                p.teleport(exitloc);
            }
        }, 40L);

        for (Player pl : this.lobby) {
            plugin.getLoggerUtility().log(pl, String.format(plugin.getConfigHandler().getLanguage_config().getString("game.exit.playerexit"), new Object[]{p.getName()}), LoggerUtility.Level.INFO);
        }
        plugin.getLoggerUtility().log(p, plugin.getConfigHandler().getLanguage_config().getString("game.exit.message"), LoggerUtility.Level.INFO);
    }

    /**
     * Clears arena for new game/round (not public because there will occure bugs with scorebord, effects and eventhandler)
     * @param plugin a CurveCraft instance
     * @param end true, if the game is over
     */
    private void reset(CurveCraft plugin, boolean end) {
        long time = System.nanoTime();
        if (end) {
            plugin.getLoggerUtility().log("resetting all", LoggerUtility.Level.DEBUG);

            this.pointsneeded = -1;
            this.voted.clear();
            for (Iterator i = this.lobby.iterator(); i.hasNext();) {
                final Player p = (Player) i.next();
                for (PotionEffect ef : p.getActivePotionEffects()) {
                    p.removePotionEffect(ef.getType());
                }

                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        p.teleport(exitloc);
                    }
                }, 40L);
            }
            this.lobby.clear();
            this.gameisrunning = false;
        }
        roundrunning = false;
        this.invincible = invincible_standard;
        this.gap.clear();
        this.horses.clear();
        Location topLeftCorner;
        Location bottomRightCorner;
        if ((this.corner1.getBlockX() > this.corner2.getBlockX()) && (this.corner1.getBlockZ() > this.corner2.getBlockZ())) {
            plugin.getLoggerUtility().log("corner typ 1", LoggerUtility.Level.DEBUG);
            bottomRightCorner = new Location(this.corner1.getWorld(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner2.getBlockZ());
            topLeftCorner = new Location(this.corner1.getWorld(), this.corner2.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ());
        } else {
            if ((this.corner1.getBlockX() > this.corner2.getBlockX()) && (this.corner1.getBlockZ() < this.corner2.getBlockZ())) {
                plugin.getLoggerUtility().log("corner typ 2", LoggerUtility.Level.DEBUG);
                bottomRightCorner = this.corner1;
                topLeftCorner = this.corner2;
            } else {
                if ((this.corner1.getBlockX() < this.corner2.getBlockX()) && (this.corner1.getBlockZ() > this.corner2.getBlockZ())) {
                    plugin.getLoggerUtility().log("corner typ 3", LoggerUtility.Level.DEBUG);
                    bottomRightCorner = this.corner2;
                    topLeftCorner = this.corner1;
                } else {
                    plugin.getLoggerUtility().log("corner typ 4", LoggerUtility.Level.DEBUG);
                    bottomRightCorner = new Location(this.corner1.getWorld(), this.corner2.getBlockX(), this.corner1.getBlockY(), this.corner1.getBlockZ());
                    topLeftCorner = new Location(this.corner1.getWorld(), this.corner1.getBlockX(), this.corner1.getBlockY(), this.corner2.getBlockZ());
                }
            }
        }
        int count = 0;

        int xrange = bottomRightCorner.getBlockX() - topLeftCorner.getBlockX();
        int zrange = topLeftCorner.getBlockZ() - bottomRightCorner.getBlockZ();
        plugin.getLoggerUtility().log("xrange: " + xrange, LoggerUtility.Level.DEBUG);
        plugin.getLoggerUtility().log("zrange: " + zrange, LoggerUtility.Level.DEBUG);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < xrange; j++) {
                for (int x = 0; x < zrange; x++) {
                    count++;
                    Block b = this.corner1.getWorld().getBlockAt(new Location(this.corner1.getWorld(), topLeftCorner.getBlockX() + j, topLeftCorner.getBlockY() + 1 + i, bottomRightCorner.getBlockZ() + x));
                    b.setType(Material.AIR);
                    b.getState().update(true);
                }
            }
        }
        plugin.getLoggerUtility().log("Reset arena \"" + this.name + "\" with " + count + " blocks handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }
}
