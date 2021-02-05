package fr.iban.survivalcore.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.iban.survivalcore.utils.PluginMessageHelper;

public class EntityDeathListener implements Listener {
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		// ☠ 
		Player killer = e.getEntity().getKiller();
		Player player = e.getEntity();
        String message = "☠ " + player.getName() + " ";

        EntityDamageEvent damageCause = e.getEntity().getLastDamageCause();
        if (damageCause == null)
            return;

        switch (damageCause.getCause()) {
            case SUICIDE:
                message += "s'est suicidé !";
                break;
            case BLOCK_EXPLOSION:
                message += "s'est fait broyer par de la TNT";
                break;
            case CONTACT:
                message += "a essayé de piquer un cactus !";
                break;
            case DROWNING:
                message += "s'est noyé !";
                break;
            case ENTITY_EXPLOSION:
                message += "s'est fait exploser par un Creeper !";
                break;
            case FIRE_TICK:
                message += "a brûlé.";
                break;
            case LAVA:
                message += "a essayé de nager dans la lave !";
                break;
            case MAGIC:
                message += "a été tué par une potion!";
                break;
            case POISON:
                message += "a été empoisonné!";
                break;
            case PROJECTILE:
                if (killer != null)
                    message += "s'est fait sniper par " + killer.getName() + " !";
                else
                    message += "s'est prit une flêche dans le crâne.";
                break;
            case STARVATION:
                message += "est mort de faim.";
                break;
            case SUFFOCATION:
                message += "a suffoqué dans un mur.";
                break;
            case VOID:
                message += "a disparru dans le néant...";
                break;
            case FALL:
                message += "a fait une terrible chute !";
                break;
            case WITHER:
                message += "a été tué par un Wither.";
                break;
            case LIGHTNING:
                message += "a été abattu par la colère de Zeus!";
                break;
            case HOT_FLOOR:
                message += "a marché sur un bloc très chaud !";
                break;
            case FLY_INTO_WALL:
                message += "a frappé le mur à une vitesse supersonique !";
                break;
            case FALLING_BLOCK:
                message += "s'est fait écraser par une entité tombée du ciel !";
                break;
            case FIRE:
                message += "a joué avec le feu mais s'est brûlé.";
                break;
            case DRAGON_BREATH:
                message += "a subi la colère du Dragon !";
                break;
            case ENTITY_ATTACK:
                if (damageCause instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent entity = (EntityDamageByEntityEvent) damageCause;
                    if (entity.getDamager() instanceof Player) {
                    	killer = (Player)entity.getDamager();
                        Material weapon = killer.getInventory().getItemInMainHand().getType();
                        if (weapon == Material.AIR)
                            message += "s'est fait boxer par " + killer.getName() + " !";
                        else
                            message += "s'est fait assassiner par " + killer.getName() + " !";
                    } else if (entity.getDamager() instanceof Creature)
                        message += "s'est fait tuer par un " + entity.getDamager().getType().toString().toLowerCase().replace("_", " ") + " !";
                }
                break;
            default:
                message += "est mort.";
        }

        if (message != null)
            e.setDeathMessage(message);
        
	}

}
