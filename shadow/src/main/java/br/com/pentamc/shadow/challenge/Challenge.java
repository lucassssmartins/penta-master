package br.com.pentamc.shadow.challenge;

import br.com.pentamc.shadow.event.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.vanish.AdminMode;
import br.com.pentamc.bukkit.api.vanish.VanishAPI;
import br.com.pentamc.shadow.event.GladiatorSpectatorEvent.Action;

import java.util.*;
import java.util.stream.Stream;

@Getter
public class Challenge {

    private Player player;
    private Player enimy;

    private Set<Player> spectatorSet;
    private List<Block> playerBlockList;
    private List<Block> blockList;
    private List<Item> itemList;

    @Setter
    private Location mainLocation;

    private long expireTime;
    private int time;
    private int witherTime;

    private boolean ranked;

    public Challenge(Player player, Player enimy, boolean ranked) {
        this.player = player;
        this.enimy = enimy;

        this.ranked = ranked;

        this.spectatorSet = new HashSet<>();
        this.playerBlockList = new ArrayList<>();
        this.blockList = new ArrayList<>();
        this.itemList = new ArrayList<>();
        this.expireTime = System.currentTimeMillis() + 15000L;
    }

    public void spectate(Player player) {
        GladiatorTrySpectatorEvent event = new GladiatorTrySpectatorEvent(player, this);

        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            spectatorSet.add(player);

            player.teleport(mainLocation.clone().add(0, 2, 0));
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setHealth(20D);

            Bukkit.getPluginManager().callEvent(new GladiatorSpectatorEvent(player, this, Action.JOIN));

            if (!AdminMode.getInstance().isAdmin(player)) {
                Stream.concat(spectatorSet.stream(), Arrays.asList(this.player, enimy).stream())
                      .forEach(p -> p.sendMessage("§7" + player.getName() + " está assistindo!"));
            }
        }
    }

    public void removeSpectator(Player player) {
        Stream.concat(spectatorSet.stream(), Arrays.asList(this.player, enimy).stream())
              .forEach(p -> p.sendMessage("§7" + player.getName() + " não está mais assistindo!"));
        spectatorSet.remove(player);
        Bukkit.getPluginManager().callEvent(new GladiatorSpectatorEvent(player, this, Action.LEAVE));
    }

    /**
     * Start the Gladiator
     */

    public void start() {
        GladiatorStartEvent event = new GladiatorStartEvent(this);

        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            handleInventory(player);
            handleInventory(enimy);

            VanishAPI.getInstance().hideAllPlayers(player);
            VanishAPI.getInstance().hideAllPlayers(enimy);

            player.showPlayer(enimy);
            enimy.showPlayer(player);
        }
    }

    /**
     * Finish the Gladiator
     *
     * @param loser
     */

    public void finish(Player loser) {
        Bukkit.getPluginManager().callEvent(new GladiatorFinishEvent(this, loser, loser == enimy ? player : enimy));
        getSpectatorSet().forEach(
                player -> Bukkit.getPluginManager().callEvent(new GladiatorSpectatorEvent(player, this, Action.LEAVE)));

        VanishAPI.getInstance().getHideAllPlayers().remove(getPlayer().getUniqueId());
        VanishAPI.getInstance().updateVanishToPlayer(getPlayer());

        VanishAPI.getInstance().getHideAllPlayers().remove(getEnimy().getUniqueId());
        VanishAPI.getInstance().updateVanishToPlayer(getEnimy());
    }

    public void pulse() {
        time++;

        if (time % 180 == 0) {
            itemList.removeIf(item -> item.isDead());

            for (Item item : player.getWorld().getEntitiesByClass(Item.class))
                if (itemList.contains(item)) {
                    item.remove();
                    itemList.remove(item);
                }

            Bukkit.getPluginManager().callEvent(new GladiatorClearEvent(this));
        }

        Bukkit.getPluginManager().callEvent(new GladiatorPulseEvent(this));
    }

    public void addItem(Item item) {
        this.itemList.add(item);
    }

    public void removeItem(Item item) {
        this.itemList.remove(item);
    }

    public void addBlock(Block block) {
        playerBlockList.add(block);
    }

    public void removeBlock(Block block) {
        playerBlockList.remove(block);
    }

    public boolean hasExpired() {
        return expireTime < System.currentTimeMillis();
    }

    public boolean isInFight(Player target) {
        return player == target || enimy == target;
    }

    public boolean isPlayerBlock(Block block) {
        return playerBlockList.contains(block);
    }

    public void handleInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.getInventory().setHeldItemSlot(0);
        player.getInventory().setItem(0, new ItemBuilder().type(Material.DIAMOND_SWORD)
                .enchantment(Enchantment.DAMAGE_ALL).build());

        for (int x = 0; x < 8; x++)
            player.getInventory().setItem(1 + x, new ItemStack(Material.MUSHROOM_SOUP));

        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        player.updateInventory();
    }
}
