package br.com.pentamc.bungee.command.register;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bungee.BungeeMain;
import br.com.pentamc.bungee.command.BungeeCommandArgs;
import br.com.pentamc.common.account.Member;
import br.com.pentamc.common.account.MemberModel;
import br.com.pentamc.common.account.MemberVoid;
import br.com.pentamc.common.ban.Category;
import br.com.pentamc.common.ban.constructor.Ban;
import br.com.pentamc.common.ban.constructor.Mute;
import br.com.pentamc.common.ban.constructor.Warn;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.utils.DateUtils;
import com.google.common.base.Joiner;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BanCommand implements CommandClass {

    @CommandFramework.Command(name = "cban", aliases = {"cheatingban"}, groupToUse = Group.TRIAL, runAsync = true)
    public void cheatingBanCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if (args.length < 1) {
            sender.sendMessage("§cUse /" + commandArgs.getLabel() + " <player> <reason> para punir um jogador na categoria cheating.");
            return;
        }

        UUID uniqueId = CommonGeneral.getInstance().getUuid(args[0]);

        if (uniqueId == null) {
            sender.sendMessage("§cO jogador " + args[0] + " não existe.");
            return;
        }

        Member member = CommonGeneral.getInstance().getMemberManager().getMember(uniqueId);

        if (member == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uniqueId);

                if (loaded == null) {
                    sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor.");
                    return;
                }

                member = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + ".");
                return;
            }
        }

        Group playerGroup;

        if (commandArgs.isPlayer()) {
            playerGroup = Member.getGroup(sender.getUniqueId());
        } else {
            playerGroup = Group.ADMIN;
        }

        if (commandArgs.isPlayer()) {
            if (playerGroup.ordinal() < member.getGroup().ordinal()) {
                sender.sendMessage("§cVocê não pode banir esse jogador.");
                return;
            }
        }

        StringBuilder builder = new StringBuilder();

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }
        }

        Ban ban = new Ban(
                Category.CHEATING,
                member.getUniqueId(),
                member.getName(),
                commandArgs.isPlayer() ? commandArgs.getSender().getName() : "CONSOLE",
                sender.getUniqueId(),
                builder.toString(),
                -1L);

        if (BungeeMain.getInstance().getPunishManager().ban(member, ban)) {
            sender.sendMessage("§aVocê baniu o jogador " + member.getName() + " na categoria cheating por " + builder.toString() + " com sucesso.");
        } else {
            sender.sendMessage("§cO jogador já está banido.");
        }

        CommonGeneral.getInstance().getServerData().getPlayers(CommonGeneral.getInstance().getServerId()).forEach(uniqueIds -> {
            ProxiedPlayer human = BungeeCord.getInstance().getPlayer(uniqueIds);

            if (human != null) {
                human.sendMessage("§cUm jogador usando trapaças em sua sala foi banido. Agradecemos o report!");
            }
        });
    }

    @CommandFramework.Command(name = "p", aliases = {"punish", "punir"}, groupToUse = Group.TRIAL, runAsync = true)
    public void punishCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length < 4) {
            sender.sendMessage(" §cUse /" + cmdArgs.getLabel() + " <ban:mute> <target> <category> <time> <reason> para punir um jogador.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[1]);

        if (uuid == null) {
            sender.sendMessage("§cO jogador " + args[1] + " não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage("§cO jogador " + args[1] + " nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + ".");
                return;
            }
        }

        Group playerGroup;

        if (cmdArgs.isPlayer()) {
            playerGroup = Member.getGroup(cmdArgs.getSender().getUniqueId());
        } else {
            playerGroup = Group.ADMIN;
        }

        if (cmdArgs.isPlayer()) {
            if (playerGroup.ordinal() < player.getGroup().ordinal()) {
                sender.sendMessage("§cVocê não pode banir esse jogador.");
                return;
            }
        }

        Category category = null;

        try {
            category = Category.valueOf(args[2].toUpperCase());
        } catch (Exception e) {
            sender.sendMessage(" §cCategoria invalida.");
            return;
        }

        long expiresCheck;

        try {
            expiresCheck = args[3].equalsIgnoreCase("0") ? -1L : DateUtils.parseDateDiff(args[3], true);
        } catch (Exception e1) {
            sender.sendMessage(" §cFormato de tempo invalido.");
            return;
        }

        StringBuilder builder = new StringBuilder();

        if (args.length > 4) {
            for (int i = 4; i < args.length; i++) {
                builder.append(args[i]).append(" ");
            }
        } else {
            builder.append("Sem motivo");
        }

        if (args[0].equalsIgnoreCase("ban")) {
            Ban ban = new Ban(category, player.getUniqueId(), player.getPlayerName(),
                              cmdArgs.isPlayer() ? cmdArgs.getSender().getName() : "CONSOLE", sender.getUniqueId(),
                              builder.toString(), expiresCheck);

            if (BungeeMain.getInstance().getPunishManager().ban(player, ban)) {
                sender.sendMessage(
                        " §aVocê baniu o jogador §a" + player.getPlayerName() + "§a por §a" + ban.getReason() +
                        "§a.");

                CommonGeneral.getInstance().getServerData().getPlayers(CommonGeneral.getInstance().getServerId()).forEach(uniqueIds -> {
                    ProxiedPlayer human = BungeeCord.getInstance().getPlayer(uniqueIds);

                    if (human != null) {
                        human.sendMessage("§cUm jogador usando trapaças em sua sala foi banido. Agradecemos o report!");
                    }
                });
            } else {
                sender.sendMessage("§cO jogador já está banido.");
            }
        } else if (args[0].equalsIgnoreCase("mute")) {
            Mute mute = new Mute(category, player.getUniqueId(),
                                 cmdArgs.isPlayer() ? cmdArgs.getSender().getName() : "CONSOLE",
                                 sender.getUniqueId(), builder.toString(), expiresCheck);

            if (BungeeMain.getInstance().getPunishManager().mute(player, mute)) {
                sender.sendMessage(
                        " §aVocê mutou o jogador §a" + player.getPlayerName() + "§a por §a" + mute.getReason() +
                        "§a.");
            } else {
                sender.sendMessage(" §cNão foi possível mutar o jogador.");
            }
        } else {
            sender.sendMessage(" §cUse /p <ban:mute:warn> <player> <tempo> <motivo> para punir um jogador.");
        }
    }

    @CommandFramework.Command(name = "unban", aliases = {"desbanir"}, runAsync = true, groupToUse = Group.ADMIN)
    public void unbanCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /unban <player> <unbanReason> para desbanir um jogador.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador " + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador " + args[0] + "§c nunca entrou no servidor!");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§f!");
                return;
            }
        }

        Ban.UnbanReason unbanReason = Ban.UnbanReason.OTHER;

        try {
            unbanReason = Ban.UnbanReason.valueOf(args[1].toUpperCase());
        } catch (Exception ex) {
            unbanReason = Ban.UnbanReason.OTHER;
        }

        if (BungeeMain.getInstance().getPunishManager().unban(player, sender.getUniqueId(),
                                                              cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() :
                                                              "CONSOLE", unbanReason)) {
            sender.sendMessage(" §aVocê desbaniu o jogador §a" + player.getPlayerName() + "§a.");
        } else {
            sender.sendMessage(" §cNão foi possível banir o jogador.");
        }
    }

    @CommandFramework.Command(name = "unmute", runAsync = true, groupToUse = Group.MODPLUS)
    public void unmuteCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /unmute <player> para desmutar um jogador.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador " + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador " + args[0] + " nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador " + args[0] + "§c.");
                return;
            }
        }

        if (BungeeMain.getInstance().getPunishManager().unmute(player, sender.getUniqueId(),
                                                               cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() :
                                                               "CONSOLE")) {
            sender.sendMessage(" §aVocê desmutou o jogador §a" + player.getPlayerName() + "§a.");
        } else {
            sender.sendMessage(" §cNão foi possível desmutar o jogador!");
        }
    }

    @CommandFramework.Command(name = "warn", aliases = {"avisar"}, runAsync = true, groupToUse = Group.TRIAL)
    public void warnCommand(BungeeCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /warn <player> <motivo> para avisar um jogador.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage(" §cO jogador §c" + args[0] + "§c não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage(" §cO jogador §c" + args[0] + "§c nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage(" §cNão foi possível pegar as informações do jogador §c" + args[0] + "§c.");
                return;
            }
        }

        Group playerGroup = Group.MEMBRO;

        if (cmdArgs.isPlayer()) {
            playerGroup = Member.getGroup(cmdArgs.getPlayer().getUniqueId());
        } else {
            playerGroup = Group.ADMIN;
        }

        if (cmdArgs.isPlayer()) {
            if (playerGroup.ordinal() < player.getGroup().ordinal()) {
                sender.sendMessage(" §cVocê não pode majenar o grupo desse jogador.");
                return;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++)
            sb.append(args[i]).append(" ");

        int id = CommonGeneral.getInstance().getPunishData().getTotalWarn() + 1;

        Warn warn = new Warn(uuid, id, cmdArgs.isPlayer() ? cmdArgs.getPlayer().getName() : "CONSOLE",
                             sender.getUniqueId(), sb.toString().trim(),
                             System.currentTimeMillis() + (1000 * 60 * 60 * 12));

        if (BungeeMain.getInstance().getPunishManager().warn(player, warn)) {
            sender.sendMessage(
                    " §aVocê alertou o jogador §a" + player.getPlayerName() + "§a por §a" + warn.getReason() +
                    "§a.");
        } else {
            sender.sendMessage(" §cNão foi possível alertar o jogador.");
        }
    }

    @CommandFramework.Command(name = "dupeip", runAsync = true, groupToUse = Group.ADMIN)
    public void dupeipCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(
                    " §cUse §c/" + cmdArgs.getLabel() + " <player>cf para ver os jogadores com o mesmo ip.");
            return;
        }

        UUID uuid = CommonGeneral.getInstance().getUuid(args[0]);

        if (uuid == null) {
            sender.sendMessage("§cO jogador " + args[0] + " não existe.");
            return;
        }

        Member player = CommonGeneral.getInstance().getMemberManager().getMember(uuid);

        if (player == null) {
            try {
                MemberModel loaded = CommonGeneral.getInstance().getPlayerData().loadMember(uuid);

                if (loaded == null) {
                    sender.sendMessage("§cO jogador " + args[0] + " nunca entrou no servidor.");
                    return;
                }

                player = new MemberVoid(loaded);
            } catch (Exception e) {
                e.printStackTrace();
                sender.sendMessage("§cNão foi possível pegar as informações do jogador " + args[0] + ".");
                return;
            }
        }

        Collection<MemberModel> memberCollection = CommonGeneral.getInstance().getPlayerData()
                                                                .loadMemberByIp(player.getLastIpAddress());

        sender.sendMessage("   §3§lDUPEIP");
        sender.sendMessage(" ");
        sender.sendMessage("§7Conta pesquisada: §a" + player.getName() + "");
        sender.sendMessage("§7Numeros de conta: §a" + memberCollection.size());
        sender.sendMessage("§7Conta" + (memberCollection.size() > 1 ? "" : "s") + ": §a" +
                           (memberCollection.isEmpty() ? "§cNenhuma conta encontrada" : Joiner.on(", ")
                                                                                              .join(memberCollection
                                                                                                            .stream()
                                                                                                            .map(memberModel -> memberModel.getPlayerName())
                                                                                                            .collect(
                                                                                                                    Collectors.toList()))));
    }
}
