package fr.iban.bungeecore.commands;

import java.util.UUID;

import fr.iban.bungeecore.CoreBungeePlugin;
import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class IgnoreCMD extends Command {

	public IgnoreCMD(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer)sender;
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("listignored")) {
					AccountProvider ap = new AccountProvider(player.getUniqueId());
					Account account = ap.getAccount();
					if(!account.getIgnoredPlayers().isEmpty()) {
					for(UUID ignoredPlayer : account.getIgnoredPlayers()) {
						player.sendMessage(new TextComponent("§8- §c" + CoreBungeePlugin.getInstance().getProxy().getPlayer(ignoredPlayer).getName()));
					}
				  } else {
					  player.sendMessage(new TextComponent("§cVous n'ignorez personne."));
				  }
				}
			}
			if(args.length == 2){
				if(args[0].equalsIgnoreCase("add")) {
					if(CoreBungeePlugin.getInstance().getProxy().getPlayer(args[1]) != null) {
					  UUID ignoredPlayer = CoreBungeePlugin.getInstance().getProxy().getPlayer(args[1]).getUniqueId();
					  if(ignoredPlayer != player.getUniqueId()) {
						AccountProvider ap = new AccountProvider(player.getUniqueId());
						Account account = ap.getAccount();
						if(!account.getIgnoredPlayers().contains(ignoredPlayer)) {
							account.getIgnoredPlayers().add(ignoredPlayer);
							player.sendMessage(new TextComponent("§aVous ignoré maintenant ce joueur."));
							ap.sendAccountToRedis(account);
						}else {
							player.sendMessage(new TextComponent("§cVous ignorez déjà ce joueur"));
						}
					  } else {
						  player.sendMessage(new TextComponent("§cVous ne pouvez pas vous ignoré vous même.")); 
					  }
					}else {
						player.sendMessage(new TextComponent("§cCe joueur n'existe pas."));
					}
				}
				if(args[0].equalsIgnoreCase("remove")) {
					if(CoreBungeePlugin.getInstance().getProxy().getPlayer(args[1]) != null) {
					  UUID ignoredPlayer = CoreBungeePlugin.getInstance().getProxy().getPlayer(args[1]).getUniqueId();
					  if(ignoredPlayer != player.getUniqueId()) {
						AccountProvider ap = new AccountProvider(player.getUniqueId());
						Account account = ap.getAccount();
						if(account.getIgnoredPlayers().contains(ignoredPlayer)) {
							account.getIgnoredPlayers().remove(ignoredPlayer);
							player.sendMessage(new TextComponent("§aVous n'ignorez plus ce joueur."));
							ap.sendAccountToRedis(account);
						}else {
							player.sendMessage(new TextComponent("§cVous n'ignorez pas ce joueur"));
						}
					  } else {
						  player.sendMessage(new TextComponent("§cVous ne pouvez pas vous débloqué vous même.")); 
					  }
					}else {
						player.sendMessage(new TextComponent("§cCe joueur n'existe pas."));
					}
				}
			}
		}

	}

}