package de.dafuqs.matchbooks.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ItemDamagingRecipe<C extends CraftingContainer> extends ShapelessRecipe {

    public ItemDamagingRecipe(ShapelessRecipe parent) {
        super(parent.getGroup(), parent.category(), parent.getResultItem(null), parent.getIngredients());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inventory) {
        NonNullList<ItemStack> defaultedList = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem().canBeDepleted() && stack.getDamageValue() + 1 < stack.getMaxDamage()) { // Override damageable, fallback onto remainders
                stack = stack.copy();
                stack.setDamageValue(stack.getDamageValue() + 1); // Damage item by one
                defaultedList.set(i, stack);
            } else if (stack.getItem().hasCraftingRemainingItem()) {
                assert stack.getItem().getCraftingRemainingItem() != null;
                defaultedList.set(i, new ItemStack(stack.getItem().getCraftingRemainingItem()));
            }
        }
        return defaultedList;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return de.dafuqs.matchbooks.recipe.ItemDamagingRecipe.Serializer.INSTANCE;
    }

    public static class Serializer extends ShapelessRecipe.Serializer {

        public static final de.dafuqs.matchbooks.recipe.ItemDamagingRecipe.Serializer INSTANCE = new de.dafuqs.matchbooks.recipe.ItemDamagingRecipe.Serializer();

        @Override
        public ShapelessRecipe fromNetwork(FriendlyByteBuf buf) {
            return new ItemDamagingRecipe<>(super.fromNetwork(buf));
        }

    }
}

