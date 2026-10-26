package com.industrialevolution.registry;

import com.industrialevolution.IndustrialEvolution;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, IndustrialEvolution.MOD_ID);

    /** Structural crafting component; electrical behavior is added in the energy stage. */
    public static final RegistryObject<Block> MACHINE_CASING = BLOCKS.register(
            "machine_casing",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops())
    );

    private ModBlocks() {
    }
}
