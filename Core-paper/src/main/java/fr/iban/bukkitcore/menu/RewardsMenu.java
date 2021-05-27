package fr.iban.bukkitcore.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.rewards.Reward;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.bukkitcore.utils.Head;
import fr.iban.bukkitcore.utils.ItemBuilder;

public class RewardsMenu extends PaginatedMenu {

	private List<Reward> rewards;
	private Map<Integer, Reward> rewardAtSlot;

	public RewardsMenu(Player player, List<Reward> rewards) {
		super(player);
		this.rewards = rewards;
	}

	@Override
	public String getMenuName() {
		return "§2Vos récompenses :";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();

		if(e.getClickedInventory() != e.getView().getTopInventory()) {
			return;
		}

		checkBottonsClick(item, player);


		Reward reward = rewardAtSlot.get(e.getSlot());

		if(reward == null) {
			return;
		}

		CoreBukkitPlugin core = CoreBukkitPlugin.getInstance();
		if(reward.getServer().equals(core.getServerName())) {
			if(rewards.contains(reward)) {
				rewards.remove(reward);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.getCommand().replace("{player}", player.getName()));
				player.sendMessage("§aVous avez récupéré une récompense.");
				RewardsDAO.removeRewardAsync(player.getUniqueId().toString(), reward).thenRun(() -> {
					Bukkit.getScheduler().runTask(core, () -> {
						open();
					});
				});
				return;
			}else {
				player.sendMessage("§cUne erreur est survenue.");
			}
		}else {
			player.sendMessage("§cVous devez vous trouver dans le serveur " + reward.getServer() + " pour récupérer cette récompense.");
		}
		open();

	}

	@Override
	public int getElementAmount() {
		return rewards.size();
	}

	@Override
	public void setMenuItems() {
		addMenuBorder();

		this.rewardAtSlot = new HashMap<>();

		if(rewards != null && !rewards.isEmpty()) {
			for(int i = 0; i < getMaxItemsPerPage(); i++) {
				index = getMaxItemsPerPage() * page + i;
				if(index >= rewards.size()) break;
				if (rewards.get(index) != null){
					final int slot = inventory.firstEmpty();
					Reward reward = rewards.get(index);
					rewardAtSlot.put(slot, reward);
					inventory.setItem(slot, getRewardItem(reward));
				}
			}
		}	
	}

	private ItemStack getRewardItem(Reward reward) {
		return new ItemBuilder(Head.BAG.get())
				.setDisplayName("§2" + reward.getName())
				.addLore("§aClic pour récupérer la récompense.")
				.build();
	}

}
