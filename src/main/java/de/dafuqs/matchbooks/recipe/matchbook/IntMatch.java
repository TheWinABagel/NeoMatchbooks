package de.dafuqs.matchbooks.recipe.matchbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class IntMatch extends Match {
    public static final String TYPE = "int";

    private int targetInt;

    public IntMatch(String name, String key) {
        super(name, key);
    }

    @Override
    boolean matches(CompoundTag nbt) {
        if(nbt != null && nbt.contains(key)) {
            return nbt.getInt(key) == targetInt;
        }

        return false;
    }

    @Override
    void configure(JsonObject json) {
        targetInt = json.get(RecipeParser.TARGET).getAsInt();
    }

    @Override
    void configure(FriendlyByteBuf buf) {
        targetInt = buf.readInt();
    }

    @Override
    JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add(RecipeParser.TYPE, new JsonPrimitive(TYPE));
        main.add(RecipeParser.KEY, new JsonPrimitive(this.name));
        main.add(RecipeParser.TARGET, new JsonPrimitive(targetInt));
        return main;
    }

    @Override
    void write(FriendlyByteBuf buf) {
        buf.writeInt(targetInt);
    }

    public static class Factory extends MatchFactory<IntMatch> {

        public Factory() {
            super(TYPE);
        }

        @Override
        public IntMatch create(String key, JsonObject object) {
            var match = new IntMatch(name, key);
            match.configure(object);

            return match;
        }

        @Override
        public IntMatch fromPacket(FriendlyByteBuf buf) {
            var match = new IntMatch(name, buf.readUtf());
            match.configure(buf);

            return match;
        }
    }

}
