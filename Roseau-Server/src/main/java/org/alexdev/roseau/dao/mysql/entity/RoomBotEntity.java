package org.alexdev.roseau.dao.mysql.entity;

import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

@Entity(table = "room_bots")
public class RoomBotEntity {

	@Id(auto = true)
	private int id;

	@Column(name = "room_id")
	private int roomId;

	private String name;
	private String figure;
	private String motto;

	@Column(name = "start_x")
	private int startX;

	@Column(name = "start_y")
	private int startY;

	@Column(name = "start_z")
	private int startZ;

	@Column(name = "start_rotation")
	private int startRotation;

	@Column(name = "walk_to")
	private String walkTo;

	private String messages;
	private String triggers;
	private String responses;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFigure() {
		return figure;
	}

	public void setFigure(String figure) {
		this.figure = figure;
	}

	public String getMotto() {
		return motto;
	}

	public void setMotto(String motto) {
		this.motto = motto;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getStartZ() {
		return startZ;
	}

	public void setStartZ(int startZ) {
		this.startZ = startZ;
	}

	public int getStartRotation() {
		return startRotation;
	}

	public void setStartRotation(int startRotation) {
		this.startRotation = startRotation;
	}

	public String getWalkTo() {
		return walkTo;
	}

	public void setWalkTo(String walkTo) {
		this.walkTo = walkTo;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public String getTriggers() {
		return triggers;
	}

	public void setTriggers(String triggers) {
		this.triggers = triggers;
	}

	public String getResponses() {
		return responses;
	}

	public void setResponses(String responses) {
		this.responses = responses;
	}
}
