package fr.iban.common.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import fr.iban.common.data.redis.RedisAccess;
import fr.iban.common.data.sql.DbManager;
import fr.iban.spartacube.data.Account;

public class AccountProvider {

	public static final String REDIS_KEY = "account:";

	private UUID uuid;
	private RedisAccess redisAccess;
	private boolean hasPlayedBefore = true;

	public AccountProvider(UUID uuid) {
		this.uuid = uuid;
		this.redisAccess = RedisAccess.getInstance();
	}

	public Account getAccount(){
		Account account = getAccountFromRedis();

		if(account == null) {
			account = getAccountFromDB();
			sendAccountToRedis(account);

		}

		return account;
	}

	private Account getAccountFromDB(){
		DataSource ds = DbManager.BD.getDbAccess().getDataSource();
		Account account = new Account(uuid);

		try(Connection connection = ds.getConnection()){
			try(PreparedStatement ps = connection.prepareStatement("SELECT * FROM sc_players WHERE uuid = ?")){
				ps.setString(1, uuid.toString());
				try(ResultSet rs = ps.executeQuery()){
					if(rs.next()) {
						long exp = rs.getLong("exp");
						account.setExp(exp);

						long lastseen = rs.getLong("lastseen");
						account.setLastSeen(lastseen);
						
						boolean allowpvp = rs.getBoolean("allowpvp");
						account.setPvp(allowpvp);
						
						short maxclaims = rs.getShort("maxclaims");
						account.setMaxClaims(maxclaims);
					}else {
						hasPlayedBefore = false;
					}
				}
			}
			account.setBlackListedAnnounces(getBlackListedAnnouncesFromDB(connection));
		}catch (SQLException e) {
			e.printStackTrace();
		}		
		return account;
	}

	public void sendAccountToDB(Account account) {
		try (Connection connection = DbManager.BD.getDbAccess().getDataSource().getConnection()){
			try(PreparedStatement ps = connection.prepareStatement("INSERT INTO sc_players (uuid, name, exp, lastseen, maxclaims, allowpvp) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), exp=VALUES(exp), lastseen=VALUES(lastseen), maxclaims=VALUES(maxclaims), allowpvp=VALUES(allowpvp)")){
				ps.setString(1, uuid.toString());
				ps.setString(2, (account.getName() == null ? "NonDefini" : account.getName()));
				ps.setLong(3, account.getExp());
				ps.setLong(4, account.getLastSeen());
				ps.setInt(5, account.getMaxClaims());
				ps.setBoolean(6, account.isPvp());
				ps.executeUpdate();
			}
			saveBlackListedAnnouncesToDB(account.getBlackListedAnnounces(), connection);
			if(account.getIp() != null) {
				saveIpToDB(account.getIp(), connection);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Account getAccountFromRedis() {
		RedissonClient client = redisAccess.getRedissonClient();
		String key = REDIS_KEY+uuid.toString();
		RBucket<Account> accountBukket = client.getBucket(key);
		return accountBukket.get();
	}

	public void sendAccountToRedis(Account account) {
		RedissonClient client = redisAccess.getRedissonClient();
		String key = REDIS_KEY+uuid.toString();
		RBucket<Account> accountBukket = client.getBucket(key);
		accountBukket.set(account);
	}
	
	public void removeAccountFromRedis() {
		RedissonClient client = redisAccess.getRedissonClient();
		String key = REDIS_KEY+uuid.toString();
		RBucket<Account> accountBukket = client.getBucket(key);
		accountBukket.delete();
	}

	public Set<Integer> getBlackListedAnnouncesFromDB(Connection connection) throws SQLException{
		Set<Integer> announces = new HashSet<>();
			try(PreparedStatement ps = 
					connection.prepareStatement(
							"SELECT idAnnonce "
									+ "FROM sc_players, sc_annonces_blacklist "
									+ "WHERE uuid = ? "
									+ "AND sc_players.id=sc_annonces_blacklist.id"))
			{
				ps.setString(1, uuid.toString());
				try(ResultSet rs = ps.executeQuery()){
					while(rs.next()) {
						announces.add(rs.getInt("idAnnonce"));
					}
				}
			}
		return announces;
	}
	
	public void saveBlackListedAnnouncesToDB(Set<Integer> blacklist, Connection connection) throws SQLException {
		final String INSERT_SQL = "INSERT INTO sc_annonces_blacklist(id, idAnnonce) VALUES ((SELECT id FROM sc_players WHERE uuid=?), ?) ON DUPLICATE KEY UPDATE id=VALUES(id);";
		for(int idA : blacklist) {
			PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
			ps.setString(1, uuid.toString());
			ps.setInt(2, idA);
			ps.executeUpdate();
			ps.close();
		}
	}
	
	public void saveIpToDB(String ip, Connection connection) throws SQLException {
		final String INSERT_SQL = "INSERT INTO sc_players_ip(id, ip) VALUES ((SELECT id FROM sc_players WHERE uuid=?), HEX(INET6_ATON(?))) ON DUPLICATE KEY UPDATE id=VALUES(id);";
		PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
		ps.setString(1, uuid.toString());
		ps.setString(2, ip);
		ps.executeUpdate();
		ps.close();
	}

	public boolean hasPlayedBefore() {
		return hasPlayedBefore;
	}
}