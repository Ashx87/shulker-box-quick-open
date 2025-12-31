package com.ashx.shulkerbox;
import com.ashx.shulkerbox.mixin.InventoryAccessor;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;

import java.util.ArrayList;
import java.util.List;

public class ShulkerBoxHandler {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            // Only handle main hand
            if (hand != InteractionHand.MAIN_HAND) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);

            // Check if it is a shulker box
            if (!isShulkerBox(stack)) {
                return InteractionResult.PASS;
            }

            // Check if pointing at air
            HitResult hitResult = player.pick(5.0, 0, false);
            if (hitResult.getType() != HitResult.Type.MISS) {
                return InteractionResult.PASS;
            }

            // Only process on server side
            if (!world.isClientSide()) {
                int slot = ((InventoryAccessor) player.getInventory()).getSelected();
                SimpleContainer container = new SimpleContainer(27);
                loadFromShulkerBox(stack, container);
                Component displayName = stack.getHoverName();

                player.openMenu(new SimpleMenuProvider(
                        (syncId, playerInventory, p) -> {
                            return new ShulkerBoxScreenHandler(syncId, playerInventory, container, slot);
                        },
                        displayName
                ));
            }

            return InteractionResult.SUCCESS;
        });

        ShulkerBoxQuickOpen.LOGGER.info("ShulkerBox quick open event register success");
    }

    public static boolean isShulkerBox(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock() instanceof ShulkerBoxBlock;
        }
        return false;
    }

    public static void loadFromShulkerBox(ItemStack shulkerStack, SimpleContainer container) {
        ItemContainerContents contents = shulkerStack.get(DataComponents.CONTAINER);
        if (contents != null) {
            List<ItemStack> items = new ArrayList<>();
            contents.nonEmptyItems().forEach(items::add);
            for (int i = 0; i < Math.min(items.size(), 27); i++) {
                container.setItem(i, items.get(i).copy());
            }
        }
    }

    public static void saveToShulkerBox(ItemStack shulkerStack, SimpleContainer container) {
        if (!isShulkerBox(shulkerStack)) return;

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty()) {
                items.add(s.copy());
            }
        }

        if (items.isEmpty()) {
            shulkerStack.remove(DataComponents.CONTAINER);
        } else {
            shulkerStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }
}