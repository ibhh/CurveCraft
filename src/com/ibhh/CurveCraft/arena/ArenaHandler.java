package com.ibhh.CurveCraft.arena;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author ibhh
 */
public class ArenaHandler {
    
    private final CurveCraft plugin;
    private final ArrayList<CCArena> arena = new ArrayList<>();
    
    public ArenaHandler(CurveCraft plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads all areanas
     */
    public void loadArenas() {
        for (World world : plugin.getServer().getWorlds()) {
            File f = new File(plugin.getDataFolder() + File.separator + "arena-saves" + File.separator + world.getName());
            if (f.exists()) {
                File[] files = f.listFiles(new FileFilter() {
                    
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().contains(".yml");
                    }
                });
                
                for (File file : files) {
                    CCArena cf = new CCArena(plugin, file, world);
                    cf.updateSave(plugin);
                    cf.saveToFolder(plugin);
                    arena.add(cf);
                    plugin.getLoggerUtility().log("Arena \"" + file.getName().replace(".yml", "") + "\" in world \"" + world.getName() + "\" loaded!", LoggerUtility.Level.INFO);
                }
            }
        }
    }
    
    /**
     * Get the list of all arenas
     * @return ArrayList CCArena
     */
    public ArrayList<CCArena> getArena() {
        return arena;
    }
    
    
    /**
     * Get the arena by location
     * @param l Location
     * @return CCArena arena which contains this location
     */
    public CCArena isInAnArena(Location l) {
        for (CCArena a : arena) {
            if (a.isInArena(l)) {
                return a;
            }
        }
        return null;
    }
    
    /**
     * Get the arena by a player, who is in the arena
     * @param p
     * @return CCArena / Arena of player
     */
    public CCArena getArenaOfPlayer(Player p) {
        for (CCArena a : arena) {
            if (a.getLobby().contains(p)) {
                return a;
            }
        }
        return null;
    }
    
    /**
     * Returns the arena if the name matches else null
     * @param s Name of the arena
     * @return Returns the arena if the name matches else null
     */
    public CCArena getArenaByName(String s) {
        for (CCArena a : arena) {
            if (a.getName().equalsIgnoreCase(s)) {
                return a;
            }
        }
        return null;
    }
}
