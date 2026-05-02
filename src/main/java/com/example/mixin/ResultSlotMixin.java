package com.example.mixin;

import com.example.Randomdrop;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResultSlot.class)
public class ResultSlotMixin {
	@Inject(method = "remove", at = @At("RETURN"), cancellable = true)
	private void randomdrop$replaceCraftingResult(int amount, CallbackInfoReturnable<ItemStack> info) {
		if (!Randomdrop.isRandomCraftingResultsEnabled()) {
			return;
		}

		ItemStack originalStack = info.getReturnValue();
		if (originalStack.isEmpty()) {
			return;
		}

		ItemStack randomStack = Randomdrop.createRandomCraftingResultItemStack();
		if (randomStack.isEmpty()) {
			return;
		}

		randomStack.setCount(originalStack.getCount());
		info.setReturnValue(randomStack);
	}
}
