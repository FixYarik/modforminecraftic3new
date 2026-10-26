package com.industrialevolution.registry;

import com.industrialevolution.IndustrialEvolution;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, IndustrialEvolution.MOD_ID);

    public static final RegistryObject<Item> RUBBER = ITEMS.register(
            "rubber", () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> MACHINE_CASING = ITEMS.register(
            "machine_casing",
            () -> new BlockItem(ModBlocks.MACHINE_CASING.get(), new Item.Properties())
    );

    private ModItems() {
    }
}
