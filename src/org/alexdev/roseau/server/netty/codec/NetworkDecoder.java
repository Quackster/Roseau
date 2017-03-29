/*
 * Copyright (c) 2012 Quackster <alex.daniel.97@gmail>. 
 * 
 * This file is part of Sierra.
 * 
 * Sierra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Sierra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sierra.  If not, see <http ://www.gnu.org/licenses/>.
 */

package org.alexdev.roseau.server.netty.codec;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.server.encoding.Base64Encoding;
import org.alexdev.roseau.server.netty.readers.NettyRequest;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class NetworkDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {

		try  {		



			if (buffer.readableBytes() < 5) { // 3 letter long B64 length + 2 letter long B64 header
				channel.close();
				return null;
			}	

			Player player = (Player) ctx.getChannel().getAttachment();

			if (player == null) {
				return null;
			}

			/*if (player.getNetwork().isEncrypted()) {

				Log.println("Encryption: " + new String(buffer.array()));
				return null;

			} else {*/
			byte[] length = buffer.readBytes(3).array();
			byte[] header = buffer.readBytes(2).array();

			int decodedLength = Base64Encoding.DecodeInt32(length);
			int decodedHeader = Base64Encoding.DecodeInt32(header);

			ChannelBuffer messageBuffer = buffer.readBytes(decodedLength - 2);
			return new NettyRequest(decodedHeader, messageBuffer);


			/*if (length[0] == 60) {

				buffer.discardReadBytes();

				channel.write("<?xml version=\"1.0\"?>\r\n"
					+ "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\r\n"
					+ "<cross-domain-policy>\r\n"
					+ "<allow-access-from domain=\"*\" to-ports=\"*\" />\r\n"
					+ "</cross-domain-policy>\0");
			} else {

				int messageLength = ByteBuffer.wrap(length).asIntBuffer().get();
				ChannelBuffer messageBuffer = buffer.readBytes(messageLength);
				Short header = messageBuffer.readShort();
				return new NettyRequest(header, messageBuffer);
			}*/
			//}

		} catch (Exception e){

		}


		return null;
	}
}