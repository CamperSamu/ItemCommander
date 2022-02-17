package com.campersamu.itemcommander.mixin;

import com.campersamu.itemcommander.exception.CommanderException;
import com.campersamu.itemcommander.nbt.Commander;
import com.campersamu.itemcommander.nbt.CommanderAction;
import com.campersamu.itemcommander.nbt.CommanderSource;
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
            Commander commander = Commander.fromNbt(stack.getOrCreateNbt());
            var server = player.server;

            if (commander.source() == CommanderSource.SERVER)
                server.getCommandManager().execute(server.getCommandSource(), commander.command());
            else if(commander.source() == CommanderSource.PLAYER) {
                server.getCommandManager().execute(player.getCommandSource(), commander.command());
            }

            if (commander.action() != CommanderAction.CONSUME) {
                cir.setReturnValue(ActionResult.PASS);
            } else {
                stack.decrement(1);
                cir.setReturnValue(ActionResult.CONSUME);
            }
        } catch (CommanderException ignored) {}
    }
}
