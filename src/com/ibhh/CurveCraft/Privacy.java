package com.ibhh.CurveCraft;

import com.ibhh.CurveCraft.logger.LoggerUtility;
import java.io.File;
import java.util.HashMap;

public class Privacy {

    private HashMap<String, Boolean> config = new HashMap();
    private CurveCraft plugin;

    public Privacy(CurveCraft plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void loadData() {
        File dir = new File(this.plugin.getDataFolder() + File.separator + "privacy");
        dir.mkdirs();
        if (!new File(this.plugin.getDataFolder() + File.separator + "privacy" + File.separator + "User.privacy").exists()) {
            this.plugin.getLoggerUtility().log("Cannot load privacy files statistics: no data found", LoggerUtility.Level.DEBUG);
            return;
        }
        try {
            this.config = ((HashMap) ObjectManager.load(this.plugin.getDataFolder() + File.separator + "privacy" + File.separator + "User.privacy"));
        } catch (Exception e) {
            this.plugin.getLoggerUtility().log("Cannot load privacy files statistics!", LoggerUtility.Level.ERROR);
            if (this.plugin.getConfigHandler().getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
        }
    }

    public void autoSave() {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            public void run() {
                Privacy.this.savePrivacyFiles();
                Privacy.this.plugin.getLoggerUtility().log("Saving privacy files!", LoggerUtility.Level.DEBUG);
            }
        }, 0L, 6000L);
    }

    public HashMap<String, Boolean> getConfig() {
        return this.config;
    }

    public void savePrivacyFiles() {
        File dir = new File(this.plugin.getDataFolder() + File.separator + "privacy");
        dir.mkdirs();
        try {
            ObjectManager.save(this.config, this.plugin.getDataFolder() + File.separator + "privacy" + File.separator + "User.privacy");
        } catch (Exception e) {
            this.plugin.getLoggerUtility().log("Cannot save Privacy files statistics!", LoggerUtility.Level.ERROR);
            if (this.plugin.getConfigHandler().getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
        }
    }
}
