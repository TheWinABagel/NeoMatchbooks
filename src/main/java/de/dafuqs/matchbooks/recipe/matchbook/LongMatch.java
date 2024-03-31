package de.dafuqs.matchbooks.recipe.matchbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class LongMatch extends Match {
    public static final String TYPE = "long";

    private long targetLong;

    public LongMatch(String name, String key) {
        super(name, key);
    }

    @Override
    boolean matches(CompoundTag nbt) {
        if(nbt != null && nbt.contains(key)) {
            return nbt.getLong(key) == targetLong;
        }

        return false;
    }

    @Override
    void configure(JsonObject json) {
        targetLong = json.get(RecipeParser.TARGET).getAsLong();
    }

    @Override
    void configure(FriendlyByteBuf buf) {
        targetLong = buf.readLong();
    }

    @Override
    JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add(RecipeParser.TYPE, new JsonPrimitive(TYPE));
        main.add(RecipeParser.KEY, new JsonPrimitive(this.name));
        main.add(RecipeParser.TARGET, new JsonPrimitive(targetLong));
        return main;
    }

    @Override
    void write(FriendlyByteBuf buf) {
        buf.writeLong(targetLong);
    }

    public static class Factory extends MatchFactory<LongMatch> {

        public Factory() {
            super(TYPE);
        }

        @Override
        public LongMatch create(String key, JsonObject object) {
            var match = new LongMatch(name, key);
            match.configure(object);

            return match;
        }

        @Override
        public LongMatch fromPacket(FriendlyByteBuf buf) {
            var match = new LongMatch(name, buf.readUtf());
            match.configure(buf);

            return match;
        }
    }

}
