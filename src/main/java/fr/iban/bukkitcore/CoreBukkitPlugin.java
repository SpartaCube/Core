package fr.iban.bukkitcore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.api.RedissonClient;

import fr.iban.bukkitcore.commands.AnnonceCMD;
import fr.iban.bukkitcore.commands.BanHammerCMD;
import fr.iban.bukkitcore.commands.FeedCMD;
import fr.iban.bukkitcore.commands.PacketLogCMD;
import fr.iban.bukkitcore.commands.RepairCMD;
import fr.iban.bukkitcore.commands.RessourceCMD;
import fr.iban.bukkitcore.commands.ServeurCMD;
import fr.iban.bukkitcore.commands.SurvieCMD;
import fr.iban.bukkitcore.listeners.AsyncChatListener;
import fr.iban.bukkitcore.listeners.DeathListener;
import fr.iban.bukkitcore.listeners.HeadDatabaseListener;
import fr.iban.bukkitcore.listeners.InventoryListener;
import fr.iban.bukkitcore.listeners.JoinQuitListeners;
import fr.iban.bukkitcore.listeners.PlayerMoveListener;
import fr.iban.bukkitcore.teleport.TeleportToLocationListener;
import fr.iban.bukkitcore.teleport.TeleportToPlayerListener;
import fr.iban.bukkitcore.utils.PluginMessageHelper;
import fr.iban.bukkitcore.utils.TextCallback;
import fr.iban.common.data.redis.RedisAccess;
import fr.iban.common.data.redis.RedisCredentials;
import fr.iban.common.data.sql.DbAccess;
import fr.iban.common.data.sql.DbCredentials;
import net.milkbowl.vault.economy.Economy;

public final class CoreBukkitPlugin extends JavaPlugin {
	
	private static CoreBukkitPlugin instance;
	private String serverName;
	private boolean packetlogging = false;
	private Map<UUID, TextCallback> textInputs;

	  
	public static String[] lores;
	  
	public static boolean blockall;
	  
	private boolean blockanvil;
	  
	private String msg;
	  
	private int delay;
	
	private Economy econ = null;

    @Override
    public void onEnable() {
    	instance = this;
	    String loresraw = getConfig().getString("lores");
	    blockall = getConfig().getBoolean("blockalllores");
	    this.blockanvil = getConfig().getBoolean("blockanvilrepair");
	    this.msg = getConfig().getString("message");
	    this.delay = Integer.parseInt(getConfig().getString("delay"));
    	saveDefaultConfig();
		if (loresraw.contains(";")) {
		  lores = loresraw.split(";"); 
		}
    	
    	
    	DbAccess.initPool(new DbCredentials(getConfig().getString("database.host"), getConfig().getString("database.user"), getConfig().getString("database.password"), getConfig().getString("database.dbname"), getConfig().getInt("database.port")));
        RedisAccess.init(new RedisCredentials(getConfig().getString("redis.host"), getConfig().getString("redis.password"), getConfig().getInt("redis.port"), getConfig().getString("redis.clientName")));
        
        textInputs = new HashMap<>();
        
        registerListeners(
        		new HeadDatabaseListener(),
        		new InventoryListener(),
        		new AsyncChatListener(this),
        		new JoinQuitListeners(this),
        		new PlayerMoveListener(),
        		new DeathListener(this)
        		);
        
        getCommand("serveur").setExecutor(new ServeurCMD());

        getCommand("annonce").setExecutor(new AnnonceCMD(this));
        getCommand("survie").setExecutor(new SurvieCMD());
        getCommand("ressource").setExecutor(new RessourceCMD());
        getCommand("banhammer").setExecutor(new BanHammerCMD(this));
        getCommand("packetlog").setExecutor(new PacketLogCMD(this));
        getCommand("feed").setExecutor(new FeedCMD(this));
        getCommand("repair").setExecutor(new RepairCMD(this));
        setupEconomy();
        
        PluginMessageHelper.registerChannels(this);
        
    	RedissonClient redisClient = RedisAccess.getInstance().getRedissonClient();
        redisClient.getTopic("TeleportToPlayer").addListener(new TeleportToPlayerListener());
        redisClient.getTopic("TeleportToLocation").addListener(new TeleportToLocationListener());
        
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
        	for(Player player : Bukkit.getOnlinePlayers()) {
        		PluginMessageHelper.askServerName(player);
        		break;
        	}
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
        DbAccess.closePool();;
    }
    
	private void registerListeners(Listener... listeners) {

		PluginManager pm = Bukkit.getPluginManager();

		for (Listener listener : listeners) {
			pm.registerEvents(listener, this);
		}

	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Economy getEcon() {
		return econ;
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
	
	public String getMSG() {
		return msg;
	}
	
	public boolean getBlockAnvil() {
		return blockanvil;
	}
	public String[] getLores() {
		return lores;
	}
	
	public int getDelay() {
		return delay;
	}

	public void setPacketlogging(boolean packetlogging) {
		this.packetlogging = packetlogging;
	}

	public Map<UUID, TextCallback> getTextInputs() {
		return textInputs;
	}

}
