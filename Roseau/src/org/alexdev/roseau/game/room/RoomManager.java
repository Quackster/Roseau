package org.alexdev.roseau.game.room;

import java.util.List;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.settings.RoomType;

import com.google.common.collect.Lists;

public class RoomManager {

	public List<Room> loadedRooms;

	public RoomManager() {
		this.loadedRooms = Lists.newArrayList();
	}
	
	public void load() {
		Roseau.getDataAccess().getRoom().getPublicRooms(true);
	}

	public void add(Room room) {

		boolean add = true;
		
		for (Room loadedRoom : this.loadedRooms) {

			if (room.getData().getId() == loadedRoom.getData().getId()) {
				add = false;
			}
		}

		if (add) {
			this.loadedRooms.add(room);
		}
	}
	
	public List<Room> getPublicRooms() {
		try {
			return this.loadedRooms.stream().filter(room -> room.getData().getRoomType() == RoomType.PUBLIC && room.getData().isHidden() == false).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Room> getPlayerRooms(int userId) {
		try {
			return this.loadedRooms.stream().filter(room -> room.getData().getOwnerId() == userId && room.getData().isHidden() == false).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	public Room find(int roomId) {

		try {
			return Roseau.getGame().getRoomManager().getLoadedRooms().stream().filter(r -> r.getData().getId() == roomId).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Room getRoomByPort(int port) {

		try {
			return Roseau.getGame().getRoomManager().getLoadedRooms().stream().filter(r -> r.getData().getServerPort() == port).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Room> getLoadedRooms() {
		return loadedRooms;
	}

}
