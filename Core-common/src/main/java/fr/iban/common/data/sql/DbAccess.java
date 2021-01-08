package fr.iban.common.data.sql;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DbAccess {

	private DbCredentials credentials;
	private HikariDataSource dataSource;

	public DbAccess(DbCredentials credentials){
		this.credentials = credentials;
	}


	public void initPool() {

		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl(credentials.toURI());
		hikariConfig.setUsername(credentials.getUser());
		hikariConfig.setPassword(credentials.getPass());
		hikariConfig.setMaximumPoolSize(10);
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		System.out.println("Tentative de connexion à la bdd " + credentials.toURI());
		this.dataSource = new HikariDataSource(hikariConfig);
		System.out.println("Connection effectuée.");
	}

	public void closePool(){
		if(this.dataSource != null && !this.dataSource.isClosed())
			this.dataSource.close();
	}

	public DataSource getDataSource() {
		return this.dataSource;
	}

}
