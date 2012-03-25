package me.darazo.ancasino.command;

import org.bukkit.entity.Player;

import me.darazo.ancasino.AnCasino;
import me.darazo.ancasino.slot.SlotMachine;

public abstract class AnCommand {

	public AnCasino plugin;
	public Player player;
	public String[] args;

	// Initializes new command
	public AnCommand(AnCasino plugin, String[] args, Player player) {

		this.plugin = plugin;
		this.args = args;
		this.player = player;

	}

	// Processes command, handled by subclasses
	public Boolean process() {
		return false;
	}

	// Returns true if player owns this slot machine
	public Boolean isOwner(SlotMachine slot) {

		if (plugin.permission.isAdmin(player)
				|| slot.getOwner().equalsIgnoreCase(player.getName())) {
			return true;
		}
		return false;
	}

	// Called when a player is denied permission to a command
	public void noPermission() {
		plugin.sendMessage(player, "You don't have permission to do this.");
	}

	// Sends a message to the player
	public void sendMessage(String message) {
		plugin.sendMessage(player, message);
	}

}
