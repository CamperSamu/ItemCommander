package com.campersamu.itemcommander.config;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static com.campersamu.itemcommander.ItemCommanderInit.LOGGER;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@SuppressWarnings("deprecation") // Suppress FabricLoader#getGameInstance warnings
public interface CommanderIO {
    Path CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("commander");

    static void init() {
        try {
            Files.createDirectories(CONFIG_FOLDER);
            try (Stream<Path> stream = Files.list(CONFIG_FOLDER)) {
                stream
                        .filter(file -> !Files.isDirectory(file))
                        .forEach(file -> {
                            final var fileName = file.getFileName().toString();
                            if (fileName.contains(" ")) {
                                try {
                                    Files.move(
                                            file, CONFIG_FOLDER.resolve(fileName.replace(" ", "_")),
                                            REPLACE_EXISTING, COPY_ATTRIBUTES);
                                } catch (IOException e) {
                                    LOGGER.error("Failed to fixup config files", e);
                                }
                            }
                        });
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Utils method that simplifies fetching of all the config names.
     *
     * @return An array containing all the item files present inside the {@link CommanderIO#CONFIG_FOLDER config folder}
     * (usually {@code "config/commander/"})
     */
    static @Nullable String[] getFileNames() {
        return CONFIG_FOLDER.toFile().list();
    }

    /**
     * Method used to save an {@link ItemStack} to a file inside the {@link CommanderIO#CONFIG_FOLDER config folder}
     * (usually {@code "config/commander/"})
     *
     * @param stack    The {@link ItemStack} that needs to be serialized
     * @param fileName Name of the file, will append ".nbt" if it's not explicit.
     */
    static void saveItemToFile(final @NotNull ItemStack stack, @NotNull String fileName) {
        fileName = (fileName.contains(".nbt"))
                ? fileName
                : fileName + ".nbt";
        try {
            if (!(FabricLoader.getInstance().getGameInstance() instanceof MinecraftServer server)) return;
            NbtIo.write((NbtCompound) stack.encodeAllowEmpty(server.getRegistryManager()), CONFIG_FOLDER.resolve(fileName));
        } catch (IOException e) {
            LOGGER.error("Failed to save item to file", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads an item from a nbt file contained inside the {@link CommanderIO#CONFIG_FOLDER config folder}
     * (usually {@code "config/commander/"}) and returns it.
     *
     * @param fileName Name of the file, will append ".nbt" if it's not explicit.
     * @return The item corresponding to the nbt.
     * @throws IOException Thrown when the specified item file does not exist.
     */
    static ItemStack loadFromFile(@NotNull String fileName) throws IOException {
        fileName = (fileName.contains(".nbt"))
                ? fileName
                : fileName + ".nbt";
        if (!(FabricLoader.getInstance().getGameInstance() instanceof MinecraftServer server)) return ItemStack.EMPTY;
//        server.getDataFixer().update()
        return ItemStack.fromNbtOrEmpty(server.getRegistryManager(), Objects.requireNonNull(NbtIo.read(CONFIG_FOLDER.resolve(fileName))));
    }
}
