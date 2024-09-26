package br.com.pentamc.login;

import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.tag.Tag;
import br.com.pentamc.common.tag.TagWrapper;
import br.com.pentamc.login.listener.CaptchaListener;
import br.com.pentamc.login.listener.PlayerListener;
import br.com.pentamc.login.listener.QueueListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import br.com.pentamc.bukkit.command.BukkitCommandFramework;
import br.com.pentamc.bukkit.command.manual.LoginCommand;
import br.com.pentamc.login.listener.LoginListener;

public class LoginMain extends JavaPlugin implements Listener {

	@Getter
	private static LoginMain instance;

	public static final Tag ORIGINAL_TAG = TagWrapper.create("ORIGINAL", "§6§lORIGINAL§6", Group.VIP, 24)
			.setCustom(true);
	public static final Tag LOGGING_TAG = TagWrapper.create("LOGGING", "§8§lLOGANDO§8", Group.MEMBRO, 24)
			.setCustom(true);

	@Override
	public void onEnable() {
		instance = this;
		BukkitCommandFramework.INSTANCE.registerCommands(new LoginCommand());
		Tag.registerTag(LOGGING_TAG);
		Tag.registerTag(ORIGINAL_TAG);

		Bukkit.getPluginManager().registerEvents(new QueueListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new LoginListener(), this);
		Bukkit.getPluginManager().registerEvents(new CaptchaListener(), this);
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

}
