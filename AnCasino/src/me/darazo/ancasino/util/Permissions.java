package me.darazo.ancasino.util;

import org.bukkit.entity.Player;

import me.darazo.ancasino.AnCasino;
import me.darazo.ancasino.slot.SlotMachine;
import me.darazo.ancasino.slot.Type;

public class Permissions {

	protected AnCasino plugin;

	private String admin = "ancasino.admin";
	private String create = "ancasino.create";
	private String manage = "ancasino.manage";
	private String use = "ancasino.use";

	// Initialize permissions
	public Permissions(AnCasino plugin) {
		this.plugin = plugin;
	}

	// Returns true if player has admin permissions
	public boolean isAdmin(Player player) {
		return player.isOp() || player.hasPermission(admin);
	}

	// Returns true if player can create slots
	public boolean canCreate(Player player) {
		return isAdmin(player) || player.hasPermission(create);
	}

	// Returns true if player can create slots with the type
	public boolean canCreate(Player player, Type type) {
		return isAdmin(player)
				|| player.hasPermission(create + "." + type.getName());
	}

	// Returns true if player can create slots with the type
	public boolean canCreate(Player player, String type) {
		return isAdmin(player) || player.hasPermission(create + "." + type);
	}

	// Returns true if the player can manage slot machines
	public boolean canManage(Player player) {
		return isAdmin(player) || player.hasPermission(manage);
	}

	// Returns true if the player can use the type
	public boolean canUse(Player player, Type type) {
		return isAdmin(player)
				|| player.hasPermission(use + "." + type.getName());
	}

	public boolean isOwner(Player player, SlotMachine slot) {
		return isAdmin(player)
				|| (slot.getOwner() != null && slot.getOwner()
						.equalsIgnoreCase(player.getName()));
	}
}
