package com.campersamu.itemcommander.nbt;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.exception.CommanderNoCommandException;
import com.campersamu.itemcommander.exception.CommanderNoTagException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.campersamu.itemcommander.nbt.CommanderAction.CONSUME;
import static com.campersamu.itemcommander.nbt.CommanderSource.PLAYER;
import static com.campersamu.itemcommander.nbt.CommanderSource.SERVER;

public record Commander(String command, CommanderAction action, CommanderSource source) {
    public static final String COMMANDER_TAG_KEY = "ItemCommander";
    public static final String COMMANDER_COMMAND_KEY = "Command";
    public static final String COMMANDER_ACTION_KEY = "Action";
    public static final String COMMANDER_SOURCE_KEY = "Source";
    private static final CommanderNoCommandException CommanderNoCommandException = new CommanderNoCommandException();
    private static final CommanderNoTagException CommanderNoTagException = new CommanderNoTagException();

    @Contract("_ -> new")
    public static @NotNull Commander fromNbt(@NotNull NbtCompound nbt) throws CommanderException {
        if (nbt.contains(COMMANDER_TAG_KEY, NbtElement.COMPOUND_TYPE)) {
            nbt = nbt.getCompound(COMMANDER_TAG_KEY);
            if (nbt.contains(COMMANDER_COMMAND_KEY, NbtElement.STRING_TYPE)) {
                if (nbt.contains(COMMANDER_ACTION_KEY, NbtElement.BYTE_TYPE)) {
                    if (nbt.contains(COMMANDER_SOURCE_KEY, NbtElement.BYTE_TYPE)) {
                        return new Commander(nbt.getString(COMMANDER_COMMAND_KEY), CommanderAction.fromId(nbt.getByte(COMMANDER_ACTION_KEY)), CommanderSource.fromId(nbt.getByte(COMMANDER_SOURCE_KEY)));
                    }
                    return new Commander(nbt.getString(COMMANDER_COMMAND_KEY), CommanderAction.fromId(nbt.getByte(COMMANDER_ACTION_KEY)), SERVER);
                }
                return new Commander(nbt.getString(COMMANDER_COMMAND_KEY), CONSUME, SERVER);
            }
            throw CommanderNoCommandException;
        }
        throw CommanderNoTagException;
    }

    public static @NotNull NbtCompound toNbt(@NotNull Commander commander) {
        NbtCompound nbt = new NbtCompound();

        nbt.putString(COMMANDER_COMMAND_KEY, commander.command());
        nbt.putByte(COMMANDER_ACTION_KEY, commander.action().id);
        nbt.putByte(COMMANDER_SOURCE_KEY, commander.source().id);

        return nbt;
    }

    public static ActionResult executeCommand(Commander commander, ServerPlayerEntity player, ItemStack itemStack) {
        MinecraftServer server = player.server;

        if (commander.source() == SERVER) {
            String parsedCommand = commander.command().replace("@p", player.getEntityName()).replace("@s", player.getEntityName());
            server.getCommandManager().execute(server.getCommandSource(), parsedCommand);
        } else if (commander.source() == PLAYER) {
            server.getCommandManager().execute(player.getCommandSource(), commander.command());
        }

        if (commander.action() != CONSUME) {
            return ActionResult.PASS;
        } else {
            itemStack.decrement(1);
            return ActionResult.CONSUME;
        }
    }

    public @NotNull NbtCompound toNbt() {
        return toNbt(this);
    }

    public ActionResult executeCommand(ServerPlayerEntity player, ItemStack itemStack) {
        return executeCommand(this, player, itemStack);
    }
}
