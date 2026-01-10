package org.alexdev.roseau.game.room;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.room.settings.RoomType;

public class RoomManager {

	private final Map<Integer, Room> loadedRooms;

	public RoomManager() {
		this.loadedRooms = new HashMap<>();
	}
	
	public void load() {
		Roseau.getDao().getRoom().getPublicRooms(true);
	}

	public void add(Room room) {
		Optional.ofNullable(room)
			.map(Room::getData)
			.map(RoomData::getId)
			.ifPresent(roomId -> loadedRooms.putIfAbsent(roomId, room));
	}
	
	public List<Room> getPublicRooms() {
		return loadedRooms.values().stream()
			.filter(room -> room.getData().getRoomType() == RoomType.PUBLIC)
			.filter(room -> !room.getData().isHidden())
			.sorted(Comparator.comparingInt(Room::getOrderId))
			.collect(Collectors.toList());
	}
	
	public List<Room> getPopularRooms(int multiplier) {
		List<Integer> loadedIds = loadedRooms.values().stream()
			.filter(room -> room.getData().getRoomType() == RoomType.PRIVATE)
			.filter(room -> !room.getData().isHidden())
			.filter(room -> room.getData().getUsersNow() > 0)
			.map(room -> room.getData().getId())
			.collect(Collectors.toList());
		
		int range = (multiplier > 0) ? multiplier / 11 : 0;
		
		List<Room> latestRooms = Roseau.getDao().getRoom().getLatestPlayerRooms(loadedIds, range);
		
		List<Room> allRooms = loadedRooms.values().stream()
			.filter(room -> room.getData().getRoomType() == RoomType.PRIVATE)
			.filter(room -> !room.getData().isHidden())
			.filter(room -> room.getData().getUsersNow() > 0)
			.collect(Collectors.toList());
		
		allRooms.addAll(latestRooms);
		allRooms.sort(Comparator.<Room>comparingInt(room -> room.getPlayers().size()).reversed());
		
		int fromIndex = Math.min(range, allRooms.size());
		return allRooms.subList(fromIndex, allRooms.size());
	}
	
	public List<Room> getPlayerRooms(int userId) {
		return loadedRooms.values().stream()
			.filter(room -> room.getData().getOwnerId() == userId)
			.filter(room -> !room.getData().isHidden())
			.collect(Collectors.toList());
	}

	public Room getRoomByID(int roomId) {
		return Optional.ofNullable(loadedRooms.get(roomId))
			.orElse(null);
	}
	
	public Room getRoomByPort(int port) {
		return loadedRooms.values().stream()
			.filter(room -> room.getData().getServerPort() == port)
			.findFirst()
			.orElse(null);
	}
	
	public Room getRoomByName(String name) {
		return loadedRooms.values().stream()
			.filter(room -> room.getData().getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public Map<Integer, Room> getLoadedRooms() {
		return loadedRooms;
	}
}
