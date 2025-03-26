package flaxbeard.cyberware.common.contents.item.cyberware;

import flaxbeard.cyberware.common.contents.item.CyberwareItem;

public abstract class EyeCyberware extends CyberwareItem {
    public EyeCyberware(Properties properties) {
        super(properties, BodyRegion.EYES);
    }
}
