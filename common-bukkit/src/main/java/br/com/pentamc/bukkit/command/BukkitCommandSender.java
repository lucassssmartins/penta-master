package br.com.pentamc.bukkit.command;

import java.util.UUID;

import br.com.pentamc.common.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

@AllArgsConstructor
@Getter
public class BukkitCommandSender implements CommandSender {

	private org.bukkit.command.CommandSender sender;

	@Override
	public UUID getUniqueId() {
		if (sender instanceof Player)
			return ((Player) sender).getUniqueId();
		return UUID.randomUUID();
	}

	@Override
	public String getName() {
		if (sender instanceof Player)
			return sender.getName();
		return "CONSOLE";
	}

	@Override
	public void sendMessage(String str) {
		sender.sendMessage(str);
	}

	@Override
	public void sendMessage(BaseComponent str) {
		if (sender instanceof Player)
			((Player) sender).spigot().sendMessage(str);
		else
			sender.sendMessage(str.toLegacyText());
	}

	@Override
	public void sendMessage(BaseComponent[] str) {
		if (sender instanceof Player)
			((Player) sender).spigot().sendMessage(str);
		else
			System.out.println("Mensagem nao suportada pelo console!");
	}

	@Override
	public boolean isPlayer() {
		return sender instanceof Player;
	}

}