package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_REQUESTBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, ClientMessage reader) {
        String username = reader.getArgument(0, Character.toString((char) 13));

        if (username == null) {
            return;
        }

        int toId = Roseau.getDao().getPlayer().getId(username);

        if (toId < 1) {
            return;
        }

        if (toId == player.getDetails().getId()) {
            return;
        }

        if (player.getMessenger().hasRequest(toId)) {
            return;
        }

        if (Roseau.getDao().getMessenger().newRequest(player.getDetails().getId(), toId)) {
            MessengerUser user = new MessengerUser(toId);
            player.getMessenger().getRequests().add(user);

            if (user.isOnline()) {
                user.getPlayer().getMessenger().getRequests().add(new MessengerUser(player.getDetails().getId()));
                user.getPlayer().getMessenger().sendRequests();
            }
        }
    }
}
