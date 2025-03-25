package flaxbeard.cyberware.common;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.capabilities.EntityCapability;

public class CyberwareCapabilities {
    public static final EntityCapability<Entity, ICyberwareUserData> CYBERWARE_CAPABILITY =
            EntityCapability.create(CyberwareAPI.modResource("cyberware"), Entity.class, ICyberwareUserData.class);
}
