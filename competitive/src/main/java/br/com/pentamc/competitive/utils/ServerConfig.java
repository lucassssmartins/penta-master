package br.com.pentamc.competitive.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.pentamc.competitive.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import br.com.pentamc.competitive.constructor.SimpleKit;
import br.com.pentamc.competitive.kit.Kit;
import br.com.pentamc.competitive.kit.KitType;
import lombok.Data;
import lombok.Getter;
import br.com.pentamc.common.permission.Group;

@Data
public class ServerConfig {

    @Getter
    private static ServerConfig instance = new ServerConfig();

    private SimpleKit defaultSimpleKit = null;

    private Map<KitType, Kit> defaultKit;
    private Map<KitType, List<Kit>> disabledKits;

    private Map<GameState, Map<Integer, String>> commandMap;

    private boolean timeInWaiting = false;

    private boolean spectatorEnabled = true;
    private boolean respawnEnabled = true;
    private boolean joinEnabled;

    private boolean finalBattle = true;
    private boolean forceWin = true;
    private boolean surpriseDisable = true;

    private boolean buildEnabled = true;
    private boolean placeEnabled = true;
    private boolean bucketEnabled = true;
    private Set<Material> materialSet;

    private boolean pvpEnabled = true;
    private boolean damageEnabled = true;

    private Group spectatorGroup;
    private Group respawnGroup;
    private Group kitSpawnGroup;

    public ServerConfig() {
        defaultKit = new HashMap<>();
        disabledKits = new HashMap<>();

        commandMap = new HashMap<>();

        spectatorGroup = Group.VIP;
        respawnGroup = Group.VIP;
        kitSpawnGroup = Group.PENTA;

        materialSet = new HashSet<>();
    }

    public void registerCommand(GameState gameState, Integer time, String command) {
        commandMap.computeIfAbsent(gameState, v -> new HashMap<>()).put(time, command);
    }

    public void execute(GameState gameState, Integer time) {
        if (!commandMap.containsKey(gameState)) {
            return;
        }

        if (!commandMap.get(gameState).containsKey(time)) {
            return;
        }

        String label = commandMap.get(gameState).get(time);

        if (label.contains(";")) {
            for (String command : label.split("; *"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), label);
        }

        commandMap.get(gameState).remove(time);
    }

    public void clearCommands() {
        commandMap.clear();
    }

    public boolean isDisabled(Kit kit, KitType kitType) {
        return disabledKits.computeIfAbsent(kitType, v -> new ArrayList<>()).contains(kit);
    }

    public void disableKit(Kit kit, KitType kitType) {
        if (isDisabled(kit, kitType)) {
            return;
        }

        disabledKits.computeIfAbsent(kitType, v -> new ArrayList<>()).add(kit);
    }

    public void enableKit(Kit kit, KitType kitType) {
        if (!isDisabled(kit, kitType)) {
            return;
        }

        disabledKits.computeIfAbsent(kitType, v -> new ArrayList<>()).remove(kit);
    }

    public boolean hasPrimaryKit() {
        return defaultKit.containsKey(KitType.PRIMARY);
    }

    public boolean hasSecondaryKit() {
        return defaultKit.containsKey(KitType.SECONDARY);
    }

    public boolean hasDefaultSimpleKit() {
        return defaultSimpleKit != null;
    }

    public int getPlayersToStart() {
        return 5;
    }
}
