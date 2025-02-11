package br.com.pentamc.login.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import br.com.pentamc.bukkit.api.actionbar.ActionBarAPI;
import br.com.pentamc.bukkit.event.update.UpdateEvent;
import br.com.pentamc.bukkit.event.update.UpdateEvent.UpdateType;
import br.com.pentamc.login.captcha.Captcha;
import br.com.pentamc.login.captcha.Captcha.CaptchaHandler;
import br.com.pentamc.login.captcha.types.MenuCaptcha;
import br.com.pentamc.login.event.captcha.CaptchaSuccessEvent;

public class CaptchaListener implements Listener {

	private Map<Player, Captcha> captchaMap;

	public CaptchaListener() {
		captchaMap = new HashMap<>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (!member.getLoginConfiguration().isPassCaptcha()) {
			if (member.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.ORIGINAL) {
				ActionBarAPI.send(event.getPlayer(), "§aCaptcha concluido com sucesso!");
				Bukkit.getPluginManager().callEvent(new CaptchaSuccessEvent(event.getPlayer()));
				member.getLoginConfiguration().setCaptcha(true);
				return;
			}

			MenuCaptcha catpcha = new MenuCaptcha(event.getPlayer(),
					CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId()),
					new CaptchaHandler() {

						@Override
						public void handle(boolean success) {
							if (success) {
								member.getLoginConfiguration().setCaptcha(true);
								Bukkit.getPluginManager().callEvent(new CaptchaSuccessEvent(event.getPlayer()));
								ActionBarAPI.send(event.getPlayer(), "§aCaptcha concluido com sucesso!");
							} else {
								event.getPlayer().kickPlayer("§cVocê não passou no captcha!");

								if (member.getOnlineTime() < 1000 * 60 * 3)
									CommonGeneral.getInstance().getPlayerData()
											.deleteMember(event.getPlayer().getUniqueId());
							}
						}
					});

			captchaMap.put(event.getPlayer(), catpcha);
			catpcha.start();
		}
	}

	@EventHandler
	public void onCaptchaSuccess(CaptchaSuccessEvent event) {
		if (captchaMap.containsKey(event.getPlayer()))
			captchaMap.get(event.getPlayer()).setComplete(true);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Captcha>> iterator = captchaMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Captcha> entry = iterator.next();

				if (!entry.getKey().isOnline()) {
					iterator.remove();
					continue;
				}

				Captcha captcha = entry.getValue();

				if (captcha.isComplete()) {
					iterator.remove();
					continue;
				}

				long time = System.currentTimeMillis() - captcha.getCaptchaTime();

				if (time > 30000) {
					captcha.getCaptchaHandler().handle(false);
					iterator.remove();
				} else
					ActionBarAPI.send(captcha.getPlayer(),
							"§cVocê tem " + ((30000 - time) / 1000l) + " segundos para completar o captcha!");
			}
		}
	}

}
