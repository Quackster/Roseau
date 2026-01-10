package org.alexdev.roseau.game.player;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.util.Util;

import java.util.List;
import java.util.Optional;

public class Bot implements Entity {
    private static final SimpleLog logger = SimpleLog.of(Bot.class);

	private final PlayerDetails details;
	private final RoomUser roomEntity;

	private final Position startPosition;

	private final List<int[]> positions;
	private final List<String> responses;
	private final List<String> triggers;

	public Bot(Position position, List<int[]> positions, List<String> responses, List<String> triggers) {
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomUser(this);

		this.positions = positions;
		this.startPosition = position;
		this.responses = responses;
		this.triggers = triggers;
		
		responses.forEach(response -> logger.debug("Bot response: " + response));
	}
	
	public String containsTrigger(String phrase) {
		return triggers.stream()
			.filter(trigger -> phrase.toLowerCase().contains(trigger.toLowerCase()))
			.findFirst()
			.orElse(null);
	}

	public String getResponse(String username, String item) {
		return Optional.of(responses)
			.filter(resps -> !resps.isEmpty())
			.map(resps -> resps.get(Util.getRandom().nextInt(resps.size())))
			.map(response -> response
				.replace("%username%", username)
				.replace("%item%", item))
			.orElse(null);
	}

	public PlayerDetails getDetails() {
		return details;
	}

	@Override
	public EntityType getType() {
		return EntityType.BOT;
	}

	@Override
	public RoomUser getRoomUser() {
		return this.roomEntity;
	}

	public Position getStartPosition() {
		return startPosition;
	}

	public List<int[]> getPositions() {
		return positions;
	}
}
