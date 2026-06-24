package org.alexdev.roseau.dao.mysql.entity;

import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

@Entity(table = "rooms")
public class RoomEntity {

	@Id(auto = true)
	private int id;

	private String name;

	@Column(name = "order_id")
	private int orderId;

	@Column(name = "room_type")
	private int roomType;

	private int enabled;
	private int hidden;

	@Column(name = "owner_id")
	private int ownerId;

	private String description;
	private String password;
	private int state;

	@Column(name = "show_owner_name")
	private int showOwnerName;

	private int allsuperuser;

	@Column(name = "users_now")
	private int usersNow;

	@Column(name = "users_max")
	private int usersMax;

	private String cct;
	private String model;
	private String wallpaper;
	private String floor;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getRoomType() {
		return roomType;
	}

	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getHidden() {
		return hidden;
	}

	public void setHidden(int hidden) {
		this.hidden = hidden;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getShowOwnerName() {
		return showOwnerName;
	}

	public void setShowOwnerName(int showOwnerName) {
		this.showOwnerName = showOwnerName;
	}

	public int getAllsuperuser() {
		return allsuperuser;
	}

	public void setAllsuperuser(int allsuperuser) {
		this.allsuperuser = allsuperuser;
	}

	public int getUsersNow() {
		return usersNow;
	}

	public void setUsersNow(int usersNow) {
		this.usersNow = usersNow;
	}

	public int getUsersMax() {
		return usersMax;
	}

	public void setUsersMax(int usersMax) {
		this.usersMax = usersMax;
	}

	public String getCct() {
		return cct;
	}

	public void setCct(String cct) {
		this.cct = cct;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getWallpaper() {
		return wallpaper;
	}

	public void setWallpaper(String wallpaper) {
		this.wallpaper = wallpaper;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}
}
