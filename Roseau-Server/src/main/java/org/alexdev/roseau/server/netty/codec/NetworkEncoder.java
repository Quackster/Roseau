package org.alexdev.roseau.server.netty.codec;

import org.oldskooler.simplelogger4j.SimpleLog;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.util.Util;

import java.nio.charset.StandardCharsets;

public class NetworkEncoder extends MessageToByteEncoder<Object> {
    private static final SimpleLog logger = SimpleLog.of(NetworkEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
		try {
			if (msg instanceof String) {
				byte[] bytes = ((String) msg).getBytes(StandardCharsets.ISO_8859_1);
				out.writeBytes(bytes);
				return;
			}

			if (msg instanceof OutgoingMessageComposer) {
				OutgoingMessageComposer composer = (OutgoingMessageComposer) msg;
				if (!composer.getResponse().isFinalised()) {
					composer.write();
				}

				if (Util.getConfiguration().get("Logging", "log.packets", Boolean.class)) {
					logger.debug("SENT: " + composer.getResponse().getBodyString());
				}

				byte[] bytes = composer.getResponse().get().getBytes();
				out.writeBytes(bytes);
				return;
			}
		} catch (Exception ex) {
			logger.error("Error encoding message", ex);
		}
	}
}
