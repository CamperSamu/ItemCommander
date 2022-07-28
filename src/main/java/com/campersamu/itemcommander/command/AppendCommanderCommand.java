package com.campersamu.itemcommander.command;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.nbt.Commander;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static com.campersamu.itemcommander.command.CreateCommanderCommand.*;
import static com.campersamu.itemcommander.nbt.Commander.COMMANDER_TAG_KEY;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AppendCommanderCommand {
    protected static final Text errorNotCommanderItemText = Text.literal("Not a Commander Item!").formatted(Formatting.RED);

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            dispatcher.getRoot().addChild(
                    literal("commander")
                            .then(literal("append")
                            .requires(require("commander.command.append", 4))
                            .then(
                                    argument(argumentCommand, string()).executes(context -> execute(context, context.getArgument(argumentCommand, String.class)))
                            )).build()

            );
            dispatcher.getRoot().addChild(literal("commander append").build());
            //base command
//            for (CommanderAction action : CommanderAction.values()) {
//                for (CommanderSource source : CommanderSource.values()) {
//                    dispatcher.getRoot().addChild(
//                            literal("commander")
//                                    .requires(require("commander.command.create", 4))
//                                    .then(
//                                            argument(argumentCommand, string()).executes(context -> execute(context, CommanderAction.CONSUME, CommanderSource.SERVER)).then(
//                                                    literal(action.name()).executes(context -> execute(context, action, CommanderSource.SERVER)).then(
//                                                            literal(source.name()).executes(context -> execute(context, action, source)).then(
//                                                                    argument("cooldownTicks", integer()).executes(context -> execute(context, action, source))
//                                                            )
//                                                    )
//                                            )
//                                    ).build()
//                    );
//                }
//            }

        });
    }

    private static int execute(final @NotNull CommandContext<ServerCommandSource> context, final String command) throws CommandSyntaxException {
        final var ctxSource = context.getSource();
        final var player = ctxSource.getPlayerOrThrow();
        if (player == null) {
            ctxSource.sendError(errorNotPlayerText);
            return -1;
        }

        if (command.isEmpty()) {
            ctxSource.sendError(errorNoCommandText);
            return -1;
        }

        try {
            final Commander commander = Commander.fromNbt(player.getMainHandStack().getOrCreateNbt()).appendCommand(command);
            player.getMainHandStack().getOrCreateNbt()
                    .put(COMMANDER_TAG_KEY, commander.toNbt());
        } catch (CommanderException e) {
            ctxSource.sendError(errorNotCommanderItemText);
            return -1;
        }

        ctxSource.sendFeedback(success, true);
        return 1;
    }
}
