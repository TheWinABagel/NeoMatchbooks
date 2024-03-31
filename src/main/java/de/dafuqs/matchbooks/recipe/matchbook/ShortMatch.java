package de.dafuqs.matchbooks.recipe.matchbook;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.dafuqs.matchbooks.recipe.RecipeParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ShortMatch extends Match {
    public static final String TYPE = "short";

    private short targetShort;

    public ShortMatch(String name, String key) {
        super(name, key);
    }

    @Override
    boolean matches(CompoundTag nbt) {
        if(nbt != null && nbt.contains(key)) {
            return nbt.getShort(key) == targetShort;
        }

        return false;
    }

    @Override
    void configure(JsonObject json) {
        targetShort = json.get(RecipeParser.TARGET).getAsShort();
    }

    @Override
    void configure(FriendlyByteBuf buf) {
        targetShort = buf.readShort();
    }

    @Override
    JsonObject toJson() {
        JsonObject main = new JsonObject();
        main.add(RecipeParser.TYPE, new JsonPrimitive(TYPE));
        main.add(RecipeParser.KEY, new JsonPrimitive(this.name));
        main.add(RecipeParser.TARGET, new JsonPrimitive(targetShort));
        return main;
    }

    @Override
    void write(FriendlyByteBuf buf) {
        buf.writeShort(targetShort);
    }

    public static class Factory extends MatchFactory<ShortMatch> {

        public Factory() {
            super(TYPE);
        }

        @Override
        public ShortMatch create(String key, JsonObject object) {
            var match = new ShortMatch(name, key);
            match.configure(object);

            return match;
        }

        @Override
        public ShortMatch fromPacket(FriendlyByteBuf buf) {
            var match = new ShortMatch(name, buf.readUtf());
            match.configure(buf);

            return match;
        }
    }

}
