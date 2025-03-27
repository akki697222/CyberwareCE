package flaxbeard.cyberware.api.hud;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public class HudData implements IHudSaveData {
    private final Map<String, String> strings;
    private final Map<String, Boolean> booleans;
    private final Map<String, Float> floats;
    private final Map<String, Integer> integers;

    public HudData() {
        this.strings = new HashMap<>();
        this.booleans = new HashMap<>();
        this.floats = new HashMap<>();
        this.integers = new HashMap<>();
    }

    @Override
    public void setString(String key, String s) {
        strings.put(key, s);
    }

    @Override
    public String getString(String key) {
        return strings.getOrDefault(key, "");
    }

    @Override
    public void setBoolean(String key, boolean b) {
        booleans.put(key, b);
    }

    @Override
    public boolean getBoolean(String key) {
        return booleans.getOrDefault(key, false);
    }

    @Override
    public void setFloat(String key, float f) {
        floats.put(key, f);
    }

    @Override
    public float getFloat(String key) {
        return floats.getOrDefault(key, 0.0f);
    }

    @Override
    public void setInteger(String key, int i) {
        integers.put(key, i);
    }

    @Override
    public int getInteger(String key) {
        return integers.getOrDefault(key, 0);
    }

    public static final Codec<HudData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("strings").forGetter(h -> h.strings),
                    Codec.unboundedMap(Codec.STRING, Codec.BOOL).fieldOf("booleans").forGetter(h -> h.booleans),
                    Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("floats").forGetter(h -> h.floats),
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("integers").forGetter(h -> h.integers)
            ).apply(instance, HudData::new)
    );

    private HudData(Map<String, String> strings, Map<String, Boolean> booleans,
                    Map<String, Float> floats, Map<String, Integer> integers) {
        this.strings = new HashMap<>(strings);
        this.booleans = new HashMap<>(booleans);
        this.floats = new HashMap<>(floats);
        this.integers = new HashMap<>(integers);
    }

    public HudData cloneToNewInstance() {
        return new HudData(strings, booleans, floats, integers);
    }
}