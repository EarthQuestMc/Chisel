package team.chisel.item;

import team.chisel.Chisel;

/**
 * Backport of the Ender Offset Wand item from Chisel 1.12.2.
 *
 * The 1.12 implementation stores CTM offsets in per-chunk capability data. The
 * 1.7.10 CTMLib renderer has no equivalent hook, so this class intentionally
 * provides the item, recipe and explanatory tooltip without mutating blocks.
 */
public class ItemOffsetTool extends BaseItem {

	public ItemOffsetTool() {
		setMaxStackSize(1);
		setTextureName(Chisel.MOD_ID + ":offsettool");
		setUnlocalizedName("chisel.offsettool");
		setFull3D();
	}
}
