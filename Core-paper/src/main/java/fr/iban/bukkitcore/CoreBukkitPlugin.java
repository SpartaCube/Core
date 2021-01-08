package fr.iban.bukkitcore;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RedissonClient;

import com.google.common.collect.Iterables;

import fr.iban.bukkitcore.commands.PacketLogCMD;
import fr.iban.bukkitcore.commands.RessourceCMD;
import fr.iban.bukkitcore.commands.ServeurCMD;
import fr.iban.bukkitcore.commands.SurvieCMD;
import fr.iban.bukkitcore.listeners.AsyncChatListener;
import fr.iban.bukkitcore.listeners.DeathListener;
import fr.iban.bukkitcore.listeners.InventoryListener;
import fr.iban.bukkitcore.listeners.JoinQuitListeners;
import fr.iban.bukkitcore.listeners.PlayerMoveListener;
import fr.iban.bukkitcore.teleport.TeleportToLocationListener;
import fr.iban.bukkitcore.teleport.TeleportToPlayerListener;
import fr.iban.bukkitcore.utils.PluginMessageHelper;
import fr.iban.common.data.redis.RedisAccess;
import fr.iban.common.data.redis.RedisCredentials;
import fr.iban.common.data.sql.DbManager;

public final class CoreBukkitPlugin extends JavaPlugin {
	
	private static CoreBukkitPlugin instance;
	private String serverName;
	private boolean packetlogging = false;

    @Override
    public void onEnable() {
    	instance = this;
    	saveDefaultConfig();
    	DbManager.initAllDbConnections();
        RedisAccess.init(new RedisCredentials(getConfig().getString("redis.host"), getConfig().getString("redis.password"), getConfig().getInt("redis.port"), getConfig().getString("redis.clientName")));
        
        registerListeners(
        		new InventoryListener(),
        		new AsyncChatListener(),
        		new JoinQuitListeners(this),
        		new PlayerMoveListener(),
        		new DeathListener(this)
        		);
        
        getCommand("serveur").setExecutor(new ServeurCMD());
        getCommand("survie").setExecutor(new SurvieCMD());
        getCommand("ressource").setExecutor(new RessourceCMD());
        getCommand("packetlog").setExecutor(new PacketLogCMD(this));
        
        PluginMessageHelper.registerChannels(this);
        
    	RedissonClient redisClient = RedisAccess.getInstance().getRedissonClient();
        redisClient.getTopic("TeleportToPlayer").addListener(new TeleportToPlayerListener());
        redisClient.getTopic("TeleportToLocation").addListener(new TeleportToLocationListener());
        
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
        	PluginMessageHelper.askServerName(Iterables.getOnlyElement(Bukkit.getOnlinePlayers()));
        }
        
//        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this,
//                ListenerPriority.NORMAL) {
//            @Override
//            public void onPacketReceiving(PacketEvent event) {
//            	if(isPacketlogging()) {
//            		System.out.println(event.getPacketType().name());
//            		for (Player player : Bukkit.getOnlinePlayers()) {
//						if(player.hasPermission("spartacube.packetlog")) {
//							player.sendMessage(event.getPacketType().getSender().name() + " -> " + event.getPacketType().name());
//						}
//					}
//            	}
//            }
//        });
        
//        new BukkitRunnable() {
//			
//			@Override
//			public void run() {
//				RedisAccess.getInstance().getRedissonClient().getTopic("test").publish("blblbl");
//				Bukkit.broadcastMessage("pub");
//			}
//		}.runTaskTimer(this, 10, 1);
    }

    @Override
    public void onDisable() {
        RedisAccess.close();
        DbManager.closeAllDbConnections();
    }
    
	private void registerListeners(Listener... listeners) {

		PluginManager pm = Bukkit.getPluginManager();

		for (Listener listener : listeners) {
			pm.registerEvents(listener, this);
		}

	}
	
	public static CoreBukkitPlugin getInstance() {
		return instance;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public boolean isPacketlogging() {
		return packetlogging;
	}

	public void setPacketlogging(boolean packetlogging) {
		this.packetlogging = packetlogging;
	}

}
