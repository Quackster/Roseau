package org.alexdev.roseau.messages.incoming;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerMessage;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_MARKREAD implements MessageEvent {
    @Override
    public void handle(Player player, ClientMessage reader) {
        int messageId = Integer.valueOf(reader.getMessageBody());

        List<MessengerMessage> unreadMessages = Roseau.getDao().getMessenger().getUnreadMessages(player.getDetails().getId());
        for (MessengerMessage message : unreadMessages) {
            if (message.getId() == messageId) {
                Roseau.getDao().getMessenger().markMessageRead(messageId);

                return;
            }
        }
    }
}
