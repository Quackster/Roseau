package org.alexdev.roseau.game.item.interactors;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.DOOR_IN;
import org.alexdev.roseau.messages.outgoing.DOOR_OUT;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TeleporterInteractor extends Interaction {
    private static final SimpleLog logger = SimpleLog.of(TeleporterInteractor.class);

	public TeleporterInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStoppedWalking(Player player) {
		Item teleporter = Optional.ofNullable(player.getRoomUser().getRoom())
			.map(room -> room.getItem(item.getTargetTeleporterId()))
			.orElseGet(() -> Roseau.getDao().getItem().getItem(item.getTargetTeleporterId()));

		final Item targetTeleporter = teleporter;
		final Room targetRoom = Optional.ofNullable(targetTeleporter)
			.map(tp -> Roseau.getDao().getRoom().getRoom(tp.getRoomId(), true))
			.orElse(null);
		final Room previousRoom = player.getRoomUser().getRoom();

		Optional.ofNullable(targetRoom).ifPresentOrElse(room -> {
			player.getRoomUser().setCanWalk(false);
			player.getRoomUser().getRoom().send(new DOOR_OUT(item, player.getDetails().getName()));

			final Item currentTeleporter = this.item;

			Runnable task = () -> {
				if (currentTeleporter.getRoomId() != targetTeleporter.getRoomId()) {
					Optional.ofNullable(previousRoom)
						.ifPresent(prevRoom -> prevRoom.leaveRoom(player, false));

					room.loadRoom(player, targetTeleporter.getPosition(), targetTeleporter.getPosition().getRotation());
				} else {
					player.getRoomUser().getPosition().set(targetTeleporter.getPosition());
					player.getRoomUser().sendStatusComposer();
					
					if (targetTeleporter.getInteraction() instanceof TeleporterInteractor interactor) {
						interactor.leaveTeleporter(player);
					}
				}
			};

			Roseau.getGame().getScheduler().schedule(task, GameVariables.TELEPORTER_DELAY, TimeUnit.MILLISECONDS);
		}, () -> logger.warn("Teleporter error: target teleporter not found"));
	}
	
	public void leaveTeleporter(Player player) {
		if (!this.definition.getBehaviour().isTeleporter()) {
			return;
		}

		Optional.ofNullable(player.getRoomUser().getRoom())
			.ifPresent(room -> {
				player.getRoomUser().setCanWalk(false);
				room.send(new DOOR_IN(this.item, player.getDetails().getName()));

				Runnable task = () -> {
					item.setCustomData("TRUE");
					item.updateStatus();

					player.getRoomUser().setCanWalk(true);
					player.getRoomUser().walkTo(
						item.getPosition().getSquareInFront().getX(), 
						item.getPosition().getSquareInFront().getY()
					);
				};

				Roseau.getGame().getScheduler().schedule(task, GameVariables.TELEPORTER_DELAY, TimeUnit.MILLISECONDS);
			});
	}
}
