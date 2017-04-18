package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class BUDDYLIST implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		
		response.init("BUDDYLIST");
        response.appendNewArgument("1");
        response.appendTabArgument("Alerts"); // friend name
        response.appendTabArgument("In a Java JVM"); //location
        response.appendNewArgument("System alerts"); // messenger motto
        response.appendTabArgument(DateTime.toString(Roseau.getUtilities().getTimestamp())); // last online
	}

}
