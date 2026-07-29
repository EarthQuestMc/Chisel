package team.chisel.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/**
 * Holds the static block/item ids used by Chisel. Ids are stored in their own config file so that a world can be moved
 * between installs without the blocks shifting around.
 */
public class ConfigRegistry {

	public static Configuration config;

	public static int StartBlockID = 450;
	public static int StartItemID = 4100;

	public static void init(File configFile) {
		config = new Configuration(configFile);

		try {
			config.load();

			StartBlockID = config.get("General", "Start Block ID", 450, "First id handed out to Chisel blocks (0-4095)").getInt();
			StartItemID = config.get("General", "Start Item ID", 4100, "First id handed out to Chisel items (4096-31999)").getInt();
		} finally {
			save();
		}
	}

	public static void save() {
		if (config != null && config.hasChanged()) {
			config.save();
		}
	}
}
