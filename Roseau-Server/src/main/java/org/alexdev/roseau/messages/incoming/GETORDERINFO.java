package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueDeal;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.ItemDefinition;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ORDERINFO;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETORDERINFO implements MessageEvent {
    @Override
    public void handle(Player player, ClientMessage reader) {
        String callId = reader.getMessageBody().substring(4).replace(" " + player.getDetails().getName(), "");
        String catalogueId = callId;

        if (callId.contains("L ") || callId.contains("T ") || callId.contains("juliste ")) {
            catalogueId = callId.split(" ")[0];
        }

        CatalogueItem item = Roseau.getGame().getCatalogueManager().getItemByCall(catalogueId);
        CatalogueDeal deal = Roseau.getGame().getCatalogueManager().getDealByCall(catalogueId);

        boolean validOrderInfo = false;

        if (deal != null) {
            player.send(new ORDERINFO(deal.getCallId(), deal.getCost()));
            validOrderInfo = true;
        } else if (item != null) {
            ItemDefinition definition = item.getDefinition();

            if (definition == null) {
                return;
            }

            String extraData = "";

            if (callId.contains("L ") || callId.contains("T ") || callId.contains("juliste ")) {
                extraData += " " + callId.split(" ")[1];
            }

            player.send(new ORDERINFO(item.getCallId() + extraData, item.getCredits()));
            validOrderInfo = true;
        }

        if (validOrderInfo) {
            player.setOrderInfoProtection(DateTime.getTime());
        }
    }
}
