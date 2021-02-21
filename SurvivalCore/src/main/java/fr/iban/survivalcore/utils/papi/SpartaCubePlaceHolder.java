package fr.iban.survivalcore.utils.papi;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import fr.iban.common.data.AccountProvider;
import fr.iban.spartacube.data.Account;
import fr.iban.survivalcore.SurvivalCorePlugin;
import fr.iban.survivalcore.utils.LevelUtils;
import fr.iban.survivalcore.utils.XPProvider;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SpartaCubePlaceHolder extends PlaceholderExpansion {

    private SurvivalCorePlugin plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public SpartaCubePlaceHolder(SurvivalCorePlugin plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "spartacube";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }
    
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        // %someplugin_placeholder1%
        if(identifier.equals("niveau")){
        	return getCachedLevelBar(player);
        }
        
        if(identifier.equals("boost")){
        	return getCachedBoosts(player);
        }

        return null;
    }
    
	private Cache<UUID, String> barCache = Caffeine.newBuilder()
			.refreshAfterWrite(5, TimeUnit.SECONDS)
			.build(uuid -> LevelUtils.getLevelProgressBar(new AccountProvider(uuid).getAccount(), 20));
	
	private Cache<UUID, String> boostCache = Caffeine.newBuilder()
			.expireAfterWrite(5, TimeUnit.SECONDS)
			.build(uuid -> getBoosts(uuid));

	private String getCachedLevelBar(Player player) {
		return barCache.get(player.getUniqueId(), bar -> LevelUtils.getLevelProgressBar(new AccountProvider(player.getUniqueId()).getAccount(), 20));
	}

	private String getBoosts(UUID uuid) {
    	AccountProvider ap = new AccountProvider(uuid);
        Account account = ap.getAccount();
        int boost = XPProvider.getTotalBoost(account, ap) + XPProvider.getTotalGlobalBoost();
    	return boost == 0 ? "Aucun boost" : "+" + boost + "%";
	}
	
	private String getCachedBoosts(Player player) {
		return boostCache.get(player.getUniqueId(), bar -> getBoosts(player.getUniqueId()));
	}
	
}
