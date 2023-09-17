package com.teamabnormals.savage_and_ravage.core.mixin;

import com.teamabnormals.savage_and_ravage.core.SRConfig;
import com.teamabnormals.savage_and_ravage.core.other.tags.SREntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem {

	private IronGolemMixin(EntityType<? extends AbstractGolem> entity, Level world) {
		super(entity, world);
	}

	@Shadow
	public abstract boolean isPlayerCreated();

	@Inject(at = @At("HEAD"), method = "doPush", cancellable = true)
	public void collideWithEntity(Entity entity, CallbackInfo ci) {
	}

	@Inject(at = @At("RETURN"), method = "canAttackType", cancellable = true)
	public void canAttack(EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
		if ((this.isPlayerCreated() && type == EntityType.PLAYER)) {
			cir.setReturnValue(false);
		} else {
			cir.setReturnValue(super.canAttackType(type));
		}
	}
}
