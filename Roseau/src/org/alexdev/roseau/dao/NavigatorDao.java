package org.alexdev.roseau.dao;

import java.util.List;

import org.alexdev.roseau.game.room.Room;

public interface NavigatorDao {

	List<Room> getRoomsByLikeName(String name);

}