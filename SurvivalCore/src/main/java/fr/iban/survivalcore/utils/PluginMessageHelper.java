package fr.iban.survivalcore.utils;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import org.bukkit.plugin.Plugin;

import fr.iban.survivalcore.listeners.PluginMessageReceivedListener;

public class PluginMessageHelper {
	
	private static final String BUNGEECORD_CHANNEL = "BungeeCord";

	public static void registerChannels(Plugin plugin) {
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, BUNGEECORD_CHANNEL, new PluginMessageReceivedListener());
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BUNGEECORD_CHANNEL);
	}
	
	public static void receivePluginMessage(String channel, byte[] bytes) {
		if(!channel.equalsIgnoreCase(PluginMessageHelper.BUNGEECORD_CHANNEL)) {
			return;
		}
		
		final ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		final String sub = in.readUTF();
	}
	
	// public static void sendPlayerToServer(Player player , String targetName) {
	// 	final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		//TODO envoyer au bungee qui vérifie que le joueur est en ligne et envois la requete sur le bon serveur.
	// }
}
