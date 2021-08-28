package xt9.deepmoblearningbm.common.tile;

import WayofTime.bloodmagic.altar.AltarUpgrade;
import WayofTime.bloodmagic.altar.BloodAltar;
import WayofTime.bloodmagic.block.enums.BloodRuneType;
import WayofTime.bloodmagic.tile.TileAltar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import xt9.deepmoblearning.common.energy.DeepEnergyStorage;
import xt9.deepmoblearning.common.handlers.BaseItemHandler;
import xt9.deepmoblearning.common.handlers.DataModelHandler;
import xt9.deepmoblearning.common.items.ItemDataModel;
import xt9.deepmoblearning.common.util.DataModel;
import xt9.deepmoblearningbm.DeepMobLearningBM;
import xt9.deepmoblearningbm.ModConfig;
import xt9.deepmoblearningbm.client.gui.DigitalAgonizerGui;
import xt9.deepmoblearningbm.common.inventory.CatalystInputHandler;
import xt9.deepmoblearningbm.common.inventory.ContainerDigitalAgonizer;
import xt9.deepmoblearningbm.util.Catalyst;
import xt9.deepmoblearningbm.util.EssenceHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by xt9 on 2018-06-30.
 */
public class TileEntityDigitalAgonizer extends TileEntity implements ITickable, IContainerProvider {
    public static final int GUI_ID = 1;

    private BaseItemHandler dataModel = new DataModelHandler();
    private BaseItemHandler input = new CatalystInputHandler();
    private DeepEnergyStorage energyCap = new DeepEnergyStorage(ModConfig.getagonizerRFStorage(), ModConfig.getagonizerRFInput(), 0, 0);
    private int highlightingTicks = 0;
    private int catalystOperations = 0;
    private int catalystOperationsMax = 0;
    private int saveTicks = 0;
    private int progress = 0;
    private BlockPos altarPos = BlockPos.fromLong(0);
    private int numOfSacrificeRunes = 0;
    private double multiplier = 1.0;

    @Override
    public void update() {
        if(!world.isRemote) {
            saveTicks++;

            if(highlightingTicks > 0) {
                highlightingTicks--;
                if(highlightingTicks == 0) {
                    updateState(true);
                }
            }

            if(catalystOperations == 0) {
                consumeCatalyst();
            }

            if(canContinueCraft()) {
                if(progress == 0) {
                    updateSacrificeRuneCount();
                }

                progress++;
                energyCap.voidEnergy(ModConfig.getAgonizerRFCost());
                if(progress % 60 == 0) {
                    fillAltarTank();
                    progress = 0;
                }
            } else if(!(hasDataModel() && isValidDataModelTier())) {
                progress = 0;
            } else if(getAltarTank() == null) {
                setAltarPos(BlockPos.fromLong(0));
                numOfSacrificeRunes = 0;
                progress = 0;
            }

            doStaggeredDiskSave(100);
        } else {
            if(highlightingTicks > 0) {
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                if(getAltarTank() != null) {
                    DeepMobLearningBM.proxy.spawnParticle(
                        world,
                        getAltarPos().getX() + 0.5D + rand.nextDouble(-0.33D, 0.33D),
                        getAltarPos().getY() + 1.2D,
                        getAltarPos().getZ() + 0.5D + rand.nextDouble(-0.33D, 0.33D),
                        rand.nextDouble(-0.02D, 0.02D),
                        0,
                        rand.nextDouble(-0.02D, 0.02D)
                    );
                }
            }
        }
    }

    public double getSacrificeMultiplier() {
        BloodAltar altar = getAltarTank();
        if(altar != null) {
            return altar.getSacrificeMultiplier();
        } else {
            return 0;
        }
    }

    public void updateSacrificeRuneCount() {
        BloodAltar altar = getAltarTank();
        if(altar != null) {
            AltarUpgrade altarUpgrade = altar.getUpgrade();
            if(altarUpgrade != null) {
                numOfSacrificeRunes = altarUpgrade.getLevel(BloodRuneType.SACRIFICE);
            } else {
                numOfSacrificeRunes = 0;
            }
        } else {
            numOfSacrificeRunes = 0;
        }
    }

    private void fillAltarTank() {
        BloodAltar altar = getAltarTank();
        if(altar != null) {
            altar.fillMainTank(getFillAmount());
        }

        if(catalystOperations > 0) {
            catalystOperations--;
        } else {
            multiplier = 1.0;
        }
    }

    private void consumeCatalyst() {
        Catalyst catalyst = Catalyst.getCatalyst(getCatalystStack());
        if(catalyst != null) {
            catalystOperations = catalyst.getOperations();
            catalystOperationsMax = catalyst.getOperations();
            multiplier = catalyst.getMultiplier();
            getCatalystStack().shrink(1);
        }
    }

    private boolean canContinueCraft() {
        return energyCap.getEnergyStored() > ModConfig.getAgonizerRFCost() && !altarIsFull() && hasDataModel() && isValidDataModelTier();
    }

    public BlockPos getAltarPos() {
        return altarPos;
    }

    public void setAltarPos(BlockPos pos) {
        altarPos = pos;
    }

    public BloodAltar getAltarTank() {
        if(altarPos != null) {
            TileEntity tile = world.getTileEntity(altarPos);
            if(tile instanceof TileAltar) {
                return (BloodAltar) tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            } else {
                return null;
            }
        }
        return null;
    }

    private boolean altarIsFull() {
        return getAltarTank() == null || (getAltarTank().getFluidAmount() + getFillAmount()) >= getAltarTank().getCapacity();
    }

    public void setHighlightingTicks(int highlightingTicks) {
        this.highlightingTicks = highlightingTicks;
        updateState(true);
    }

    public int getNumOfSacrificeRunes() {
        return numOfSacrificeRunes;
    }

    private ItemStack getCatalystStack() {
        return input.getStackInSlot(0);
    }

    private ItemStack getDataModelStack() {
        return dataModel.getStackInSlot(0);
    }

    public boolean hasDataModel() {
        return getDataModelStack().getItem() instanceof ItemDataModel;
    }

    public boolean isValidDataModelTier() {
        return DataModel.getTier(getDataModelStack()) != 0;
    }

    public boolean hasValidCatalyst() {
        return Catalyst.isValidCatalyst(getCatalystStack());
    }

    public int getFillAmount() {
        return EssenceHelper.getFillAmount(getDataModelStack(), (getMultiplier() + getSacrificeMultiplier()));
    }

    public int getCatalystOperations() {
        return catalystOperations;
    }

    public int getCatalystOperationsMax() {
        return catalystOperationsMax;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public int getProgress() {
        return progress;
    }

    private void doStaggeredDiskSave(int divisor) {
        if(saveTicks % divisor == 0) {
            markDirty();
            saveTicks = 0;
        }
    }

    public void updateState(boolean markDirty) {
        IBlockState state = world.getBlockState(getPos());
        world.notifyBlockUpdate(getPos(), state, state, 3);
        if(markDirty) {
            markDirty();
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 3, writeToNBT(new NBTTagCompound()));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("highlightingTicks", highlightingTicks);
        compound.setInteger("catalystOperations", catalystOperations);
        compound.setInteger("catalystOperationsMax", catalystOperationsMax);
        compound.setInteger("progress", progress);
        compound.setDouble("mutliplier", multiplier);
        compound.setLong("altarPos", altarPos.toLong());
        compound.setInteger("numOfSacrificeRunes", numOfSacrificeRunes);
        compound.setTag("dataModel", dataModel.serializeNBT());
        compound.setTag("input", input.serializeNBT());
        energyCap.writeEnergy(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        highlightingTicks = compound.hasKey("highlightingTicks", Constants.NBT.TAG_INT) ? compound.getInteger("highlightingTicks") : 0;
        catalystOperations = compound.hasKey("catalystOperations", Constants.NBT.TAG_INT) ? compound.getInteger("catalystOperations") : 0;
        catalystOperationsMax = compound.hasKey("catalystOperationsMax", Constants.NBT.TAG_INT) ? compound.getInteger("catalystOperationsMax") : 0;
        progress = compound.hasKey("progress", Constants.NBT.TAG_INT) ? compound.getInteger("progress") : 0;
        multiplier = compound.hasKey("mutliplier", Constants.NBT.TAG_DOUBLE) ? compound.getDouble("mutliplier") : 1.0;
        numOfSacrificeRunes = compound.hasKey("numOfSacrificeRunes", Constants.NBT.TAG_INT) ? compound.getInteger("numOfSacrificeRunes") : 0;
        altarPos = compound.hasKey("altarPos", Constants.NBT.TAG_LONG) ? BlockPos.fromLong(compound.getLong("altarPos")) : null;
        dataModel.deserializeNBT(compound.getCompoundTag("dataModel"));
        input.deserializeNBT(compound.getCompoundTag("input"));
        energyCap.readEnergy(compound);
        super.readFromNBT(compound);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean hasCapability(Capability capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new CombinedInvWrapper(dataModel, input));
        } if(capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyCap);
        } else {
            return super.getCapability(capability, facing);
        }
    }

    @Override
    public ContainerDigitalAgonizer getContainer(TileEntity entity, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerDigitalAgonizer((TileEntityDigitalAgonizer) world.getTileEntity(new BlockPos(x, y, z)), player.inventory, world);
    }

    @Override
    public DigitalAgonizerGui getGui(TileEntity entity, EntityPlayer player, World world, int x, int y, int z) {
        return new DigitalAgonizerGui((TileEntityDigitalAgonizer) world.getTileEntity(new BlockPos(x, y, z)), player.inventory, world);
    }
}
