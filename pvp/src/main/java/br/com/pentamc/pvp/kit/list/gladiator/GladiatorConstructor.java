package br.com.pentamc.pvp.kit.list.gladiator;

import br.com.pentamc.pvp.GameMain;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GladiatorConstructor {

    private final Player gladiator;
    private final Player opponent;

    private final Location gladiatorLocation;
    private final Location backLocation;

    private final List<Block> gladiatorBlocks;
    private final List<Block> opponentBlocks;

    private int time;

    public GladiatorConstructor(Player gladiator, Player opponent) {
        this.gladiator = gladiator;
        this.opponent = opponent;

        this.gladiatorBlocks = new ArrayList<>();
        this.opponentBlocks = new ArrayList<>();

        this.gladiatorLocation = gladiator.getLocation();
        this.backLocation = gladiator.getLocation();

        setupGladiatorArena();
    }

    private void setupGladiatorArena() {
        Location[] locations = GameMain.getPlugin().getGladiatorController().createGladiator(gladiatorBlocks, gladiatorLocation);

        Location gladiatorSpawn = locations[0].clone();

        gladiatorSpawn.setYaw(135.0F);

        gladiator.teleport(gladiatorSpawn);
        gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));

        Location opponentSpawn = locations[1].clone();

        opponentSpawn.setYaw(315.0F);

        opponent.teleport(opponentSpawn);
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));

        opponent.damage(1, gladiator);
        gladiator.damage(1, opponent);
    }

    public void handleEscape(boolean teleportBack) {
        clearGladiator();

        if (teleportBack)
            teleportBack();

        gladiator.removePotionEffect(PotionEffectType.WITHER);
        opponent.removePotionEffect(PotionEffectType.WITHER);
        GameMain.getPlugin().getGladiatorController().removeGladiator(this);
    }

    public void handleWin(Player death) {
        Player winner = (death == gladiator ? opponent : gladiator);

        clearGladiator();

        winner.teleport(backLocation);
        winner.removePotionEffect(PotionEffectType.WITHER);
        winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));

        GameMain.getPlugin().getGladiatorController().removeGladiator(this);
    }

    public void handleFinish() {
        clearGladiator();
        teleportBack();

        if (gladiator.isOnline())
            gladiator.removePotionEffect(PotionEffectType.WITHER);

        if (opponent.isOnline())
            opponent.removePotionEffect(PotionEffectType.WITHER);

        GameMain.getPlugin().getGladiatorController().removeGladiator(this);
    }

    public void pulse() {
        time++;

        if (time == 10) {
            for (Block block : gladiatorBlocks) {
                if (block.hasMetadata("gladiatorBreakable")) {
                    block.setType(Material.AIR);
                }
            }
        }

        if (time == 120) {
            gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 3));
            opponent.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 3));
        }

        if (time == 180) {
            handleFinish();
        }
    }

    public void addBlock(Block block) {
        if (!opponentBlocks.contains(block))
            opponentBlocks.add(block);
    }

    public boolean removeBlock(Block block) {
        return opponentBlocks.remove(block);
    }

    private void clearGladiator() {
        for (Block block : gladiatorBlocks) {
            block.setType(Material.AIR);

            if (GameMain.getPlugin().getGladiatorController().getBlockList().contains(block))
                GameMain.getPlugin().getGladiatorController().getBlockList().remove(block);
        }

        for (Block block : opponentBlocks) {
            block.setType(Material.AIR);
            GameMain.getPlugin().getGladiatorController().getBlockList().remove(block);
        }
    }

    private void teleportBack() {
        gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));
        gladiator.teleport(backLocation);

        opponent.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));
        opponent.teleport(backLocation);
    }

    public boolean isInGladiator(Player player) {
        return player == opponent || player == gladiator;
    }
}