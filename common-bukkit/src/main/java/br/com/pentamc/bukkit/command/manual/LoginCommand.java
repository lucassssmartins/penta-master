package br.com.pentamc.bukkit.command.manual;

import java.util.regex.Pattern;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.configuration.LoginConfiguration;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import org.bukkit.Bukkit;

import br.com.pentamc.bukkit.event.login.PlayerChangeLoginStatusEvent;
import br.com.pentamc.bukkit.event.login.PlayerRegisterEvent;

public class LoginCommand implements CommandClass {

	public static final Pattern PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9_]{8,48}");

	@CommandFramework.Command(name = "login", aliases = { "registrar" })
	public void loginCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (player.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.ORIGINAL) {
			player.sendMessage("§cVocê não pode executar esse comando!");
			return;
		}

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage("§eUse /login <senha> para se logar!");
			return;
		}

		if (player.getLoginConfiguration().isLogged()) {
			player.sendMessage("§cVocê já está logado no servidor!");
			return;
		}

		if (!player.getLoginConfiguration().isRegistred()) {
			player.sendMessage("§cSua conta não está registrada no servidor!");
			return;
		}

		if (args[0].equals(player.getLoginConfiguration().getPassword())) {
			player.getLoginConfiguration().login(player.getLastIpAddress());

			player.sendMessage("§aVocê se logou com sucesso!");

			if (player.getLoginConfiguration().startSession(player.getLastIpAddress()))
				player.sendMessage("§aAgora você possui uma sessão ativa no servidor!");
			else
				player.sendMessage("§cNão foi possível estabelecer uma sessão no servidor!");

			Bukkit.getPluginManager().callEvent(new PlayerChangeLoginStatusEvent(cmdArgs.getPlayer(), player, true));
		} else {
			player.sendMessage("§cSua senha está incorreta!");
		}
	}

	@CommandFramework.Command(name = "register", aliases = { "registrar" })
	public void registerCommand(BukkitCommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(cmdArgs.getSender().getUniqueId());

		if (player.getLoginConfiguration().getAccountType() == LoginConfiguration.AccountType.ORIGINAL) {
			player.sendMessage("§cVocê não pode executar esse comando!");
			return;
		}

		if (!player.getLoginConfiguration().isPassCaptcha()) {
			player.sendMessage("");
			return;
		}

		CommandSender sender = cmdArgs.getSender();
		String[] args = cmdArgs.getArgs();

		if (args.length <= 1) {
			sender.sendMessage("§eUse /register <senha> <repita sua senha> para se logar!");
			return;
		}

		if (player.getLoginConfiguration().isRegistred()) {
			player.sendMessage("§cSua conta está registrada no servidor!");
			return;
		}

		String password = args[0];

		if (!PASSWORD_PATTERN.matcher(password).matches() || password.equals("minecraft") || password.equals("abc123")
				|| password.equals("qwerty")) {
			player.sendMessage("§cSenha muito fraca!");
			player.sendMessage("§cVocê precisa colocar pelo menos 8 caracteres!");
			player.sendMessage("§cVocê pode usar letras, numeros e underline!");
			return;
		}

		if (password.equals(args[1])) {
			player.sendMessage("§aSua conta foi registrada no servidor!");
			player.getLoginConfiguration().register(args[0], player.getLastIpAddress());

			Bukkit.getPluginManager().callEvent(new PlayerChangeLoginStatusEvent(cmdArgs.getPlayer(), player, true));
			Bukkit.getPluginManager().callEvent(new PlayerRegisterEvent(cmdArgs.getPlayer(), player));
		} else {
			sender.sendMessage("§eUse /register <senha> <repita sua senha> para se logar!");
		}
	}

}
