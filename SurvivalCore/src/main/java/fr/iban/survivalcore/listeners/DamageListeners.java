package fr.iban.survivalcore.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Trident;
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
		if(event.getCause() == DamageCause.PROJECTILE) {
			if(event.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) event.getDamager();
				if(arrow.getShooter() instanceof Player) {
					player = (Player)arrow.getShooter();
				}
			}if(event.getDamager() instanceof SpectralArrow) {
				SpectralArrow arrow = (SpectralArrow) event.getDamager();
				if(arrow.getShooter() instanceof Player) {
					player = (Player)arrow.getShooter();
				}
			}else if(event.getDamager() instanceof Trident) {
				Trident trident = (Trident)event.getDamager();
				if(trident.getShooter() instanceof Player) {
					player = (Player)trident.getShooter();
				}
			}else if(event.getDamager() instanceof Firework) {
				Firework firework = (Firework)event.getDamager();
				if(firework.getShooter() instanceof Player) {
					player = (Player)firework.getShooter();
				}
			}else if(event.getDamager() instanceof EnderPearl) {
				EnderPearl enderpearl = (EnderPearl)event.getDamager();
				if(enderpearl.getShooter() instanceof Player) {
					player = (Player)enderpearl.getShooter();
				}
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
