package com.campersamu.itemcommander.nbt;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.exception.CommanderNoCommandException;
import com.campersamu.itemcommander.exception.CommanderNoTagException;
import eu.pb4.placeholders.TextParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.campersamu.itemcommander.nbt.CommanderAction.CONSUME;
import static com.campersamu.itemcommander.nbt.CommanderSource.PLAYER;
import static com.campersamu.itemcommander.nbt.CommanderSource.SERVER;

/**
 * A container {@link Record} that allows easy manipulation of a {@link Commander Commander Item}.
 */
public record Commander(String command, CommanderAction action, CommanderSource source) {
    /**
     * Defines the tag name of the root of a {@link Commander Commander Item} NBT data.
     *
     * @see net.minecraft.nbt.NbtCompound
     */
    public static final String COMMANDER_TAG_KEY = "ItemCommander";
    /**
     * Defines the tag name of a {@link Commander Commander Item}'s embedded command as NBT data.
     *
     * @see net.minecraft.nbt.NbtString
     */
    public static final String COMMANDER_COMMAND_KEY = "Command";
    /**
     * Defines the tag name of a {@link Commander Commander Item}'s action as NBT data.
     * Can either be {@link CommanderAction#CONSUME CONSUME on use} or {@link CommanderAction#KEEP KEEP on use}.
     *
     * @see net.minecraft.nbt.NbtByte
     */
    public static final String COMMANDER_ACTION_KEY = "Action";
    /**
     * Defines the tag name of a {@link Commander Commander Item}'s command source as NBT data.
     * Can either be {@link CommanderSource#SERVER executed by the SERVER} or {@link CommanderSource#PLAYER executed by the PLAYER}.
     */
    public static final String COMMANDER_SOURCE_KEY = "Source";
    private static final CommanderNoCommandException CommanderNoCommandException = new CommanderNoCommandException();
    private static final CommanderNoTagException CommanderNoTagException = new CommanderNoTagException();

    /**
     * Creates a Commander from a {@link ItemStack} NBT data.
     *
     * @param nbt the {@link ItemStack} NBT data
     * @return a new {@link Commander} created from NBT data
     * @throws CommanderNoCommandException if no command is attached to the item
     * @throws CommanderNoTagException     if no Commander NBT tag ({@link Commander#COMMANDER_TAG_KEY}) is attached to the item
     */
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

    /**
     * Converts a commander to NBT data.
     *
     * @param commander {@link Commander} source.
     * @return the corresponding NBT data, attachable to a {@link ItemStack}.
     */
    public static @NotNull NbtCompound toNbt(@NotNull Commander commander) {
        NbtCompound nbt = new NbtCompound();

        nbt.putString(COMMANDER_COMMAND_KEY, commander.command());
        nbt.putByte(COMMANDER_ACTION_KEY, commander.action().id);
        nbt.putByte(COMMANDER_SOURCE_KEY, commander.source().id);

        return nbt;
    }


    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @param pos       position of the interaction, usually is equals to where the item gets used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    public static ActionResult executeCommand(Commander commander, ServerPlayerEntity player, ItemStack itemStack, Vec3d pos) {
        MinecraftServer server = player.server;

        String parsedCommand = TextParser.parse(
                commander.command()
                        .replace("@itemname", itemStack.getName().getString())
                        .replace("@pitch", "" + player.getPitch())
                        .replace("@yaw", "" + player.getHeadYaw())
                        .replace("@ix", "" + pos.getX())
                        .replace("@iy", "" + pos.getY())
                        .replace("@iz", "" + pos.getZ())
                        .replace("@x", "" + player.getPos().getX())
                        .replace("@y", "" + player.getPos().getY())
                        .replace("@z", "" + player.getPos().getZ())
                        .replace("@p", player.getEntityName())
                        .replace("@s", player.getEntityName())
        ).getString();

        if (commander.source() == SERVER) {
            server.getCommandManager().execute(server.getCommandSource(), parsedCommand);
        } else if (commander.source() == PLAYER) {
            server.getCommandManager().execute(player.getCommandSource(), parsedCommand);
        }

        if (commander.action() != CONSUME) {
            return ActionResult.PASS;
        } else {
            itemStack.decrement(1);
            return ActionResult.CONSUME;
        }
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    public static ActionResult executeCommand(Commander commander, ServerPlayerEntity player, ItemStack itemStack) {
        return executeCommand(commander, player, itemStack, player.getPos());
    }

    /**
     * Converts this commander to NBT data.
     *
     * @return the corresponding NBT data, attachable to a {@link ItemStack}.
     */
    public @NotNull NbtCompound toNbt() {
        return toNbt(this);
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    public ActionResult executeCommand(ServerPlayerEntity player, ItemStack itemStack) {
        return executeCommand(this, player, itemStack, player.getPos());
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @param pos       position of the interaction, usually is equals to where the item gets used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    public ActionResult executeCommand(ServerPlayerEntity player, ItemStack itemStack, Vec3d pos) {
        return executeCommand(this, player, itemStack, pos);
    }
}
