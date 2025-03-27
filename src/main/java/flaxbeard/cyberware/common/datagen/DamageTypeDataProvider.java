package flaxbeard.cyberware.common.datagen;

import com.google.gson.JsonObject;
import flaxbeard.cyberware.Cyberware;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DamageTypeDataProvider implements DataProvider {
    private final PackOutput output;
    private final Map<String, JsonObject> damageTypes = new HashMap<>();

    public DamageTypeDataProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        // DamageTypeを定義
        addDamageType("brainless", "cyberware.brainless");
        addDamageType("heartless", "cyberware.heartless");
        addDamageType("surgery", "cyberware.surgery");
        addDamageType("spineless", "cyberware.spineless");
        addDamageType("nomuscles", "cyberware.nomuscles");
        addDamageType("noessence", "cyberware.noessence");
        addDamageType("lowessence", "cyberware.lowessence");

        // JSONファイルを生成
        CompletableFuture<?>[] futures = damageTypes.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    JsonObject json = entry.getValue();
                    Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                            .resolve(Cyberware.MOD_ID)
                            .resolve("damage_type")
                            .resolve(name + ".json");
                    return DataProvider.saveStable(cache, json, path);
                })
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    private void addDamageType(String name, String messageId) {
        JsonObject json = new JsonObject();
        json.addProperty("message_id", messageId);
        json.addProperty("exhaustion", (float) 0.1);
        json.addProperty("effects", "hurt");
        json.addProperty("scaling", "when_caused_by_living_non_player");
        damageTypes.put(name, json);
    }

    @Override
    public String getName() {
        return "Cyberware Damage Types";
    }
}