package flaxbeard.cyberware.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CyberwareData {
    private boolean isEnabled;
    private int hotkey;

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

    public static final Codec<CyberwareData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(CyberwareData::isEnabled),
                    Codec.INT.fieldOf("hotkey").forGetter(CyberwareData::getHotkey)
            ).apply(instance, CyberwareData::new)
    );
}
