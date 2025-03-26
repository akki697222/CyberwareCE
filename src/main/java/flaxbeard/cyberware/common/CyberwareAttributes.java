package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CyberwareAttributes {
    public static final DeferredRegister<Attribute> REGISTER =
            DeferredRegister.create(Registries.ATTRIBUTE, Cyberware.MOD_ID);

    /**
     * Maximum Tolerance, per-player
     */
    public static final DeferredHolder<Attribute, Attribute> TOLERANCE = REGISTER.register("tolerance",
            () -> new RangedAttribute(
                    "cyberware.tolerance",
                    CyberwareConfig.essence,
                    0.0,
                    Double.MAX_VALUE
            ).setSyncable(true)
    );
}
