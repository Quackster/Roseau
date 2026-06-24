package org.alexdev.roseau.dao.mysql.entity;

import org.oldskooler.entity4j.annotations.Column;
import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

@Entity(table = "room_models")
public class RoomModelEntity {

	@Id
	private String id;

	@Column(name = "door_x")
	private int doorX;

	@Column(name = "door_y")
	private int doorY;

	@Column(name = "door_z")
	private int doorZ;

	@Column(name = "door_dir")
	private int doorDir;

	private String heightmap;

	@Column(name = "has_pool")
	private int hasPool;

	@Column(name = "disable_height_check")
	private int disableHeightCheck;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDoorX() {
		return doorX;
	}

	public void setDoorX(int doorX) {
		this.doorX = doorX;
	}

	public int getDoorY() {
		return doorY;
	}

	public void setDoorY(int doorY) {
		this.doorY = doorY;
	}

	public int getDoorZ() {
		return doorZ;
	}

	public void setDoorZ(int doorZ) {
		this.doorZ = doorZ;
	}

	public int getDoorDir() {
		return doorDir;
	}

	public void setDoorDir(int doorDir) {
		this.doorDir = doorDir;
	}

	public String getHeightmap() {
		return heightmap;
	}

	public void setHeightmap(String heightmap) {
		this.heightmap = heightmap;
	}

	public int getHasPool() {
		return hasPool;
	}

	public void setHasPool(int hasPool) {
		this.hasPool = hasPool;
	}

	public int getDisableHeightCheck() {
		return disableHeightCheck;
	}

	public void setDisableHeightCheck(int disableHeightCheck) {
		this.disableHeightCheck = disableHeightCheck;
	}
}
