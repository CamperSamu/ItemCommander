package com.campersamu.itemcommander.command;

import com.campersamu.itemcommander.config.CommanderIO;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

import static com.campersamu.itemcommander.config.CommanderIO.saveItemToFile;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SaveCommanderCommand
        implements CommanderIO {
    protected static final Text errorCannotSaveAirCommanderItemError = Text.literal("Cannot save air!").formatted(Formatting.RED);
    protected static final String successSavedCommanderItemString = "Item saved! File name: %s";
    protected static final String fileNameArgument = "fileName";

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            dispatcher.getRoot().addChild(
                    literal("commander")
                            .then(literal("save")
                                    .requires(require("commander.command.save", 4))
                                    .then(argument(fileNameArgument, string()).executes(context -> execute(context, context.getArgument(fileNameArgument, String.class))))
                                    .executes(context -> execute(context, null))).build()

            );
            dispatcher.getRoot().addChild(literal("commander save").build());

        });
    }

    private static int execute(final @NotNull CommandContext<ServerCommandSource> context, @Nullable String fileName)
            throws CommandSyntaxException {
        final var ctxSource = context.getSource();
        final var player = ctxSource.getPlayerOrThrow();
        final var stack = player.getMainHandStack();

        if (stack.equals(ItemStack.EMPTY) || stack.equals(new ItemStack(Items.AIR))) {
            ctxSource.sendFeedback(() -> errorCannotSaveAirCommanderItemError, true);
            return -1;
        }

        //region denullify and replace spaces
        if (fileName == null)
            fileName = stack.getName().getString() + "-" + UUID.randomUUID();
        fileName = fileName.replace(" ", "_");
        //endregion

        saveItemToFile(stack, fileName);

        @NotNull String finalFileProxy = fileName;
        ctxSource.sendFeedback(() -> successText(finalFileProxy), true);
        return 0;
    }

    protected static Text successText(final @NotNull String fileName) {
        return Text.literal(successSavedCommanderItemString.formatted(fileName)).formatted(Formatting.GREEN);
    }
}
