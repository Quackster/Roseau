package org.alexdev.roseau.messages.incoming;

import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MESSENGER_MSG;
import org.alexdev.roseau.server.messages.ClientMessage;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class MESSENGER_SENDMSG implements MessageEvent {
    private static final SimpleLog logger = SimpleLog.of(MESSENGER_SENDMSG.class);

    @Override
    public void handle(Player player, ClientMessage reader) {
        String[] data = reader.getMessageBody().split("\r", 2);
        String[] szReceiverIds = data[0].split(" ");

        try {
            int[] receiverIds = Arrays.stream(szReceiverIds)
                    .mapToInt(Integer::parseInt)
                    .toArray();

            String message = data[1].replace((char) 13, (char) 10);

            IntStream.of(receiverIds)
                    .mapToObj(friendId -> Optional.ofNullable(player.getMessenger().getFriend(friendId)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(user -> {
                        int messageId = Roseau.getDao().getMessenger().newMessage(
                                player.getDetails().getId(),
                                user.getUserId(),
                                message
                        );

                        if (user.isOnline()) {
                            user.getPlayer().send(new MESSENGER_MSG(
                                    messageId,
                                    player.getDetails().getId(),
                                    DateTime.getTime(),
                                    message,
                                    player.getDetails().getFigure()
                            ));
                        }
                    });
        } catch (NumberFormatException ex) {
            logger.warn("Invalid receiver ID format in MESSENGER_SENDMSG");
        }
    }
}
