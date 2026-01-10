package org.alexdev.roseau.server.netty;

import io.netty.channel.Channel;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;

public class NettyPlayerNetwork extends IPlayerNetwork {

	private final Channel channel;

	public NettyPlayerNetwork(Channel channel, int connectionId) {
		super(connectionId, ((java.net.InetSocketAddress) channel.localAddress()).getPort());
		this.channel = channel;
	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public void send(OutgoingMessageComposer response) {
		channel.writeAndFlush(response);
	}
}
