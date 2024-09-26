package br.com.pentamc.lobby.listener;

import br.com.pentamc.bukkit.api.cooldown.CooldownController;
import br.com.pentamc.bukkit.menu.account.AccountInventory;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.bukkit.api.item.ActionItemStack;
import br.com.pentamc.bukkit.api.item.ActionItemStack.ActionType;
import br.com.pentamc.bukkit.api.item.ActionItemStack.InteractType;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import br.com.pentamc.lobby.menu.server.LobbyInventory;
import br.com.pentamc.lobby.menu.server.ServerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    private ActionItemStack compass;
    private ActionItemStack lobbies;
    private ActionItemStack collectable;

    private final Cache<UUID, Long> cooldown = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).build();

    public PlayerListener() {
        compass = new ActionItemStack(new ItemBuilder().name("§aJogos").type(Material.COMPASS).build(),
                                      new ActionItemStack.Interact(InteractType.CLICK) {

                                          @Override
                                          public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                                              new ServerInventory(player);
                                              return false;
                                          }
                                      });

        lobbies = new ActionItemStack(new ItemBuilder().name("§aLobbies").type(Material.NETHER_STAR).build(),
                                      new ActionItemStack.Interact(InteractType.CLICK) {

                                          @Override
                                          public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                                              new LobbyInventory(player);
                                              return false;
                                          }
                                      });

        collectable = new ActionItemStack(new ItemBuilder().name("§aColecionáveis").type(Material.CHEST).build(),
                                          new ActionItemStack.Interact(InteractType.CLICK) {

                                              @Override
                                              public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                                                  player.sendMessage("§cEm breve!");
                                                  return false;
                                              }
                                          });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

        player.getWorld().getPlayers().forEach(players -> {
            if (member.getAccountConfiguration().isSeeingPlayers())
                player.showPlayer(players);
            else
                player.hidePlayer(players);
        });

        addItem(player, member);
    }

    @EventHandler
    public void onPlayerChangeLoginStatus(PlayerChangeLoginStatusEvent event) {
        if (event.isLogged() || event.getMember().getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.ORIGINAL) {
            event.getPlayer().teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
            addItem(event.getPlayer(), event.getMember());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        addItem(event.getPlayer(),
                CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()));
        event.setRespawnLocation(BukkitMain.getInstance().getLocationFromConfig("spawn"));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().toString().contains("RIGHT_")) {
            Player player = event.getPlayer();
            Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

            if (member != null) {
                ItemStack stack = event.getItem();

                if (stack.getType().equals(Material.INK_SACK)) {
                    ItemMeta meta = stack.getItemMeta();

                    if (cooldown.asMap().containsKey(player.getUniqueId())) {
                        player.sendMessage("§cAguarde " + TimeUnit.MILLISECONDS.toSeconds(cooldown.getIfPresent(player.getUniqueId()) - System.currentTimeMillis()) + " segundos para usar novamente.");
                        return;
                    }

                    if (meta.getDisplayName().contains("Visíveis")) {
                        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 3000);

                        member.getAccountConfiguration().setSeeingPlayers(false);
                        player.getWorld().getPlayers().forEach(player::hidePlayer);
                        player.updateInventory();

                        player.setItemInHand(
                                new ItemBuilder()
                                        .type(Material.INK_SACK)
                                        .durability(member.getAccountConfiguration().isSeeingPlayers() ? 10 : 8)
                                        .name("§fJogadores: §cInvisíveis")
                                        .build()
                        );

                        player.sendMessage("§cVisibilidade dos jogadores desativada!");
                    } else if (meta.getDisplayName().contains("Invisíveis")) {
                        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + 3000);

                        member.getAccountConfiguration().setSeeingPlayers(true);
                        player.getWorld().getPlayers().forEach(player::showPlayer);
                        player.updateInventory();

                        player.setItemInHand(
                                new ItemBuilder()
                                        .type(Material.INK_SACK)
                                        .durability(member.getAccountConfiguration().isSeeingPlayers() ? 10 : 8)
                                        .name("§fJogadores: §aVisíveis")
                                        .build()
                        );

                        player.sendMessage("§aVisibilidade dos jogadores ativada!");
                    }
                }
            }
        }
    }

    public void addItem(Player player, Member member) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        player.setHealth(20D);
        player.setFoodLevel(20);

        player.getInventory().setItem(0, compass.getItemStack());
        player.getInventory().setItem(1, new ActionItemStack(
                new ItemBuilder().name("§aMeu perfil").skin(member.getPlayerName()).durability(3)
                        .type(Material.SKULL_ITEM).build(), new ActionItemStack.Interact(InteractType.CLICK) {

            @Override
            public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {
                new AccountInventory(player, member);
                return false;
            }
        }).getItemStack());
        player.getInventory().setItem(4, collectable.getItemStack());

        player.getInventory().setItem(7,
                new ItemBuilder()
                        .type(Material.INK_SACK)
                        .durability(member.getAccountConfiguration().isSeeingPlayers() ? 10 : 8)
                        .name("§fJogadores: " + (member.getAccountConfiguration().isSeeingPlayers() ? "§aVisíveis" : "§cInvisíveis"))
                        .build()
        );

        player.getInventory().setItem(8, lobbies.getItemStack());
        player.updateInventory();
    }
}
