package com.campersamu.itemcommander.nbt;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.exception.CommanderNoCommandException;
import com.campersamu.itemcommander.exception.CommanderNoTagException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
                    return new Commander(nbt.getString(COMMANDER_COMMAND_KEY), CommanderAction.fromId(nbt.getByte(COMMANDER_ACTION_KEY)), CommanderSource.SERVER);
                }
                return new Commander(nbt.getString(COMMANDER_COMMAND_KEY), CommanderAction.CONSUME, CommanderSource.SERVER);
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

    public @NotNull NbtCompound toNbt(){
        return toNbt(this);
    }
}
