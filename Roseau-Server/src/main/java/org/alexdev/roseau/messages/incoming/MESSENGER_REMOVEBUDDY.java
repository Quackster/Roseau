package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_REMOVEBUDDY implements MessageEvent {
    @Override
    public void handle(Player player, ClientMessage reader) {
        String username = reader.getMessageBody();

        if (username == null) {
            return;
        }

        int friendId = Roseau.getDao().getPlayer().getId(username);

        if (friendId < 1) {
            return;
        }

        if (friendId == player.getDetails().getId()) {
            return;
        }

        if (!player.getMessenger().isFriend(friendId)) {
            return;
        }

        int toId = player.getDetails().getId();
        Roseau.getDao().getMessenger().removeFriend(friendId, toId);

        MessengerUser friend = player.getMessenger().getFriend(friendId);

        if (friend.isOnline()) {
            friend.getPlayer().getMessenger().removeFriend(player.getDetails().getId());
            friend.getPlayer().getMessenger().sendFriends();
        }

        player.getMessenger().removeFriend(friendId);
        player.getMessenger().sendFriends();
    }
}
