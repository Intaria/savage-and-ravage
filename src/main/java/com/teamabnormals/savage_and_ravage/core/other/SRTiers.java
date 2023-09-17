package com.teamabnormals.savage_and_ravage.core.other;

import com.teamabnormals.blueprint.core.api.BlueprintArmorMaterial;
import com.teamabnormals.blueprint.core.api.BlueprintItemTier;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import com.teamabnormals.savage_and_ravage.core.registry.SRItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class SRTiers {
	public static final BlueprintArmorMaterial GRIEFER = new BlueprintArmorMaterial(new ResourceLocation(SavageAndRavage.MOD_ID, "griefer"), 15, new int[]{2, 5, 6, 2}, 15, () -> SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, () -> Ingredient.of(SRItems.BLAST_PROOF_PLATING.get()));
	public static final BlueprintArmorMaterial MASK = new BlueprintArmorMaterial(new ResourceLocation(SavageAndRavage.MOD_ID, "mask"), 5, new int[]{1, 1, 1, 1}, 15, () -> SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Items.LEATHER));
}
