package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.IEntity;

public class PlayerDetails {

	private int id = -1;
	private String username = "Alex";
	private String motto = "banana man";
	private String figure;
	private int rank;
	private int credits;
	
	private boolean authenticated;
	private IEntity entity;
	
	public PlayerDetails(IEntity session) {
		this.authenticated = false;
		this.entity = session;
	}
	
	public void fill(int id, String username, String motto, String figure, int rank, int credits) {
		
		this.id = id;
		this.username = username;
		this.motto = motto;
		this.figure = figure;
		this.rank = rank;
		this.credits = credits;
		this.authenticated = true;
	}
	
	public boolean hasFuse(String fuse) {
		return false;
	}
	
	public void dispose() {
		
		this.username = null;
		this.motto = null;
		this.figure = null;
	}
	
	public int getId() {
		return id;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public int getRank() {
		return rank;
	}

	public void setCredits(int newTotal, boolean sendUpdate) {
		this.credits = newTotal;
		
	}
	
	public int getCredits() {
		return credits;
	}

	public IEntity getEntity() {
		return entity;
	}

	public void setEntity(IEntity entity) {
		this.entity = entity;
	}
}
