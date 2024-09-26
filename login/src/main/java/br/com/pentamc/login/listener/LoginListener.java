package br.com.pentamc.login.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.pentamc.bukkit.api.actionbar.ActionBarAPI;
import br.com.pentamc.bukkit.api.title.types.SimpleTitle;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.pentamc.login.event.captcha.CaptchaSuccessEvent;

public class LoginListener implements Listener {

	private Map<BukkitMember, Long> playerMap;

	public LoginListener() {
		playerMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (member.getLoginConfiguration().isPassCaptcha())
			handleLogin(event.getPlayer());
	}

	@EventHandler
	public void onPlayerJoin(CaptchaSuccessEvent event) {
		handleLogin(event.getPlayer());
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<BukkitMember, Long>> iterator = playerMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<BukkitMember, Long> entry = iterator.next();
				BukkitMember member = entry.getKey();

				if (!member.getPlayer().isOnline()) {
					iterator.remove();
					continue;
				}

				if (member.getLoginConfiguration().isLogged()) {
					iterator.remove();
					return;
				}

				if (System.currentTimeMillis() > entry.getValue()) {
					long timeRemeaning = System.currentTimeMillis() - entry.getValue();

					if (timeRemeaning > 30000) {
						iterator.remove();
						member.getPlayer().kickPlayer("§cVocê excedeu o limite de tempo para se autenticar!");
					} else {
						ActionBarAPI.send(member.getPlayer(),
								"§cVocê possui " + ((30000 - timeRemeaning) / 1000l) + " para se logar!");
					}
				}
			}
		}
	}

	void handleLogin(Player player) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.CRACKED) {
			if (!member.getLoginConfiguration().isLogged()) {
				member.sendMessage(
						member.getLoginConfiguration().isRegistred() ? "§eUse o comando /login <senha> para logar."
								: "§eUse /register <senha> <senha> para se registrar.");

				new SimpleTitle("§b§lPENTA",
						member.getLoginConfiguration().isRegistred() ? "§eUse /login <senha> para se logar!"
								: "§eUse /register <senha> para se registrar!",
						10, 20 * 99, 10).send(player);

				playerMap.put(member, System.currentTimeMillis() + 1500l);
			}
		}
	}

}
