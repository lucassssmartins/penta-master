package br.com.pentamc.bukkit.listener.register;

import br.com.pentamc.bukkit.BukkitMain;
import com.hpfxd.pandaspigot.config.PacketLimiterConfig;
import com.hpfxd.pandaspigot.config.PandaSpigotConfig;
import net.minecraft.server.v1_8_R3.BlockObsidian;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class ObsidianListener implements Listener {

    private Map<UUID, Location> trappedPlayers = new HashMap<>();
    private Map<UUID, Trapped> inTrap = new HashMap<>();

    public void kick(PlayerKickEvent event) {
        String message = event.getLeaveMessage();
        if (message != null && message.toLowerCase().contains("packets")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onquit(PlayerQuitEvent E) {
        inTrap.remove(E.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void performance(PlayerMoveEvent event) {
        Player human = event.getPlayer();
        Trapped trap = inTrap.get(human.getUniqueId());

        if (trap != null) {
            if (trap.cantScape()) {
                if (trap.isScaping(event.getTo())) {
                    event.setTo(trap.getIn());
                    return;
                }
            } else {
                inTrap.remove(human.getUniqueId());
            }
        } else {
            Block block = getSuffocationOB(human);

            if (block != null && isPlayerTrapped(human) && !inTrap.containsKey(human.getUniqueId())) {
                inTrap.put(human.getUniqueId(), new Trapped(event.getFrom()));
            }
        }
    }

    private boolean isPlayerTrapped(Player player) {
        int blocks = 0;
        Location loc = player.getLocation();

        if (loc.clone().subtract(1.0, 0, 0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().add(1.0, 0, 0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().subtract(0.0, 0, 1.0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().add(0.0, 0, 1.0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().subtract(1.0, 1.0, 0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().add(1.0, 1.0, 0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().subtract(0.0, 1.0, 1.0).getBlock().getType().isSolid())
            ++blocks;
        if (loc.clone().add(0.0, 1.0, 1.0).getBlock().getType().isSolid())
            ++blocks;

        return blocks == 4;
    }

    public Block getSuffocationOB(Player player) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        EntityPlayer entityplayer = ((CraftPlayer) player).getHandle();

        for (int i = 0; i < 8; ++i) {
            int j = MathHelper.floor(entityplayer.locY + (double) (((float) ((i >> 0) % 2) - 0.5F) * 0.1F) + (double) entityplayer.getHeadHeight());
            int k = MathHelper.floor(entityplayer.locX + (double) (((float) ((i >> 1) % 2) - 0.5F) * entityplayer.width * 0.8F));
            int l = MathHelper.floor(entityplayer.locZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * entityplayer.width * 0.8F));

            if (blockposition_mutableblockposition.getX() != k || blockposition_mutableblockposition.getY() != j || blockposition_mutableblockposition.getZ() != l) {
                blockposition_mutableblockposition.c(k, j, l);
                net.minecraft.server.v1_8_R3.Block nmsBlock = entityplayer.world.getType(blockposition_mutableblockposition).getBlock();

                if (nmsBlock.getBlockData().getBlock() instanceof BlockObsidian) {
                    return player.getWorld().getBlockAt(blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getY(),
                            blockposition_mutableblockposition.getZ());
                }
            }
        }

        return null;
    }

    public class Trapped {

        private Location loc;

        public Trapped(Location location) {
            loc = location;
        }

        public Location getIn() {
            return loc;
        }

        public boolean isScaping(Location to) {
            return to.getBlockX() != loc.getBlockX() || to.getBlockY() != loc.getBlockY() || to.getBlockZ() != loc.getBlockZ();
        }

        public boolean cantScape() {
            int blocks = 0;

            if (loc.clone().subtract(1.0, 0, 0).getBlock().getType().isSolid())
                ++blocks;
            if (loc.clone().add(1.0, 0, 0).getBlock().getType().isSolid())
                ++blocks;
            if (loc.clone().subtract(0.0, 0, 1.0).getBlock().getType().isSolid())
                ++blocks;
            if (loc.clone().add(0.0, 0, 1.0).getBlock().getType().isSolid())
                ++blocks;

            if (loc.clone().subtract(1.0, 1.0, 0).getBlock().getType().isSolid())
                ++blocks;
            if (loc.clone().add(1.0, 1.0, 0).getBlock().getType().isSolid())
                ++blocks;
            if (loc.clone().subtract(0.0, 1.0, 1.0).getBlock().getType().isSolid())
                ++blocks;
            if (loc.clone().add(0.0, 1.0, 1.0).getBlock().getType().isSolid())
                ++blocks;

            return blocks == 4;
        }
    }
}