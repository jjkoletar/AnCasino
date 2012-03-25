package me.darazo.ancasino.slot.game;

import me.darazo.ancasino.AnCasino;
import me.darazo.ancasino.slot.SlotMachine;
import me.darazo.ancasino.slot.Type;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class Game {

	protected AnCasino plugin;
	protected BukkitScheduler scheduler;

	private SlotMachine slot;
	private Player player;

	// Initializes a new game object
	public Game(SlotMachine slot, Player player, AnCasino plugin) {

		this.slot = slot;
		this.player = player;
		this.plugin = plugin;

	}

	// Returns the active slot machine
	public SlotMachine getSlot() {
		return this.slot;
	}

	// Returns the type of the active slot machine
	public Type getType() {
		return plugin.typeData.getType(slot.getType());
	}

	// Returns the game's player
	public Player getPlayer() {
		return this.player;
	}

	// Plays the game.
	public void play() {

		this.scheduler = plugin.getServer().getScheduler();
		Integer[] task = new Integer[3];
		Long[] delay = { 60L, 80L, 100L };

		if (!slot.isEnabled()) {
			plugin.sendMessage(player,
					"This slot machine is currently disabled. Deposit more funds to enable.");
			return;
		}

		// Cost
		Double cost = getType().getCost();
		plugin.economy.withdrawPlayer(player.getName(), cost);
		if (slot.isManaged()) {
			slot.deposit(cost);
		}

		// Start playing
		slot.toggleBusy();
		plugin.sendMessage(player, getType().getMessages().get("start"));

		// Initiate tasks
		for (Integer i = 0; i < 3; i++) {
			task[i] = scheduler.scheduleSyncRepeatingTask(plugin,
					new RotateTask(this, i), 0L, 6L);
			scheduler.scheduleSyncDelayedTask(plugin, new StopRotateTask(this,
					task[i]), delay[2 - i]);
		}

		// Results task
		scheduler.scheduleSyncDelayedTask(plugin, new ResultsTask(this),
				delay[2]);

	}

}
