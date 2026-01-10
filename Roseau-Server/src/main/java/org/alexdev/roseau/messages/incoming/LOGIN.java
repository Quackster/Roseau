package org.alexdev.roseau.messages.incoming;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

import java.util.Optional;

public class LOGIN implements MessageEvent {
    private static final SimpleLog logger = SimpleLog.of(LOGIN.class);

    @Override
    public void handle(Player player, ClientMessage reader) {
        if (reader.getArgumentAmount() <= 1) {
            player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
            player.getNetwork().close();
            return;
        }

        String username = reader.getArgument(0);
        String password = reader.getArgument(1);

        boolean authenticated = Roseau.getDao().getPlayer().login(player, username, password);

        if (authenticated) {
            Optional.ofNullable(Roseau.getGame().getPlayerManager().getPlayerByPortDifferentConnection(
                    player.getDetails().getId(),
                    player.getNetwork().getServerPort(),
                    player.getNetwork().getConnectionId()))
                    .ifPresent(otherPlayer -> otherPlayer.getNetwork().close());

            player.getDetails().setAuthenticated(true);
            player.getDetails().setPassword(password);

            if (reader.getArgumentAmount() > 2) {
                int serverPort = player.getNetwork().getServerPort();
                Room room = Optional.ofNullable(Roseau.getGame().getRoomManager().getRoomByPort(serverPort))
                        .orElseGet(() -> {
                            // Since public rooms need to bind to their own port, I've made it so
                            // the ID of the public room is just the current players connected port minus the main server IP
                            // eg; 30045 - 30001 = public room ID 44
                            int publicRoomId = serverPort - Roseau.getServerPort();
                            Room loadedRoom = Roseau.getDao().getRoom().getRoom(publicRoomId, true);

                            if (loadedRoom == null) {
                                logger.info("Grabbed new room from database: " + publicRoomId);
                            }
                            return loadedRoom;
                        });

                Optional.ofNullable(room).ifPresent(r -> r.loadRoom(player));
            }

            player.login(reader.getArgumentAmount() <= 2);
        } else {
            player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
            player.getNetwork().close();
        }
    }
}
