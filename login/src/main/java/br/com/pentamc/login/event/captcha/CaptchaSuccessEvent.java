package br.com.pentamc.login.event.captcha;

import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.event.PlayerCancellableEvent;

public class CaptchaSuccessEvent extends PlayerCancellableEvent {

	public CaptchaSuccessEvent(Player player) {
		super(player);
	}

}
