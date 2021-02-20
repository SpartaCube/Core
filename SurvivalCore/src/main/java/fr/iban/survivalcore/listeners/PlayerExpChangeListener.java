package fr.iban.survivalcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;
import fr.iban.survivalcore.utils.XPProvider;

public class PlayerExpChangeListener implements Listener {

	@EventHandler
	public void onExpChange(PlayerExpChangeEvent e) {
		Player player = e.getPlayer();
        AccountProvider ap = new AccountProvider(player.getUniqueId());
        Account account = ap.getAccount();
		XPProvider.addXP(player, e.getAmount()*(1+((XPProvider.getTotalBoost(account, ap) + XPProvider.getTotalGlobalBoost())/100)), true);
	}

}
