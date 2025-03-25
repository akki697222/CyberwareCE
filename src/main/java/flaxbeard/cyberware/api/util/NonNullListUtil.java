package flaxbeard.cyberware.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class NonNullListUtil {
    public static NonNullList<ItemStack> copyList(NonNullList<ItemStack> nnl){
        NonNullList<ItemStack> nnlCopy = NonNullList.create();
        nnlCopy.addAll(nnl);
        return nnlCopy;
    }

    public static NonNullList<ItemStack> fromArray(@Nonnull ItemStack[] array){
        NonNullList<ItemStack> nnl = NonNullList.create();
        Collections.addAll(nnl, array);
        return nnl;
    }

    public static NonNullList<NonNullList<ItemStack>> fromArray(@Nonnull ItemStack[][] array){
        NonNullList<NonNullList<ItemStack>> nnlRoot = NonNullList.create();
        for (ItemStack[] arraySub : array)
        {
            NonNullList<ItemStack> nnlSub = NonNullList.create();
            nnlSub.addAll(Arrays.asList(arraySub));
            nnlRoot.add(nnlSub);
        }
        return nnlRoot;
    }

    public static NonNullList<ItemStack> initListOfSize(int size){
        NonNullList<ItemStack> nnl = NonNullList.create();
        for (int index = 0; index < size; index++)
        {
            nnl.add(ItemStack.EMPTY);
        }
        return nnl;
    }

    public static final Codec<NonNullList<ItemStack>> CODEC = ItemStack.CODEC.listOf()
            .xmap(
                    list -> {
                        NonNullList<ItemStack> nnl = NonNullList.create();
                        nnl.addAll(list);
                        return nnl;
                    },
                    nnl -> nnl
            );

    // サイズ指定付きのNonNullList<ItemStack>用CODEC
    public static Codec<NonNullList<ItemStack>> codec(int size, ItemStack defaultValue) {
        return ItemStack.CODEC.listOf()
                .xmap(
                        list -> {
                            NonNullList<ItemStack> nnl = NonNullList.withSize(size, defaultValue);
                            for (int i = 0; i < Math.min(list.size(), size); i++) {
                                nnl.set(i, list.get(i));
                            }
                            return nnl;
                        },
                        nnl -> nnl
                );
    }

    // デフォルト値としてItemStack.EMPTYを使用する簡略版
    public static Codec<NonNullList<ItemStack>> codec(int size) {
        return codec(size, ItemStack.EMPTY);
    }

    // NonNullList<NonNullList<ItemStack>>用のCODEC
    public static final Codec<NonNullList<NonNullList<ItemStack>>> NESTED_CODEC = CODEC.listOf()
            .xmap(
                    list -> {
                        NonNullList<NonNullList<ItemStack>> nnl = NonNullList.create();
                        nnl.addAll(list);
                        return nnl;
                    },
                    nnl -> nnl
            );

    // サイズ指定付きのネストされたNonNullList用CODEC
    public static Codec<NonNullList<NonNullList<ItemStack>>> nestedCodec(int outerSize, int innerSize) {
        return codec(innerSize).listOf()
                .xmap(
                        list -> {
                            NonNullList<NonNullList<ItemStack>> nnl = NonNullList.withSize(outerSize, NonNullList.withSize(innerSize, ItemStack.EMPTY));
                            for (int i = 0; i < Math.min(list.size(), outerSize); i++) {
                                nnl.set(i, list.get(i));
                            }
                            return nnl;
                        },
                        nnl -> nnl
                );
    }
}
