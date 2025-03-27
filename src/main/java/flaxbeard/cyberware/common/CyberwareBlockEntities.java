package flaxbeard.cyberware.common;

import flaxbeard.cyberware.Cyberware;
import flaxbeard.cyberware.common.contents.block.entity.SurgeryBlockEntity;
import flaxbeard.cyberware.common.contents.block.entity.SurgeryChamberBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CyberwareBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Cyberware.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurgeryBlockEntity>> SURGERY = REGISTER.register(
            "surgery",
            () -> BlockEntityType.Builder.of(
                    SurgeryBlockEntity::new,
                    CyberwareBlocks.SURGERY.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurgeryChamberBlockEntity>> SURGERY_CHAMBER = REGISTER.register(
            "surgery_chamber",
            () -> BlockEntityType.Builder.of(
                    SurgeryChamberBlockEntity::new,
                    CyberwareBlocks.SURGERY_CHAMBER.get()
            ).build(null)
    );
}
