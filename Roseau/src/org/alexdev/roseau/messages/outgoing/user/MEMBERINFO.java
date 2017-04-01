package org.alexdev.roseau.messages.outgoing.user;

import org.alexdev.roseau.messages.outgoing.OutgoingMessageComposer;
import org.alexdev.roseau.server.messages.Response;

public class MEMBERINFO implements OutgoingMessageComposer {

	@Override
	public void write(Response response) {
		response.init("MEMBERINFO");
		response.appendArgument("");
		response.appendNewArgument("Alex");
		response.appendNewArgument("test");
		response.appendNewArgument(""); // shows 'offline'
		response.appendNewArgument("01-01-01 15:33:12");
		response.appendNewArgument("sd=001/0&hr=001/255,255,255&hd=002/255,204,153&ey=001/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=001/232,177,55&ls=001/232,177,55&rs=001/232,177,55&lg=001/119,159,187&sh=003/121,94,83");
	}

}
