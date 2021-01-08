package fr.iban.common.data.sql;

public enum DbManager {

	BD(new DbCredentials("172.17.0.1", "u1_EZaQuXpFzM", "sL5njk=HVH=5tXPqBmG.h8aX", "s1_mcserver", 3306));

	private DbAccess dbAccess;

	private DbManager(DbCredentials credentials){
		this.dbAccess = new DbAccess(credentials);
	}

	public DbAccess getDbAccess() {
		return this.dbAccess;
	}


	public static void initAllDbConnections(){
		for(DbManager dbManager : values()){
			dbManager.dbAccess.initPool();
		}
	}

	public static void closeAllDbConnections(){
		for(DbManager dbManager : values()){
			dbManager.dbAccess.closePool();
		}
	}

}

