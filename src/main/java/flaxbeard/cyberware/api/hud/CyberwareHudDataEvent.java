package flaxbeard.cyberware.api.hud;

import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class CyberwareHudDataEvent extends Event {
    private final List<IHudElement> elements = new ArrayList<>();
    private boolean hudjackAvailable;
    private final int width;
    private final int height;

    public CyberwareHudDataEvent(int width, int height, boolean hudjackAvailable) {
        super();
        this.width = width;
        this.height = height;
        this.hudjackAvailable = hudjackAvailable;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isHudjackAvailable() {
        return hudjackAvailable;
    }

    public void setHudjackAvailable(boolean hudjackAvailable) {
        this.hudjackAvailable = hudjackAvailable;
    }

    public List<IHudElement> getElements()
    {
        return elements;
    }

    public void addElement(IHudElement element)
    {
        elements.add(element);
    }
}
