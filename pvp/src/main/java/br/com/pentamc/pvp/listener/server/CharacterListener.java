package br.com.pentamc.pvp.listener.server;

import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.character.Character;
import br.com.pentamc.bukkit.api.hologram.Hologram;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.pvp.GameMain;
import br.com.pentamc.pvp.game.Game;
import br.com.pentamc.pvp.game.type.GameType;
import br.com.pentamc.pvp.user.User;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class CharacterListener implements Listener {

    private List<HologramInfo> hologramList;

    public CharacterListener() {
        hologramList = new ArrayList<>();

        createCharacter("§bArena", "_Hyb_", "npc-battle", new Character.Interact() {
            @Override
            public boolean onInteract(Player player, boolean right) {
                User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
                Game game = GameMain.getPlugin().getGameController().getValue(GameType.BATTLE);

                if (game == null) {
                    player.sendMessage("§cO jogo não está disponível no momento!");
                    return false;
                }

                game.load(player);
                user.setGame(game);
                return true;
            }
        }, GameType.BATTLE);

        createCharacter("§bFPS", "ofishsh", "npc-fps", new Character.Interact() {
            @Override
            public boolean onInteract(Player player, boolean right) {
                User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
                Game game = GameMain.getPlugin().getGameController().getValue(GameType.FPS);

                if (game == null) {
                    player.sendMessage("§cO jogo não está disponível no momento!");
                    return false;
                }

                game.load(player);
                user.setGame(game);
                return true;
            }
        }, GameType.FPS);

        createCharacter("§bLava", "MagmaBow", "npc-lava", new Character.Interact() {
            @Override
            public boolean onInteract(Player player, boolean right) {
                User user = GameMain.getPlugin().getUserController().getValue(player.getUniqueId());
                Game game = GameMain.getPlugin().getGameController().getValue(GameType.LAVA);

                if (game == null) {
                    player.sendMessage("§cO jogo não está disponível no momento!");
                    return false;
                }

                game.load(player);
                user.setGame(game);
                return true;
            }
        }, GameType.LAVA);

        for (GameType game : GameMain.getPlugin().getGameController().getStoreMap().keySet()) {
            Character character = new Character("Nepart", BukkitMain.getInstance().getLocationFromConfig("npc-" + game.getWorldName() + "-lobby"), new Character.Interact() {
                @Override
                public boolean onInteract(Player player, boolean right) {
                    BukkitMain.getInstance().sendServer(player, ServerType.LOBBY_PVP);
                    return true;
                }
            });

            Hologram hologram = new CraftHologram("§bVoltar ao Lobby", BukkitMain.getInstance().getLocationFromConfig("npc-" + game.getWorldName() + "-lobby").add(0, 0.25, 0));

            hologram.addLineBelow("§7(Clique aqui)");

            BukkitMain.getInstance().getHologramController().loadHologram(hologram);
        }
    }

    @EventHandler
    public void onServerPlayerJoin(UpdateEvent event) {
        if (event.getType().equals(UpdateEvent.UpdateType.SECOND))
            updateHologram();
    }

    public void createCharacter(String displayName, String skinName, String configName, Character.Interact interact, GameType gameType) {
        new Character(skinName, BukkitMain.getInstance().getLocationFromConfig(configName), interact);

        Hologram hologram = new CraftHologram(displayName, BukkitMain.getInstance().getLocationFromConfig(configName)
                .add(0, 0.25, 0));

        int playerCount = 0;

        if (GameMain.getPlugin().getUserController().getStoreMap() != null && !GameMain.getPlugin().getUserController().getStoreMap().isEmpty())
            playerCount = (int) GameMain.getPlugin().getUserController().getStoreMap().values().stream().filter(users -> users.getGame().getType().equals(gameType)).count();

        Hologram hologramLine = hologram.addLineBelow("§e" + playerCount + " jogando");

        hologramList.add(new HologramInfo(gameType, hologramLine));
        BukkitMain.getInstance().getHologramController().loadHologram(hologram);
    }

    public void updateHologram() {
        if (!hologramList.isEmpty())
            hologramList.forEach(info -> {
                int playerCount = 0;

                if (GameMain.getPlugin().getUserController().getStoreMap() != null && !GameMain.getPlugin().getUserController().getStoreMap().isEmpty())
                    playerCount = (int) GameMain.getPlugin().getUserController().getStoreMap().values().stream().filter(users -> users.getGame().getType().equals(info.gameType)).count();

                info.hologram.setDisplayName("§e" + playerCount + " jogando");
            });
    }

    @AllArgsConstructor
    public static class HologramInfo {

        private GameType gameType;
        private Hologram hologram;
    }
}
