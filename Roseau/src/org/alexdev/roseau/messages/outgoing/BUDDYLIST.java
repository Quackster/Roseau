package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class BUDDYLIST implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		
		response.init("BUDDYLIST");
        response.appendNewArgument("2");
        response.appendTabArgument("Testing"); // friend name
        response.appendTabArgument("dummy text1"); // messenger motto
        response.appendNewArgument("dummy text2"); //location
        response.appendTabArgument("01/01/1970 10:12:13"); // last online

        response.appendNewArgument("3");
        response.appendTabArgument("TestAgain"); // friend name
        response.appendTabArgument("dummy text3"); // messenger motto
        response.appendNewArgument("dummy text4"); //location
        response.appendTabArgument("01/01/1970 10:12:13"); // last online
	}

}
