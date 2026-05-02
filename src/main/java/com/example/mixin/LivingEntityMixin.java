package com.example.mixin;

import com.example.Randomdrop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
	private void randomdrop$replaceDeathLoot(ServerLevel ignoredWorld, DamageSource ignoredDamageSource, CallbackInfo info) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Player) {
			return;
		}

		if (Randomdrop.dropRandomItemAtEntity(entity)) {
			info.cancel();
		}
	}
}
