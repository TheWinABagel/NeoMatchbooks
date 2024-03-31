package de.dafuqs.matchbooks.recipe;

import de.dafuqs.matchbooks.Matchbooks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class MatchbookRecipeTypes {

    public static void init() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Matchbooks.id("item_damaging"), ItemDamagingRecipe.Serializer.INSTANCE);
    }
}
