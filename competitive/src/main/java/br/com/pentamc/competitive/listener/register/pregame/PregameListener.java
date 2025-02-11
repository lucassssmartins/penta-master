package br.com.pentamc.competitive.listener.register.pregame;

import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.VarChangeEvent;
import br.com.pentamc.competitive.event.game.GameStartEvent;
import br.com.pentamc.competitive.menu.kit.SelectorInventory;
import br.com.pentamc.competitive.menu.kit.ShopInventory;
import br.com.pentamc.competitive.utils.ServerConfig;
import br.com.pentamc.competitive.GameConst;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.kit.KitType;
import br.com.pentamc.competitive.listener.GameListener;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.item.ActionItemStack;
import br.com.pentamc.bukkit.api.item.ActionItemStack.ActionType;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.title.Title;
import br.com.pentamc.bukkit.api.title.types.SimpleTitle;
import br.com.pentamc.bukkit.api.vanish.AdminMode;
import br.com.pentamc.bukkit.bukkit.BukkitMember;

public class PregameListener extends GameListener {

    private static final int RADIUS = 100;

    private final ActionItemStack kitSelector = new ActionItemStack(
            new ItemBuilder().type(Material.CHEST).name("§aSelecionar kit").build(),
            new ActionItemStack.Interact() {

                @Override
                public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                    new SelectorInventory(player, 1, KitType.PRIMARY, SelectorInventory.OrderType.MINE);
                    return false;
                }
            });

    private final ActionItemStack shopSelector = new ActionItemStack(
            new ItemBuilder().type(Material.EMERALD).name("§aLoja de kits").build(),
            new ActionItemStack.Interact() {

                @Override
                public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                    new ShopInventory(player, 1);
                    return false;
                }
            });

    private final ActionItemStack specialKit = new ActionItemStack(
            new ItemBuilder()
                    .type(Material.STORAGE_MINECART)
                    .name("§aKit diário")
                    .build(),
            new ActionItemStack.Interact() {
                @Override
                public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                    player.sendMessage("§cEm breve!");
                    return false;
                }
            }
    );

    public PregameListener() {
        Location location = new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0,
                                         Bukkit.getWorlds().stream().findFirst().orElse(null).getHighestBlockYAt(0, 0),
                                         0);

        if (location.getBlock().getBiome() != Biome.FOREST) {
            while (location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.STONE) {
                location = location.getWorld().getHighestBlockAt((int) location.getX() + 2, (int) location.getY() + 2)
                                   .getLocation();
            }
        }

        Location spawnLocation = location.add(0, 5, 0);

        for (int i = 0; i < 4; i++) {
            Block block = spawnLocation.add(i, i, i).getBlock();
            Chunk chunk = block.getChunk();
            chunk.load(true);
            block = spawnLocation.add(-i, -i, -i).getBlock();
            chunk = block.getChunk();
            chunk.load(true);
        }

        BukkitMain.getInstance().registerLocationInConfig(spawnLocation, "spawn");
        BukkitMain.getInstance().removeLocationInConfig("respawn");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

        gamer.setPlaying(true);

        player.setHealth(20.0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFoodLevel(20);
        player.setExp(0);

        handlePlayer(player);

        player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));

        Title.send(player, "§b§lCOMPETITIVO", "§eSeja bem-vindo!",
                   SimpleTitle.class);

        for (KitType kitType : KitType.values())
            if (ServerConfig.getInstance().getDefaultKit().containsKey(kitType)) {
                GameGeneral.getInstance().getKitController()
                           .setKit(player, ServerConfig.getInstance().getDefaultKit().get(kitType), kitType);
            }

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onVarChange(VarChangeEvent event) {
        switch (event.getVarName()) {
        case GameConst.MAX_PLAYERS_PER_TEAM_VAR:
        case GameConst.TEAM_STATE:
            Bukkit.getOnlinePlayers().forEach(this::handlePlayer);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId());

        event.setQuitMessage(null);
        gamer.removeKit(KitType.PRIMARY);
        gamer.removeKit(KitType.SECONDARY);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location spawnLocation = BukkitMain.getInstance().getLocationFromConfig("spawn");
        Player player = event.getPlayer();

        if (AdminMode.getInstance().isAdmin(player)) {
            return;
        }

        Location to = event.getTo();
        double distX = to.getX() - spawnLocation.getX();
        double distZ = to.getZ() - spawnLocation.getZ();

        double distance = (distX * distX) + (distZ * distZ);
        double spawnRadius = RADIUS * RADIUS;

        if (distance > spawnRadius) {
            player.teleport(spawnLocation);
            player.sendMessage("§cVocê está muito longe do spawn!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                          .getMember(event.getPlayer().getUniqueId());

        if (player.isBuildEnabled()) {
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockPlaceEvent event) {
        BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                          .getMember(event.getPlayer().getUniqueId());

        if (player.isBuildEnabled()) {
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
                                                          .getMember(event.getPlayer().getUniqueId());

        if (player.isBuildEnabled()) {
            event.setCancelled(false);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        p.spigot().respawn();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.CUSTOM) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
//		ActionItemStack.unregisterHandler(ActionItemStack.getHandler(kitSecondarySelector));
    }

    private void handlePlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setItem(0, kitSelector.getItemStack());
        player.getInventory().setItem(1, shopSelector.getItemStack());

        player.getInventory().setItem(4, specialKit.getItemStack());
    }
}
