package br.com.pentamc.pvp.controller;

import br.com.pentamc.pvp.kit.list.gladiator.GladiatorConstructor;
import br.com.pentamc.pvp.listener.server.gladiator.GladiatorListener;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GladiatorController {

    private int radius = 8;
    private int height = 12;

    private Map<Player, GladiatorConstructor> playerList;
    private List<GladiatorConstructor> gladiatorList;
    private List<Block> blockList;

    private GladiatorListener listener = new GladiatorListener();

    public GladiatorController() {
        playerList = new HashMap<>();
        gladiatorList = new ArrayList<>();
        blockList = new ArrayList<>();
    }

    public Location[] createGladiator(List<Block> blockList, Location gladiatorLocation) {
        Location loc = gladiatorLocation;
        boolean hasGladi = true;

        while (hasGladi) {
            hasGladi = false;
            boolean stop = false;
            for (double x = -8.0D; x <= 8.0D; x += 1.0D) {
                for (double z = -8.0D; z <= 8.0D; z += 1.0D) {
                    for (double y = 0.0D; y <= 10.0D; y += 1.0D) {
                        Location l = new Location(loc.getWorld(), loc.getX() + x, 190 + y, loc.getZ() + z);
                        if (l.getBlock().getType() != Material.AIR) {
                            hasGladi = true;
                            loc = new Location(loc.getWorld(), loc.getX() + 20.0D, loc.getY(), loc.getZ());
                            stop = true;
                        }
                        if (stop) {
                            break;
                        }
                    }
                    if (stop) {
                        break;
                    }
                }
                if (stop) {
                    break;
                }
            }
        }

        Block mainBlock = loc.getBlock();

        for (double x = -radius; x <= radius; x += 1.0D) {
            for (double z = -radius; z <= radius; z += 1.0D) {
                for (double y = 0.0D; y <= height; y += 1.0D) {
                    Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 190 + y,
                            mainBlock.getZ() + z);
                    l.getBlock().setType(Material.GLASS);
                    blockList.add(l.getBlock());
                    this.blockList.add(l.getBlock());
                }
            }
        }

        for (double x = -radius + 1; x <= radius - 1; x += 1.0D) {
            for (double z = -radius + 1; z <= radius - 1; z += 1.0D) {
                for (double y = 1.0D; y <= height; y += 1.0D) {
                    Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 190 + y,
                            mainBlock.getZ() + z);
                    l.getBlock().setType(Material.AIR);
                    this.blockList.remove(l.getBlock());
                }
            }
        }

        return new Location[] {
                new Location(mainBlock.getWorld(), mainBlock.getX() + 6.5D, 190 + 1.0d, mainBlock.getZ() + 6.5D),
                new Location(mainBlock.getWorld(), mainBlock.getX() - 5.5D, 190 + 1.0d,
                        mainBlock.getZ() - 5.5D) };
    }

    public boolean isInFight(Player player) {
        return playerList.containsKey(player);
    }

    public GladiatorConstructor getGladiator(Player player) {
        return playerList.get(player);
    }

    public boolean isGladiatorBlock(Block block) {
        return blockList.contains(block);
    }

    public void sendGladiator(Player player, Player target) {
        GladiatorConstructor gladiator = new GladiatorConstructor(player, target);

        playerList.put(player, gladiator);
        playerList.put(target, gladiator);
        gladiatorList.add(gladiator);
        listener.register();
    }

    public void removeGladiator(GladiatorConstructor gladiator) {
        playerList.remove(gladiator.getGladiator());
        playerList.remove(gladiator.getOpponent());
        gladiatorList.remove(gladiator);

        if (playerList.isEmpty())
            listener.unregister();
    }
}
