package com.teamabnormals.savage_and_ravage.core.registry;

import com.teamabnormals.blueprint.common.block.VerticalSlabBlock;
import com.teamabnormals.blueprint.core.util.registry.BlockSubRegistryHelper;
import com.teamabnormals.savage_and_ravage.core.SavageAndRavage;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.ToIntFunction;

@EventBusSubscriber(modid = SavageAndRavage.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SRBlocks {
	public static final BlockSubRegistryHelper HELPER = SavageAndRavage.REGISTRY_HELPER.getBlockSubHelper();

	public static class SRProperties {
		public static ToIntFunction<BlockState> getLightValuePowered(int lightValue) {
			return (stateHolder) -> stateHolder.getValue(BlockStateProperties.POWERED) ? lightValue : 0;
		}
	}
}
