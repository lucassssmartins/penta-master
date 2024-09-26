package br.com.pentamc.competitive.command;

import br.com.pentamc.competitive.game.Team;
import br.com.pentamc.competitive.menu.pair.PairInventory;
import br.com.pentamc.competitive.utils.ServerConfig;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.event.game.GameStartEvent;
import br.com.pentamc.competitive.game.GameState;
import br.com.pentamc.competitive.kit.KitType;
import br.com.pentamc.competitive.listener.register.GameListener;
import br.com.pentamc.competitive.scheduler.types.GameScheduler;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.BukkitMain;
import br.com.pentamc.bukkit.api.vanish.AdminMode;
import br.com.pentamc.bukkit.api.vanish.VanishAPI;
import br.com.pentamc.bukkit.bukkit.BukkitMember;
import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework.Command;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.utils.DateUtils;
import br.com.pentamc.common.utils.string.MessageBuilder;
import br.com.pentamc.common.utils.string.NameUtils;
import br.com.pentamc.common.utils.string.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameCommand implements CommandClass {

    private Map<UUID, Map<UUID, Request>> teamRequest = Maps.newHashMap();

    @AllArgsConstructor
    @Getter
    public abstract class Request {

        private Player inviter;
        private long createdAt;

        public abstract void response(boolean accept);
    }

    public GameCommand() {
        Bukkit.getPluginManager().registerEvents(new Listener() {


        }, BukkitMain.getInstance());
    }

    @Command(name = "compass", aliases = {"bussola"})
    public void compassCommand(BukkitCommandArgs commandArgs) {
        if (!commandArgs.isPlayer())
            return;

        Player player = commandArgs.getPlayer();
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

        if (!gamer.isPlaying()) {
            player.sendMessage("§cVocê não está jogando.");
            return;
        }

        if (!GameGeneral.getInstance().getGameState().equals(GameState.GAMETIME)) {
            player.sendMessage("§cVocê não pode fazer isso agora.");
            return;
        }

        player.getInventory().addItem(new ItemStack(Material.COMPASS));
        player.sendMessage("§aVocê recebeu uma bússola.");
    }

    @Command(name = "dupla", aliases = {"pair", "time", "team"})
    public void pairCommand(BukkitCommandArgs cmdArgs) {
        if (!cmdArgs.isPlayer())
            return;

        Player player = cmdArgs.getPlayer();
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
        String[] args = cmdArgs.getArgs();

        if (!GameMain.getPlugin().isTeamEnabled()) {
            player.sendMessage("§cComando não encontrado.");
            return;
        }

        if (GameGeneral.getInstance().getGameState().equals(GameState.GAMETIME) && !gamer.isAllowPair()) {
            player.sendMessage("§cVocê não pode fazer isso agora.");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§cUso /" + cmdArgs.getLabel() + " <player> para executar esse comando.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§cO jogador não existe.");
            return;
        }

        if (args.length > 1 && args[1].equalsIgnoreCase("remover")) {
            Gamer targetGamer = GameGeneral.getInstance().getGamerController().getGamer(target.getUniqueId());

            if (targetGamer == null || !targetGamer.isPlaying()) {
                player.sendMessage("§cO jogador não está jogando.");
                return;
            }

            Team team = targetGamer.getTeam();

            Gamer pair = team.getParticipantsAsGamer().stream()
                    .filter(g -> !g.getUniqueId().equals(target.getUniqueId()))
                    .findFirst()
                    .orElse(null);

            if (pair == null) {
                player.sendMessage("§cEste jogador está sem dupla!");
                return;
            }

            team.getParticipantsAsGamer().remove(targetGamer);
            team.getParticipantsAsGamer().remove(pair);

            GameMain.getInstance().getTeamManager().joinEmptyTeam(targetGamer);
            GameMain.getInstance().getTeamManager().joinEmptyTeam(pair);

            player.sendMessage("§cVocê dissolveu a dupla de " + target.getName() + " com sucesso.");
            return;
        }

        if (teamRequest.containsKey(target.getUniqueId()) &&
            teamRequest.get(target.getUniqueId()).containsKey(player.getUniqueId())) {
            player.sendMessage("§cVocê já convidou este jogador para sua dupla.");
            return;
        }

        if (teamRequest.containsKey(player.getUniqueId()) &&
            teamRequest.get(player.getUniqueId()).containsKey(target.getUniqueId())) {
            if (teamRequest.get(player.getUniqueId()).get(target.getUniqueId()).createdAt + 120000L <
                System.currentTimeMillis()) {
                player.sendMessage("§cO convite expirou.");
                teamRequest.get(player.getUniqueId()).remove(target.getUniqueId());
                return;
            }

            teamRequest.get(player.getUniqueId()).get(target.getUniqueId()).response(
                    args.length == 1 || args[1].equalsIgnoreCase("aceitar") || args[1].equalsIgnoreCase("accept"));
            return;
        }

        if (gamer.getTeam() != null && gamer.getTeam().isFull()) {
            player.sendMessage("§cVocê já está em uma dupla.");
            return;
        }

        Gamer targetGamer = GameGeneral.getInstance().getGamerController().getGamer(target);

        if (targetGamer.getTeam() != null && targetGamer.getTeam().isFull()) {
            player.sendMessage("§cEste jogador já está em uma dupla.");
            return;
        }

        target.sendMessage("§6" + player.getName() + "§e convidou você para se juntar ao grupo dele.");
        target.spigot().sendMessage(new MessageBuilder("§b§lCLIQUE AQUI§e para aceitar o convite de " + player.getName())
                                            .setClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                           "/" + cmdArgs.getLabel() + " " + player.getName() +
                                                           " aceitar").create());
        player.sendMessage("§aVocê convidou o jogador " + target.getName() + " com sucesso.");
        teamRequest.computeIfAbsent(target.getUniqueId(), v -> new HashMap<>())
                   .put(player.getUniqueId(), new Request(player, System.currentTimeMillis()) {

                       @Override
                       public void response(boolean accept) {
                           if (accept) {
                               if ((gamer.getTeam() == null || !gamer.getTeam().isFull())) {
                                   Team team = gamer.getTeam() == null ? GameMain.getInstance().getTeamManager()
                                           .getEmptyTeam() :
                                               gamer.getTeam();

                                   if (team == null) {
                                       player.sendMessage("§cEste jogador já está em uma dupla.");
                                       teamRequest.remove(target.getUniqueId());
                                       return;
                                   }

                                   if (targetGamer.getTeam() != null) {
                                       if (targetGamer.getTeam().getPlayerList().size() < 2) {
                                           targetGamer.getTeam().forceRemoveGamer(targetGamer);
                                       }
                                   }

                                   team.addPlayer(gamer);
                                   team.addPlayer(targetGamer);

                                   target.sendMessage("§aVocê agora é dupla de " + player.getName() + ".");
                                   player.sendMessage("§aVocê agora é dupla de " + target.getName() + ".");

                                   if (GameGeneral.getInstance().getGameState().equals(GameState.GAMETIME)) {
                                       gamer.setAllowPair(false);
                                       targetGamer.setAllowPair(false);
                                   }
                               }

                               teamRequest.remove(target.getUniqueId());
                           } else {
                               player.sendMessage("§cO jogador " + target.getName() + " recusou o convite.");
                               target.sendMessage("§cVocê recusou o convite do jogador " + player.getName() + ".");
                               teamRequest.remove(target.getUniqueId());
                           }
                       }
                   });
    }

    @Command(name = "tempo", groupToUse = Group.MOD)
    public void tempoCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse " + cmdArgs.getLabel() + " <tempo:stop> para alterar o tempo do jogo!");
            return;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            GameGeneral.getInstance().setCountTime(!GameGeneral.getInstance().isCountTime());
            ServerConfig.getInstance().setTimeInWaiting(false);

            if (GameGeneral.getInstance().getGameState() == GameState.WAITING)
                GameGeneral.getInstance().setGameState(GameState.PREGAME);

            sender.sendMessage(" §cVocê " + (GameGeneral.getInstance().isCountTime() ? "ativou" : "desativou") +
                               " a contagem!");
            return;
        }

        long time;

        try {
            time = DateUtils.parseDateDiff(args[0], true);
        } catch (Exception e) {
            sender.sendMessage(" §cO formato de tempo não é válido.");
            return;
        }

        int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000);

        if (seconds >= 60 * 120) {
            seconds = 60 * 120;
        }

        sender.sendMessage(" §aO tempo do jogo foi alterado para " + args[0] + " com sucesso.");
        GameGeneral.getInstance().setTime(seconds);
    }

    @Command(name = "reviver", groupToUse = Group.ADMIN)
    public void reviverCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage("§cUso /" + cmdArgs.getLabel() + " <player> para executar esse comando.");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage("§cO jogador não existe.");
            return;
        }

        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

        if (gamer == null) {
            sender.sendMessage("§cO jogador não existe.");
            return;
        }

        gamer.setDeathCause(null);
        gamer.setSpectator(false);
        gamer.setGamemaker(false);
        gamer.setTimeout(false);
        AdminMode.getInstance()
                 .setPlayer(player, CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.COMPASS));
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setHealth(20d);
        player.setFoodLevel(20);
        player.setExhaustion(1f);
    }

    @Command(name = "start", aliases = {"comecar", "iniciar"}, groupToUse = Group.MODPLUS)
    public void startCommand(CommandArgs args) {
        if (GameState.isPregame(GameGeneral.getInstance().getGameState())) {
            args.getSender().sendMessage(" §aO jogo foi iniciado com sucesso.");

            GameGeneral.getInstance().setCountTime(true);
            GameGeneral.getInstance().setGameState(GameState.INVINCIBILITY);
            GameMain.getInstance().registerListener(new GameListener());
            Bukkit.getPluginManager().callEvent(new GameStartEvent());

            CommonGeneral.getInstance().getMemberManager()
                         .broadcast("§7[INFO] O " + args.getSender().getName() + " iniciou a partida", Group.TRIAL);
        }
    }

    @Command(name = "game", aliases = {"help"})
    public void gameCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();

        sender.sendMessage(" ");
        sender.sendMessage("§7Tempo: §f" + StringUtils.format(GameGeneral.getInstance().getTime()));
        sender.sendMessage("§7Estágio: §f" + GameGeneral.getInstance().getGameState().name());
        sender.sendMessage(" ");

        if (cmdArgs.isPlayer()) {
            Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(sender.getUniqueId());

            sender.sendMessage("§7Kit: §b" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)));
            sender.sendMessage("§7Kills: §a" + gamer.getMatchKills());
        }

        sender.sendMessage(" ");
        sender.sendMessage("§7Sala: §b" + GameMain.getInstance().getRoomId());
    }

    @Command(name = "feast")
    public void feastCommand(BukkitCommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            Player player = cmdArgs.getPlayer();

            if (GameScheduler.feastLocation == null) {
                player.sendMessage("§cO feast ainda não spawnou!");
            } else {
                player.sendMessage("§aBussola apontando para o feast!");
                player.setCompassTarget(GameScheduler.feastLocation);
            }
        }
    }

    @Command(name = "spawn")
    public void spawnCommand(CommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            if (GameGeneral.getInstance().getGameState().isPregame()) {
                Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();

                player.teleport(BukkitMain.getInstance().getLocationFromConfig("spawn"));
            }
        }
    }

    @Command(name = "spectator", aliases = {"spec"}, groupToUse = Group.TRIAL)
    public void spectatorCommand(BukkitCommandArgs cmdArgs) {
        if (!cmdArgs.isPlayer()) {
            return;
        }

        Player player = ((BukkitMember) cmdArgs.getSender()).getPlayer();

        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

        gamer.setSpectatorsEnabled(!gamer.isSpectatorsEnabled());
        player.sendMessage(gamer.isSpectatorsEnabled() ? "§aVocê agora vê os jogadores no espectador!" :
                           "§cVocê agora não vê mais os jogadores no espectador!");
        VanishAPI.getInstance().updateVanishToPlayer(player);
    }
}
