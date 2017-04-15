package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.catalogue.CatalogueItem;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.PURCHASEOK;
import org.alexdev.roseau.messages.outgoing.PURCHASE_ADDSTRIPITEM;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PURCHASE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String callID = reader.getArgument(0).replace("/", "");
		
		/*Room room = player.getPrivateRoomPlayer().getRoomUser().getRoom();
					
		if (callID.equals("floor")) {
			
			String decoration = reader.getArgument(1);
			
			player.send(new FLATPROPERTY("floor", decoration));
			room.getData().setFloor(decoration);
			room.getData().save();
			
			return;
		} 
		
		if (callID.equals("wallpaper")) {
			
			String decoration = reader.getArgument(1);
			
			player.send(new FLATPROPERTY("wallpaper", decoration));
			room.getData().setWall(decoration);
			room.getData().save();
			
			return;
		}*/	
		
		CatalogueItem product = Roseau.getGame().getCatalogueManager().getItemByCall(callID);
		
		if (product == null) {
			return;
		}
		
		int oldCredits = player.getDetails().getCredits();
		
		if (oldCredits >= product.getCredits()) {
			
			Item item = Roseau.getDataAccess().getInventory().newItem(product.getDefinition().getID(), player.getDetails().getID(), "");
			
			if (item.getDefinition().getBehaviour().isDecoration() || callID.equals("juliste")) {
				item.setCustomData(reader.getArgument(1));
				item.save();
			}
			
			//player.send(new SYSTEMBROADCAST("Buying successful!"));
			
			// Update the player connected to the private room
			//   (because PURCHASE is handled by the connection on the main game server, but we need to update the connection
			//    inside the hotel room)
			//
			
			/*for (Player p : Roseau.getGame().getPlayerManager().getPlayers().values()) {
				
				Log.println(p.getDetails().getID() + " -- " + p.getNetwork().getServerPort());
			}*/
			
			Player p = player.getPrivateRoomPlayer();
			p.getInventory().addItem(item);
			
			player.send(new PURCHASE_ADDSTRIPITEM());
			player.send(new PURCHASEOK());
			
			player.getDetails().setCredits(player.getDetails().getCredits() - product.getCredits());
			player.getDetails().sendCredits();
			player.getDetails().save();
			
		} else {
			player.send(new SYSTEMBROADCAST("You don't have enough credits to purchase this item!"));
		}
	}

}
