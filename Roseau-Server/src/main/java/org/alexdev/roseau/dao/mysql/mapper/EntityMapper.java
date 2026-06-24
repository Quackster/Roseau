package org.alexdev.roseau.dao.mysql.mapper;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.dao.mysql.entity.ItemDefinitionEntity;
import org.alexdev.roseau.dao.mysql.entity.ItemEntity;
import org.alexdev.roseau.dao.mysql.entity.MessengerMessageEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomModelEntity;
import org.alexdev.roseau.dao.mysql.entity.RoomPublicItemEntity;
import org.alexdev.roseau.dao.mysql.entity.UserEntity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.messenger.MessengerMessage;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.RoomModel;
import org.alexdev.roseau.game.room.settings.RoomType;

public final class EntityMapper {

	private EntityMapper() {
	}

	public static ItemDefinition toItemDefinition(ItemDefinitionEntity entity) {
		return new ItemDefinition(entity.getId(), entity.getSprite(), entity.getColor(), entity.getLength(), entity.getWidth(),
				entity.getHeight(), entity.getBehaviour(), entity.getName(), entity.getDescription(), entity.getDataclass());
	}

	public static Item toItem(ItemEntity entity) {
		return new Item(entity.getId(), entity.getRoomId(), entity.getUserId(), entity.getX(), entity.getY(), entity.getZ(),
				entity.getRotation(), entity.getItemId(), "", entity.getExtraData());
	}

	public static Item toPublicItem(RoomPublicItemEntity entity, int roomId) {
		return new Item(entity.getId(), roomId, -1, entity.getX(), entity.getY(), entity.getZ(), entity.getRotation(),
				entity.getDefinitionId(), entity.getObject(), entity.getData());
	}

	public static RoomModel toRoomModel(RoomModelEntity entity) {
		return new RoomModel(entity.getId(), entity.getHeightmap(), entity.getDoorX(), entity.getDoorY(), entity.getDoorZ(),
				entity.getDoorDir(), entity.getHasPool() == 1, entity.getDisableHeightCheck() == 1);
	}

	public static MessengerMessage toMessengerMessage(MessengerMessageEntity entity) {
		return new MessengerMessage(entity.getId(), entity.getToId(), entity.getFromId(), entity.getTimeSent(), entity.getMessage());
	}

	public static PlayerDetails toPlayerDetails(UserEntity entity, PlayerDetails details) {
		details.fill(entity.getId(), entity.getUsername(), entity.getMission(), entity.getFigure(), entity.getPoolFigure(),
				entity.getEmail(), entity.getRank(), entity.getCredits(), entity.getSex(), entity.getCountry(), entity.getBadge(),
				entity.getBirthday(), entity.getLastOnline(), entity.getPersonalGreeting(), entity.getTickets());
		details.setPassword(entity.getPassword());
		return details;
	}

	public static Room toRoom(RoomEntity entity) {
		RoomType type = RoomType.getType(entity.getRoomType());
		PlayerDetails details = null;

		if (type == RoomType.PRIVATE) {
			details = Roseau.getGame().getPlayerManager().getPlayerData(entity.getOwnerId());
		}

		Room room = new Room();
		room.getData().fill(entity.getId(), entity.getHidden() == 1, type, details == null ? 0 : details.getID(),
				details == null ? "" : details.getName(), entity.getName(), entity.getState(), entity.getPassword(),
				entity.getUsersNow(), entity.getUsersMax(), entity.getDescription(), entity.getModel(), entity.getCct(),
				entity.getWallpaper(), entity.getFloor(), entity.getAllsuperuser() == 1, entity.getShowOwnerName() == 1);

		if (details != null) {
			room.getData().setOwnerName(details.getName());
		}

		try {
			room.load();
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load room " + entity.getId(), e);
		}

		return room;
	}
}
