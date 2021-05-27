package fr.iban.survivalcore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;

public class DamageListeners implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
			if(e.getEntity() instanceof Player) {
				Player damaged = (Player)e.getEntity();
				Player damager = getPlayerDamager(event);
				if(damager != null) {
					Account damagerAccount = new AccountProvider(damager.getUniqueId()).getAccount();
					Account damagedAccount = new AccountProvider(damaged.getUniqueId()).getAccount();
					if(!canPVP(damagerAccount, damagedAccount)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	private Player getPlayerDamager(EntityDamageByEntityEvent event) {
		Player player = null;
		if(event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if(projectile.getShooter() instanceof Player) {
				player = (Player)projectile.getShooter();
			}
		}
		if(event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		}
		return player;
	}

	private boolean canPVP(Account data1, Account data2) {
		return data1.isPvp() && data2.isPvp();
	}
}
