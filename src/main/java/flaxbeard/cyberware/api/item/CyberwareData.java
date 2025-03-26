package flaxbeard.cyberware.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CyberwareData {
    private boolean isEnabled;
    private int hotkey;
    private ICyberware.Quality quality;

    public CyberwareData(boolean isEnabled, int hotkey, ICyberware.Quality quality) {
        this.isEnabled = isEnabled;
        this.hotkey = hotkey;
        this.quality = quality;
    }

    public CyberwareData(boolean isEnabled, int hotkey) {
        this.isEnabled = isEnabled;
        this.hotkey = hotkey;
    }

    public CyberwareData(boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.hotkey = -1;
    }

    public CyberwareData() {
        this.isEnabled = false;
        this.hotkey = -1;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public void toggleEnabled() {
        this.isEnabled = !this.isEnabled;
    }

    public void setHotkey(int hotkey) {
        this.hotkey = hotkey;
    }

    public int getHotkey() {
        return hotkey;
    }

    public ICyberware.Quality getQuality() {
        return quality;
    }

    public void setQuality(ICyberware.Quality quality) {
        this.quality = quality;
    }

    public static final Codec<CyberwareData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(CyberwareData::isEnabled),
                    Codec.INT.fieldOf("hotkey").forGetter(CyberwareData::getHotkey),
                    ICyberware.Quality.CODEC.fieldOf("quality").forGetter(CyberwareData::getQuality)
            ).apply(instance, CyberwareData::new)
    );
}
