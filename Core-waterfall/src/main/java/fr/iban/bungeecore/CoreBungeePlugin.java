package fr.iban.bungeecore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import fr.iban.bungeecore.chat.ChatManager;
import fr.iban.bungeecore.commands.AnnounceCMD;
import fr.iban.bungeecore.commands.BackCMD;
import fr.iban.bungeecore.commands.ChatCMD;
import fr.iban.bungeecore.commands.MessageCMD;
import fr.iban.bungeecore.commands.MsgToggleCMD;
import fr.iban.bungeecore.commands.ReplyCMD;
import fr.iban.bungeecore.commands.SocialSpyCMD;
import fr.iban.bungeecore.commands.StaffChatToggle;
import fr.iban.bungeecore.commands.SudoCMD;
import fr.iban.bungeecore.commands.TpCMD;
import fr.iban.bungeecore.commands.TpaCMD;
import fr.iban.bungeecore.commands.TpahereCMD;
import fr.iban.bungeecore.commands.TphereCMD;
import fr.iban.bungeecore.commands.TpnoCMD;
import fr.iban.bungeecore.commands.TpyesCMD;
import fr.iban.bungeecore.listeners.PluginMessageListener;
import fr.iban.bungeecore.listeners.ProxyJoinQuitListener;
import fr.iban.bungeecore.listeners.ProxyPingListener;
import fr.iban.bungeecore.runnables.SaveAccounts;
import fr.iban.bungeecore.teleport.DeathLocationListener;
import fr.iban.bungeecore.teleport.TeleportManager;
import fr.iban.bungeecore.utils.AnnoncesManager;
import fr.iban.common.data.AccountProvider;
import fr.iban.common.data.GlobalBoosts;
import fr.iban.common.data.redis.RedisAccess;
import fr.iban.common.data.redis.RedisCredentials;
import fr.iban.common.data.sql.DbAccess;
import fr.iban.common.data.sql.DbCredentials;
import fr.iban.common.data.sql.DbTables;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class CoreBungeePlugin extends Plugin {

	private static final String RANKUP_CHANNEL = "survie:rankup";

	private static CoreBungeePlugin instance;
	private Configuration configuration;
	private AnnoncesManager announceManager;
	private ChatManager chatManager;
	private TeleportManager teleportManager;
	private GlobalBoosts gb;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		loadConfig();
		
		RedisAccess.init(new RedisCredentials(configuration.getString("redis.host"), configuration.getString("redis.password"), configuration.getInt("redis.port"), configuration.getString("redis.clientName")));
    	DbAccess.initPool(new DbCredentials(configuration.getString("database.host"), configuration.getString("database.user"), configuration.getString("database.password"), configuration.getString("database.dbname"), configuration.getInt("database.port")));
		DbTables.createTables();
		
		announceManager = new AnnoncesManager();
		chatManager = new ChatManager();
		teleportManager = new TeleportManager(this);
		gb = new GlobalBoosts();

		getProxy().registerChannel("proxy:chat");
		getProxy().registerChannel(RANKUP_CHANNEL);
		getProxy().registerChannel("proxy:annonce");
		getProxy().registerChannel("proxy:send");
		
		registerEvents(
				new ProxyJoinQuitListener(),
				new ProxyPingListener(),
				new PluginMessageListener()
				);

		registerCommands(
				new AnnounceCMD("announce"),
				new ChatCMD("chat"),
				new StaffChatToggle("sctoggle", "spartacube.sctoggle", "staffchattoggle"),
				new MessageCMD("msg", "spartacube.msg", "message", "m", "w", "tell", "t"),
				new ReplyCMD("reply", "spartacube.reply", "r"),
				new SudoCMD("sudo", "spartacube.sudo"),
				new SocialSpyCMD("socialspy", "spartacube.socialspy"),
				new MsgToggleCMD("msgtoggle", "spartacube.msgtoggle"),
				new TpCMD("tp", "spartacube.tp", teleportManager),
				new TphereCMD("tphere", "spartacube.tp", "s", teleportManager),
				new TpaCMD("tpa", "spartacube.tpa", teleportManager),
				new TpahereCMD("tpahere", "spartacube.tpa", teleportManager),
				new TpnoCMD("tpno", "spartacube.tpa", "tpdeny", teleportManager),
				new TpyesCMD("tpyes", "spartacube.tpa", "tpaccept", teleportManager),
				new BackCMD("back", "spartacube.back.death", teleportManager)
				);

		ProxyServer.getInstance().getScheduler().schedule(this, new SaveAccounts(), 0, 10, TimeUnit.MINUTES);
		
		gb.getGlobalBoostsFromDB();
		
		RedisAccess.getInstance().getRedissonClient().getTopic("DeathLocation").addListener(new DeathLocationListener(this));
	}

	@Override
	public void onDisable() {
		new SaveAccounts().run();
		RedisAccess.close();
		DbAccess.closePool();
		saveConfig();
		getProxy().unregisterChannel("proxy:chat");
		getProxy().unregisterChannel("proxy:annonce");
		getProxy().unregisterChannel("proxy:send");
		getProxy().unregisterChannel(RANKUP_CHANNEL);
	}

	public void registerEvents(Listener... listeners) {
		for(Listener listener : listeners) {
			getProxy().getPluginManager().registerListener(this, listener);
		}
	}

	public void registerCommands(Command... commands) {
		for(Command command : commands) {
			getProxy().getPluginManager().registerCommand(this, command);
		}
	}

	public static CoreBungeePlugin getInstance() {
		return instance;
	}

	public void loadConfig() {
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveDefaultConfig() {
		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		File file = new File(getDataFolder(), "config.yml");


		if (!file.exists()) {
			try (InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public AnnoncesManager getAnnounceManager() {
		return announceManager;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public TeleportManager getTeleportManager() {
		return teleportManager;
	}
}
