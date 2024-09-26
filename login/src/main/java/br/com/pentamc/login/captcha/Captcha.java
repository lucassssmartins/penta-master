package br.com.pentamc.login.captcha;

import br.com.pentamc.common.account.Member;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public abstract class Captcha {

	private final Player player;
	private final Member member;
	private final CaptchaHandler captchaHandler;

	private long captchaTime = System.currentTimeMillis();
	@Setter
	private boolean complete;

	public void start() {

	};

	public interface CaptchaHandler {

		void handle(boolean success);

	}

}