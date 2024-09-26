package br.com.pentamc.lobby;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.League;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.account.status.StatusType;
import br.com.pentamc.common.account.status.types.game.GameStatus;
import br.com.pentamc.lobby.listener.CharacterListener;
import br.com.pentamc.lobby.listener.ScoreboardListener;
import lombok.Getter;
import br.com.pentamc.bukkit.api.hologram.impl.CraftHologram;
import br.com.pentamc.bukkit.api.hologram.impl.TopRanking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LobbyMain extends LobbyPlatform {

	@Getter
	private static LobbyMain instance;

	@Override
	public void onLoad() {
		instance = this;

		super.onLoad();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		getServer().getPluginManager().registerEvents(new CharacterListener(), this);
		getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);

		new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lWINS",
				BukkitMain.getInstance().getLocationFromConfig("topranking-hologram-wins")),
				() -> {

					List<TopRanking.RankingModel<GameStatus>> list = new ArrayList<>();
					Collection<GameStatus> ranking = CommonGeneral.getInstance().getStatusData()
							.ranking(StatusType.HG, "wins",
									GameStatus.class);

					for (GameStatus wins : ranking) {
						Member member = CommonGeneral.getInstance().getPlayerData()
								.loadMember(wins.getUniqueId(), MemberVoid.class);

						list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(),
								member.getServerGroup()));
					}

					return list;
				}, (model, position) -> "§e" + position + ". " + (model == null ? "§7Ninguém" :
				model.getGroup().getColor() +
						model.getPlayerName()) + " §7- §e" +
				(model == null ? 0 : model.getStatus().getWins()));

		new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lRANKING", BukkitMain.getInstance().getLocationFromConfig(
				"topranking-hologram-ranking")), () -> {
			List<TopRanking.RankingModel<GameStatus>> list = new ArrayList<>();
			Collection<GameStatus> ranking = CommonGeneral.getInstance().getStatusData()
					.ranking(StatusType.HG, "xp",
							GameStatus.class);

			for (GameStatus wins : ranking) {
				Member member = CommonGeneral.getInstance().getPlayerData()
						.loadMember(wins.getUniqueId(), MemberVoid.class);

				list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(),
						member.getServerGroup()));
			}

			return list;
		}, (model, position) -> "§e" + position + ". " + (model == null ? "§7Ninguém" :
				model.getGroup().getColor() +
						model.getPlayerName()) + " §7- §e" +
				(model == null ? League.values()[0] : model.getStatus().getLeague()).getColor() +
				(model == null ? League.values()[0] : model.getStatus().getLeague()).getName());

		new TopRanking<>(new CraftHologram("§b§lTOP 100 §e§lKILLS", BukkitMain.getInstance().getLocationFromConfig(
				"topranking-hologram-kills")), () -> {

			List<TopRanking.RankingModel<GameStatus>> list = new ArrayList<>();
			Collection<GameStatus> ranking = CommonGeneral.getInstance().getStatusData()
					.ranking(StatusType.HG, "kills",
							GameStatus.class);

			for (GameStatus wins : ranking) {
				Member member = CommonGeneral.getInstance().getPlayerData()
						.loadMember(wins.getUniqueId(), MemberVoid.class);

				list.add(new TopRanking.RankingModel<>(wins, member.getPlayerName(), member.getServerGroup()));
			}

			return list;
		}, (model, position) -> "§e" + position + ". " +
				(model == null ? "§7Ninguém" : model.getGroup().getColor() + model.getPlayerName()) +
				" §7- §e" + (model == null ? 0 : model.getStatus().getKills()));
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
