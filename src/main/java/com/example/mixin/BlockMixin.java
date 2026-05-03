package com.example.mixin;

import com.example.Randomdrop;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public class BlockMixin {
	@ModifyVariable(method = "popResource", at = @At("HEAD"), argsOnly = true)
	private static ItemStack randomdrop$replaceNaturalBlockDrop(ItemStack originalStack, Level world) {
		if (!Randomdrop.isRandomNaturalBlockDropsEnabled() || originalStack.isEmpty()) {
			return originalStack;
		}

		ItemStack randomStack = Randomdrop.createRandomNaturalBlockDropItemStack(originalStack.getCount());
		return randomStack.isEmpty() ? originalStack : randomStack;
	}
}
