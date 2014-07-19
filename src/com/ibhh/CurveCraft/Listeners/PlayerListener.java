package com.ibhh.CurveCraft.Listeners;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.arena.CCArena;
import com.ibhh.CurveCraft.arena.NotInLobbyorGameException;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class PlayerListener
        implements Listener {

    public CurveCraft plugin;

    public PlayerListener(CurveCraft plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        long time = System.nanoTime();
        try {
            final Player player = event.getPlayer();
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
                public void run() {
                    if ((!PlayerListener.this.plugin.getPrivacy().getConfig().containsKey(player.getName())) && (PlayerListener.this.plugin.getConfigHandler().getConfig().getBoolean("users_can_choose_privacy"))) {
                        PlayerListener.this.plugin.getLoggerUtility().log("no privacy set!", LoggerUtility.Level.DEBUG);
                        PlayerListener.this.plugin.getLoggerUtility().log(player.getPlayer(), PlayerListener.this.plugin.getConfigHandler().getLanguage_config().getString("privacy.notification.1"), LoggerUtility.Level.WARNING);
                        PlayerListener.this.plugin.getLoggerUtility().log(player.getPlayer(), PlayerListener.this.plugin.getConfigHandler().getLanguage_config().getString("privacy.notification.2"), LoggerUtility.Level.WARNING);
                        PlayerListener.this.plugin.getLoggerUtility().log(player.getPlayer(), PlayerListener.this.plugin.getConfigHandler().getLanguage_config().getString("privacy.notification.3"), LoggerUtility.Level.WARNING);
                    }
                }
            }, 20L);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Paypassage] Error: Uncatched Exeption!");
            this.plugin.getReportHandler().report(3321, "Player join throws error", e.getMessage(), "PaypassageListener", e);
        }
        this.plugin.getLoggerUtility().log("Player join event handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity en = event.getEntity();
        if ((en instanceof Horse)) {
            Entity pas = en.getPassenger();
            if ((pas instanceof Player)) {
                Player p = (Player) pas;
                CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(p);
                if (a != null) {
                    event.getDrops().clear();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void precommand(PlayerCommandPreprocessEvent event) {
        long time = System.nanoTime();
        if ((this.plugin.isEnabled()) && (event.getMessage().toLowerCase().startsWith("/cf".toLowerCase())) && (this.plugin.getConfigHandler().getConfig().getBoolean("debugfile"))
                && (this.plugin.getPrivacy().getConfig().containsKey(event.getPlayer().getName()))) {
            this.plugin.getLoggerUtility().log("user privacy set", LoggerUtility.Level.DEBUG);
            if (((Boolean) this.plugin.getPrivacy().getConfig().get(event.getPlayer().getName())).booleanValue()) {
                this.plugin.getLoggerUtility().log("userdata not allowed", LoggerUtility.Level.DEBUG);
                this.plugin.getLoggerUtility().log("Player: Anonymous command: " + event.getMessage(), LoggerUtility.Level.DEBUG);
            } else {
                this.plugin.getLoggerUtility().log("Player: " + event.getPlayer().getName() + " command: " + event.getMessage(), LoggerUtility.Level.DEBUG);
            }
        }

        if (((event.getMessage().toLowerCase().contains("tp")) || (event.getMessage().toLowerCase().contains("tt")) || (event.getMessage().toLowerCase().contains("home")))
                && (this.plugin.getArenaHandler().getArenaOfPlayer(event.getPlayer()) != null)) {
            event.setCancelled(true);
            this.plugin.getLoggerUtility().log(event.getPlayer(), "Not allowed! /cf exit", LoggerUtility.Level.ERROR);
        }

        this.plugin.getLoggerUtility().log("PlayerCommandPreprocessEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        long time = System.nanoTime();
        CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
        if (a != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("Blockbreak handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        long time = System.nanoTime();
        CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
        if ((a != null) && (a.isGameisrunning())) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("PlayerInteractEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        long time = System.nanoTime();
        Player p = null;
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            p = (Player) e.getEntity();
        }
        if(e.getEntityType().equals(EntityType.HORSE)) {
            Horse h = (Horse) e.getEntity();
            if(h.getPassenger() != null && !h.getPassenger().isEmpty() && h.getPassenger().getType().equals(EntityType.PLAYER)) {
                p = (Player) h.getPassenger();
            }
        }
        if (p != null) {
            CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(p);
            if ((a != null) && (a.isGameisrunning())) {
                e.setCancelled(true);
            }
        }
        this.plugin.getLoggerUtility().log("EntityDamageEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            try {
                this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()).removePlayer(this.plugin, e.getPlayer());
            } catch (NotInLobbyorGameException ex) {
            }
        }
        this.plugin.getLoggerUtility().log("onWorldChange handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKick(PlayerKickEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            try {
                this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()).removePlayer(this.plugin, e.getPlayer());
            } catch (NotInLobbyorGameException ex) {
            }
        }
        this.plugin.getLoggerUtility().log("PlayerKickEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeave(PlayerQuitEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            try {
                this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()).removePlayer(this.plugin, e.getPlayer());
            } catch (NotInLobbyorGameException ex) {
            }
        }
        this.plugin.getLoggerUtility().log("PlayerLeave handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSprint(PlayerToggleSprintEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            this.plugin.getLoggerUtility().log("PlayerSprint canceled!", LoggerUtility.Level.DEBUG);
            e.getPlayer().setSprinting(false);
        }
        this.plugin.getLoggerUtility().log("PlayerSprint handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEffect(PlayerItemConsumeEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("PlayerEffekt handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDrop(PlayerDropItemEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("PlayerDrop handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlace(BlockPlaceEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("BlockPlace handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHunger(FoodLevelChangeEvent e) {
        long time = System.nanoTime();
        if (((e.getEntity() instanceof Player))
                && (this.plugin.getArenaHandler().getArenaOfPlayer((Player) e.getEntity()) != null)) {
            e.setCancelled(true);
        }

        this.plugin.getLoggerUtility().log("PlayerHunger handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAbspringen(VehicleExitEvent e) {
        long time = System.nanoTime();
        if (((e.getExited() instanceof Player))
                && (this.plugin.getArenaHandler().getArenaOfPlayer((Player) e.getExited()) != null)) {
            e.setCancelled(true);
        }

        this.plugin.getLoggerUtility().log("VehicleExitEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent e) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getEntity()) != null) {
            try {
                this.plugin.getArenaHandler().getArenaOfPlayer(e.getEntity()).removePlayer(this.plugin, e.getEntity());
            } catch (NotInLobbyorGameException ex) {
            }
        }
        this.plugin.getLoggerUtility().log("PlayerDeathEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEXPDrop(PlayerExpChangeEvent e) {
        long time = System.nanoTime();
        CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
        if ((a != null) && (a.isGameisrunning())) {
            e.setAmount(0);
        }
        this.plugin.getLoggerUtility().log("Player Teleport handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTeleport(PlayerTeleportEvent e) {
        long time = System.nanoTime();

        this.plugin.getLoggerUtility().log("Player Teleport handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }
}
