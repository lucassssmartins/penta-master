package br.com.pentamc.competitive.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.game.GameState;
import lombok.Getter;
import br.com.pentamc.common.CommonConst;
import br.com.pentamc.bukkit.api.cooldown.CooldownController;
import br.com.pentamc.bukkit.api.cooldown.types.Cooldown;
import br.com.pentamc.common.utils.string.NameUtils;

@Getter
public abstract class Ability implements Listener {

	private List<UUID> myPlayers;

	private String name;

	private List<ItemStack> itemList;

	public Ability(String name, List<ItemStack> itemList) {
		this.myPlayers = new ArrayList<>();

		this.name = name;
		this.itemList = itemList;
	}

	public boolean hasAbility(UUID uuid) {
		return GameGeneral.getInstance().getGamerController().getGamer(uuid).isPlaying() && myPlayers.contains(uuid);
	}

	public boolean hasAbility(Player p) {
		return hasAbility(p.getUniqueId());
	}

	public void registerPlayer(Player player) {
		if (!GameState.isPregame(GameGeneral.getInstance().getGameState()) && myPlayers.size() == 0) {
			Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
		}

		myPlayers.add(player.getUniqueId());
	}

	public void unregisterPlayer(Player player) {
		myPlayers.remove(player.getUniqueId());

		if (!GameState.isPregame(GameGeneral.getInstance().getGameState()) && myPlayers.size() == 0) {
			HandlerList.unregisterAll(this);
		}
	}

	public boolean isAbilityItem(ItemStack item) {
		if (item == null)
			return false;

		for (ItemStack kitItem : itemList) {
			if (kitItem.getType() == item.getType()) {
				if (kitItem.hasItemMeta() && item.hasItemMeta()) {
					if (kitItem.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
						if (item.getItemMeta().getDisplayName().equals(kitItem.getItemMeta().getDisplayName()))
							return true;
					} else if (!kitItem.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
						return true;
				} else if (!kitItem.hasItemMeta() && !item.hasItemMeta())
					return true;
			}
		}

		return false;
	}

	public boolean isItem(ItemStack itemToCheck, ItemStack item) {
		if (item == null)
			return false;

		if (itemToCheck.getType() == item.getType()) {
			if (itemToCheck.hasItemMeta() && item.hasItemMeta()) {
				if (itemToCheck.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
					if (itemToCheck.getItemMeta().getDisplayName().equals(itemToCheck.getItemMeta().getDisplayName()))
						return true;
				} else if (!itemToCheck.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
					return true;
			} else if (!itemToCheck.hasItemMeta() && !item.hasItemMeta())
				return true;

			return false;
		}

		return false;
	}

	public Cooldown getCooldown(Player player) {
		return CooldownController.getInstance().getCooldown(player.getUniqueId(),
				"Kit " + NameUtils.formatString(getName()));
	}

	public boolean isCooldown(Player player) {
		if (CooldownController.getInstance().hasCooldown(player.getUniqueId(),
				"Kit " + NameUtils.formatString(getName()))) {

			Cooldown cooldown = CooldownController.getInstance().getCooldown(player.getUniqueId(),
					"Kit " + NameUtils.formatString(getName()));

			if (cooldown == null)
				return false;

			String message = "§cAguarde " + CommonConst.DECIMAL_FORMAT.format(cooldown.getRemaining())
					+ "s para usar o Kit " + NameUtils.formatString(getName()) + " novamente!";

			player.sendMessage(message);
			return true;
		}

		return false;
	}

	public void removeCooldown(Player player) {
		CooldownController.getInstance().removeCooldown(player, "Kit " + NameUtils.formatString(getName()));
	}
	
	public boolean isCooldownSilent(Player player) {
		if (CooldownController.getInstance().hasCooldown(player.getUniqueId(),
				"Kit " + NameUtils.formatString(getName()))) {

			Cooldown cooldown = CooldownController.getInstance().getCooldown(player.getUniqueId(),
					"Kit " + NameUtils.formatString(getName()));

			if (cooldown == null)
				return false;

			String message = "§cAguarde " + CommonConst.DECIMAL_FORMAT.format(cooldown.getRemaining())
					+ "s para usar o Kit " + NameUtils.formatString(getName()) + " novamente!";

			player.sendMessage(message);
			return true;
		}

		return false;
	}

	public void addCooldown(Player player, long time) {
		CooldownController.getInstance().addCooldown(player.getUniqueId(), getName(), time);
	}

	public void addCooldown(UUID uniqueId, long time) {
		CooldownController.getInstance().addCooldown(uniqueId, getName(), time);
	}

}
