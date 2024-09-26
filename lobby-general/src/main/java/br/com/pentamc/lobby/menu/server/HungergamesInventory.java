package br.com.pentamc.lobby.menu.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.server.ServerType;
import br.com.pentamc.common.server.loadbalancer.server.HungerGamesServer;
import br.com.pentamc.common.server.loadbalancer.server.ProxiedServer;
import br.com.pentamc.common.utils.string.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.api.item.ItemBuilder;
import br.com.pentamc.bukkit.api.menu.MenuInventory;
import br.com.pentamc.bukkit.api.menu.MenuUpdateHandler;
import br.com.pentamc.lobby.menu.server.ServerInventory.SendClick;

public class HungergamesInventory {

	public HungergamesInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		List<ProxiedServer> serverList = getInitialProps(member);

		MenuInventory menu = new MenuInventory("Salas",
				2 + (serverList.size() == 0 ? 1 : (serverList.size() / 7) + 1));

		serverList.sort(new Comparator<ProxiedServer>() {

			@Override
			public int compare(ProxiedServer o1, ProxiedServer o2) {
				HungerGamesServer server = (HungerGamesServer) o1;
				HungerGamesServer server2 = (HungerGamesServer) o2;

				int object = Boolean.valueOf(server.getState().isPregame()).compareTo(server2.getState().isPregame());

				if (object != 0)
					return object;

				object = server.getState().compareTo(server2.getState());

				if (object != 0)
					return object;

				object = Integer.valueOf(o1.getOnlinePlayers()).compareTo(o2.getOnlinePlayers());

				if (object != 0)
					return object;

				object = Integer.valueOf(server.getTime()).compareTo(server2.getTime());

				if (object != 0)
					return object;

				return o1.getServerId().compareTo(o2.getServerId());
			}

		});

		create(serverList, member, menu, 0);

		menu.setUpdateHandler(new MenuUpdateHandler() {

			int times = 0;

			@Override
			public void onUpdate(Player player, MenuInventory menu) {
				create(serverList, member, menu, times++);
			}

		});

		menu.open(player);

	}

	public void create(List<ProxiedServer> serverList, Member member, MenuInventory menu, int times) {
		if (serverList.isEmpty()) {
			menu.setItem(13, new ItemBuilder().name("§cNenhum servidor disponivel no momento!").type(Material.BARRIER).build());
		} else {
			int w = 13;

			for (ProxiedServer proxiedServer : serverList) {
				HungerGamesServer server = (HungerGamesServer) proxiedServer;

				if (server.getTime() <= 0)
					continue;

				ItemBuilder builder = new ItemBuilder();
				builder.type(Material.STAINED_GLASS_PANE);

				String name = "Competitivo";
				String nameId = "§a" + name;
				String lore = null;

				switch (server.getState()) {
					case WAITING:
					case PREGAME: {
						lore = "\n§cNão há um competitivo em andamento.";
						builder.durability(14);
						break;
					}

					case STARTING: {
						lore = "\n§aO comp iniciará em %time%\n§8%online% jogando.";
						builder.durability(5);
						break;
					}

					case INVINCIBILITY: {
						nameId = "§c" + name;
						lore = "\n§cA invencibilidade acabará em %time%\n§8%online% jogando.";
						builder.durability(4);
						break;
					}

					case GAMETIME: {
						nameId = "§c" + name;
						lore = "\n§cO comp está em andamento há %time%\n§8%online% jogando.";
						builder.durability(4);
						break;
					}

					case NONE: {
						if (!member.hasGroupPermission(Group.TRIAL))
							continue;

						nameId = "§4" + name;
						lore = "\n§cO servidor está sendo carregado\n Tempo passado: 0s\n Tempo previsto: 1m 32s";
						builder.durability(4);
						break;
					}

					default: {
						nameId = "§c" + name;
						lore = "\n§cO comp está em andamento há %time%\n§8%online% jogando.";
						builder.durability(4);
						break;
					}
				}

				lore = lore.replace("%time%", StringUtils.format(server.getTime())).replace("%online%",
						"" + server.getOnlinePlayers());

				builder.amount(server.getOnlinePlayers());
				builder.name(nameId);
				builder.lore(lore);

				if (w % 9 == 8) {
					w += 2;
				}

				menu.setItem(w, builder.build(), new SendClick(server.getServerId()));
				w++;
			}
		}
	}

	private List<ProxiedServer> getInitialProps(Member member) {
		return new ArrayList<>(
				BukkitMain.getInstance().getServerManager().getBalancer(ServerType.HUNGERGAMES).getList());
	}

}
