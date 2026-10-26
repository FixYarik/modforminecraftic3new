package com.industrialevolution;

import com.industrialevolution.registry.ModBlocks;
import com.industrialevolution.registry.ModCreativeTabs;
import com.industrialevolution.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Common entry point, loaded on both the Minecraft client and dedicated server. */
@Mod(IndustrialEvolution.MOD_ID)
public final class IndustrialEvolution {
    public static final String MOD_ID = "industrialevolution";

    public IndustrialEvolution() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
    }
}
