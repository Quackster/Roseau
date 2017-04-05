package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class UPDATE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		if (reader.getMessageBody().contains("ph_figure")) {
			
			// ph_figure=ch=s02/255,146,90
			String poolFigure = reader.getMessageBody().substring(10);
			
			player.getDetails().setPoolFigure(poolFigure);
			player.getDetails().save();
			
			if (player.getRoomUser().getRoom() != null) {
				player.getRoomUser().getRoom().send(player.getRoomUser().getUsersComposer());
			}
		} else {
			
			/*UPDATE / name=Alex1336
password=123
email=wdd@cc.com
figure=sd=001/0&hr=007/255,255,255&hd=002/255,204,153&ey=001/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=005/217,113,69&ls=001/217,113,69&rs=001/217,113,69&lg=001/119,159,187&sh=002/192,180,199
directMail=0
birthday=04.08.1997
phonenumber=
customData=wdddw
has_read_agreement=1
sex=Male
country=UK*/
			
			String password = reader.getArgument(1, Character.toString((char)13)).split("=")[1];
			String email = reader.getArgument(2, Character.toString((char)13)).split("=")[1];
			String figure = reader.getArgument(3, Character.toString((char)13)).substring(7); // remove "figure="
			String mission = reader.getArgument(7, Character.toString((char)13)).substring(11);// remove "customData=" in case they put a = in their motto
			String sex = reader.getArgument(9, Character.toString((char)13)).split("=")[1];
			
			
			if (password.length() > 50) {
				password = email.substring(0, 50);
				return;
			}
			
			if (email.length() > 256) {
				email = email.substring(0, 256);
				return;
			}
			
			if (mission.length() > 100) {
				mission = mission.substring(0, 100);
				return;
			}
			
			if (sex.length() < 4) {
				return;
			}
			
			if (sex.length() > 6) {
				return;
			}
			
			if (password.length() < 3) {
				return;
			}
			
			if (figure.length() < 3) {
				return;
			}
			
			player.getDetails().setPassword(password);
			player.getDetails().setEmail(email);
			player.getDetails().setFigure(figure);
			player.getDetails().setMission(mission);
			player.getDetails().setSex(sex);
			player.getDetails().save();
		}
	}

}
