package com.campersamu.itemcommander.command;

import com.campersamu.itemcommander.nbt.Commander;
import com.campersamu.itemcommander.nbt.CommanderAction;
import com.campersamu.itemcommander.nbt.CommanderSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static com.campersamu.itemcommander.nbt.Commander.COMMANDER_TAG_KEY;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommanderCommand {
    protected static final Text errorNoCommandText = Text.literal("A command must be specified!").formatted(Formatting.RED);
    protected static final Text errorNotPlayerText = Text.literal("This command requires a player!").formatted(Formatting.RED);
    protected static final Text success = Text.literal("Commander attached to item in hand!").formatted(Formatting.GREEN);
    protected static final String argumentCommand = "command", argumentCooldown = "cooldownTicks";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            //base command
            for (CommanderAction action : CommanderAction.values()) {
                for (CommanderSource source : CommanderSource.values()) {
                    dispatcher.getRoot().addChild(
                            literal("commander")
                                    .then(literal("create")
                                            .requires(require("commander.command.create", 4))
                                            .then(
                                                    argument(argumentCommand, string()).executes(context -> execute(context, CommanderAction.CONSUME, CommanderSource.SERVER)).then(
                                                            literal(action.name()).executes(context -> execute(context, action, CommanderSource.SERVER)).then(
                                                                    literal(source.name()).executes(context -> execute(context, action, source)).then(
                                                                            argument("cooldownTicks", integer()).executes(context -> execute(context, action, source))
                                                                    )
                                                            )
                                                    )
                                            )).build()
                    );
                }
            }
            dispatcher.getRoot().addChild(literal("commander create").build());
        });
    }

    private static int execute(final @NotNull CommandContext<ServerCommandSource> context, final CommanderAction action, final CommanderSource source) throws CommandSyntaxException {
        final var ctxSource = context.getSource();
        final var player = ctxSource.getPlayerOrThrow();
        if (player == null) {
            ctxSource.sendError(errorNotPlayerText);
            return -1;
        }
        final String command = getString(context, argumentCommand);

        if (command.isEmpty()) {
            ctxSource.sendError(errorNoCommandText);
            return -1;
        }

        int cooldown;
        try {
            cooldown = getInteger(context, argumentCooldown);
        } catch (Exception e) {
            cooldown = 0;
        }

        final Commander commander = new Commander(command, action, source, cooldown);

        player.getMainHandStack().getOrCreateNbt()
                .put(COMMANDER_TAG_KEY, commander.toNbt());

        ctxSource.sendFeedback(success, true);

        return 1;
    }
}
