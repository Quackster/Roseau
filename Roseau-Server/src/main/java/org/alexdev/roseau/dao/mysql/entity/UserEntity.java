package org.alexdev.roseau.dao.mysql.entity;

import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

@Entity(table = "users")
public class UserEntity {

	@Id(auto = true)
	private int id;

	private String username;
	private String password;
	private int rank;
	private String mission;
	private String figure;

	@Column(name = "pool_figure")
	private String poolFigure;

	private String email;
	private int credits;
	private String sex;
	private String country;
	private String badge;
	private String birthday;

	@Column(name = "join_date")
	private long joinDate;

	@Column(name = "last_online")
	private long lastOnline;

	@Column(name = "personal_greeting")
	private String personalGreeting;

	private int tickets;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public String getPoolFigure() {
		return poolFigure;
	}

	public void setPoolFigure(String poolFigure) {
		this.poolFigure = poolFigure;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public long getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(long joinDate) {
		this.joinDate = joinDate;
	}

	public long getLastOnline() {
		return lastOnline;
	}

	public void setLastOnline(long lastOnline) {
		this.lastOnline = lastOnline;
	}

	public String getPersonalGreeting() {
		return personalGreeting;
	}

	public void setPersonalGreeting(String personalGreeting) {
		this.personalGreeting = personalGreeting;
	}

	public int getTickets() {
		return tickets;
	}

	public void setTickets(int tickets) {
		this.tickets = tickets;
	}
}
