package fr.iban.bukkitcore.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.iban.bukkitcore.CoreBukkitPlugin;

public class RepairCMD implements CommandExecutor {
	
	private static HashMap<String, Date> delaymap = new HashMap<>();
		  
	private CoreBukkitPlugin plugin;
	
	private boolean blockall = true;
	
	private int delay = 3000;
	
	public static String[] lores;
	  
    public RepairCMD(CoreBukkitPlugin plugin) {
	    this.plugin = plugin;
	}
	
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		    if (cmd.getName().equalsIgnoreCase("repair"))
		      if (s instanceof Player) {
		        Player m = (Player)s;
		        if (m.hasPermission("spartacube.repair")) {
		    	  String loresraw = CoreBukkitPlugin.getInstance().getConfig().getString("lores");
		    	  if (loresraw.contains(";")) {
		    		 lores = loresraw.split(";"); 
		    	  }
		          String norepair = ChatColor.RED + "Erreur: " + ChatColor.DARK_RED + "Cette item n'est pas réparable";
		          if (args.length == 0 || (args.length >= 1 && args[0].equalsIgnoreCase("hand"))) {
		            Material material = m.getItemInHand().getType();
		            if (material.isBlock() || material.getMaxDurability() < 1 || m.getItemInHand().getDurability() == 0) {
		              m.sendMessage(norepair);
		            } else {
		              m.getItemInHand().setDurability((short)0);
		              m.sendMessage(ChatColor.GOLD + "Vous avez réparé avec succès votre: " + ChatColor.RED + m.getItemInHand().getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' '));
		            } 
		          } else if (args[0].equalsIgnoreCase("all")) {
		       if (m.hasPermission("spartacube.repair")) {
		            String itemlist = "";
		            for (ItemStack item : m.getInventory()) {
		              if (item != null) {
		                Material material = item.getType();
		                if (!material.isBlock() && material.getMaxDurability() >= 1 && item.getDurability() != 0) {
		                  item.setDurability((short)0);
		                  itemlist = String.valueOf(itemlist) + item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ') + ", ";
		                } 
		              } 
		            } 
		            m.sendMessage(itemlist);
		            } else {
		                m.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas autorisé à utiliser le repairall");
		            }
		          } 
		        } else if (m.hasPermission("spartacube.repair.nolore")) {
		          if (delaymap.containsKey(m.getName())) {
		            Date d = delaymap.get(m.getName());
		            long differenceInMin = DelayCheck(d);
		            if (differenceInMin < delay) {
		              int time = (int)(delay - differenceInMin);
		              m.sendMessage(ChatColor.RED + "Vous devez attendre " + time + " minutes avant de pouvoir utiliser à nouveau cette commande.");
		            } else {
		              RepairCheck(args, m);
		              delaymap.remove(m.getName());
		            } 
		          } else {
		            RepairCheck(args, m);
		          } 
		        } else {
		          m.sendMessage(ChatColor.DARK_RED + "Vous n'êtes pas autorisé à utiliser cette commande");
		        } 
		      }  
		    return true;
		  }
		  
		@SuppressWarnings("deprecation")
		private void RepairCheck(String[] args, Player m) {
		    int num = 0;
		    short dur = 0;
		    String norepair = ChatColor.RED + "Erreur: " + ChatColor.DARK_RED + "Cet item ne peut pas être réparé";
		    if (args.length == 0 || (args.length >= 1 && args[0].equalsIgnoreCase("hand"))) {
		      Material material = m.getItemInHand().getType();
		      if (m.getInventory().getItemInHand().getType() != null) {
		        if (m.getInventory().getItemInHand().getItemMeta().hasLore()) {
		          if (blockall) {
		            m.sendMessage(ChatColor.RED + "Vous n'êtes pas autorisé à réparer des items avec des lores.");
		          } else {
		            List<String> lore = m.getInventory().getItemInHand().getItemMeta().getLore();
		            for (int i = 0; i < lores.length; i++) {
		              if (lore.contains(lores[i])) {
		                m.sendMessage(ChatColor.RED + "Cette item n'est pas réparable.");
		              } else if (material.isBlock() || material.getMaxDurability() < 1 || m.getItemInHand().getDurability() == 0) {
		                m.sendMessage(norepair);
		              } else {
		                m.getItemInHand().setDurability(dur);
		                m.sendMessage(ChatColor.GOLD + "Vous avez réparé avec succès votre: " + ChatColor.RED + m.getItemInHand().getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' '));
		                delaymap.put(m.getName(), Calendar.getInstance().getTime());
		              } 
		            } 
		          } 
		        } else if (material.isBlock() || material.getMaxDurability() < 1 || m.getItemInHand().getDurability() == 0) {
		          m.sendMessage(norepair);
		        } else {
		          m.getItemInHand().setDurability(dur);
		          m.sendMessage(ChatColor.GOLD + "Vous avez réparé avec succès votre: " + ChatColor.RED + m.getItemInHand().getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' '));
		          delaymap.put(m.getName(), Calendar.getInstance().getTime());
		        } 
		      } else {
		        m.sendMessage(norepair);
		      } 
		    } else if (args[0].equalsIgnoreCase("all")) {
		      num = 0;
		      String itemlist = "";
		      boolean repaircheck = true;
		      for (ItemStack item : m.getInventory()) {
		        if (item != null) {
		          Material material = item.getType();
		          if (item.hasItemMeta() && !blockall) {
		            if (item.getItemMeta().hasLore()) {
		              List<String> lore = m.getInventory().getItemInHand().getItemMeta().getLore();
		              for (int i = 0; i < lores.length; i++) {
		                if (lore.contains(lores[i])) {
		                  num--;
		                } else if (!material.isBlock() && material.getMaxDurability() >= 1 && m.getItemInHand().getDurability() != 0) {
		                  item.setDurability(dur);
		                  num++;
		                  itemlist = String.valueOf(itemlist) + item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ') + ", ";
		                } 
		              } 
		              continue;
		            } 
		            if (!material.isBlock() && material.getMaxDurability() >= 1 && m.getItemInHand().getDurability() != 0) {
		              item.setDurability(dur);
		              num++;
		              itemlist = String.valueOf(itemlist) + item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ') + ", ";
		            } 
		            continue;
		          } 
		          if (!material.isBlock() && material.getMaxDurability() >= 1 && m.getItemInHand().getDurability() != 0) {
		            item.setDurability(dur);
		            num++;
		            itemlist = String.valueOf(itemlist) + item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ') + ", ";
		          } 
		        } 
		      } 
		      if (!repaircheck)
		        m.sendMessage(norepair); 
		      if (num <= 0) {
		        m.sendMessage(ChatColor.RED + "Aucun item de votre inventaire ne peut être réparé");
		      } else {
		        if (itemlist.length() > 0 && itemlist.charAt(itemlist.length() - 2) == ',')
		          itemlist = itemlist.substring(0, itemlist.length() - 2); 
		        m.sendMessage(ChatColor.GOLD + "Vous avez réparé avec succès votre: " + ChatColor.RED + itemlist);
		        delaymap.put(m.getName(), Calendar.getInstance().getTime());
		      } 
		    } else {
		      m.sendMessage(ChatColor.DARK_RED + "Utilisation: " + ChatColor.RED + "/repair hand, /repair all, /repair");
		      m.sendMessage(ChatColor.DARK_RED + "Alias: " + ChatColor.RED + "/repair, /rp, /fix");
		    } 
		  }
		  
		  private long DelayCheck(Date date) {
		    Date now = Calendar.getInstance().getTime();
		    long differenceInMillis = now.getTime() - date.getTime();
		    long differenceInMin = differenceInMillis / 1000L / 60L;
		    return differenceInMin;
		  }

}
