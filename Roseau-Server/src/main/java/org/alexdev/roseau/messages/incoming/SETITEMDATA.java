package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.oldskooler.simplelogger4j.SimpleLog;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SETITEMDATA implements MessageEvent {
    private static final SimpleLog logger = SimpleLog.of(SETITEMDATA.class);

    @Override
    public void handle(Player player, ClientMessage reader) {
        int itemId = Integer.valueOf(reader.getArgument(1, "/"));
        String stickyData = reader.getMessageBody().replace("/" + itemId + "/", "");

        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.hasRights(player, false)) {
            return;
        }

        Item item = room.getItem(itemId);

        if (item == null) {
            return;
        }

        if (!stickyData.startsWith(item.getCustomData())) {
            logger.warn("Potential scripting detected for item ID: " + itemId);
            return;
        }

        item.setCustomData(stickyData);
        item.save();
    }
}
