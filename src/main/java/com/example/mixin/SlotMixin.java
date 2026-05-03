package com.example.mixin;

import com.example.Randomdrop;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
	@Unique
	private ItemStack randomdrop$cachedCraftingResult = ItemStack.EMPTY;

	@Inject(method = "getItem", at = @At("RETURN"), cancellable = true)
	private void randomdrop$replaceDisplayedCraftingResult(CallbackInfoReturnable<ItemStack> info) {
		if (!((Object) this instanceof ResultSlot)) {
			return;
		}

		ItemStack originalStack = info.getReturnValue();
		if (!Randomdrop.isRandomCraftingResultsEnabled() || originalStack.isEmpty()) {
			randomdrop$cachedCraftingResult = ItemStack.EMPTY;
			return;
		}

		if (randomdrop$cachedCraftingResult.isEmpty() || randomdrop$cachedCraftingResult.getCount() != originalStack.getCount()) {
			randomdrop$cachedCraftingResult = Randomdrop.createRandomCraftingResultItemStack();
			if (!randomdrop$cachedCraftingResult.isEmpty()) {
				randomdrop$cachedCraftingResult.setCount(originalStack.getCount());
			}
		}

		if (!randomdrop$cachedCraftingResult.isEmpty()) {
			info.setReturnValue(randomdrop$cachedCraftingResult.copy());
		}
	}

	@Inject(method = "remove", at = @At("RETURN"), cancellable = true)
	private void randomdrop$replaceRemovedCraftingResult(int amount, CallbackInfoReturnable<ItemStack> info) {
		if (!((Object) this instanceof ResultSlot) || !Randomdrop.isRandomCraftingResultsEnabled()) {
			return;
		}

		ItemStack originalStack = info.getReturnValue();
		if (originalStack.isEmpty()) {
			return;
		}

		ItemStack randomStack = randomdrop$cachedCraftingResult.isEmpty() ? Randomdrop.createRandomCraftingResultItemStack() : randomdrop$cachedCraftingResult.copy();
		if (randomStack.isEmpty()) {
			return;
		}

		randomStack.setCount(originalStack.getCount());
		info.setReturnValue(randomStack);
	}

	@Inject(method = "onTake", at = @At("RETURN"))
	private void randomdrop$clearCachedCraftingResult(CallbackInfo info) {
		if ((Object) this instanceof ResultSlot) {
			randomdrop$cachedCraftingResult = ItemStack.EMPTY;
		}
	}
}
