package fr.iban.spartacube.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import fr.iban.common.data.Boost;

public class Account {



	/*
	 * PLAYERDATA
	 * 
	 * Contient toutes les informations et méthodes relatives à un joueur.
	 * 
	 */

	private UUID uuid;
	private String name;
	private long exp = 0;
	private short maxClaims = 1;
	private long lastSeen = 0;
	private boolean bypass = false;
	private Set<Integer> blackListedAnnounces = new HashSet<>();
	private boolean pvp = false;
	private String ip;
	private List<Boost> boosts;

	public Account(UUID uuid) {
		this.uuid = uuid;
	}
	
	public Account() {}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public long getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(long lastseen) {
		this.lastSeen = lastseen;
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean isBypass() {
		return bypass;
	}

	public void setBypass(boolean bypass) {
		this.bypass = bypass;
	}

	public short getMaxClaims() {
		return maxClaims;
	}

	public void setMaxClaims(short maxClaims) {
		this.maxClaims = maxClaims;
	}

	public void addMaxClaims(short nombre) {
		maxClaims += nombre;
	}

	public void removeMaxClaims(short nombre) {
		maxClaims -= nombre;
		if(maxClaims < 0) maxClaims = 0;
	}

	/*
	 * LEVELS
	 */
	public short getLevel() {
		return (short) (Math.floor(25 + Math.sqrt(625 + 100 * exp)) / 50);
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public void addExp(int amount) {
		this.exp += amount;
	}

	public Set<Integer> getBlackListedAnnounces() {
		if(blackListedAnnounces == null) {
			blackListedAnnounces = new HashSet<>();
		}
		return blackListedAnnounces;
	}
	
	public void setBlackListedAnnounces(Set<Integer> blackListedAnnounces) {
		this.blackListedAnnounces = blackListedAnnounces;
	}

	public boolean isPvp() {
		return pvp;
	}
	
	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}

	public void togglePVP() {
		if(pvp) {
			pvp = false;
		}else {
			pvp = true;
		}
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<Boost> getBoosts() {
		if(boosts == null) {
			boosts = new ArrayList<>();
		}
		return boosts;
	}
	
	public void setBoosts(List<Boost> boosts) {
		this.boosts = boosts;
	}
	
	public int getTotalBoost() {
		int somme = 0;
		Iterator<Boost> it = boosts.iterator();
		while(it.hasNext()) {
			Boost boost = it.next();
			if(boost.getEnd() > System.currentTimeMillis()) {
				somme += boost.getValue();
			}else {
				//TODO remove from db
				it.remove();
			}
		}
		return somme > 100 ? 100 : somme;
	}
}
