package br.com.pentamc.bukkit.menu.account;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.combat.CombatStatus;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.common.account.status.types.game.pvp.PvPStatus;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class StatusInventory extends MenuInventory {

	protected final Member member;
	protected final StatsCategory category;

	public StatusInventory(Player human, Member member, StatsCategory category) {
		super("Suas estatísticas", 3);

		this.member = member;
		this.category = category;

		handle(human);
		createAndOpenInventory(human);
	}

	private void handle(Player human) {
		clear();

		switch (category) {
			case PRINCIPAL: {
				PvPStatus pvpStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(member.getUniqueId(), StatusType.PVP, PvPStatus.class);
				GameStatus competitiveStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(member.getUniqueId(), StatusType.HG, GameStatus.class);

				setItem(10, new ItemBuilder()
						.type(Material.IRON_CHESTPLATE)
						.name("§aPvP")
						.lore(
								"§7Arena: Kills: §a" + pvpStatus.getBattle().getKills(),
								"§7Arena: Deaths: §a" + pvpStatus.getBattle().getDeaths(),
								"§7Arena: Streak: §a" + pvpStatus.getBattle().getActualStreak(),
								"§7Arena: Max-Streak: §a" + pvpStatus.getBattle().getMaxStreak(),
								"",
								"§7FPS: Kills: §a" + pvpStatus.getFps().getKills(),
								"§7FPS: Deaths: §a" + pvpStatus.getFps().getDeaths(),
								"§7FPS: Streak: §a" + pvpStatus.getFps().getActualStreak(),
								"§7FPS: Max-Streak: §a" + pvpStatus.getFps().getMaxStreak(),
								"",
								"§7Coins: §6" + pvpStatus.getCoins()
						).build(), (player, inv, type, stack, slot) -> false);

				setItem(11, new ItemBuilder()
						.type(Material.MUSHROOM_SOUP)
						.name("§aCompetitivo")
						.lore(
								"§7Wins: §a" + competitiveStatus.getWins(),
								"§7Kills: §a" + competitiveStatus.getKills(),
								"§7Deaths: §a" + competitiveStatus.getDeaths(),
								"",
								"§7Liga: " + competitiveStatus.getLeague().getColor() + competitiveStatus.getLeague().getName(),
								"§7XP: §b" + competitiveStatus.getXp(),
								"",
								"§7Coins: §6" + pvpStatus.getCoins()
						).build(), (player, inv, type, stack, slot) -> false);

				setItem(12, new ItemBuilder()
								.type(Material.BLAZE_ROD)
								.name("§aDuels")
								.lore(
										"§eClique para ver mais informações."
								).build(),
						(player, inv, type, stack, slot) -> {
							new StatusInventory(human, member, StatsCategory.DUELS);
							return false;
						});

				break;
			}

			case DUELS: {
				CombatStatus gladiatorStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(member.getUniqueId(), StatusType.GLADIATOR, CombatStatus.class);
				GameStatus shadowStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(member.getUniqueId(), StatusType.SHADOW, GameStatus.class);

				setItem(10, new ItemBuilder()
						.type(Material.IRON_FENCE)
						.name("§aGladiator")
						.lore(
								"§8Você jogou " + gladiatorStatus.getGames() + " partidas.",
								"",
								"§7Wins: §a" + gladiatorStatus.getWins(),
								"§7Kills: §a" + gladiatorStatus.getKills(),
								"§7Deaths: §a" + gladiatorStatus.getDeaths(),
								"",
								"§7Streak: §a" + gladiatorStatus.getWinStreak(),
								"§7Max-Streak: §a" + gladiatorStatus.getMaxStreak(),
								"",
								"§7ELO: §6" + gladiatorStatus.getElo()
						).build(), (player, inv, type, stack, slot) -> false);

				setItem(11, new ItemBuilder()
						.type(Material.MUSHROOM_SOUP)
						.name("§a1v1")
						.lore(
								"§8Você jogou " + shadowStatus.getGames() + " partidas.",
								"",
								"§7Wins: §a" + shadowStatus.getWins(),
								"§7Kills: §a" + shadowStatus.getKills(),
								"§7Deaths: §a" + shadowStatus.getDeaths(),
								"",
								"§7Streak: §a" + shadowStatus.getWinStreak(),
								"§7Max-Streak: §a" + shadowStatus.getMaxStreak(),
								"",
								"§7Liga: " + shadowStatus.getLeague().getColor() + shadowStatus.getLeague().getName(),
								"§7XP: §b" + shadowStatus.getXp()
						).build(), (player, inv, type, stack, slot) -> false);

				break;
			}

			default:
				close(human);
		}

		setItem(22, new ItemBuilder()
						.type(Material.ARROW)
						.name("§aVoltar")
						.build(),
				(player, inv, type, stack, slot) -> {
					if (category.equals(StatsCategory.PRINCIPAL)) {
						new AccountInventory(human, member);
					} else {
						new StatusInventory(human, member, StatsCategory.PRINCIPAL);
					}

					handle(human);
					return false;
				});
	}

	public enum StatsCategory {
		PRINCIPAL, DUELS;
	}
}
