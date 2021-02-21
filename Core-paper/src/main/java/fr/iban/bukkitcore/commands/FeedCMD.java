package fr.iban.bukkitcore.commands;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.iban.bukkitcore.CoreBukkitPlugin;

public class FeedCMD implements CommandExecutor {
	  
	HashMap<String, Long> cooldowns = new HashMap<>();
	  
	private CoreBukkitPlugin plugin;

    public FeedCMD(CoreBukkitPlugin plugin) {
	    this.plugin = plugin;
	}
	  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    if (args.length != 1) {
		  if (sender instanceof Player) {
		    if (sender.hasPermission("spartacube.feed")) {
		      if (cmd.getName().equalsIgnoreCase("feed")) {
		       Player p = (Player)sender;
		         if (p.hasPermission("spartacube.feed.bypass")) {
		          p.setFoodLevel(20);
		          sender.sendMessage("§cVous avez été rassasié");
		          return true;
		        } 
		       int seconds = 300;
		        if (cooldowns.containsKey(p.getName())) {
		          long secondsLeft = ((Long)this.cooldowns.get(p.getName())).longValue() / 1000L + seconds - 
		            System.currentTimeMillis() / 1000L;
		          if (secondsLeft > 0L) {
		             p.sendMessage("§cVous pouvez utiliser cette commande dans " + secondsLeft + " secondes!");
		            return true;
		           } 
		          this.cooldowns.remove(p.getName());
		          p.setFoodLevel(20);
		          sender.sendMessage("§cVous avez été rassasié");
		          this.cooldowns.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
		          if(sender.hasPermission("spartacube.feed.bypass")) {
		        	 this.cooldowns.remove(p.getName()); 
		          }
		        } else {
		          this.cooldowns.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
		          if(sender.hasPermission("spartacube.feed.bypass")) {
		        	 this.cooldowns.remove(p.getName()); 
		          }
		          p.setFoodLevel(20);
		          sender.sendMessage("§cVous avez été rassasié");
		        } 
		      } 
		    } else {
		      sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande!");
		    } 
		  } else {
		    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande!");
		   } 
	 } else if (sender.hasPermission("spartacube.feed.others")) {
		  Player target = Bukkit.getPlayer(args[0]);
		  if (target == null) {
		    sender.sendMessage("§c" + args[1] + " n'est pas en ligne!");
		  } else {
		    target.setFoodLevel(20);
		    sender.sendMessage("§cVous avez rassasié " + target.getName());
		    target.sendMessage("§ cVous avez été rassasié par " + sender.getName());
		   } 
		} else {
		 sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande!");
		} 
	 return true;
	}
}
