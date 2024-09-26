package br.com.pentamc.competitive.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pentamc.bukkit.utils.schematic.object.Schematic;
import br.com.pentamc.competitive.GameMain;
import br.com.pentamc.competitive.constructor.Gamer;
import br.com.pentamc.competitive.controller.VarManager;
import br.com.pentamc.competitive.game.GameState;
import br.com.pentamc.competitive.game.Team;
import br.com.pentamc.competitive.listener.register.ScoreboardListener;
import br.com.pentamc.competitive.menu.pair.PairInventory;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

import br.com.pentamc.competitive.GameGeneral;
import br.com.pentamc.competitive.constructor.SimpleKit;
import br.com.pentamc.competitive.utils.ServerConfig;
import br.com.pentamc.common.CommonGeneral;
import br.com.pentamc.bukkit.command.BukkitCommandArgs;
import br.com.pentamc.common.command.CommandArgs;
import br.com.pentamc.common.command.CommandClass;
import br.com.pentamc.common.command.CommandFramework.Command;
import br.com.pentamc.common.command.CommandFramework.Completer;
import br.com.pentamc.common.command.CommandSender;
import br.com.pentamc.common.permission.Group;
import br.com.pentamc.common.utils.DateUtils;
import br.com.pentamc.common.utils.string.NameUtils;
import br.com.pentamc.common.utils.string.StringUtils;

public class ModeratorCommand implements CommandClass {

    @Command(name = "autocommand.clear", aliases = {"autoexec.clear"}, groupToUse = Group.MODPLUS)
    public void autocommandClearSubcommand(CommandArgs cmdArgs) {
        ServerConfig.getInstance().clearCommands();
        cmdArgs.getSender().sendMessage("§aComandos removidos com sucesso.");
    }

    @Command(name = "spawnarena", aliases = { "arena" }, groupToUse = Group.MODPLUS)
    public void spawnarena(CommandArgs cmdArgs) {

        if (cmdArgs.getArgs().length == 0) {
            cmdArgs.getSender().sendMessage("§cEspecifique qual schematic deverá ser spawnado. Use /" + cmdArgs.getLabel() + " <schematic> para spawnar um schematic.");
            return;
        }

        String schematicName = Joiner.on(' ').join(Arrays.copyOfRange(cmdArgs.getArgs(), 0, cmdArgs.getArgs().length));
        Location location = new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0, 60, 0);
        File file = new File(GameMain.getInstance().getDataFolder(), schematicName + ".schematic");

        if (!file.exists()) {
            cmdArgs.getSender().sendMessage("§cO schematic " + schematicName + " não existe.");
            return;
        }

        EditSession session = new EditSessionBuilder(BukkitUtil.getLocalWorld(location.getWorld())).fastmode(true).build();

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        ClipboardReader reader = null;

        try {
            reader = format.getReader(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Clipboard clipboard = null;

        try {
            clipboard = reader.read(session.getWorld().getWorldData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Operation operation = new ClipboardHolder(clipboard, session.getWorld().getWorldData())
                    .createPaste(session, session.getWorld().getWorldData())
                    .to(BukkitUtil.toVector(location))
                    .ignoreAirBlocks(false)
                    .build();

            Operations.complete(operation);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        session.flushQueue();

        final Location teleportLocation = location.add(0, 1, 0);
        Bukkit.getOnlinePlayers().forEach(player -> player.teleport(teleportLocation));
    }

    @Command(name = "openevento", groupToUse = Group.MODPLUS)
    public void openeventoCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /" + cmdArgs.getLabel() + " <evento> para abrir um evento");
            return;
        }

        String evento = args[0];

        if (!GameMain.getInstance().getConfig().contains("evento." + evento.toLowerCase())) {
            sender.sendMessage("§cEvento não encontrado.");
            return;
        }

        String forceCommand = GameMain.getInstance().getConfig().getString("evento." + evento.toLowerCase() + ".force");

        if (forceCommand.contains(";")) {
            for (String command : forceCommand.split("; *"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), forceCommand);
        }

        GameMain.getInstance().getConfig().getStringList("evento." + evento.toLowerCase() + ".commands")
                .forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });

        ScoreboardListener.createScore();
    }

    @Command(name = "autocommand", aliases = {"autoexec"}, groupToUse = Group.MODPLUS)
    public void autocommandCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length <= 2) {
            sender.sendMessage("§cUse /" + cmdArgs.getLabel() + " <state> <tempo> <comando> para autoexecutar algum comando");
            return;
        }

        GameState gameState = null;

        try {
            gameState = GameState.valueOf(args[0].toUpperCase());
        } catch (Exception e) {
            sender.sendMessage("§cEstado inválido.");
            return;
        }

        long time;

        try {
            time = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            sender.sendMessage(
                    " §cUse /" + cmdArgs.getLabel() + " <tempo> <comando> para autoexecutar algum comando.");
            return;
        }

        int seconds = (int) Math.floor((time - System.currentTimeMillis()) / 1000.0F);

        StringBuilder builder = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            String space = " ";
            if (i >= args.length - 1) {
                space = "";
            }
            builder.append(args[i]).append(space);
        }

        ServerConfig.getInstance().registerCommand(gameState, seconds, builder.toString().trim());
        sender.sendMessage("§aO comando " + builder.toString().trim() + " irá ser executado aos " +
                           StringUtils.formatTime(seconds) + " do " + gameState.name() + "!");
    }

    @Command(name = "simplekit", aliases = {"skit"}, groupToUse = Group.MODPLUS)
    public void simplekitCommand(BukkitCommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            handleHelp(sender, cmdArgs.getLabel());
            return;
        }

        switch (args[0].toLowerCase()) {
        case "criar": {
            if (args.length == 1) {
                handleHelp(sender, cmdArgs.getLabel());
            } else {
                if (!cmdArgs.isPlayer()) {
                    sender.sendMessage(" §cApenas jogadores podem criar kits!");
                    break;
                }

                String kitName = args[1];

                if (GameGeneral.getInstance().getSimplekitController().containsKey(kitName)) {
                    sender.sendMessage(" §cO kit \"" + kitName + "\" já existe!");
                    break;
                }

                sender.sendMessage(" §aVocê criou o kit \"" + kitName + "\" com sucesso.");
                GameGeneral.getInstance().getSimplekitController().load(kitName, new SimpleKit(kitName, cmdArgs.getPlayer()));
            }
            break;
        }
        case "editar": {
            if (args.length == 1) {
                handleHelp(sender, cmdArgs.getLabel());
            } else {
                if (!cmdArgs.isPlayer()) {
                    sender.sendMessage(" §cApenas jogadores podem editar kits!");
                    break;
                }

                SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

                if (simpleKit == null) {
                    sender.sendMessage(" §cO kit \"" + args[1] + "\" não existe!");
                    break;
                }

                sender.sendMessage(" §aVocê editou o kit \"" + simpleKit.getKitName() + "\" com sucesso.");
                simpleKit.updateKit(cmdArgs.getPlayer());
                staffLog("O " + sender.getName() + " editou o kit " + simpleKit.getKitName() + "!", Group.MOD);
            }
            break;
        }
        case "salvar": {
            if (args.length == 1) {
                handleHelp(sender, cmdArgs.getLabel());
            } else {
                SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

                if (simpleKit == null) {
                    sender.sendMessage(" §cO kit \"" + args[1] + "\" não existe!");
                    break;
                }

                GameGeneral.getInstance().getSimplekitController().saveKit(simpleKit);
                sender.sendMessage(" §aVocê salvou o kit \"" + simpleKit.getKitName() + "\" com sucesso.");
            }
            break;
        }
        case "default": {
            if (args.length == 1) {
                handleHelp(sender, cmdArgs.getLabel());
            } else {

                if (args[1].equalsIgnoreCase("remove")) {
                    sender.sendMessage(" §cO kit default foi removido com sucesso.");
                    ServerConfig.getInstance().setDefaultSimpleKit(null);
                    CommonGeneral.getInstance().getMemberManager().broadcast("", Group.MOD);
                    break;
                }

                SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

                if (simpleKit == null) {
                    sender.sendMessage(" §cO kit \"" + args[1] + "\" não existe!");
                    break;
                }

                sender.sendMessage(" §aVocê alterou o kit default para \"" + simpleKit.getKitName() + "\" com sucesso.");
                ServerConfig.getInstance().setDefaultSimpleKit(simpleKit);
                staffLog("O " + sender.getName() + " setou o kit " + simpleKit.getKitName() + " como default!", Group.MOD);
            }
            break;
        }
        case "list": {
            sender.sendMessage(" §eOs kits disponíveis são: " +
                               (GameGeneral.getInstance().getSimplekitController().getStoreMap().values().isEmpty() ?
                                "§cNenhum disponível" : "§a" + Joiner.on(", ").join(GameGeneral.getInstance()
                                                                                               .getSimplekitController()
                                                                                               .getStoreMap().values()
                                                                                               .stream()
                                                                                               .map(SimpleKit::getKitName)
                                                                                               .collect(
                                                                                                       Collectors.toList()))));
            break;
        }
        case "aplicar": {
            if (args.length <= 2) {
                handleHelp(sender, cmdArgs.getLabel());
            } else {
                SimpleKit simpleKit = GameGeneral.getInstance().getSimplekitController().getValue(args[1]);

                if (simpleKit == null) {
                    sender.sendMessage(" §cO kit \"" + args[1] + "\" não existe!");
                    break;
                }

                if (args[2].equalsIgnoreCase("all")) {
                    Bukkit.getOnlinePlayers().forEach(simpleKit::apply);
                    sender.sendMessage("§aKit " + simpleKit.getKitName() + " aplicado para todos os jogadores!");
                    staffLog("§7O " + sender.getName() + " aplicou o kit " + simpleKit.getKitName() +
                             " em todos os jogadores!", Group.MOD);
                } else {
                    Player player = Bukkit.getPlayer(args[2]);

                    if (player == null) {
                        if (!cmdArgs.isPlayer()) {
                            sender.sendMessage("§cSomente jogadores podem usar esse comando!");
                            break;
                        }

                        Integer v = null;

                        try {
                            v = Integer.valueOf(args[2]);
                        } catch (NumberFormatException ex) {
                            handleHelp(sender, cmdArgs.getLabel());
                            return;
                        }

                        if (v >= 1000) {
                            v = 1000;
                        }

                        final int value = v;

                        Bukkit.getOnlinePlayers().stream()
                              .filter(target -> target.getLocation().distance(cmdArgs.getPlayer().getLocation()) <=
                                                value)
                              .forEach(simpleKit::apply);

                        sender.sendMessage("§aKit " + simpleKit.getKitName() +
                                           " aplicado para o todos os jogadores em um raio de  " + v + "!");
                        CommonGeneral.getInstance().getMemberManager().broadcast(
                                "§7O " + sender.getName() + " aplicou o kit " + simpleKit.getKitName() +
                                " em todos em um raio de " + v + "!", Group.MOD);
                        break;
                    }

                    simpleKit.apply(player);
                    sender.sendMessage(
                            "§aKit " + simpleKit.getKitName() + " aplicado para o jogador " + player.getName() + "!");
                    CommonGeneral.getInstance().getMemberManager().broadcast(
                            "§7O " + sender.getName() + " aplicou o kit " + simpleKit.getKitName() + " em " +
                            simpleKit.getKitName() + "!", Group.MOD);
                }
            }
            break;
        }
        default:
            handleHelp(sender, cmdArgs.getLabel());
        }
    }

    @Command(name = "cleardrop", aliases = {"cleardrops"}, groupToUse = Group.MODPLUS)
    public void cleardropCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();

        for (World world : Bukkit.getWorlds())
            for (Item entity : world.getEntitiesByClass(Item.class)) {
                entity.remove();
            }

        Bukkit.broadcastMessage("§aO chão foi limpo!");
        CommonGeneral.getInstance().getMemberManager()
                     .broadcast("§7O " + sender.getName() + " limpou o chão!", Group.MOD);
    }

    @Command(name = "togglebuild", groupToUse = Group.MODPLUS)
    public void togglebuildCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            ServerConfig.getInstance().setBuildEnabled(!ServerConfig.getInstance().isBuildEnabled());
            ServerConfig.getInstance().setPlaceEnabled(!ServerConfig.getInstance().isPlaceEnabled());
            ServerConfig.getInstance().setBucketEnabled(!ServerConfig.getInstance().isBucketEnabled());

            Bukkit.broadcastMessage(ServerConfig.getInstance().isBuildEnabled() ? "§aO build foi ativado!" : "§cO build foi desativado!");
            return;
        }

        Material blockMaterial = Material.getMaterial(args[0]);

        if (blockMaterial == null) {
            try {
                blockMaterial = Material.getMaterial(Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {
            }
        }

        if (blockMaterial == null) {
            sender.sendMessage("§cO bloco " + args[1] + " não existe!");
            return;
        }

        if (ServerConfig.getInstance().getMaterialSet().contains(blockMaterial)) {
            sender.sendMessage("§aO bloco " + blockMaterial + " foi removido da lista de blocos inquebraveis!");
            ServerConfig.getInstance().getMaterialSet().remove(blockMaterial);
        } else {
            sender.sendMessage("§aO bloco " + blockMaterial + " foi adicionado na lista de blocos inquebraveis!");
            ServerConfig.getInstance().getMaterialSet().add(blockMaterial);
        }
    }

    @Command(name = "toggleplace", aliases = {"place"}, groupToUse = Group.MODPLUS)
    public void toggleplaceCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        ServerConfig.getInstance().setPlaceEnabled(!ServerConfig.getInstance().isPlaceEnabled());

        Bukkit.broadcastMessage(
                ServerConfig.getInstance().isPlaceEnabled() ? "§aO place foi ativado!" : "§cO place foi desativado!");
        CommonGeneral.getInstance().getMemberManager().broadcast("§7O " + sender.getName() + " " +
                                                                 (ServerConfig.getInstance().isPlaceEnabled() ?
                                                                  "ativou" : "desativou") + " o place!", Group.MOD);
    }

    @Command(name = "togglebucket", aliases = {"bucket"}, groupToUse = Group.MODPLUS)
    public void togglebucketCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        ServerConfig.getInstance().setBucketEnabled(!ServerConfig.getInstance().isBucketEnabled());

        Bukkit.broadcastMessage(ServerConfig.getInstance().isBucketEnabled() ? "§aO bucket foi ativado!" :
                                "§cO bucket foi desativado!");
        CommonGeneral.getInstance().getMemberManager().broadcast("§7O " + sender.getName() + " " +
                                                                 (ServerConfig.getInstance().isBucketEnabled() ?
                                                                  "ativou" : "desativou") + " o bucket!", Group.MOD);
    }

    @Command(name = "togglepvp", groupToUse = Group.MODPLUS)
    public void togglepvpCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        ServerConfig.getInstance().setPvpEnabled(!ServerConfig.getInstance().isPvpEnabled());

        Bukkit.broadcastMessage(
                ServerConfig.getInstance().isPvpEnabled() ? "§aO pvp foi ativado!" : "§cO pvp foi desativado!");
        CommonGeneral.getInstance().getMemberManager().broadcast(
                "§7O " + sender.getName() + " " + (ServerConfig.getInstance().isPvpEnabled() ? "ativou" : "desativou") +
                " o pvp!", Group.MOD);
    }

    @Command(name = "toggledamage", groupToUse = Group.MODPLUS)
    public void toggledamageCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();

        ServerConfig.getInstance().setDamageEnabled(!ServerConfig.getInstance().isDamageEnabled());

        Bukkit.broadcastMessage(
                ServerConfig.getInstance().isDamageEnabled() ? "§aO dano foi ativado!" : "§cO dano foi desativado!");
        CommonGeneral.getInstance().getMemberManager().broadcast("§7O " + sender.getName() + " " +
                                                                 (ServerConfig.getInstance().isDamageEnabled() ?
                                                                  "ativou" : "desativou") + " o dano!", Group.MOD);
    }

    @Command(name = "setrespawn", aliases = {"respawn"}, groupToUse = Group.MODPLUS)
    public void respawnCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /" + cmdArgs.getLabel() +
                               " <group:on:off> para mudar o grupo de resnascer, desativar ou ativar o reanscimento!");
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            sender.sendMessage("§aVocê ativou o renascimento de jogadores!");
            ServerConfig.getInstance().setRespawnEnabled(true);
        } else if (args[0].equalsIgnoreCase("off")) {
            sender.sendMessage("§cVocê desativou o renascimento de jogadores!");
            ServerConfig.getInstance().setRespawnEnabled(false);
        } else {
            Group group = null;

            try {
                group = Group.valueOf(args[0].toUpperCase());
            } catch (Exception ex) {
                sender.sendMessage("§cO grupo " + args[0] + " não foi encontrado!");
                return;
            }

            ServerConfig.getInstance().setRespawnGroup(group);
            Bukkit.broadcastMessage(
                    "§aSomente os jogadores " + NameUtils.formatString(group.name()) + " ou superior podem renascer!");
            CommonGeneral.getInstance().getMemberManager().broadcast(
                    "§7O " + sender.getName() + " alterou o grupo com permissão para renascer para o " +
                    NameUtils.formatString(group.name()) + "!", Group.MOD);
        }
    }

    @Command(name = "setspectator", groupToUse = Group.MODPLUS)
    public void spectatorCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /" + cmdArgs.getLabel() +
                               " <group:on:off> para mudar o grupo de espectar, desativar ou ativar o reanscimento!");
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (ServerConfig.getInstance().isSpectatorEnabled()) {
                sender.sendMessage("§cOs espectadores já estão ativados!");
            } else {
                sender.sendMessage("§aVocê ativou os espectadores!");
                ServerConfig.getInstance().setSpectatorEnabled(true);
            }
        } else if (args[0].equalsIgnoreCase("off")) {
            if (ServerConfig.getInstance().isSpectatorEnabled()) {
                sender.sendMessage("§aVocê desativou os espectadores!");
                ServerConfig.getInstance().setSpectatorEnabled(false);
            } else {
                sender.sendMessage("§cOs espectadores já estão desativados!");
            }
        } else {
            Group group = null;

            try {
                group = Group.valueOf(args[0].toUpperCase());
            } catch (Exception ex) {
                sender.sendMessage("§cO grupo " + args[0] + " não foi encontrado!");
                return;
            }

            ServerConfig.getInstance().setSpectatorGroup(group);
            Bukkit.broadcastMessage(
                    "§aSomente os jogadores " + NameUtils.formatString(group.name()) + " ou superior podem espectar!");
            CommonGeneral.getInstance().getMemberManager().broadcast(
                    "§7O " + sender.getName() + " alterou o grupo com permissão para renascer para o " +
                    NameUtils.formatString(group.name()) + "!", Group.MOD);
        }
    }

    @Command(name = "setsurprisedisable", groupToUse = Group.MODPLUS)
    public void setsurprisedisableCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(
                    " §cUse /" + cmdArgs.getLabel() + " para ativar com que o surprise remova os kits!");
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (ServerConfig.getInstance().isSurpriseDisable()) {
                sender.sendMessage("§cOs espectadores já estão ativados!");
            } else {
                sender.sendMessage("§aVocê ativou os espectadores!");
                ServerConfig.getInstance().setSurpriseDisable(true);
            }
        } else if (args[0].equalsIgnoreCase("off")) {
            if (ServerConfig.getInstance().isSurpriseDisable()) {
                sender.sendMessage("§aVocê desativou os espectadores!");
                ServerConfig.getInstance().setSurpriseDisable(false);
            } else {
                sender.sendMessage("§cOs espectadores já estão desativados!");
            }
        }
    }

    @Command(name = "var", aliases = {"variables"}, groupToUse = Group.MODPLUS)
    public void varCommand(CommandArgs cmdArgs) {
        CommandSender sender = cmdArgs.getSender();
        String[] args = cmdArgs.getArgs();

        if (args.length == 0) {
            sender.sendMessage(" §cUse /" + cmdArgs.getLabel() + " <var> <value> para alterar uma variável.");
            return;
        }

        String varName = args[0];

        if (!GameMain.getInstance().getVarManager().hasVariable(varName)) {
            sender.sendMessage("§cA variável " + varName + " não existe.");
            return;
        }

        if (args.length == 1) {
            sender.sendMessage("§a" + varName);
            sender.sendMessage("  §fTipo: §a" + GameMain.getInstance().getVarManager().getClassType(varName));
            sender.sendMessage("  §fValor: §a" + GameMain.getInstance().getVarManager().getVar(varName));
            return;
        }

        if (args[1].equalsIgnoreCase("save")) {
            GameMain.getInstance().getVarManager().saveVar(varName);
            sender.sendMessage("§aA variável " + varName + " foi salva.");
            return;
        }

        Object value = null;
        String valueString = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

        if (VarManager.ClassType.getClassType(valueString) !=
            GameMain.getInstance().getVarManager().getClassType(varName)) {
            sender.sendMessage("§cO valor " + valueString + " não é válido para a variável " + varName + ".");
            return;
        }

        value = valueString.replace("&", "§");

        GameMain.getInstance().getVarManager().setVar(varName, value);
        sender.sendMessage("§aA variável " + varName + " foi alterada para " + value + ".");
    }

    public void handleHelp(CommandSender sender, String label) {
        sender.sendMessage(" §cUse /" + label + " criar <nome> para criar um skit");
        sender.sendMessage(" §cUse /" + label + " editar <nome> para editar um skit");
        sender.sendMessage(" §cUse /" + label + " default <nome:remove> para setar o kit default");
        sender.sendMessage(" §cUse /" + label + " aplicar <nome> <all:distancia:player> para aplicar um skit");
    }

    @Completer(name = "simplekit", aliases = {"skit"})
    public List<String> simplekitCompleter(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            List<String> effectList = new ArrayList<>();
            String[] args = {"aplicar", "default", "criar", "editar"};

            if (cmdArgs.getArgs()[0].isEmpty()) {
                effectList.addAll(Arrays.asList(args));
            } else {
                for (String arg : args)
                    if (arg.startsWith(cmdArgs.getArgs()[0].toLowerCase())) {
                        effectList.add(arg);
                    }
            }

            return effectList;
        } else if (cmdArgs.getArgs().length == 2) {
            List<String> effectList = new ArrayList<>();
            Collection<SimpleKit> simpleKits = GameGeneral.getInstance().getSimplekitController().getStoreMap()
                                                          .values();

            if (cmdArgs.getArgs()[1].isEmpty()) {
                for (SimpleKit arg : simpleKits)
                    effectList.add(arg.getKitName());
            } else {
                for (SimpleKit arg : simpleKits)
                    if (arg.getKitName().startsWith(cmdArgs.getArgs()[1].toLowerCase())) {
                        effectList.add(arg.getKitName());
                    }
            }

            return effectList;
        }

        return getPlayerList(cmdArgs.getArgs());
    }

    @Command(name = "verduplas", groupToUse = Group.TRIAL)
    public void viewPairsCommand(BukkitCommandArgs commandArgs) {
        if (!commandArgs.isPlayer())
            return;

        Player player = commandArgs.getPlayer();

        if (!GameMain.getPlugin().isTeamEnabled()) {
            player.sendMessage("§cComando não encontrado.");
            return;
        }

        new PairInventory(player, 1);
    }

    @Command(name = "verdupla", groupToUse = Group.TRIAL)
    public void viewPairCommand(BukkitCommandArgs commandArgs) {
        if (!commandArgs.isPlayer())
            return;

        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if (!GameMain.getPlugin().isTeamEnabled()) {
            player.sendMessage("§cComando não encontrado.");
            return;
        }

        if (args.length < 1) {
            player.sendMessage("§cUso /" + commandArgs.getLabel() + " <player> para ver a dupla do player inserido.");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(target);

        if (gamer == null || !gamer.isPlaying()) {
            player.sendMessage("§cEste jogador não foi encontrado.");
            return;
        }

        Team team = gamer.getTeam();

        if (team.getParticipantsAsPlayer().size() < 2) {
            player.sendMessage("§cEste jogador não está em uma dupla.");
            return;
        }

        Player pair = team.getParticipantsAsPlayer().stream()
                .filter(pairs -> !pairs.getName().equals(target.getName()))
                .findFirst()
                .orElse(null);

        if (pair == null) {
            player.sendMessage("§cEste jogador não está em uma dupla.");
            return;
        }

        player.sendMessage("§eEste jogador está em dupla com §b" + pair.getName() + "§e.");
    }

    @Completer(name = "var")
    public List<String> varComplete(CommandArgs cmdArgs) {
        if (cmdArgs.getArgs().length == 1) {
            return GameMain.getInstance().getVarManager().getVariables().stream()
                           .filter(string -> string.toLowerCase().startsWith(cmdArgs.getArgs()[0].toLowerCase()))
                           .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public List<String> getPlayerList(String[] args) {
        List<String> playerList = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (args[args.length - 1].isEmpty()) {
                if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    playerList.add(player.getName());
                }
            } else {
                if (player.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    playerList.add(player.getName());
                }
            }
        }

        return playerList;
    }
}
