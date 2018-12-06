package com.wazustudio.spigot.custommobsnames;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	private final String PREFIX = "§6[CustomMobsNames] ";
	private FileConfiguration config;
	
	private List<String> disabledWorlds = new ArrayList<String>();
	private boolean setMobNameOnSpawn = true;
	private boolean setMobNameOnChunkLoad = true;
	
	@Override
	public void onEnable() {
		try {
			reload();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("custommobsnames")) {
			if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
				if (sender.hasPermission("custommobsnames.reload")) {
					try {
						reload();
					} catch (Exception e) {
						e.printStackTrace();
					}
					sender.sendMessage(PREFIX + "§aPlugin has been reloaded!");
				} else {
					sender.sendMessage(PREFIX + "§cYou don't have permission to do this.");
				}
			} else {
				sender.sendMessage(PREFIX + "§7Plugin by https://wazustudio.com");
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {
		if (!setMobNameOnSpawn) {
			return;
		}
		if (!isWorldEnabled(event.getEntity().getWorld())) {
			return;
		}
		manageMob(event.getEntity());
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if (!setMobNameOnChunkLoad) {
			return;
		}
		if (!isWorldEnabled(event.getWorld())) {
			return;
		}
		for (Entity entity : event.getChunk().getEntities()) {
			manageMob(entity);
		}
	}
	
	private void manageMob(Entity entity) {
		if (entity == null) {
			return;
		}
		String mobName = getMobName(entity);
		if (mobName == null) {
			return;
		}
		if (!config.contains("mobs." + mobName + ".custom-names")) {
			return;
		}
		if (entity.getCustomName() != null && !config.getBoolean("mobs." + mobName + ".force-change", false)) {
			return;
		}
		List<String> mobNames = config.getStringList("mobs." + mobName + ".custom-names");
		if (mobNames == null || mobNames.isEmpty()) {
			return;
		}
		String randomName = mobNames.get((int)(Math.random()*mobNames.size()));
		if (randomName == null || randomName.isEmpty()) {
			entity.setCustomNameVisible(false);
			entity.setCustomName(null);
			return;
		}
		entity.setCustomNameVisible(true);
		entity.setCustomName(randomName.replace('&', '§'));
	}
	
	private void reload() {
		saveDefaultConfig();
		reloadConfig();
		config = getConfig();
		disabledWorlds = config.getStringList("settings.disabled-worlds");
		if (disabledWorlds == null) {
			disabledWorlds = new ArrayList<String>();
		}
		setMobNameOnSpawn = config.getBoolean("settings.set-mob-name-on-spawn", true);
		setMobNameOnChunkLoad = config.getBoolean("settings.set-mob-name-on-chunk-load", true);
		getLogger().log(Level.INFO, "CustomMobsNames loaded settings:");
		getLogger().log(Level.INFO, "set-mob-name-on-spawn: " + setMobNameOnSpawn);
		getLogger().log(Level.INFO, "set-mob-name-on-chunk-load: " + setMobNameOnChunkLoad);
		if (setMobNameOnChunkLoad) {
			for (World world : getServer().getWorlds()) {
				if (isWorldEnabled(world)) {
					for (Entity entity : world.getEntities()) {
						manageMob(entity);
					}
				}
			}
		}
	}
	
	private String getMobName(Entity entity) {
		if (entity == null || entity.getType() == null) {
			return null;
		}
		//Easy support for 1.7 - 1.13
		switch (entity.getType().name().toLowerCase()) {
		case "armor_stand":
		case "bat":
		case "blaze":
		case "boat":
		case "cave_spider":
		case "chicken":
		case "cod":
		case "cow":
		case "creeper":
		case "dolphin":
		case "donkey":
		case "elder_guardian":
		case "enderman":
		case "endermite":
		case "evoker":
		case "giant":
		case "guardian":
		case "horse":
		case "husk":
		case "illusioner":
		case "iron_golem":
		case "llama":
		case "magma_cube":
		case "mule":
		case "mushroom_cow":
		case "ocelot":
		case "parrot":
		case "phantom":
		case "pig":
		case "pig_zombie":
		case "polar_bear":
		case "pufferfish":
		case "rabbit":
		case "salmon":
		case "sheep":
		case "shulker":
		case "silverfish":
		case "skeleton":
		case "skeleton_horse":
		case "slime":
		case "snowman":
		case "spider":
		case "squid":
		case "stray":
		case "tropical_fish":
		case "turtle":
		case "vex":
		case "villager":
		case "vindicator":
		case "witch":
		case "wither":
		case "wither_skeleton":
		case "wolf":
		case "zombie":
		case "zombie_horse":
		case "zombie_villager":
			return entity.getType().name().toLowerCase();
		default:
			return null;
		}
	}
	
	private boolean isWorldEnabled(World world) {
		if (world == null || world.getName() == null) {
			return false;
		}
		for (String worldName : disabledWorlds) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				return false;
			}
		}
		return true;
	}
	
}
