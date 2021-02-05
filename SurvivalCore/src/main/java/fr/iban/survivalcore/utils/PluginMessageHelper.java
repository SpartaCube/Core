package fr.iban.survivalcore.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.survivalcore.SurvivalCorePlugin;
import fr.iban.survivalcore.listeners.PluginMessageReceivedListener;

public class PluginMessageHelper {
	
	private static final String BUNGEECORD_CHANNEL = "BungeeCord";
	private static final String RANKUP_CHANNEL = "survie:rankup";

	public static void registerChannels(Plugin plugin) {
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, BUNGEECORD_CHANNEL, new PluginMessageReceivedListener());
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BUNGEECORD_CHANNEL);
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, RANKUP_CHANNEL);
	    plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, RANKUP_CHANNEL, new PluginMessageReceivedListener());
	}
	
	public static void receivePluginMessage(String channel, byte[] bytes) {
		if(!channel.equalsIgnoreCase(PluginMessageHelper.BUNGEECORD_CHANNEL)) {
			return;
		}
		
		final ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		final String sub = in.readUTF();
	}
	
	public static void sendRankUp(Player player , String group) {
	    ByteArrayDataOutput out = ByteStreams.newDataOutput();
	    out.writeUTF("Rankup");
	    out.writeUTF(player.getUniqueId().toString());
	    out.writeUTF(group);
	    player.sendPluginMessage(SurvivalCorePlugin.getInstance(), RANKUP_CHANNEL, out.toByteArray());
	}
	
	// public static void sendPlayerToServer(Player player , String targetName) {
	// 	final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		//TODO envoyer au bungee qui vérifie que le joueur est en ligne et envois la requete sur le bon serveur.
	// }
}
