package fr.iban.common.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbTables {

	public static void createTables() {
		createplayersTable();
		createAnnounceBLTable();
		createIpTable();
		createBoostsTable();
	}

	/*
	 * Création de la table
	 */
	private static void createplayersTable() {
		createTable("CREATE TABLE IF NOT EXISTS sc_players (" +
					"  id          int auto_increment PRIMARY KEY," +
					"  uuid    varchar(36)  not null," +
					"  name        varchar(16)  not null," +
					"  date_created timestamp default now()," +
					"  lastseen       bigint DEFAULT 0," +
					"  exp bigint DEFAULT 0," +
					"  maxclaims smallint DEFAULT 1," +
					"  allowpvp boolean DEFAULT false," +
					"  CONSTRAINT  UC_sc_players" +
					"  UNIQUE (id)," +
					"  CONSTRAINT UC_sc_players_uuid" +
					"  UNIQUE (uuid)" +
					") engine = InnoDB;");
	}
	
	private static void createBoostsTable() {
		createTable("CREATE TABLE IF NOT EXISTS sc_boosts ("
				+ " id int auto_increment PRIMARY KEY,"
				+ " owner varchar(255), "
				+ " end bigint, "
				+ " value int "
				+ ");"
				);
	}

	private static void createAnnounceBLTable() {
		createTable("CREATE TABLE IF NOT EXISTS sc_annonces_blacklist (" +
							"  id int," +
							"  idAnnonce int," +
							"  CONSTRAINT PK_sc_annonces_blacklist" +
							"  PRIMARY KEY (id, idAnnonce)," +
							"  CONSTRAINT FK_sc_annonces" +
							"  FOREIGN KEY (id) REFERENCES sc_players(id)" +
					") engine = InnoDB;");
	}

	private static void createIpTable() {
		createTable("CREATE TABLE IF NOT EXISTS sc_players_ip (" +
							"  id int," +
							"  ip VARBINARY(16)," +
							"  CONSTRAINT PK_sc_players_ip" +
							"  PRIMARY KEY (id, ip)," +
							"  CONSTRAINT FK_sc_players_ip" +
							"  FOREIGN KEY (id) REFERENCES sc_players(id)" +
					") engine = InnoDB;");
	}
	private static void createTable(String statement) {
		try (Connection connection = DbAccess.getDataSource().getConnection()) {
			try(PreparedStatement preparedStatemente = connection.prepareStatement(statement)){
				preparedStatemente.executeUpdate();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
