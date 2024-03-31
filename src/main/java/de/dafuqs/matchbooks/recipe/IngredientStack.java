package de.dafuqs.matchbooks.recipe;

import com.google.gson.*;
import de.dafuqs.matchbooks.recipe.matchbook.Matchbook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@SuppressWarnings("unused")
public final class IngredientStack {

    public static final IngredientStack EMPTY = new IngredientStack(Ingredient.EMPTY, Matchbook.empty(), Optional.empty(), 0);
    private final Ingredient ingredient;
    private final Matchbook matchbook;
    private final Optional<CompoundTag> recipeViewNbt;
    private final int count;

    private IngredientStack(@NotNull Ingredient ingredient, @NotNull Matchbook matchbook, Optional<CompoundTag> recipeViewNbt, int count) {
        this.ingredient = ingredient;
        this.matchbook = matchbook;
        this.recipeViewNbt = recipeViewNbt;
        this.count = count;
    }

    public static IngredientStack of(@NotNull Ingredient ingredient, @NotNull Matchbook matchbook, @Nullable CompoundTag recipeViewNbt, int count) {
        if(ingredient.isEmpty()) {
            return EMPTY;
        }
        return new IngredientStack(ingredient, matchbook, Optional.ofNullable(recipeViewNbt), count);
    }

    public static IngredientStack of(Ingredient ingredient) {
        return of(ingredient, Matchbook.empty(), null, 1);
    }

    public static IngredientStack ofItems(ItemLike... items) {
        return of(Ingredient.of(items), Matchbook.empty(), null, 1);
    }

    public static IngredientStack ofItems(int count, ItemLike... items) {
        return of(Ingredient.of(items), Matchbook.empty(), null, count);
    }

    public static IngredientStack ofStacks(ItemStack... stacks) {
        return of(Ingredient.of(stacks), Matchbook.empty(), null, 1);
    }

    public static IngredientStack ofStacks(int count, ItemStack... stacks) {
        return of(Ingredient.of(stacks), Matchbook.empty(), null, count);
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count && matchbook.test(stack);
    }

    public boolean testStrict(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() == count && matchbook.test(stack);
    }

    public boolean testCountless(ItemStack stack) {
        return ingredient.test(stack) && matchbook.test(stack);
    }

    public Matchbook getMatchbook() {
        return  matchbook;
    }

    public void write(FriendlyByteBuf buf) {
        ingredient.toNetwork(buf);
        matchbook.write(buf);
        buf.writeBoolean(recipeViewNbt.isPresent());
        recipeViewNbt.ifPresent(buf::writeNbt);
        buf.writeInt(count);
    }

    public JsonElement toJson() {
        JsonObject main = new JsonObject();
        main.add("ingredient", this.ingredient.toJson());
        if (this.count > 1) main.add(RecipeParser.COUNT, new JsonPrimitive(this.count));
        if (!this.matchbook.isEmpty()) main.add(RecipeParser.MATCHBOOK, this.matchbook.toJson());
        if (this.recipeViewNbt.isPresent()) main.add("recipeViewNbt", RecipeParser.asJson(recipeViewNbt.get()));
        return main;
    }

    public static IngredientStack fromByteBuf(FriendlyByteBuf buf) {
        return new IngredientStack(Ingredient.fromNetwork(buf), Matchbook.fromByteBuf(buf), buf.readBoolean() ? Optional.ofNullable(buf.readNbt()) : Optional.empty(), buf.readInt());
    }

    public static List<IngredientStack> decodeByteBuf(FriendlyByteBuf buf, int size) {
        List<IngredientStack> ingredients = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ingredients.add(fromByteBuf(buf));
        }
        return ingredients;
    }

    public List<ItemStack> getStacks() {
        var stacks = ingredient.getItems();

        if (stacks == null)
            return new ArrayList<>();

        return Arrays.stream(stacks)
                .peek(stack -> stack.setCount(count))
                .peek(stack -> recipeViewNbt.ifPresent(stack::setTag))
                .collect(Collectors.toList());
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return this == EMPTY || ingredient.isEmpty();
    }

    public static NonNullList<Ingredient> listIngredients(List<IngredientStack> ingredients) {
        NonNullList<Ingredient> preview = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            preview.set(i, ingredients.get(i).getIngredient());
        }
        return preview;
    }


    public static boolean matchInvExclusively(Container inv, List<IngredientStack> ingredients, int size, int offset) {
        List<ItemStack> invStacks = new ArrayList<>(size);
        for (int i = offset; i < size + offset; i++) {
            invStacks.add(inv.getItem(i));
        }
        AtomicInteger matches = new AtomicInteger();
        ingredients.forEach(ingredient -> {
            for (int i = 0; i < invStacks.size(); i++) {
                if(ingredient.isEmpty()) {
                    matches.getAndIncrement();
                    break;
                }
                ItemStack stack = invStacks.get(i);
                if(ingredient.test(stack)) {
                    matches.getAndIncrement();
                    invStacks.remove(i);
                    break;
                }
            }
        });
        return matches.get() == size;
    }

    public static void decrementExclusively(Container inv, List<IngredientStack> ingredients, int size, int offset) {
        List<ItemStack> invStacks = new ArrayList<>(size);
        for (int i = offset; i < size + offset; i++) {
            invStacks.add(inv.getItem(i));
        }
        ingredients.forEach(ingredient -> {
            for (int i = 0; i < invStacks.size(); i++) {
                if(ingredient.isEmpty()) {
                    break;
                }
                ItemStack stack = invStacks.get(i);
                if(ingredient.test(stack)) {
                    stack.shrink(ingredient.count);
                    invStacks.remove(i);
                    break;
                }
            }
        });
    }
}
