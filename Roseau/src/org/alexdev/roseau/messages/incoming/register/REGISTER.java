package org.alexdev.roseau.messages.incoming.register;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.incoming.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class REGISTER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		/*[04/02/2017 00:15:13] [ROSEAU] >> [1924177861] Received: REGISTER / name=ssws
password=123
email=wdwddw@Cc.com
figure=sd=001/0&hr=001/255,255,255&hd=002/255,204,153&ey=001/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=001/232,177,55&ls=001/232,177,55&rs=001/232,177,55&lg=001/119,159,187&sh=001/175,220,223
directMail=0
birthday=08.08.1997
phonenumber=+44
customData=dwdwd
has_read_agreement=1
sex=Male
country=*/
		
		String name = reader.getArgument(0, Character.toString((char)13)).split("=")[1];
		String password = reader.getArgument(1, Character.toString((char)13)).split("=")[1];
		String email = reader.getArgument(2, Character.toString((char)13)).split("=")[1];
		String figure = reader.getArgument(3, Character.toString((char)13)).substring(7); // remove "figure="
		String birthday = reader.getArgument(5, Character.toString((char)13)).split("=")[1];
		String mission = reader.getArgument(7, Character.toString((char)13)).substring(11); // remove "customData=" in case they put a = in their motto
		String sex = reader.getArgument(9, Character.toString((char)13)).split("=")[1];
		
		if (name.length() < 3) {
			Log.println("Invalid name: " + name);
			return;
		}
		
		if (password.length() < 3) {
			Log.println("Invalid password: " + password);
			return;
		}
		
		if (figure.length() < 3) {
			Log.println("Invalid figure: " + figure);
			return;
		}
		
		if (!Roseau.getGame().getPlayerManager().approveName(name)) {
			Log.println("Name not approved: " + name);
			return;
		}
			
		if (Roseau.getDataAccess().getPlayer().isNameTaken(name)) {
			Log.println("Name taken: " + name);
			return;
		}
		
		Roseau.getDataAccess().getPlayer().createPlayer(name, password, email, mission, figure, -1, sex, birthday);
	}

}
