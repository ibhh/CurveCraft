package com.ibhh.CurveCraft.Listeners;

import com.ibhh.CurveCraft.CurveCraft;
import com.ibhh.CurveCraft.arena.AlreadyVotedException;
import com.ibhh.CurveCraft.arena.CCArena;
import com.ibhh.CurveCraft.arena.LobbyJoinException;
import com.ibhh.CurveCraft.arena.NotInLobbyorGameException;
import com.ibhh.CurveCraft.arena.VotingNotEnabledException;
import com.ibhh.CurveCraft.logger.LoggerUtility;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
                        //PlayerListener.this.plugin.getLoggerUtility().log("no privacy set!", LoggerUtility.Level.DEBUG);
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
    public void onSignPlace(SignChangeEvent e) {
        if (e.getLine(0).toLowerCase().contains("curvecraft") && e.getLine(1).toLowerCase().contains("join")) {
            CCArena a = plugin.getArenaHandler().getArenaByName(e.getLine(2));
            if (a == null) {
                this.plugin.getLoggerUtility().log(e.getPlayer(), String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.noarena"), e.getLine(2)), LoggerUtility.Level.ERROR);
                e.getBlock().breakNaturally();
                e.setCancelled(true);
            } else {
                if (a.getStatussign() != null) {
                    this.plugin.getLoggerUtility().log(e.getPlayer(), "Only one status sing per arena!", LoggerUtility.Level.ERROR);
                } else {
                    if (plugin.getPermissions().checkpermissions(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.create.permission"))) {
                        a.setStatussign(e.getBlock().getLocation(), plugin);
                        this.plugin.getLoggerUtility().log(e.getPlayer(), "Created!", LoggerUtility.Level.INFO);
                    } else {
                        e.getBlock().breakNaturally();
                        e.setCancelled(true);
                    }
                }
            }
        }
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
    public void precommand(PlayerCommandPreprocessEvent event) {
        long time = System.nanoTime();
        if (!plugin.isEnabled()) {
            return;
        }
        if ((event.getMessage().toLowerCase().startsWith("/cc".toLowerCase())) && (this.plugin.getConfigHandler().getConfig().getBoolean("debugfile"))
                && (this.plugin.getPrivacy().getConfig().containsKey(event.getPlayer().getName()))) {
            //this.plugin.getLoggerUtility().log("user privacy set", LoggerUtility.Level.DEBUG);
            if ((this.plugin.getPrivacy().getConfig().get(event.getPlayer().getName()))) {
                //this.plugin.getLoggerUtility().log("userdata not allowed", LoggerUtility.Level.DEBUG);
                this.plugin.getLoggerUtility().log("Player: Anonymous command: " + event.getMessage(), LoggerUtility.Level.DEBUG);
            } else {
                this.plugin.getLoggerUtility().log("Player: " + event.getPlayer().getName() + " command: " + event.getMessage(), LoggerUtility.Level.DEBUG);
            }
        }

        CCArena a = plugin.getArenaHandler().getArenaOfPlayer(event.getPlayer());
        if (a != null) {
            if ((!plugin.getConfigHandler().getConfig().getBoolean("globalcommandwhitelist") && a.isCommandwhitelist() && !a.commandWhiteListed(event.getMessage().split(" ")[0].replace("/", ""))) || (plugin.getConfigHandler().getConfig().getBoolean("globalcommandwhitelist") && a.isCommandwhitelist() && !plugin.getArenaHandler().isCommandGlobalWhitelisted(event.getMessage().split(" ")[0].replace("/", "")))) {
                event.setCancelled(true);
                this.plugin.getLoggerUtility().log(event.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.notallowed"), LoggerUtility.Level.ERROR);
            }
        }

        if (((event.getMessage().toLowerCase().contains("tp")) || (event.getMessage().toLowerCase().contains("tt")) || (event.getMessage().toLowerCase().contains("home")))
                && (this.plugin.getArenaHandler().getArenaOfPlayer(event.getPlayer()) != null)) {
            event.setCancelled(true);
            this.plugin.getLoggerUtility().log(event.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.notallowed"), LoggerUtility.Level.ERROR);
        }

        this.plugin.getLoggerUtility().log("PlayerCommandPreprocessEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        long time = System.nanoTime();
        CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
        if (a != null) {
            e.setCancelled(true);
        } else {
            if (e.getBlock().getState() instanceof Sign) {
                Sign s = (Sign) e.getBlock().getState();
                if (s.getLine(0).toLowerCase().contains("curvecraft") && s.getLine(1).toLowerCase().contains("join")) {
                    CCArena ar = plugin.getArenaHandler().getArenaByName(s.getLine(2));
                    if (ar != null) {
                        if (plugin.getPermissions().checkpermissions(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.create.permission"))) {
                            ar.setStatussign(null, plugin);
                            this.plugin.getLoggerUtility().log(e.getPlayer(), "Destroyed!", LoggerUtility.Level.INFO);
                        } else {
                            e.setCancelled(true);
                        }

                    }
                }
            }
        }

        this.plugin.getLoggerUtility().log("Blockbreak handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        long time = System.nanoTime();
        CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
        if ((a != null) && (a.isGameisrunning())) {
            e.setCancelled(true);
        } else if (e.hasBlock()) {
            Block b = e.getClickedBlock();
            if (!e.getPlayer().isSneaking() && b != null && b.getState() instanceof Sign) {
                plugin.getLoggerUtility().log("Sign clicked!", LoggerUtility.Level.DEBUG);
                Sign s = (Sign) b.getState();
                if (s.getLines().length >= 3 && s.getLine(0).toLowerCase().contains("curvecraft") && s.getLine(1).toLowerCase().contains("join")) {
                    plugin.getLoggerUtility().log("Has ccjoin", LoggerUtility.Level.DEBUG);
                    if (plugin.getPermissions().checkpermissions(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.join.permission"))) {
                        if (plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
                            plugin.getLoggerUtility().log(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("lobby.join.already"), LoggerUtility.Level.ERROR);
                        } else {
                            CCArena arena = plugin.getArenaHandler().getArenaByName(s.getLine(2));
                            if (arena == null) {
                                plugin.getLoggerUtility().log(e.getPlayer(), String.format(plugin.getConfigHandler().getLanguage_config().getString("lobby.join.noarena"), s.getLine(2)), LoggerUtility.Level.ERROR);
                            } else if ((plugin.getConfigHandler().getConfig().getBoolean("everyArenaOwnJoinPermission") && (plugin.getPermissions().checkpermissions(e.getPlayer(), new StringBuilder().append("CurveFerver.join.").append(s.getLine(2)).toString()))) || (!plugin.getConfigHandler().getConfig().getBoolean("everyArenaOwnJoinPermission"))) {
                                try {
                                    arena.addPlayerToLobby(plugin, e.getPlayer());
                                } catch (LobbyJoinException ex) {
                                    plugin.getLoggerUtility().log(e.getPlayer(), ex.getMessage(), LoggerUtility.Level.ERROR);
                                }
                            }
                        }
                    }
                }
                if (s.getLines().length >= 2 && s.getLine(0).toLowerCase().contains("curvecraft") && s.getLine(1).toLowerCase().contains("start")) {
                    plugin.getLoggerUtility().log("Has ccstart", LoggerUtility.Level.DEBUG);
                    if (plugin.getPermissions().checkpermissions(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.start.permission"))) {
                        try {
                            CCArena are = plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
                            if (are == null) {
                                plugin.getLoggerUtility().log(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"), LoggerUtility.Level.ERROR);
                            } else {
                                are.voteForStart(plugin, e.getPlayer());
                            }
                        } catch (NotInLobbyorGameException | AlreadyVotedException | VotingNotEnabledException ex) {
                            plugin.getLoggerUtility().log(e.getPlayer(), ex.getMessage(), LoggerUtility.Level.ERROR);
                        }
                    }
                }
                if (s.getLines().length >= 2 && s.getLine(0).toLowerCase().contains("curvecraft") && s.getLine(1).toLowerCase().contains("leave")) {
                    plugin.getLoggerUtility().log("Has ccleave", LoggerUtility.Level.DEBUG);
                    if (plugin.getPermissions().checkpermissions(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.exit.permission"))) {
                        try {
                            CCArena aren = plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
                            if (aren == null) {
                                plugin.getLoggerUtility().log(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("lobby.exit.noarena"), LoggerUtility.Level.ERROR);
                            } else {
                                aren.removePlayer(plugin, e.getPlayer());
                            }
                        } catch (NotInLobbyorGameException ex) {
                            plugin.getLoggerUtility().log(e.getPlayer(), ex.getMessage(), LoggerUtility.Level.ERROR);
                        }
                    }
                }
                if (s.getLines().length >= 2 && s.getLine(0).toLowerCase().contains("curvecraft") && s.getLine(1).toLowerCase().contains("viewlobby")) {
                    plugin.getLoggerUtility().log("Has cclobby", LoggerUtility.Level.DEBUG);
                    if (plugin.getPermissions().checkpermissions(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("commands.lobby.permission"))) {
                        CCArena ar = plugin.getArenaHandler().getArenaByName(s.getLine(2));
                        if (ar == null) {
                            plugin.getLoggerUtility().log(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("lobby.join.noarena"), LoggerUtility.Level.ERROR);
                        } else {
                            plugin.getLoggerUtility().log(e.getPlayer(), plugin.getConfigHandler().getLanguage_config().getString("lobby.players"), LoggerUtility.Level.INFO);
                            StringBuilder st = new StringBuilder();
                            for (int i = 0; i < ar.getLobby().size(); i++) {
                                st.append(((Player) ar.getLobby().get(i)).getName());
                                if (i + 1 < ar.getLobby().size()) {
                                    st.append(", ");
                                }
                            }
                            plugin.getLoggerUtility().log(e.getPlayer(), st.toString(), LoggerUtility.Level.INFO);
                        }
                    }
                }
            }
        }
        this.plugin.getLoggerUtility().log("PlayerInteractEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e
    ) {
        long time = System.nanoTime();
        Player p = null;
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            p = (Player) e.getEntity();
        }
        if (e.getEntityType().equals(EntityType.HORSE)) {
            Horse h = (Horse) e.getEntity();
            if (h.getPassenger() != null && !h.getPassenger().isEmpty() && h.getPassenger().getType().equals(EntityType.PLAYER)) {
                p = (Player) h.getPassenger();
            }
        }
        if (p != null) {
            CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(p);
            if (a != null) {
                p.setFireTicks(0);
                p.setRemainingAir(p.getMaximumAir());
                e.setCancelled(true);
            }
        }
        this.plugin.getLoggerUtility().log("EntityDamageEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(final EntityDamageByEntityEvent e
    ) {
        long time = System.nanoTime();
        Player p = null;
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            p = (Player) e.getEntity();
        }
        if (e.getEntityType().equals(EntityType.HORSE)) {
            Horse h = (Horse) e.getEntity();
            if (h.getPassenger() != null && !h.getPassenger().isEmpty() && h.getPassenger().getType().equals(EntityType.PLAYER)) {
                p = (Player) h.getPassenger();
            }
        }
        if (p != null) {
            CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(p);
            if (a != null) {
                p.setFireTicks(0);
                p.setRemainingAir(p.getMaximumAir());
                e.setCancelled(true);
            }
        }
        this.plugin.getLoggerUtility().log("EntityDamagebyEntityEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCombust(final EntityCombustEvent e
    ) {
        long time = System.nanoTime();
        Player p = null;
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            p = (Player) e.getEntity();
        }
        if (e.getEntityType().equals(EntityType.HORSE)) {
            Horse h = (Horse) e.getEntity();
            if (h.getPassenger() != null && !h.getPassenger().isEmpty() && h.getPassenger().getType().equals(EntityType.PLAYER)) {
                p = (Player) h.getPassenger();
            }
        }
        if (p != null) {
            CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(p);
            if (a != null) {
                p.setFireTicks(0);
                p.setRemainingAir(p.getMaximumAir());
                e.setCancelled(true);
            }
        }
        this.plugin.getLoggerUtility().log("EntityCombustEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldChange(PlayerChangedWorldEvent e
    ) {
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
    public void onKick(PlayerKickEvent e
    ) {
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
    public void onLeave(PlayerQuitEvent e
    ) {
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
    public void onSprint(PlayerToggleSprintEvent e
    ) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            //this.plugin.getLoggerUtility().log("PlayerSprint canceled!", LoggerUtility.Level.DEBUG);
            e.getPlayer().setSprinting(false);
        }
        this.plugin.getLoggerUtility().log("PlayerSprint handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEffect(PlayerItemConsumeEvent e
    ) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("PlayerEffekt handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDrop(PlayerDropItemEvent e
    ) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("PlayerDrop handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlace(BlockPlaceEvent e
    ) {
        long time = System.nanoTime();
        if (this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer()) != null) {
            e.setCancelled(true);
        }
        this.plugin.getLoggerUtility().log("BlockPlace handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHunger(FoodLevelChangeEvent e
    ) {
        long time = System.nanoTime();
        if (((e.getEntity() instanceof Player))
                && (this.plugin.getArenaHandler().getArenaOfPlayer((Player) e.getEntity()) != null)) {
            e.setCancelled(true);
        }

        this.plugin.getLoggerUtility().log("PlayerHunger handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAbspringen(VehicleExitEvent e
    ) {
        long time = System.nanoTime();
        if (((e.getExited() instanceof Player))
                && (this.plugin.getArenaHandler().getArenaOfPlayer((Player) e.getExited()) != null)) {
            e.setCancelled(true);
        }

        this.plugin.getLoggerUtility().log("VehicleExitEvent handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onDeath(PlayerDeathEvent e
    ) {
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
    public void onEXPDrop(PlayerExpChangeEvent e
    ) {
        long time = System.nanoTime();
        CCArena a = this.plugin.getArenaHandler().getArenaOfPlayer(e.getPlayer());
        if ((a != null) && (a.isGameisrunning())) {
            e.setAmount(0);
        }
        this.plugin.getLoggerUtility().log("Player Teleport handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTeleport(PlayerTeleportEvent e
    ) {
        long time = System.nanoTime();

        this.plugin.getLoggerUtility().log("Player Teleport handled in " + (System.nanoTime() - time) / 1000000L + " ms", LoggerUtility.Level.DEBUG);
    }
}
