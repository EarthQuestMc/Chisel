package team.chisel.proxy;

import java.lang.reflect.Constructor;

import team.chisel.Chisel;
import team.chisel.block.tileentity.TileEntityAutoChisel;
import team.chisel.block.tileentity.TileEntityCarvableBeacon;
import team.chisel.block.tileentity.TileEntityPresent;
import team.chisel.config.Configurations;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;

import com.google.common.collect.ObjectArrays;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

	private int registryItemID;
	private int registryBlockID;

	public void registryInit() {
		this.registryBlockID = Configurations.startBlockID;
		this.registryItemID = Configurations.startItemID;
	}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityAutoChisel.class, "autoChisel");
		GameRegistry.registerTileEntity(TileEntityPresent.class, "tile.chisel.present");
		GameRegistry.registerTileEntity(TileEntityCarvableBeacon.class, "tile.chisel.beacon");
	}

	public void preInit() {
	}

	public void init() {
	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public void itemRegister(Item item, String name) {
		while (isIdUsed(this.registryItemID)) {
			this.registryItemID++;
		}

		int itemID = Configurations.config.get(Configurations.CATEGORY_ITEM_IDS, name, this.registryItemID).getInt();
		if (itemID == this.registryItemID) {
			Configurations.save();
			this.registryItemID++;
		}

		itemID = resolveId(itemID, this.registryItemID, name);

		GameData.getItemRegistry().addObject(itemID, name, item);
	}

	public void blockRegister(Block block, String name) {
		blockRegister(block, ItemBlock.class, name);
	}

	public void blockRegister(Block block, Class<? extends ItemBlock> itemBlockClass, String name, Object... itemCtorArgs) {
		while (isIdUsed(this.registryBlockID)) {
			this.registryBlockID++;
		}

		int blockID = Configurations.config.get(Configurations.CATEGORY_BLOCK_IDS, name, this.registryBlockID).getInt();
		if (blockID == this.registryBlockID) {
			Configurations.save();
			this.registryBlockID++;
		}

		blockID = resolveId(blockID, this.registryBlockID, name);

		ItemBlock itemBlock = null;
		if (itemBlockClass != null) {
			try {
				Class<?>[] ctorArgClasses = new Class<?>[itemCtorArgs.length + 1];
				ctorArgClasses[0] = Block.class;
				for (int i = 1; i < ctorArgClasses.length; i++) {
					ctorArgClasses[i] = itemCtorArgs[i - 1].getClass();
				}
				Constructor<? extends ItemBlock> itemCtor = itemBlockClass.getConstructor(ctorArgClasses);
				itemBlock = itemCtor.newInstance(ObjectArrays.concat(block, itemCtorArgs));
			} catch (Exception e) {
				Chisel.logger.error("Could not build the ItemBlock for " + name, e);
				return;
			}
		}

		if (itemBlock != null) {
			GameData.getItemRegistry().addObject(blockID, name, itemBlock);
		}
		GameData.getBlockRegistry().addObject(blockID, name, block);
	}

	/**
	 * Falls back to the next free id when the configured one is already taken, FML would otherwise silently drop the
	 * registration down into the vanilla id range.
	 */
	private int resolveId(int wantedID, int nextFreeID, String name) {
		if (!isIdUsed(wantedID)) {
			return wantedID;
		}

		int fallback = nextFreeID;
		while (isIdUsed(fallback)) {
			fallback++;
		}
		Chisel.logger.warn("Id " + wantedID + " requested for " + name + " is already taken, using " + fallback + " instead.");
		return fallback;
	}

	private static boolean isIdUsed(int id) {
		return GameData.getBlockRegistry().containsId(id) || GameData.getItemRegistry().containsId(id);
	}
}
