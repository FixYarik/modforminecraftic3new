package com.industrialevolution.registry;

import com.industrialevolution.IndustrialEvolution;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IndustrialEvolution.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.industrialevolution.main"))
                    .icon(() -> new ItemStack(ModItems.MACHINE_CASING.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RUBBER.get());
                        output.accept(ModItems.MACHINE_CASING.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }
}
