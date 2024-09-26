package br.com.pentamc.competitive.abilities.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import br.com.pentamc.competitive.abilities.Ability;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.event.player.PlayerDamagePlayerEvent;

public class KangarooAbility extends Ability {

	private List<UUID> jumpList;

	public KangarooAbility() {
		super("Kangaroo", Arrays.asList(new ItemBuilder().name("§aKangaroo").type(Material.FIREWORK).build()));
		jumpList = new ArrayList<>();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (hasAbility(player) && event.getAction() != Action.PHYSICAL && isAbilityItem(event.getItem())) {
			event.setCancelled(true);

			if (jumpList.contains(player.getUniqueId()))
				return;

			if (isCooldown(player))
				return;

			Vector vector = player.getEyeLocation().getDirection().multiply(player.isSneaking() ? 2.3F : 0.7f)
					.setY(player.isSneaking() ? 0.5 : 1F);
			player.setFallDistance(-1.0F);
			player.setVelocity(vector);
			jumpList.add(player.getUniqueId());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (jumpList.contains(e.getPlayer().getUniqueId())
				&& (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR
						|| e.getPlayer().isOnGround()))
			jumpList.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(PlayerDamagePlayerEvent event) {
		if (GameGeneral.getInstance().getGameState().isInvenciblity())
			return;

		if (hasAbility(event.getPlayer()))
			if (GameGeneral.getInstance().getGamerController().getGamer(event.getDamager()).isPlaying())
				addCooldown(event.getPlayer(), 7l);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && hasAbility((Player) e.getEntity())
				&& e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getDamage() > 7.0D)
			e.setDamage(7.0D);
	}

}
