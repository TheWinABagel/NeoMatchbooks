package de.dafuqs.matchbooks.recipe;

import de.dafuqs.matchbooks.Matchbooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class MatchbookRecipeTypes {

    @SubscribeEvent
    public static void registerSerializer(RegisterEvent e) {
        e.register(BuiltInRegistries.RECIPE_SERIALIZER.key(),
                helper -> helper.register(Matchbooks.id("item_damaging"), ItemDamagingRecipe.Serializer.INSTANCE));
    }
}
