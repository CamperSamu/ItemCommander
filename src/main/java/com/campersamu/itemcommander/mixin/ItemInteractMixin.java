package com.campersamu.itemcommander.mixin;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.nbt.Commander;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ItemInteractMixin {
    @Inject(
            method = "interactItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getCount()I"
            ),
            cancellable = true
    )
    private void checkInteraction(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        try {
            cir.setReturnValue(Commander.fromNbt(stack.getOrCreateNbt()).executeCommand(player, stack, player.raycast(4, player.server.getTickTime(), false).getPos()
            ));
        } catch (CommanderException ignored) {
        }
    }

    //bruh it does it twice and I have not enough time to find a good solution, if you read this and you have one open an issue!
//    @Inject(
//            method = "interactBlock",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"
//            ),
//            cancellable = true
//    )
//    private void onBlockInteraction(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
//        try {
//            cir.setReturnValue(Commander.fromNbt(stack.getOrCreateNbt()).executeCommand(player, stack, hitResult.getPos()));
//        } catch (CommanderException ignored) {}
//    }
}
