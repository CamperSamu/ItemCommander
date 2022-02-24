package com.campersamu.itemcommander.mixin;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.nbt.Commander;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static java.util.Objects.requireNonNullElse;

@Mixin(LecternBlock.class)
public class LecternMixin {
    @Shadow
    @Final
    public static BooleanProperty HAS_BOOK;

    @Inject(
            method = "onUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onUseCommand(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer && state.get(HAS_BOOK))
            try {
                final ItemStack stack = requireNonNullElse((LecternBlockEntity) world.getBlockEntity(pos), new LecternBlockEntity(pos, state)).getBook();
                cir.setReturnValue(Commander.fromNbt(stack.getOrCreateNbt()).executeCommand(serverPlayer, stack));
            } catch (CommanderException ignored) {
            }
    }
}
