package br.com.pentamc.competitive.listener.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public final class DamageListener extends GameListener {

    public static final HashMap<Material, Double> damageMaterial = new HashMap<>();

    public DamageListener() {

        damageMaterial.put(Material.DIAMOND_SWORD, 6.0D);
        damageMaterial.put(Material.IRON_SWORD, 5.0D);
        damageMaterial.put(Material.STONE_SWORD, 4.0D);
        damageMaterial.put(Material.WOOD_SWORD, 3.0D);
        damageMaterial.put(Material.GOLD_SWORD, 3.0D);

        damageMaterial.put(Material.DIAMOND_AXE, 5.0D);
        damageMaterial.put(Material.IRON_AXE, 4.0D);
        damageMaterial.put(Material.STONE_AXE, 3.0D);
        damageMaterial.put(Material.WOOD_AXE, 2.0D);
        damageMaterial.put(Material.GOLD_AXE, 2.0D);

        damageMaterial.put(Material.DIAMOND_PICKAXE, 4.0D);
        damageMaterial.put(Material.IRON_PICKAXE, 3.0D);
        damageMaterial.put(Material.STONE_PICKAXE, 2.0D);
        damageMaterial.put(Material.WOOD_PICKAXE, 1.0D);
        damageMaterial.put(Material.GOLD_PICKAXE, 1.0D);

        for (Material material : Material.values()) {
            if (damageMaterial.containsKey(material))
                continue;

            damageMaterial.put(material, 1.0D);
        }
    }

    @EventHandler
    public void lava(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause().equals(DamageCause.LAVA))
                event.setDamage(4.0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void hit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        Player player = (Player) event.getDamager();

        double damage = 1.0D;

        ItemStack itemStack = player.getItemInHand();

        if (itemStack != null) {
            damage = damageMaterial.get(itemStack.getType());

            if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                damage += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            }
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                int amplifier = effect.getAmplifier() + 1;

                damage += (amplifier * 2);
            } else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
                damage -= (effect.getAmplifier() + 1);
            }
        }

        event.setDamage(damage - 2.0);
    }
}