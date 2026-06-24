package org.alexdev.roseau.dao.mysql.entity;

import org.oldskooler.entity4j.annotations.Entity;
import org.oldskooler.entity4j.annotations.Id;

@Entity(table = "users_permissions")
public class UserPermissionEntity {

	@Id(auto = true)
	private int id;

	private int rank;
	private String permission;
	private int inheritable;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public int getInheritable() {
		return inheritable;
	}

	public void setInheritable(int inheritable) {
		this.inheritable = inheritable;
	}
}
