package com.industrialevolution.gametest;

import com.industrialevolution.IndustrialEvolution;
import com.industrialevolution.registry.ModBlocks;
import com.industrialevolution.registry.ModItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/** Server-side checks against registered content and the loaded datapack recipes. */
@GameTestHolder(IndustrialEvolution.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FoundationGameTests {
    @GameTest(template = "empty")
    public static void rubberRecipe(GameTestHelper helper) {
        CraftingContainer input = newGrid();
        input.setItem(0, new ItemStack(Items.SLIME_BALL));
        input.setItem(8, new ItemStack(Items.DRIED_KELP));
        CraftingRecipe recipe = matchingRecipe(helper, input, "rubber");
        assertResult(helper, recipe, input, ModItems.RUBBER.get().getDefaultInstance(), 2);

        input.setItem(0, new ItemStack(Items.DRIED_KELP));
        input.setItem(8, new ItemStack(Items.SLIME_BALL));
        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Rubber recipe must be shapeless");
        input.setItem(8, ItemStack.EMPTY);
        helper.assertTrue(!recipe.matches(input, helper.getLevel()), "Rubber must require slime");
        input.setItem(8, new ItemStack(Items.KELP));
        helper.assertTrue(!recipe.matches(input, helper.getLevel()), "Fresh kelp cannot replace slime");
        input.setItem(8, new ItemStack(Items.SLIME_BALL));
        input.setItem(4, new ItemStack(Items.DIRT));
        helper.assertTrue(!recipe.matches(input, helper.getLevel()), "Extra ingredients must reject the recipe");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void casingRecipe(GameTestHelper helper) {
        CraftingContainer input = newGrid();
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
            input.setItem(slot, new ItemStack(slot == 4 ? ModItems.RUBBER.get() : Items.IRON_INGOT));
        }
        CraftingRecipe recipe = matchingRecipe(helper, input, "machine_casing");
        assertResult(helper, recipe, input, ModItems.MACHINE_CASING.get().getDefaultInstance(), 1);

        input.setItem(4, ItemStack.EMPTY);
        helper.assertTrue(!recipe.matches(input, helper.getLevel()), "Casing must require its rubber core");
        input.setItem(4, new ItemStack(ModItems.RUBBER.get()));
        input.setItem(0, new ItemStack(Items.COPPER_INGOT));
        helper.assertTrue(!recipe.matches(input, helper.getLevel()), "Copper cannot replace iron in the casing");
        input.setItem(0, ItemStack.EMPTY);
        helper.assertTrue(!recipe.matches(input, helper.getLevel()), "All eight iron ingots are required");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void casingPlacementAndHarvest(GameTestHelper helper) {
        BlockPos position = new BlockPos(1, 1, 1);
        helper.setBlock(position, ModBlocks.MACHINE_CASING.get());
        BlockState state = helper.getBlockState(position);
        helper.assertTrue(state.is(ModBlocks.MACHINE_CASING.get()), "Registered casing must be placeable");
        helper.assertTrue(BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(id("machine_casing")),
                "Placed block must retain its namespaced registry identity");
        helper.assertTrue(state.is(BlockTags.MINEABLE_WITH_PICKAXE), "Casing must have the pickaxe mining tag");
        helper.assertTrue(state.is(BlockTags.NEEDS_STONE_TOOL), "Casing must require at least a stone pickaxe");
        helper.assertTrue(state.requiresCorrectToolForDrops(), "Casing must require the correct harvesting tool");
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
        helper.assertTrue(pickaxe.isCorrectToolForDrops(state), "Iron pickaxe must harvest casing");
        helper.assertTrue(!new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(state),
                "A wooden pickaxe must not meet the casing's tool tier");
        helper.assertTrue(!new ItemStack(Items.IRON_AXE).isCorrectToolForDrops(state),
                "An axe must not qualify as the harvesting tool");
        List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), helper.absolutePos(position),
                null, null, pickaxe);
        helper.assertTrue(drops.size() == 1 && drops.get(0).is(ModItems.MACHINE_CASING.get())
                        && drops.get(0).getCount() == 1,
                "Casing loot table must return exactly one casing item");
        helper.succeed();
    }

    private static CraftingRecipe matchingRecipe(GameTestHelper helper, CraftingContainer input, String path) {
        CraftingRecipe recipe = helper.getLevel().getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel())
                .orElseThrow(() -> new AssertionError("No loaded crafting recipe matches " + path));
        helper.assertTrue(recipe.getId().equals(id(path)), "Expected Industrial Evolution recipe " + path);
        return recipe;
    }

    private static void assertResult(GameTestHelper helper, CraftingRecipe recipe, CraftingContainer input,
                                     ItemStack expected, int count) {
        ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(expected.getItem()) && result.getCount() == count,
                "Recipe " + recipe.getId() + " must produce " + count + " of " + expected.getItem());
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(IndustrialEvolution.MOD_ID, path);
    }

    private static CraftingContainer newGrid() {
        return new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        }, 3, 3);
    }
}
