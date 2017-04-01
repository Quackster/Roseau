package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.IEntity;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class PlayerDetails implements SerializableObject {

	private int id = -1;
	private String username;
	private String mission;
	private String figure;
	private String email;
	private int rank;
	private int credits;
	private String sex;
	private String country;
	private String badge;
	private String birthday;
	
	
	private boolean authenticated;
	private IEntity entity;
	
	public PlayerDetails(IEntity session) {
		this.authenticated = false;
		this.entity = session;
	}
	
	public void fill(int id, String username, String mission, String figure, String email, int rank, int credits, String sex, String country, String badge, String birthday) {
		this.id = id;
		this.username = username;
		this.mission = mission;
		this.figure = figure;
		this.email = email;
		this.rank = rank;
		this.credits = credits;
		this.sex = sex;
		this.country = country;
		this.badge = badge;
		this.birthday = birthday;
	}
	
	@Override
	public void serialise(Response response) {
        response.appendKVArgument("name", this.username);
        response.appendKVArgument("figure", this.figure); 
        response.appendKVArgument("email", this.email);
        response.appendKVArgument("birthday", this.birthday);
        response.appendKVArgument("phonenumber", "+44");
        response.appendKVArgument("customData", this.mission);
        response.appendKVArgument("has_read_agreement", "1");
        response.appendKVArgument("sex", this.sex);
        response.appendKVArgument("country", this.country);
        response.appendKVArgument("has_special_rights", "0");
        response.appendKVArgument("badge_type", this.badge);
	}
	
	public boolean hasFuse(String fuse) {
		return false;
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

	public String getMission() {
		return mission;
	}

	public void setMission(String motto) {
		this.mission = motto;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getSex() {
		return sex;
	}

	public String getCountry() {
		return country;
	}

	public String getBadge() {
		return badge;
	}

	public String getBirthday() {
		return birthday;
	}

	public IEntity getEntity() {
		return entity;
	}

	public void setEntity(IEntity entity) {
		this.entity = entity;
	}
}
