package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.RoomModel;

public interface RoomDao {

	public List<Room> getPublicRooms(boolean storeInMemory);
	public List<Room> getPlayerRooms(PlayerDetails details);
	public List<Room> getPlayerRooms(PlayerDetails details, boolean storeInMemory);
	public Room getRoom(int roomId);
	public Room getRoom(int roomId, boolean storeInMemory);
	public List<Integer> getRoomRights(int roomId);
	public void updateRoom(Room room);
	public RoomModel getModel(String model);
	public void deleteRoom(Room room);
	Room createRoom(Player player, String name, String description, String model, int state);
	
}
