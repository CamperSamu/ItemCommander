package com.campersamu.itemcommander.nbt;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.exception.CommanderNoCommandException;
import com.campersamu.itemcommander.exception.CommanderNoTagException;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.campersamu.itemcommander.ItemCommanderInit.PLACEHOLDERS_LOADED;
import static com.campersamu.itemcommander.nbt.CommanderAction.CONSUME;
import static com.campersamu.itemcommander.nbt.CommanderSource.SERVER;
import static java.lang.Integer.max;

/**
 * A container {@link Record} that allows easy manipulation of a {@link Commander Commander Item}.
 */
public record Commander(String[] commands, CommanderAction action, CommanderSource source, int cooldown) {
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
     *
     * @see net.minecraft.nbt.NbtInt
     */
    public static final String COMMANDER_COOLDOWN_KEY = "Cooldown";
    /**
     * Defines the tag name of a {@link Commander Commander Item}'s cooldown as NBT data.
     * Can be any value between 0 and {@link Integer#MAX_VALUE 2147483647}.
     *
     * @see net.minecraft.nbt.NbtByte
     */
    public static final String COMMANDER_SOURCE_KEY = "Source";

    /**
     * Item Name Command Placeholder
     */
    public static final String
            ITEMNAME_PLACEHOLDER = "@itemname",

            /**
             * Player Pitch Command Placeholder
             */
            PLAYER_PITCH_PLACEHOLDER = "@pitch",

            /**
             * Player Yaw Command Placeholder
             */
            PLAYER_YAW_PLACEHOLDER = "@yaw",

            /**
             * Item Use X Coordinate Command Placeholder
             */
            ITEM_USE_X_PLACEHOLDER = "@ix",

            /**
             * Item Use Y Coordinate Command Placeholder
             */
            ITEM_USE_Y_PLACEHOLDER = "@iy",

            /**
             * Item Use Z Coordinate Command Placeholder
             */
            ITEM_USE_Z_PLACEHOLDER = "@iz",

            /**
             * Player X Coordinate Command Placeholder
             */
            PLAYER_X_PLACEHOLDER = "@x",

            /**
             * Player Y Coordinate Command Placeholder
             */
            PLAYER_Y_PLACEHOLDER = "@y",

            /**
             * Player Z Coordinate Command Placeholder
             */
            PLAYER_Z_PLACEHOLDER = "@z",

            /**
             * Command Placeholder Alias for {@link Commander#TARGET_SELF_PLACEHOLDER}
             */
            CLOSEST_TARGET_PLACEHOLDER = "@p",

            /**
             * Player (Target Self) Command Placeholder
             */
            TARGET_SELF_PLACEHOLDER = "@s";

    private static final CommanderNoCommandException CommanderNoCommandException = new CommanderNoCommandException();
    private static final CommanderNoTagException CommanderNoTagException = new CommanderNoTagException();

    /**
     * A container {@link Record} that allows easy manipulation of a {@link Commander Commander Item}.
     */
    public Commander(String command, CommanderAction action, CommanderSource source, int cooldown) {
        this(new String[]{command}, action, source, cooldown);
    }

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
                        if (nbt.contains(COMMANDER_COOLDOWN_KEY, NbtElement.INT_TYPE)) {
                            return new Commander(nbt.getString(COMMANDER_COMMAND_KEY).split("[|]"), CommanderAction.fromId(nbt.getByte(COMMANDER_ACTION_KEY)), CommanderSource.fromId(nbt.getByte(COMMANDER_SOURCE_KEY)), max(0, nbt.getInt(COMMANDER_COOLDOWN_KEY)));
                        }
                        return new Commander(nbt.getString(COMMANDER_COMMAND_KEY).split("[|]"), CommanderAction.fromId(nbt.getByte(COMMANDER_ACTION_KEY)), CommanderSource.fromId(nbt.getByte(COMMANDER_SOURCE_KEY)), 0);
                    }
                    return new Commander(nbt.getString(COMMANDER_COMMAND_KEY).split("[|]"), CommanderAction.fromId(nbt.getByte(COMMANDER_ACTION_KEY)), SERVER, 0);
                }
                return new Commander(nbt.getString(COMMANDER_COMMAND_KEY).split("[|]"), CONSUME, SERVER, 0);
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
        StringBuilder commandsData = new StringBuilder(commander.commands()[0]);
        if (commander.commands().length > 1) {
            String[] strings = commander.commands();
            for (int i = 1, stringsLength = strings.length; i < stringsLength; i++) {
                String command = strings[i];
                commandsData.append("|").append(command);
            }
        }
        nbt.putString(COMMANDER_COMMAND_KEY, commandsData.toString());
        nbt.putByte(COMMANDER_ACTION_KEY, commander.action().id);
        nbt.putByte(COMMANDER_SOURCE_KEY, commander.source().id);
        nbt.putInt(COMMANDER_COOLDOWN_KEY, commander.cooldown());

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
    @SuppressWarnings("unused") //utils
    public static ActionResult executeCommand(final @NotNull Commander commander, final @NotNull ServerPlayerEntity player, final ItemStack itemStack, final Vec3d pos, final boolean isLectern) {
        final MinecraftServer server = player.server;

        if (commander.cooldown() != 0){
            if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) return ActionResult.FAIL;
        }

        final ArrayList<String> parsedCommands = new ArrayList<>();

        for (String command : commander.commands()) {
            parsedCommands.add(applyPlaceholdersApiIfPresent(
                    command
                            .replace(ITEMNAME_PLACEHOLDER, itemStack.getName().getString())
                            .replace(PLAYER_PITCH_PLACEHOLDER, String.valueOf(player.getPitch()))
                            .replace(PLAYER_YAW_PLACEHOLDER, String.valueOf(player.getHeadYaw()))
                            .replace(ITEM_USE_X_PLACEHOLDER, String.valueOf(pos.getX()))
                            .replace(ITEM_USE_Y_PLACEHOLDER, String.valueOf(pos.getY()))
                            .replace(ITEM_USE_Z_PLACEHOLDER, String.valueOf(pos.getZ()))
                            .replace(PLAYER_X_PLACEHOLDER, String.valueOf(player.getPos().getX()))
                            .replace(PLAYER_Y_PLACEHOLDER, String.valueOf(player.getPos().getY()))
                            .replace(PLAYER_Z_PLACEHOLDER, String.valueOf(player.getPos().getZ()))
                            .replace(CLOSEST_TARGET_PLACEHOLDER, player.getGameProfile().getName())
                            .replace(TARGET_SELF_PLACEHOLDER, player.getGameProfile().getName())
            ));
        }


        for (String parsedCommand : parsedCommands) {
            switch (commander.source()) {
                case SERVER -> server.getCommandManager().executeWithPrefix(server.getCommandSource(), parsedCommand);
                case SERVER_AS_PLAYER -> server.getCommandManager().executeWithPrefix(server.getCommandSource().withEntity(player), parsedCommand);
                case OP -> server.getCommandManager().executeWithPrefix(player.getCommandSource().withLevel(4), parsedCommand);
                default -> server.getCommandManager().executeWithPrefix(player.getCommandSource(), parsedCommand);
            }
        }

        player.getItemCooldownManager().set(itemStack.getItem(), commander.cooldown());

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
    @SuppressWarnings("unused") //utils
    public static ActionResult executeCommand(final Commander commander, final ServerPlayerEntity player, final ItemStack itemStack) {
        return executeCommand(commander, player, itemStack, player.getPos(), false);
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    public ActionResult executeCommand(final ServerPlayerEntity player, final ItemStack itemStack) {
        return executeCommand(this, player, itemStack, player.getPos(), false);
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @param pos       position of the interaction, usually is equals to where the item gets used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    public ActionResult executeCommand(final ServerPlayerEntity player, final ItemStack itemStack, final Vec3d pos) {
        return executeCommand(this, player, itemStack, pos, false);
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    @SuppressWarnings("unused") //utils
    public static ActionResult executeCommand(final Commander commander, final ServerPlayerEntity player, final ItemStack itemStack, final boolean isLectern) {
        return executeCommand(commander, player, itemStack, player.getPos(), isLectern);
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    @SuppressWarnings("unused") //utils
    public ActionResult executeCommand(final ServerPlayerEntity player, final ItemStack itemStack, final boolean isLectern) {
        return executeCommand(this, player, itemStack, player.getPos(), isLectern);
    }

    /**
     * Executes the command attached to the item.
     *
     * @param player    player using the item.
     * @param itemStack stack being used.
     * @param pos       position of the interaction, usually is equals to where the item gets used.
     * @return the result of the action, {@link ActionResult#PASS} if it's successful, {@link ActionResult#CONSUME} if it's successful and the item is  consumed.
     */
    @SuppressWarnings("unused") //utils
    public ActionResult executeCommand(final ServerPlayerEntity player, final ItemStack itemStack, final Vec3d pos, final boolean isLectern) {
        return executeCommand(this, player, itemStack, pos, isLectern);
    }

    /**
     * Converts this commander to NBT data.
     *
     * @return the corresponding NBT data, attachable to a {@link ItemStack}.
     */
    public @NotNull NbtCompound toNbt() {
        return toNbt(this);
    }

    @Contract("_, _ -> new")
    public static @NotNull Commander appendCommand(final @NotNull Commander commander, final String command){
        final String[] newCommands = Arrays.copyOf(commander.commands(), commander.commands().length+1);
        newCommands[commander.commands().length] = command;
        return new Commander(newCommands, commander.action(), commander.source(), commander.cooldown());
    }

    public @NotNull Commander appendCommand(final String command){
        return appendCommand(this, command);
    }

    public static String applyPlaceholdersApiIfPresent(final @NotNull String text) {
        return PLACEHOLDERS_LOADED
                ? TextParserUtils.formatText(text).getString()
                : text;
    }
}
