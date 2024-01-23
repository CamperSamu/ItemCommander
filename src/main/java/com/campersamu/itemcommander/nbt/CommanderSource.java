package com.campersamu.itemcommander.nbt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public enum CommanderSource {
    DANGEROUSLY_OP((byte) -2),
    SERVER_AS_PLAYER((byte) -1),
    SERVER((byte) 0),
    PLAYER((byte) 1),
    OP((byte) 2);

    public final byte id;

    CommanderSource(byte id) {
        this.id = id;
    }

    public static CommanderSource fromId(byte id) {
        return switch (id) {
            case -2 -> DANGEROUSLY_OP;
            case -1 -> SERVER_AS_PLAYER;
            case 1 -> PLAYER;
            case 2 -> OP;
            default -> SERVER;
        };
    }

    @Environment(EnvType.SERVER)
    @SuppressWarnings("deprecation")
    public static ServerCommandSource vanilla(@NotNull CommanderSource source, @Nullable ServerPlayerEntity player) {
        var server = (player == null)
                ? ((MinecraftServer) FabricLoader.getInstance().getGameInstance())
                : player.getServer();
        assert server != null;  // we're in a serverside env, if not we *need* to crash.
        return switch (source) {
            case PLAYER -> requireNonNull(player).getCommandSource();
            case OP, DANGEROUSLY_OP -> requireNonNull(player).getCommandSource().withLevel(server.getOpPermissionLevel());
            case SERVER_AS_PLAYER -> server.getCommandSource().withEntity(player);
            default -> server.getCommandSource();
        };
    }

    @Environment(EnvType.SERVER)
    public ServerCommandSource vanilla(@Nullable ServerPlayerEntity player) {
        return vanilla(this, player);
    }
}
