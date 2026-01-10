package org.alexdev.roseau.server.netty.connections;

import org.oldskooler.simplelogger4j.SimpleLog;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.HELLO;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.server.netty.readers.NettyRequest;
import org.alexdev.roseau.util.Util;

public class ConnectionHandler extends ChannelInboundHandlerAdapter {
    private static final SimpleLog logger = SimpleLog.of(ConnectionHandler.class);
	private final IServerHandler serverHandler;
	private static final io.netty.util.AttributeKey<Player> PLAYER_KEY = 
		io.netty.util.AttributeKey.valueOf("player");
	
	public ConnectionHandler(IServerHandler serverHandler) {
		this.serverHandler = serverHandler;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(new HELLO());
		
		Player player = this.serverHandler.getSessionManager().addSession(ctx.channel());
		ctx.channel().attr(PLAYER_KEY).set(player);
		
		if (Util.getConfiguration().get("Logging", "log.connections", Boolean.class)) {
			String remoteAddress = ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0];
			logger.info("[" + player.getNetwork().getConnectionId() + "] Connection from " + remoteAddress);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Player player = ctx.channel().attr(PLAYER_KEY).get();
		
		if (player != null) {
			this.serverHandler.getSessionManager().removeSession(ctx.channel());
			
			if (Util.getConfiguration().get("Logging", "log.connections", Boolean.class)) {
				String remoteAddress = ctx.channel().remoteAddress().toString().replace("/", "").split(":")[0];
				logger.info("[" + player.getNetwork().getConnectionId() + "] Disconnection from " + remoteAddress);
			}
			
			player.dispose();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			if (!(msg instanceof NettyRequest request)) {
				return;
			}

			Player player = ctx.channel().attr(PLAYER_KEY).get();

			if (Util.getConfiguration().get("Logging", "log.packets", Boolean.class)) {
				if ((request.getHeader().equals("LOGIN") || request.getHeader().equals("INFORETRIEVE")) && request.getArgumentAmount() > 1) {
					// Don't log password out of respect to the user
					logger.debug("[" + player.getNetwork().getConnectionId() + "] Received: " + request.getHeader() + " " + request.getArgument(0));
				} else if (request.getHeader().equals("UPDATE")) {
					// Don't log password out of respect to the user
					logger.debug("[" + player.getNetwork().getConnectionId() + "] Received: " + request.getHeader());
				} else {
					logger.debug("[" + player.getNetwork().getConnectionId() + "] Received: " + request.getHeader() + " " + request.getMessageBody());
				}
			}

			if (player != null) {
				Roseau.getServer().getMessageHandler().handleRequest(player, request);
			}

		} catch (Exception ex) {
			logger.error("Error handling message", ex);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("Exception in channel handler", cause);
		ctx.channel().close();
	}
}
