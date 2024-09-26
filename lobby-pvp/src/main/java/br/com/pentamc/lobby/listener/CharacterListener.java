package br.com.pentamc.lobby.listener;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.character.Character;
import br.com.pentamc.bukkit.api.character.Character.Interact;
import br.com.pentamc.bukkit.api.hologram.Hologram;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.event.server.ServerPlayerJoinEvent;
import br.com.pentamc.bukkit.event.server.ServerPlayerLeaveEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.lobby.LobbyMain;
import br.com.pentamc.lobby.menu.server.HungergamesInventory;
import br.com.pentamc.lobby.menu.server.ServerInventory;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterListener implements Listener {

    private List<HologramInfo> hologramList;

    public CharacterListener() {
        hologramList = new ArrayList<>();

        createCharacter("§bArena", "_Hyb_", "npc-battle", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
                    jedis.setex("route$" + player.getUniqueId().toString(), 5, "battle");
                }

                BukkitMain.getInstance().sendServer(player, ServerType.PVP);
                return true;
            }
        }, "route:battle-count");

        createCharacter("§bFPS", "ofishsh", "npc-fps", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
                    jedis.setex("route$" + player.getUniqueId().toString(), 5, "fps");
                }

                BukkitMain.getInstance().sendServer(player, ServerType.PVP);
                return true;
            }
        }, "route:fps-count");

        createCharacter("§bLava", "MagmaBow", "npc-lava", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
                    jedis.setex("route$" + player.getUniqueId().toString(), 5, "lava");
                }

                BukkitMain.getInstance().sendServer(player, ServerType.PVP);
                return true;
            }
        }, "route:lava-count");
    }

    @EventHandler
    public void updater(UpdateEvent event) {
        if (event.getType() == UpdateEvent.UpdateType.SECOND)
            updateHologram();
    }

    public void createCharacter(String displayName, String skinName, String configName, Interact interact, String route) {
        new Character(skinName, BukkitMain.getInstance().getLocationFromConfig(configName), interact);

        Hologram hologram = new CraftHologram(displayName, BukkitMain.getInstance().getLocationFromConfig(configName).add(0, 0.25, 0));

        int playerCount = 0;

        try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
            if (jedis.exists(route))
                playerCount = Integer.parseInt(jedis.get(route));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Hologram hologramLine = hologram.addLineBelow("§e" + playerCount + " jogando");

        hologramList.add(new HologramInfo(route, hologramLine));
        BukkitMain.getInstance().getHologramController().loadHologram(hologram);
    }

    public void updateHologram() {
        hologramList.forEach(info -> {
            int playerCount = 0;

            try (Jedis jedis = BukkitMain.getInstance().getRedis().getPool().getResource()) {
                if (jedis.exists(info.route))
                    playerCount = Integer.parseInt(jedis.get(info.route));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            info.hologram.setDisplayName("§e" + playerCount + " jogando");
        });
    }

    @AllArgsConstructor
    public static class HologramInfo {

        private String route;
        private Hologram hologram;
    }
}
