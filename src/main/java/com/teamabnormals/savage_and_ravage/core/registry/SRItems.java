package com.teamabnormals.savage_and_ravage.core.registry;

import com.teamabnormals.blueprint.core.util.registry.ItemSubRegistryHelper;
import com.teamabnormals.savage_and_ravage.common.item.*;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import com.teamabnormals.savage_and_ravage.core.other.SRTiers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = SavageAndRavage.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SRItems {
	public static final ItemSubRegistryHelper HELPER = SavageAndRavage.REGISTRY_HELPER.getItemSubHelper();

	public static final RegistryObject<Item> BLAST_PROOF_PLATING = HELPER.createItem("blast_proof_plating", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
	public static final RegistryObject<Item> GRIEFER_HELMET = HELPER.createItem("griefer_helmet", () -> new GrieferArmorItem(SRTiers.GRIEFER, EquipmentSlot.HEAD, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
	public static final RegistryObject<Item> GRIEFER_CHESTPLATE = HELPER.createItem("griefer_chestplate", () -> new GrieferArmorItem(SRTiers.GRIEFER, EquipmentSlot.CHEST, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
	public static final RegistryObject<Item> GRIEFER_LEGGINGS = HELPER.createItem("griefer_leggings", () -> new GrieferArmorItem(SRTiers.GRIEFER, EquipmentSlot.LEGS, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));
	public static final RegistryObject<Item> GRIEFER_BOOTS = HELPER.createItem("griefer_boots", () -> new GrieferArmorItem(SRTiers.GRIEFER, EquipmentSlot.FEET, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));

	public static final RegistryObject<Item> MASK_OF_DISHONESTY = HELPER.createItem("mask_of_dishonesty", () -> new MaskOfDishonestyItem(SRTiers.MASK, EquipmentSlot.HEAD, new Item.Properties().rarity(Rarity.UNCOMMON).tab(CreativeModeTab.TAB_COMBAT)));

	public static final RegistryObject<ForgeSpawnEggItem> ICEOLOGER_SPAWN_EGG = HELPER.createSpawnEggItem("iceologer", SREntityTypes.ICEOLOGER::get, 0x8E9393, 0x152F6A);
	public static final RegistryObject<ForgeSpawnEggItem> TRICKSTER_SPAWN_EGG = HELPER.createSpawnEggItem("trickster", SREntityTypes.TRICKSTER::get, 0x617743, 0x734B4B);

	public static void registerItemProperties() {
		ItemProperties.register(Items.CROSSBOW, new ResourceLocation(SavageAndRavage.MOD_ID, "spectral_arrow"), (stack, world, entity, integer) -> entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack, Items.SPECTRAL_ARROW) ? 1.0F : 0.0F);
	}
}