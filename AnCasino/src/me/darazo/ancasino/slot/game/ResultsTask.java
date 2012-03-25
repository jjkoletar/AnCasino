package me.darazo.ancasino.slot.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.darazo.ancasino.slot.Reward;
import me.darazo.ancasino.slot.SlotMachine;
import me.darazo.ancasino.slot.Type;
import me.darazo.ancasino.util.Stat;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ResultsTask implements Runnable {

	private Game game;

	// Deploys rewards after game is finished
	public ResultsTask(Game game) {
		this.game = game;
	}

	@Override
	public void run() {

		Type type = game.getType();
		Player player = game.getPlayer();
		String name = type.getName();
		Double cost = type.getCost();
		Double won = 0.0;
		Stat stat;

		ArrayList<Reward> results = getResults();

		if (!results.isEmpty()) {

			// Send the rewards
			for (Reward reward : results) {
				game.plugin.rewardData.send(player, reward);
				won += reward.getMoney();
			}

			// Managed
			SlotMachine slot = game.getSlot();
			if (slot.isManaged()) {

				slot.withdraw(won);
				Double max = game.plugin.typeData.getMaxPrize(type.getName());
				if (slot.getFunds() < max) {
					slot.setEnabled(false);
				}

				game.plugin.slotData.saveSlot(slot);
				game.plugin.configData.saveSlots();
			}
		}

		// No win
		else {
			game.plugin.sendMessage(player, type.getMessages().get("noWin"));
		}

		// Register statistics
		if (game.plugin.configData.trackStats) {
			if (game.plugin.statsData.isStat(name)) {
				stat = game.plugin.statsData.getStat(name);
				stat.add(won, cost);
			} else {
				stat = new Stat(name, 1, won, cost);
			}
			game.plugin.statsData.addStat(stat);
			if (!results.isEmpty()) {
				game.plugin.configData.saveStats();
			}
		}

		// All done
		game.getSlot().toggleBusy();

	}

	// Gets the results
	private ArrayList<Reward> getResults() {

		ArrayList<Reward> results = new ArrayList<Reward>();
		ArrayList<Block> blocks = game.getSlot().getBlocks();

		// checks horizontal matches
		for (int i = 0; i < 5; i++) {
			Reward reward;
			ArrayList<Integer> currentId = new ArrayList<Integer>();
			List<Block> current = null;
			if (i < 3) {
				int start = 0 + 3 * i;
				int end = 3 + 3 * i;
				current = blocks.subList(start, end);
			}

			// diagonals
			else {
				if (game.plugin.configData.allowDiagonals) {
					current = new ArrayList<Block>();
					for (int j = 0; j < 3; j++) {
						if (i == 3) {
							current.add(blocks.get(j * 4));
						} else {
							current.add(blocks.get(2 + (2 * j)));
						}
					}
				}

				// Break loop if diagonals are disabled
				else {
					break;
				}
			}

			for (Block b : current) {
				currentId.add(b.getTypeId());
			}

			// Check for matches, deploy rewards
			Set<Integer> currentSet = new HashSet<Integer>(currentId);
			if (currentSet.size() == 1) {

				int id = current.get(0).getTypeId();
				reward = game.getType().getReward(id);
				results.add(reward);
			}
		}

		return results;
	}

}
