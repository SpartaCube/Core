package fr.iban.survivalcore.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public abstract class BoostsTab implements TabExecutor {

	private String desc = null, usage = null, permission = null;

	/**
	 * Gets the description that is used for this command, can be null or empty.
	 */
	public String getDescription() {
		return desc;
	}

	/**
	 * Gets the permission that is used to check if a player has permission
	 * <br>
	 * to perform this command. This is not always the case, so take care
	 * <br>
	 * with using this method. Sometimes other permissions are used to check the
	 * player.
	 */
    public String getPermission() {
	   return permission;
    }

	/**
	 * Gets the way this command is supposed to be used.
	 * <br>
	 * For example, /ar times &lt;player&gt; &lt;type&gt;
	 */
	public String getUsage() {
		return usage;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public abstract boolean onCommand(final CommandSender sender, final Command cmd, final String label,
			final String[] args);

	public void setDesc(final String desc) {
		this.desc = desc;
	}

	public void setPermission(final String perm) {
		this.permission = perm;
	}

	public void setUsage(final String usage) {
		this.usage = usage;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public abstract List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args);
}
