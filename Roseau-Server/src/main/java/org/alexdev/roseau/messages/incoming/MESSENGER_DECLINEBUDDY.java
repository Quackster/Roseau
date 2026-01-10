package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_DECLINEBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, ClientMessage reader) {
        String username = reader.getMessageBody();

        if (username == null) {
            return;
        }

        int fromId = Roseau.getDao().getPlayer().getId(username);

        if (fromId < 1) {
            return;
        }

        if (fromId == player.getDetails().getId()) {
            return;
        }

        if (!player.getMessenger().hasRequest(fromId)) {
            return;
        }

        int toId = player.getDetails().getId();

        Roseau.getDao().getMessenger().removeRequest(fromId, toId);
    }
}
