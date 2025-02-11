package br.com.pentamc.competitive.listener.register.winner;

import javax.swing.ImageIcon;

import br.com.pentamc.competitive.constructor.Gamer;
import com.google.common.base.Joiner;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.firework.FireworkAPI;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.permission.Group;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class WinnerListener implements Listener {

    private Player[] winner;
    private Location cakeLocation;

    private String winnerName;
    private Location winnerLocation;

    private int time;

    public WinnerListener(Player... winner) {
        this.winner = winner;

        if (this.winner == null) {
            Bukkit.broadcastMessage("§aNenhum jogador ganhou!");

            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> BukkitMain.getInstance().sendPlayerToLobby(player));

                    if (Bukkit.getOnlinePlayers().size() == 0) {
                        Bukkit.shutdown();
                    }
                }
            }.runTaskTimer(GameMain.getInstance(), 60, 10);
            return;
        }

        Player player = Arrays.stream(winner).filter(Objects::nonNull).findFirst().orElse(null);

        if (player == null) {
            Bukkit.shutdown();
            return;
        }

        winnerName = Joiner.on(", ").join(Stream.of(winner).filter(Objects::nonNull).map(Player::getName).toArray());
        winnerLocation = player.getLocation();
        cakeLocation = new Location(winnerLocation.getWorld(), 0, 145, 0);

        int r = 4;
        int rSquared = r * r;

        int cx = (int) cakeLocation.getX();
        int cz = (int) cakeLocation.getZ();
        World w = cakeLocation.getWorld();

        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                    w.getBlockAt(x, (int) cakeLocation.getY(), z).setType(Material.GLASS);
                    w.getBlockAt(x, (int) cakeLocation.getY() + 1, z).setType(Material.CAKE_BLOCK);
                }
            }
        }

        for (Player winnerPlayer : winner) {
            if (winnerPlayer == null) continue;

            winnerPlayer.getInventory().clear();
            winnerPlayer.teleport(cakeLocation.clone().add(0, 3.5, 0));
            winnerPlayer.getInventory().setItem(0, new ItemBuilder().type(Material.MAP).build());
            winnerPlayer.getInventory().setItem(1, new ItemStack(Material.WATER_BUCKET));
            winnerPlayer.updateInventory();

            Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(winnerPlayer);

            gamer.getStatus().addWin();

            Member member = CommonGeneral.getInstance().getMemberManager().getMember(winnerPlayer.getUniqueId());

            member.addXp(StatusType.HG, 230);
            member.addMoney(380);
        }
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        MapView map = event.getMap();

        map.getRenderers().forEach(map::removeRenderer);

        map.addRenderer(new MapRenderer() {

            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                mapCanvas.drawText(38, 6, MinecraftFont.Font, "Parabens,");
                mapCanvas.drawText(30, 15, MinecraftFont.Font, "voce venceu!");
                mapCanvas.drawImage(27, 40, new ImageIcon(GameMain.getInstance().getDataFolder().getPath() + "/cake.png").getImage());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerLoginEvent event) {
        Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

        if (player == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO servidor está sendo finalizado!");
            return;
        }

        if (player.hasGroupPermission(Group.TRIAL)) {
            event.allow();
            return;
        }

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cO servidor está sendo finalizado!");
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateType.SECOND) {
            time++;

            if (time == 15) {
                new BukkitRunnable() {

                    int x = 0;

                    @Override
                    public void run() {
                        x++;

                        if (x == 10) {
                            Bukkit.shutdown();
                            return;
                        }

                        Bukkit.getOnlinePlayers().forEach(player -> BukkitMain.getInstance().sendPlayerToLobby(player));

                        if (Bukkit.getOnlinePlayers().size() == 0) {
                            Bukkit.shutdown();
                        }
                    }
                }.runTaskTimer(GameMain.getInstance(), 0, 5);
                return;
            }

            Player currentOnline = Arrays.stream(winner).filter(Objects::nonNull).findFirst().orElse(null);

            if (currentOnline != null && !currentOnline.isOnline()) {
                winnerLocation = currentOnline.getLocation();
            }

            FireworkAPI.spawn(winnerLocation.add(4, 0, 0), Color.GREEN, Color.GRAY, Type.BURST, true);
            FireworkAPI.spawn(winnerLocation.add(-4, 0, 0), Color.GREEN, Color.GRAY, Type.BURST, true);
            FireworkAPI.spawn(winnerLocation.add(0, 0, 4), Color.GREEN, Color.GRAY, Type.BURST, true);
            FireworkAPI.spawn(winnerLocation.add(0, 0, -4), Color.GREEN, Color.GRAY, Type.BURST, true);

            FireworkAPI.spawn(winnerLocation.add(6, 0, 0), Color.RED, Color.GRAY, Type.BURST, true);
            FireworkAPI.spawn(winnerLocation.add(-6, 0, 0), Color.RED, Color.GRAY, Type.BURST, true);
            FireworkAPI.spawn(winnerLocation.add(0, 0, 6), Color.RED, Color.GRAY, Type.BURST, true);
            FireworkAPI.spawn(winnerLocation.add(0, 0, -6), Color.RED, Color.GRAY, Type.BURST, true);

            Bukkit.broadcastMessage("§a" + winnerName + " ganhou a partida!");
        }
    }
}
