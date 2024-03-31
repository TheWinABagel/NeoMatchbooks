package de.dafuqs.matchbooks.recipe;

import de.dafuqs.matchbooks.Matchbooks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class MatchbookRecipeTypes {

    @SubscribeEvent
    public static void registerSerializer(RegisterEvent e) {
        e.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(),
                helper -> helper.register(Matchbooks.id("item_damaging"), ItemDamagingRecipe.Serializer.INSTANCE));
    }
}
