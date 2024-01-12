package com.campersamu.itemcommander.command;

import com.campersamu.itemcommander.config.CommanderIO;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.campersamu.itemcommander.config.CommanderIO.getFileNames;
import static com.campersamu.itemcommander.config.CommanderIO.loadFromFile;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.campersamu.itemcommander.ItemCommanderInit.LOGGER;
import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GiveCommanderCommand
        implements CommanderIO {
    protected static final String successCommanderItemString = "Item %s given!";
    protected static final String errorCommanderItemString = "Item file %s was not found!";
    protected static final String fileNameArgument = "fileName";
    protected static final String quantityArgument = "quantity";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {

            dispatcher.getRoot().addChild(
                    literal("commander")
                            .then(literal("give")
                                    .requires(require("commander.command.give", 4))
                                    .then(argument(fileNameArgument, string())
                                            .suggests((context, builder) -> {

                                                final var files = getFileNames();
                                                if (files != null)
                                                    for (String file : files) {
                                                        file = file.replace(".nbt", "");
                                                        builder.suggest(file);
                                                    }

                                                return builder.buildFuture();
                                            })
                                            .executes(context -> execute(context, context.getArgument(fileNameArgument, String.class), 1))
                                            .then(argument(quantityArgument, integer()).executes(context -> execute(context, context.getArgument(fileNameArgument, String.class), context.getArgument(quantityArgument, Integer.class)))))
                            )
                            .build()

            );

            dispatcher.getRoot().addChild(literal("commander give").build());
        });
    }

    private static int execute(final @NotNull CommandContext<ServerCommandSource> context, @NotNull String fileName, final int quantity) throws CommandSyntaxException {
        final var ctxSource = context.getSource();
        final var player = ctxSource.getPlayerOrThrow();

        try {
            final var stack = loadFromFile(fileName);
            stack.setCount(quantity);
            player.giveItemStack(stack);
        } catch (IOException e) {
            ctxSource.sendFeedback(() -> errorText(fileName), true);
            LOGGER.error("Failed to load item from file " + fileName, e);
            return -1;
        }

        ctxSource.sendFeedback(() -> successText(fileName), true);
        return 0;
    }

    protected static Text successText(final @NotNull String fileName) {
        return Text.literal(successCommanderItemString.formatted(fileName)).formatted(Formatting.GREEN);
    }

    protected static Text errorText(final @NotNull String fileName) {
        return Text.literal(errorCommanderItemString.formatted(fileName)).formatted(Formatting.RED);
    }

}
