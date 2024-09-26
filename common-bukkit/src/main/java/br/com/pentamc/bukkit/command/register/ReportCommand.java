package br.com.pentamc.bukkit.command.register;

import java.util.UUID;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.report.Report;
import org.bukkit.entity.Player;

import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.menu.report.ReportInventory;
import br.com.pentamc.bukkit.menu.report.ReportListInventory;

public class ReportCommand implements CommandClass {

	@CommandFramework.Command(name = "report", aliases = { "reports", "reportar" })
	public void reportCommand(CommandArgs cmdArgs) {
		if (!cmdArgs.isPlayer())
			return;

		Player sender = ((BukkitMember) cmdArgs.getSender()).getPlayer();
		String[] args = cmdArgs.getArgs();

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(sender.getUniqueId());

		if (!member.hasGroupPermission(Group.TRIAL)) {
			sender.sendMessage("§cVocê não tem permissão para executar esse comando!");
			return;
		}

		if (args.length == 0) {
			new ReportListInventory(sender, 1);
			return;
		}

		UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

		if (uuid == null) {
			sender.sendMessage("§cO jogador " + args[0] + " não existe!");
			return;
		}

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

		if (player == null) {
			try {
				MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

				if (loaded == null) {
					sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor!");
					return;
				}

				player = new MemberVoid(loaded);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + "!");
				return;
			}
		}

		Report report = CommonGeneral.getInstance().getReportManager().getReport(player.getUniqueId());

		if (report == null) {
			sender.sendMessage("§cO jogador " + args[0] + " não foi reportado!");
			return;
		}

		new ReportInventory(sender, report);
	}

}