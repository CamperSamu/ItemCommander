package com.campersamu.itemcommander.command;

import com.campersamu.itemcommander.nbt.Commander;
import com.campersamu.itemcommander.nbt.CommanderAction;
import com.campersamu.itemcommander.nbt.CommanderSource;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.campersamu.itemcommander.nbt.Commander.COMMANDER_TAG_KEY;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CreateCommanderCommand {
    private static final Text errorNoCommandText = new LiteralText("A command must be specified!").formatted(Formatting.RED);
    private static final Text errorNotPlayerText = new LiteralText("This command requires a player!").formatted(Formatting.RED);
    private static final Text success = new LiteralText("Commander attached to item in hand!").formatted(Formatting.GREEN);
    private static final String argumentCommand = "command";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            //base command
            for (CommanderAction action : CommanderAction.values()) {
                for (CommanderSource source : CommanderSource.values()) {
                    dispatcher.getRoot().addChild(
                            literal("commander")
                                    .requires(require("commander.command.create", 4))
                                    .then(
                                            argument(argumentCommand, string()).executes(context -> execute(context, CommanderAction.CONSUME, CommanderSource.SERVER)).then(
                                                    literal(action.name()).executes(context -> execute(context, action, CommanderSource.SERVER)).then(
                                                            literal(source.name()).executes(context -> execute(context, action, source))
                                                    )
                                            )
                                    ).build()
                    );
                }
            }
        });
    }

    private static int execute(CommandContext<ServerCommandSource> context, CommanderAction action, CommanderSource source) throws CommandSyntaxException {
        var ctxSource = context.getSource();
        var player = ctxSource.getPlayer();
        if (player == null) {
            ctxSource.sendError(errorNotPlayerText);
            return -1;
        }
        String command = getString(context, argumentCommand);

        if (command.isEmpty()) {
            ctxSource.sendError(errorNoCommandText);
            return -1;
        }

        Commander commander = new Commander(command, action, source);

        player.getMainHandStack().getOrCreateNbt()
                .put(COMMANDER_TAG_KEY, commander.toNbt());

        ctxSource.sendFeedback(success, true);

        return 1;
    }
}
