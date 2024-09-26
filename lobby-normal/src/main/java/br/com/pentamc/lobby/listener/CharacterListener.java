package br.com.pentamc.lobby.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.lobby.LobbyMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.AllArgsConstructor;
import br.com.pentamc.bukkit.api.character.Character;
import br.com.pentamc.bukkit.api.character.Character.Interact;
import br.com.pentamc.bukkit.api.hologram.Hologram;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.event.server.ServerPlayerJoinEvent;
import br.com.pentamc.bukkit.event.server.ServerPlayerLeaveEvent;
import br.com.pentamc.lobby.menu.server.HungergamesInventory;
import br.com.pentamc.lobby.menu.server.ServerInventory;

public class CharacterListener implements Listener {

    private List<HologramInfo> hologramList;

    public CharacterListener() {
        hologramList = new ArrayList<>();

        createCharacter("§bCompetitivo", "coquetel", "npc-competitive", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {

                if (ServerInventory.LOBBY_HG) {
                    BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_HG);
                } else {
                    if (right) {
                        BukkitMain.getInstance().sendServer(player, ServerType.HUNGERGAMES);
                    } else {
                        new HungergamesInventory(player);
                    }
                }

                return false;
            }
        }, ServerType.HUNGERGAMES, ServerType.LOBBY_HG);

        createCharacter("§bDuels", "gabriaum", "npc-duels", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_DUELS);
                return false;
            }
        }, ServerType.LOBBY_DUELS, ServerType.SIMULATOR, ServerType.GLADIATOR, ServerType.ONEXONE);

        createCharacter("§bPvP", "REVOGADO", "npc-pvp", new Interact() {

            @Override
            public boolean onInteract(Player player, boolean right) {
                BukkitMain.getInstance().sendServer(player, ServerType.PVP);
                return false;
            }
        }, ServerType.PVP);
    }

    @EventHandler
    public void onServerPlayerJoin(ServerPlayerJoinEvent event) {
        updateHologram(event.getServerType());
    }

    @EventHandler
    public void onServerPlayerJoin(ServerPlayerLeaveEvent event) {
        updateHologram(event.getServerType());
    }

    public void createCharacter(String displayName, String skinName, String configName, Interact interact, ServerType... serverType) {
        new Character(skinName, BukkitMain.getInstance().getLocationFromConfig(configName), interact);

        Hologram hologram = new CraftHologram(displayName, BukkitMain.getInstance().getLocationFromConfig(configName)
                                                                     .add(0, 0.25, 0));

        int playerCount = 0;

        for (int integer : Arrays.stream(serverType)
                                 .map(sT -> BukkitMain.getInstance().getServerManager().getBalancer(sT)
                                                      .getTotalNumber()).collect(Collectors.toList())) {
            playerCount += integer;
        }

        Hologram hologramLine = hologram.addLineBelow(!Arrays.stream(serverType)
                                                             .map(sT -> BukkitMain.getInstance().getServerManager()
                                                                                  .getBalancer(sT).getTotalNumber())
                                                             .findAny().isPresent() ? "§cNenhum servidor disponível!" :
                                                      "§e" + playerCount + " jogando");

        hologramList.add(new HologramInfo(Arrays.asList(serverType), hologramLine));
        BukkitMain.getInstance().getHologramController().loadHologram(hologram);
    }

    public void updateHologram(ServerType type) {
        HologramInfo entry = hologramList.stream().filter(info -> info.typeList.contains(type)).findFirst()
                                         .orElse(null);

        if (entry != null) {
            if (BukkitMain.getInstance().getServerManager().getBalancer(type).getList().isEmpty()) {
                entry.hologram.setDisplayName("§cNenhum servidor disponível!");
            } else {
                int playerCount = 0;

                for (int integer : entry.typeList.stream().map(serverType -> BukkitMain.getInstance().getServerManager()
                                                                                       .getBalancer(serverType)
                                                                                       .getTotalNumber())
                                                 .collect(Collectors.toList()))
                    playerCount += integer;

                entry.hologram.setDisplayName("§e" + playerCount + " jogando");
            }
        }
    }

    private void sendPlayer(Player player, String string) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(string);
        player.sendPluginMessage(LobbyMain.getInstance(), "BungeeCord", out.toByteArray());
        player.closeInventory();
    }

    @AllArgsConstructor
    public class HologramInfo {

        private List<ServerType> typeList;
        private Hologram hologram;
    }
}
