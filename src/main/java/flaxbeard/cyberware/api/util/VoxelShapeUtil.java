package flaxbeard.cyberware.api.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeUtil {
    public static VoxelShape rotateShape(VoxelShape shape, int degrees) {
        VoxelShape rotatedShape = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            AABB rotatedBox = switch (degrees) {
                case 90 -> new AABB(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
                case 180 -> new AABB(1 - box.maxX, box.minY, 1 - box.maxZ, 1 - box.minX, box.maxY, 1 - box.minZ);
                case 270 -> new AABB(box.minZ, box.minY, 1 - box.maxX, box.maxZ, box.maxY, 1 - box.minX);
                default -> box;
            };

            rotatedShape = Shapes.joinUnoptimized(rotatedShape, Shapes.create(rotatedBox), BooleanOp.OR);
        }

        return rotatedShape;
    }
}
