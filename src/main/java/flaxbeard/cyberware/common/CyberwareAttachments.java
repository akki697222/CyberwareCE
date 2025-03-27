package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.api.CyberwareUserData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CyberwareAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Cyberware.MOD_ID);

    public static final Supplier<AttachmentType<CyberwareUserData>> CYBERWARE_USER_DATA =
            REGISTER.register("cyberware_user_data", () ->
                    AttachmentType.builder(CyberwareUserData::new)
                            .serialize(CyberwareUserData.CODEC)
                            .build());
}
