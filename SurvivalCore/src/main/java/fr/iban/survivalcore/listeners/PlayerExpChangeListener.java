package fr.iban.survivalcore.listeners;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;
import fr.iban.survivalcore.SurvivalCorePlugin;
import fr.iban.survivalcore.utils.LevelUtils;

public class PlayerExpChangeListener implements Listener {

	public Map<Player, Map<Long, Integer>> xpLogs = new ConcurrentHashMap<>();

	@EventHandler
	public void onExpChange(PlayerExpChangeEvent e) {
		Player player = e.getPlayer();
		AccountProvider ac = new AccountProvider(player.getUniqueId());
		Account account = ac.getAccount();
		if(!SurvivalCorePlugin.ess.getUser(player.getUniqueId()).isAfk()) {
			if(checkLast(player) < 10) {
				short levelbefore = account.getLevel();
				account.addExp((e.getAmount() < 20 ? e.getAmount() : 20));
				getXPLogs(player).put(System.currentTimeMillis(), e.getAmount());
				short levelafter = account.getLevel();				
				e.getPlayer().sendActionBar(LevelUtils.getLevelProgressBar(account, 20));
				if(levelbefore < levelafter)
					LevelUtils.sendLevelUpReward(account, levelbefore, levelafter);
			}
		}else {
			e.setAmount(0);
		}
		ac.sendAccountToRedis(account);
	}

	public Map<Long, Integer> getXPLogs(Player player){
		if(!xpLogs.containsKey(player)) {
			xpLogs.put(player, new ConcurrentHashMap<>());
		}
		return xpLogs.get(player);
	}

	private int checkLast(Player player) {
		int amountLastMin = 0;
		if(!getXPLogs(player).isEmpty()) {
			for(Entry<Long, Integer> log : getXPLogs(player).entrySet()) {
				if(System.currentTimeMillis() - log.getKey() > 6000) {
					getXPLogs(player).remove(log.getKey());
				}else {
					amountLastMin += log.getValue();
				}
			}
		}
		return amountLastMin;
	}

}
