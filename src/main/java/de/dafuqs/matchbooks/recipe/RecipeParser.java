package de.dafuqs.matchbooks.recipe;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.dafuqs.matchbooks.Matchbooks;
import de.dafuqs.matchbooks.recipe.matchbook.MatchRegistry;
import de.dafuqs.matchbooks.recipe.matchbook.Matchbook;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Deprecated(since = "1.20.4")
public class RecipeParser {
    public static final String COUNT = "count";
    public static final String ITEM = "item";
    public static final String KEY = "key";
    public static final String MATCHBOOK = "matchbook";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String TARGET = "target";
    public static final String TYPE = "type";

    public static JsonObject fromInputStream(InputStream in) {
        return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
    }

    public static ItemStack stackFromJson(JsonObject json, String elementName) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(json.get(elementName).getAsString()));
        int count = json.has(COUNT) ? json.get("count").getAsInt() : 1;
        return item != Items.AIR ? new ItemStack(item, count) : ItemStack.EMPTY;
    }

    public static ItemStack stackFromJson(JsonObject json) {
        return stackFromJson(json, ITEM);
    }

    public static IngredientStack ingredientStackFromJson(JsonObject json) {
        Ingredient ingredient = json.has("ingredient") ? Ingredient.fromJson(json.getAsJsonObject("ingredient"), false) : Ingredient.fromJson(json, false);
        var matchbook = Matchbook.empty();
        CompoundTag recipeViewNbt = null;
        int count = json.has(COUNT) ? json.get(COUNT).getAsInt() : 1;

        if (json.has(MATCHBOOK)) {
            try {
                matchbook = matchbookFromJson(json.getAsJsonObject(MATCHBOOK));
            } catch (MalformedJsonException e) {
                Matchbooks.LOGGER.error("Relayed exception: " + e);
            }
        }

        if (json.has("recipeViewNbt")) {
            try {
                recipeViewNbt = NbtUtils.snbtToStructure(json.get("recipeViewNbt").getAsString());
            } catch (CommandSyntaxException e) {
                Matchbooks.LOGGER.error("Relayed exception: " + e);
            }
        }

        return IngredientStack.of(ingredient, matchbook, recipeViewNbt, count);
    }

    public static List<IngredientStack> ingredientStacksFromJson(JsonArray array, int size) {
         List<IngredientStack> ingredients = new ArrayList<>(size);
         int dif = size - array.size();
        for (int i = 0; i < array.size() && i < size; i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            ingredients.add(ingredientStackFromJson(object));
        }
        if(dif > 0) {
            for (int i = 0; i < dif; i++) {
                ingredients.add(IngredientStack.EMPTY);
            }
        }
        return ingredients;
    }

    public static OptionalStack optionalStackFromJson(JsonObject json) throws MalformedJsonException {
        int count = json.has(COUNT) ? json.get(COUNT).getAsInt() : 1;
        if(json.has(ITEM)) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(json.get("item").getAsString()));
            return item != Items.AIR ? new OptionalStack(new ItemStack(item, count), count) : OptionalStack.EMPTY;
        }
        else if(json.has("tag")) {
            var tagId = ResourceLocation.tryParse(json.get("tag").getAsString());
            var tag = TagKey.create(BuiltInRegistries.ITEM.key(), tagId);
            return !RegistryHelper.isTagEmpty(tag) ? new OptionalStack(tag, count) : OptionalStack.EMPTY;
        }
        else {
            throw new MalformedJsonException("OptionalStacks must have an item or tag!");
        }
    }

    public static List<OptionalStack> optionalStacksFromJson(JsonArray array, int size) throws MalformedJsonException {
        List<OptionalStack> stacks = new ArrayList<>(size);
        int dif = size - array.size();
        for (int i = 0; i < array.size() && i < size; i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            stacks.add(optionalStackFromJson(object));
        }
        if(dif > 0) {
            for (int i = 0; i < dif; i++) {
                stacks.add(OptionalStack.EMPTY);
            }
        }
        return stacks;
    }

    public static Matchbook matchbookFromJson(JsonObject json) throws MalformedJsonException {
        var builder = new Matchbook.Builder();
        var matchArray = json.getAsJsonArray("matches");

        var mode = Matchbook.Mode.valueOf(json.get("mode").getAsString());

        for (int i = 0; i < matchArray.size(); i++) {
            var entry = matchArray.get(i).getAsJsonObject();
            var id = entry.get(TYPE).getAsString();
            var key = entry.get(KEY).getAsString();

            var optional = MatchRegistry.getOptional(id);
            if(optional.isEmpty()) {
                throw new MalformedJsonException("Invalid Match Type at index " + i);
            }

            var factory = optional.get();

            builder.add(factory.create(key, entry));
        }

        return builder.build(mode);
    }

    public static JsonElement asJson(Tag nbt) {
        if (nbt == null) {
            return JsonNull.INSTANCE;
        }
        if (nbt instanceof StringTag s) return new JsonPrimitive(s.getAsString());
        if (nbt instanceof ByteTag b) return new JsonPrimitive(b.getAsByte() == 1);
        if (nbt instanceof NumericTag n) return new JsonPrimitive(n.getAsNumber());
        if (nbt instanceof CollectionTag<?> l) {
            JsonArray arr =  new JsonArray();
            l.stream().map(RecipeParser::asJson).forEach(arr::add);
            return arr;
        }
        if (nbt instanceof CompoundTag c) {
            JsonObject o = new JsonObject();
            c.getAllKeys().forEach(k -> o.add(k, asJson(c.get(k))));
            return o;
        }
        return null;
    }
}
