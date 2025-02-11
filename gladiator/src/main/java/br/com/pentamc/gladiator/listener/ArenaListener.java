package br.com.pentamc.gladiator.listener;

import java.util.List;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.gladiator.GameGeneral;
import br.com.pentamc.gladiator.challenge.Challenge;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.gladiator.event.GladiatorClearEvent;
import br.com.pentamc.gladiator.event.GladiatorFinishEvent;
import br.com.pentamc.gladiator.event.GladiatorStartEvent;

public class ArenaListener implements Listener {

	private int index = 0;

	private World gladiatorWorld;

	private int radius = 8;
	private int height = 16;

	public ArenaListener() {
		WorldCreator worldCreator = new WorldCreator("gladiator");

		worldCreator.type(WorldType.FLAT);
		worldCreator.generatorSettings("0;0");
		worldCreator.generateStructures(false);

		gladiatorWorld = BukkitMain.getInstance().getServer().createWorld(worldCreator);
		gladiatorWorld.setGameRuleValue("doDaylightCycle", "false");
		gladiatorWorld.setGameRuleValue("naturalRegeneration", "false");
		gladiatorWorld.setAutoSave(false);
		((CraftWorld) gladiatorWorld).getHandle().savingDisabled = true;
	}

	@EventHandler
	public void onGladiatorStart(GladiatorStartEvent event) {
		Location[] location = handleArena(event.getChallenge().getBlockList());

		Location firstLocation = location[0];
		firstLocation.setYaw(135.0F);

		event.getChallenge().getEnimy().teleport(firstLocation);

		Location secondLocation = location[1];
		secondLocation.setYaw(315.0F);

		event.getChallenge().getPlayer().teleport(secondLocation);

		Location mainLocation = location[2];

		event.getChallenge().setMainLocation(mainLocation);

		if (index++ > 100) {
			index = 0;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGladiatorFinish(GladiatorFinishEvent event) {
		Challenge challenge = event.getChallenge();

		for (Block block : challenge.getBlockList())
			block.setType(Material.AIR);

		for (Block block : challenge.getPlayerBlockList())
			block.setType(Material.AIR);

		handleClear(challenge.getMainLocation(), null);
	}

	@EventHandler
	public void onGladiatorClear(GladiatorClearEvent event) {
		handleClear(event.getChallenge().getMainLocation(), null);
	}

	private Location[] handleArena(List<Block> blockList) {
		Location loc = new Location(gladiatorWorld, 0 + (index * 100), 0, 0 + (index * 100));

		Block mainBlock = loc.getBlock();

		for (double x = -radius; x <= radius; x += 1.0D) {
			for (double z = -radius; z <= radius; z += 1.0D) {
				for (double y = 0.0D; y <= height; y += 1.0D) {
					Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, y, mainBlock.getZ() + z);
					l.getBlock().setType(Material.GLASS);
					blockList.add(l.getBlock());
				}
			}
		}

		handleClear(mainBlock.getLocation(), blockList);

		return new Location[] {
				new Location(mainBlock.getWorld(), mainBlock.getX() + 7.5D, 2.5, mainBlock.getZ() + 7.5D),
				new Location(mainBlock.getWorld(), mainBlock.getX() - 6.5D, 2.5, mainBlock.getZ() - 6.5D),
				mainBlock.getLocation() };
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
			Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(player);

			challenge.addBlock(event.getBlock());
			return;
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (member.isBuildEnabled()) {
			if (member.hasGroupPermission(Group.ADMIN))
				event.setCancelled(false);
		} else
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
			Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(player);

			if (challenge.getBlockList().contains(event.getBlock())) {
				event.setCancelled(true);
				return;
			}

			if (challenge.isPlayerBlock(event.getBlock()) || event.getBlock().getType() == Material.COBBLESTONE
					|| event.getBlock().getType() == Material.OBSIDIAN) {
				event.setCancelled(false);
				challenge.removeBlock(event.getBlock());
			} else {
				event.setCancelled(true);
			}

			return;
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (member.isBuildEnabled()) {
			if (member.hasGroupPermission(Group.ADMIN))
				event.setCancelled(false);
		} else
			event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockDamageEvent event) {
		Player player = event.getPlayer();

		if (GameGeneral.getInstance().getChallengeController().containsKey(player)) {
			Challenge challenge = GameGeneral.getInstance().getChallengeController().getValue(player);

			if (challenge.getBlockList().contains(event.getBlock())) {
				Block block = event.getBlock();

				player.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte) 0);
				return;
			}
		}
	}

	public void handleClear(Location mainLocation, List<Block> blockList) {
		for (double x = -radius + 1; x <= radius - 1; x += 1.0D) {
			for (double z = -radius + 1; z <= radius - 1; z += 1.0D) {
				for (double y = 1.0D; y < height; y += 1.0D) {
					Location l = new Location(mainLocation.getWorld(), mainLocation.getX() + x, y,
							mainLocation.getZ() + z);
					l.getBlock().setType(Material.AIR);

					if (blockList != null)
						blockList.remove(l.getBlock());
				}
			}
		}
	}

}
