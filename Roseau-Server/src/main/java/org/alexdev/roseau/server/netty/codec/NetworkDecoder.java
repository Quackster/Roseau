package org.alexdev.roseau.server.netty.codec;

import org.oldskooler.simplelogger4j.SimpleLog;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.server.IServerHandler;
import org.alexdev.roseau.server.netty.readers.NettyRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class NetworkDecoder extends ByteToMessageDecoder {
    private static final SimpleLog logger = SimpleLog.of(NetworkDecoder.class);
	private final IServerHandler serverHandler;
	
	public NetworkDecoder(IServerHandler serverHandler) {
		this.serverHandler = serverHandler;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		try {
			if (in.readableBytes() < 4) {
				ctx.channel().close();
				return;
			}

			Player player = ctx.channel().attr(io.netty.util.AttributeKey.<Player>valueOf("player")).get();

			if (player == null) {
				return;
			}

			int readerIndex = in.readerIndex();
			byte[] messageLengthBytes = new byte[4];
			in.readBytes(messageLengthBytes);
			
			int length = 0;
			try {
				length = Integer.parseInt(new String(messageLengthBytes).trim());
			} catch (NumberFormatException nfe) {
				in.readerIndex(readerIndex);
				return;
			}

			if (in.readableBytes() < length) {
				in.readerIndex(readerIndex);
				return;
			}

			byte[] message = new byte[length];
			in.readBytes(message);

			String content = new String(message, StandardCharsets.ISO_8859_1);

			String header;
			String request;

			if (content.contains(" ")) {
				header = content.split(" ", 2)[0];
				request = content.substring(header.length() + 1);
			} else {
				header = content;
				request = "";
			}

			out.add(new NettyRequest(header, request));

		} catch (Exception e) {
			logger.error("Error decoding message", e);
			ctx.channel().close();
		}
	}
}
