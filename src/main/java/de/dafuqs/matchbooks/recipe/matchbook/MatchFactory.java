package de.dafuqs.matchbooks.recipe.matchbook;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A factory, tasked with both creating and configuring Matches, and also building them from packets.
 * Ensure the name matches the registry id.
 */
public abstract class MatchFactory<T extends Match> {

    protected final String name;

    protected MatchFactory(String name) {
        this.name = name;
    }

    public abstract T create(String key, JsonObject object);

    public abstract T fromPacket(FriendlyByteBuf buf);

    public static Optional<MatchFactory<?>> getForPacket(String name) {
        return MatchRegistry.getOptional(name);
    }

}
