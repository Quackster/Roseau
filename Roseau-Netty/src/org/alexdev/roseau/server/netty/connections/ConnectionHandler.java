package org.alexdev.roseau.server.netty.connections;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.handshake.HELLO;
import org.alexdev.roseau.server.encoding.Base64Encoding;
import org.alexdev.roseau.server.netty.readers.NettyRequest;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ConnectionHandler extends SimpleChannelHandler {
	
	private SessionManager sessionHandler;
	
	public ConnectionHandler(SessionManager sessionHandler) {
		this.sessionHandler = sessionHandler;
	}
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {

		if (Roseau.getUtilities().getConfiguration().getBoolean("log-connections")) {
			Log.println("Connection from " + ctx.getChannel().getRemoteAddress().toString().replace("/", "").split(":")[0]);
		}
		
		ctx.getChannel().write(new HELLO());
		
		this.sessionHandler.addSession(ctx.getChannel());

	} 

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {

		if (Roseau.getUtilities().getConfiguration().getBoolean("log-connections")) {
			Log.println("Disconnection from " + ctx.getChannel().getRemoteAddress().toString().replace("/", "").split(":")[0]);
		}
		
		this.sessionHandler.removeSession(ctx.getChannel());
		
		Player player = (Player) ctx.getChannel().getAttachment();
		player.dispose();
		
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		try {

			Player player = (Player) ctx.getChannel().getAttachment();
			NettyRequest request = (NettyRequest) e.getMessage();
			
			if (request == null) {
				return;
			}

			if (Roseau.getUtilities().getConfiguration().getBoolean("log-packets")) {
				Log.println("Received: " + request.getMessageId() + " / " + new String(Base64Encoding.EncodeInt32(request.getMessageId(), 2)) + " / " + request.getMessageBody());
			}

			if (player != null){
				Roseau.getServer().getMessageHandler().handleRequest(player, request);
			}

		} catch (Exception ex) {
			Log.exception(ex);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		ctx.getChannel().close();
	}

}
