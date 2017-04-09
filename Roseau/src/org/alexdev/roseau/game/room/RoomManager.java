package org.alexdev.roseau.game.room;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.settings.RoomType;

import com.google.common.collect.Maps;

public class RoomManager {

	public Map<Integer, Room> loadedRooms;

	public RoomManager() {
		this.loadedRooms = Maps.newHashMap();
	}
	
	public void load() {
		Roseau.getDataAccess().getRoom().getPublicRooms(true);
	}

	public void add(Room room) {

		if (!this.loadedRooms.containsKey(room.getData().getId())) {
			this.loadedRooms.put(room.getData().getId(), room);
		}
	}
	
	public List<Room> getPublicRooms() {
		try {
			List<Room> rooms =  this.loadedRooms.values().stream().filter(
					room -> room.getData().getRoomType() == RoomType.PUBLIC && 
					room.getData().isHidden() == false)
					.collect(Collectors.toList());
			
			/*Collections.sort(rooms,new Comparator<Room>() {
			    @Override
			    public int compare(Room a, Room b) {
			        return b.getUsers().size() - a.getUsers().size();
			    }
			});*/
			
			Collections.sort(rooms,new Comparator<Room>() {
			    @Override
			    public int compare(Room a, Room b) {
			        return a.getOrderId() - b.getOrderId();
			    }
			});
			
			return rooms;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Room> getPopularRooms() {
		try {
			List<Room> rooms =  this.loadedRooms.values().stream().filter(
					room -> room.getData().getRoomType() == RoomType.PRIVATE && 
					room.getData().isHidden() == false && 
					room.getData().getUsersNow() > 0).
					collect(Collectors.toList());
			
			Collections.sort(rooms,new Comparator<Room>() {
			    @Override
			    public int compare(Room a, Room b) {
			        return b.getPlayers().size() - a.getPlayers().size();
			    }
			});
			
			return rooms;
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Room> getPlayerRooms(int userId) {
		try {
			return this.loadedRooms.values().stream().filter(room -> room.getData().getOwnerId() == userId && room.getData().isHidden() == false).collect(Collectors.toList());
		} catch (Exception e) {
			return null;
		}
	}

	public Room getRoomById(int roomId) {

		if (this.loadedRooms.containsKey(roomId)) {
			return this.loadedRooms.get(roomId);
		}
		
		return null;
	}
	
	public Room getRoomByPort(int port) {

		try {
			return Roseau.getGame().getRoomManager().getLoadedRooms().values().stream().filter(r -> r.getData().getServerPort() == port).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Room getRoomByName(String name) {

		try {
			return Roseau.getGame().getRoomManager().getLoadedRooms().values().stream().filter(r -> r.getData().getName().equals(name)).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public Map<Integer, Room> getLoadedRooms() {
		return this.loadedRooms;
	}

}
