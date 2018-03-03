package com.japaricraft.japaricraftmod.mob;

import com.google.common.collect.Sets;
import com.japaricraft.japaricraftmod.gui.FriendMobNBTs;
import com.japaricraft.japaricraftmod.gui.InventoryFriendEquipment;
import com.japaricraft.japaricraftmod.gui.InventoryFriendMain;
import com.japaricraft.japaricraftmod.handler.JapariItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;

public class EntityFriend extends EntityTameable{
    private static final Set<Item> Heal_ITEMS = Sets.newHashSet(JapariItems.japariman, JapariItems.japarimanapple, JapariItems.japarimancocoa, JapariItems.japarimanfruit);

    private InventoryFriendMain inventoryFriendMain;
    private InventoryFriendEquipment inventoryFriendEquipment;

    protected EntityFriend(World worldIn) {
        super(worldIn);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }
    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }


    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        compound.setTag(FriendMobNBTs.ENTITY_FRIEND_INVENTORY, this.getInventoryFriendMain().writeInventoryToNBT());

        compound.setTag(FriendMobNBTs.ENTITY_FRIEND_EQUIPMENT, this.getInventoryFriendEquipment().writeInventoryToNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        this.getInventoryFriendMain().readInventoryFromNBT(compound.getTagList(FriendMobNBTs.ENTITY_FRIEND_INVENTORY, 10));

        this.getInventoryFriendEquipment().readInventoryFromNBT(compound.getTagList(FriendMobNBTs.ENTITY_FRIEND_EQUIPMENT, 10));

    }

    @Override
    public void damageArmor(float pDamage) {
        pDamage = Math.max(pDamage / 4, 1);

        if (inventoryFriendEquipment.getbootItem() != null && inventoryFriendEquipment.getbootItem().getItem() instanceof ItemArmor) {
            inventoryFriendEquipment.getbootItem().damageItem((int) pDamage, this);
        }
        if (inventoryFriendEquipment.getChestItem() != null && inventoryFriendEquipment.getChestItem().getItem() instanceof ItemArmor) {
            inventoryFriendEquipment.getChestItem().damageItem((int) pDamage, this);
        }
        if (inventoryFriendEquipment.getheadItem() != null && inventoryFriendEquipment.getheadItem().getItem() instanceof ItemArmor) {
            inventoryFriendEquipment.getheadItem().damageItem((int) pDamage, this);
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote) {
            if (this.isEntityAlive()) {
                pickupItem();
            }
            //やばい時はじゃぱりまんを食べる
            if (getHealth() < 15 && this.rand.nextInt(20) == 0) {
                eatJapariman();
            }
        }
    }

    //インベントリにじゃぱりまんが入ってた時に消費して回復する処理
    private void eatJapariman() {
        ItemStack friendsstack;
        //そのスロットに置くとじゃぱりまんを食べてくれる
        friendsstack = getInventoryFriendMain().getStackInSlot(0);

        if (!friendsstack.isEmpty()) {
            //じゃぱりまんがあるか確認
            if (Heal_ITEMS.contains(friendsstack.getItem())) {
                ItemFood itemfood = (ItemFood) friendsstack.getItem();
                this.heal((float) itemfood.getHealAmount(friendsstack));
                friendsstack.shrink(1);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    private void pickupItem() {
        if (ticksExisted % 10 != 0) {
            return;
        }

        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(0.65D))) {
            if (entityItem.isEntityAlive() && entityItem.onGround) {
                ItemStack stack = entityItem.getItem();

                if (!stack.isEmpty()) {
                    stack = onItemStackPickup(stack);

                    if (stack.isEmpty()) {
                        entityItem.setDead();

                        playPickupSound();
                    } else {
                        entityItem.setItem(stack);
                    }
                }
                break;
            }
        }
    }

    public ItemStack onItemStackPickup(ItemStack stack) {
        return getInventoryFriendMain().addItem(stack);
    }

    protected void playPickupSound() {
        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.25F, 0.85F);
    }


    public InventoryFriendMain getInventoryFriendMain()
    {
        if (this.inventoryFriendMain == null) {
            this.inventoryFriendMain = new InventoryFriendMain(this);
        }

        return this.inventoryFriendMain;
    }
    public InventoryFriendEquipment getInventoryFriendEquipment()
    {
        if (this.inventoryFriendEquipment == null) {
            this.inventoryFriendEquipment = new InventoryFriendEquipment(this);
        }

        return this.inventoryFriendEquipment;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        ItemStack itemStack;

        switch (slotIn) {
            case CHEST:

                itemStack = this.getInventoryFriendEquipment().getChestItem();
                break;
            case FEET:

                itemStack = this.getInventoryFriendEquipment().getbootItem();
                break;
            case HEAD:

                itemStack = this.getInventoryFriendEquipment().getheadItem();
                break;

            default:

                itemStack = ItemStack.EMPTY;
                break;
        }

        return itemStack;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        switch (slotIn) {

            case CHEST:

                this.getInventoryFriendEquipment().setInventorySlotContents(0, stack);
                break;
            case FEET:

                this.getInventoryFriendEquipment().setInventorySlotContents(1, stack);
                break;
            case HEAD:

                this.getInventoryFriendEquipment().setInventorySlotContents(2, stack);
                break;
        }
    }

    //死んだ時に持ってるアイテム落とす
    @Override
    public void onDeath(DamageSource cause) {
        World world = this.getEntityWorld();

        if (!world.isRemote) {
            InventoryHelper.dropInventoryItems(world, this, this.getInventoryFriendMain());

            InventoryHelper.dropInventoryItems(world, this, this.getInventoryFriendEquipment());
        }

        super.onDeath(cause);
    }

    //ここから下は体力バーの表示方法
    public EntityFriend.Condition getCondition() {
        int health = (int) this.getHealth();
        int healthMax = (int) this.getMaxHealth();

        EntityFriend.Condition condition = EntityFriend.Condition.FINE;

        if (health < (healthMax / 2)) {
            condition = EntityFriend.Condition.HURT;

            if (health < (healthMax / 4)) {
                condition = EntityFriend.Condition.DYING;
            }
        }

        return condition;
    }

    public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
        if (!(target instanceof EntityGhast)) {
            if (target instanceof EntityTameable) {
                EntityTameable entityTameable = (EntityTameable) target;
                if (entityTameable.isTamed() && entityTameable.getOwner() == owner) {
                    return false;
                }
            }
            if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target)) {
                return false;
            } else {
                return !(target instanceof AbstractHorse) || !((AbstractHorse) target).isTame();
            }
        } else {
            return true;
        }
    }

    public enum Condition
    {

        FINE,
        HURT,
        DYING,

    }
}
