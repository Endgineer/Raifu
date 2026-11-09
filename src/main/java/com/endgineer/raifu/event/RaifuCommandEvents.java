package com.endgineer.raifu.event;

import com.endgineer.raifu.Raifu;
import com.endgineer.raifu.api.RaifuApi;
import com.endgineer.raifu.config.RaifuConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Raifu.MOD_ID)
public final class RaifuCommandEvents {
    private static final int OP_PERMISSION_LEVEL = 2;
    private static final ResourceLocation HARDCORE_SOURCE =
            ResourceLocation.fromNamespaceAndPath(Raifu.MOD_ID, "command");

    private RaifuCommandEvents() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var hardcore = Commands.literal("hardcore")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(context -> setHardcore(
                                        context.getSource(),
                                        EntityArgument.getPlayers(context, "players"),
                                        BoolArgumentType.getBool(context, "enabled")
                                ))));

        var addLives = Commands.literal("add")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument(
                                        "amount",
                                        IntegerArgumentType.integer(-RaifuApi.MAX_LIVES, RaifuApi.MAX_LIVES)
                                )
                                .executes(context -> addLives(
                                        context.getSource(),
                                        EntityArgument.getPlayers(context, "players"),
                                        IntegerArgumentType.getInteger(context, "amount")
                                ))));

        var setLives = Commands.literal("set")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument(
                                        "lives",
                                        IntegerArgumentType.integer(0, RaifuApi.MAX_LIVES)
                                )
                                .executes(context -> setLives(
                                        context.getSource(),
                                        EntityArgument.getPlayers(context, "players"),
                                        IntegerArgumentType.getInteger(context, "lives")
                                ))));

        var lives = Commands.literal("lives")
                .then(addLives)
                .then(setLives);

        event.getDispatcher().register(
                Commands.literal("raifu")
                        .requires(source -> source.hasPermission(OP_PERMISSION_LEVEL))
                        .then(hardcore)
                        .then(lives)
        );
    }

    private static int setHardcore(
            CommandSourceStack source,
            Collection<ServerPlayer> players,
            boolean enabled
    ) {
        if (enabled && !RaifuConfig.ENABLED.get()) {
            source.sendFailure(Component.literal(
                    "Raifu is disabled in the server config; hardcore cannot be enabled."
            ));
            return 0;
        }

        for (ServerPlayer player : players) {
            RaifuApi.setHardcore(player, HARDCORE_SOURCE, enabled);
        }
        source.sendSuccess(
                () -> Component.literal(
                        (enabled ? "Enabled" : "Disabled") + " the " + HARDCORE_SOURCE
                                + " hardcore source for " + describePlayers(players) + "."
                ),
                true
        );
        return players.size();
    }

    private static int addLives(
            CommandSourceStack source,
            Collection<ServerPlayer> players,
            int amount
    ) {
        for (ServerPlayer player : players) {
            RaifuApi.addLives(player, amount);
        }
        source.sendSuccess(
                () -> Component.literal(
                        "Added " + amount + " lives to " + describePlayers(players)
                                + " (clamped to 0-" + RaifuApi.MAX_LIVES + ")."
                ),
                true
        );
        return players.size();
    }

    private static int setLives(
            CommandSourceStack source,
            Collection<ServerPlayer> players,
            int lives
    ) {
        for (ServerPlayer player : players) {
            RaifuApi.setLives(player, lives);
        }
        source.sendSuccess(
                () -> Component.literal(
                        "Set the lives of " + describePlayers(players) + " to " + lives + "."
                ),
                true
        );
        return players.size();
    }

    private static String describePlayers(Collection<ServerPlayer> players) {
        if (players.size() == 1) {
            return players.iterator().next().getGameProfile().getName();
        }
        return players.size() + " players";
    }
}
