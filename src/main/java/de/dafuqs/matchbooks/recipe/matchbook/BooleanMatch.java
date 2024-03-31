package de.dafuqs.matchbooks.recipe.matchbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class BooleanMatch extends Match {
    public static final String TYPE = "boolean";

    private boolean booleanValue;

    public BooleanMatch(String name, String key) {
        super(name, key);
    }

    @Override
    boolean matches(CompoundTag nbt) {
        if(nbt != null && nbt.contains(key)) {
            return nbt.getBoolean(key) == booleanValue;
        }

        return false;
    }

    @Override
    void configure(JsonObject json) {
        booleanValue = json.get("value").getAsBoolean();
    }

    @Override
    void configure(FriendlyByteBuf buf) {
        booleanValue = buf.readBoolean();
    }

    @Override
    JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add(RecipeParser.TYPE, new JsonPrimitive(TYPE));
        main.add(RecipeParser.KEY, new JsonPrimitive(this.name));
        main.add("value", new JsonPrimitive(this.booleanValue));
        return main;
    }

    @Override
    void write(FriendlyByteBuf buf) {
        buf.writeBoolean(booleanValue);
    }

    public static class Factory extends MatchFactory<BooleanMatch> {

        public Factory() {
            super(TYPE);
        }

        @Override
        public BooleanMatch create(String key, JsonObject object) {
            var match = new BooleanMatch(name, key);
            match.configure(object);

            return match;
        }

        @Override
        public BooleanMatch fromPacket(FriendlyByteBuf buf) {
            var match = new BooleanMatch(name, buf.readUtf());
            match.configure(buf);

            return match;
        }
    }

}
