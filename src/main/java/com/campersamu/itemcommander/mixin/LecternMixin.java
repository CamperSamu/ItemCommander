package com.campersamu.itemcommander.mixin;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.nbt.Commander;
import com.campersamu.itemcommander.nbt.CommanderAction;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.util.Objects.requireNonNullElse;

@Mixin(LecternBlock.class)
public abstract class LecternMixin extends BlockWithEntity {
    @Shadow
    @Final
    public static BooleanProperty HAS_BOOK;

    @Shadow
    public static void setHasBook(@Nullable Entity user, World world, BlockPos pos, BlockState state, boolean hasBook) {
    }

    private LecternMixin(Settings settings) {
        super(settings);
    }

    @Inject(
            method = "onUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onUseCommand(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer && state.get(HAS_BOOK))
            try {
                final LecternBlockEntity lectern = requireNonNullElse((LecternBlockEntity) world.getBlockEntity(pos), new LecternBlockEntity(pos, state));
                final ItemStack stack = lectern.getBook();
                final Commander commander = Commander.fromNbt(stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(new NbtCompound())).getNbt());
                final ActionResult result = commander.executeCommand(serverPlayer, stack);
                if (commander.action() == CommanderAction.CONSUME) {
                    lectern.setBook(stack.split(1));
                    setHasBook(player, world, pos, state, false);
                }
                cir.setReturnValue(result);
            } catch (CommanderException ignored) {
            }
    }
}
