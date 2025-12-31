package com.ashx.shulkerbox;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.ClickType;

public class ShulkerBoxScreenHandler extends ShulkerBoxMenu {
    private final Player player;
    private final int slot;
    private final SimpleContainer shulkerContainer;
    private final int lockedSlotIndex;

    public ShulkerBoxScreenHandler(int syncId, Inventory playerInventory,
                                   SimpleContainer container, int slot) {
        super(syncId, playerInventory, container);
        this.player = playerInventory.player;
        this.slot = slot;
        this.shulkerContainer = container;

        // Slot layout: 0-26 shulker box, 27-53 inventory, 54-62 hotbar
        this.lockedSlotIndex = 54 + slot;
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        if (slotIndex == lockedSlotIndex) {
            return;
        }
        super.clicked(slotIndex, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex == lockedSlotIndex) {
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, slotIndex);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            ItemStack shulkerStack = player.getInventory().getItem(slot);
            ShulkerBoxHandler.saveToShulkerBox(shulkerStack, shulkerContainer);
        }
    }
}