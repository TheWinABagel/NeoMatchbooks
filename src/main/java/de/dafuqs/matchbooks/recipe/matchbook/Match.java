package de.dafuqs.matchbooks.recipe.matchbook;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Tests ItemStack nbt against a set of rules. Defined by json, must be able to be loaded from a bytebuf.
 */
public abstract class Match {

    protected final String name;
    protected final String key;

    protected Match(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    abstract boolean matches(CompoundTag nbt);

    abstract void configure(JsonObject json);

    abstract void configure(FriendlyByteBuf buf);

    abstract JsonObject toJson();

    public void writeInternal(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUtf(key);
        write(buf);
    }

    abstract void write(FriendlyByteBuf buf);

}
