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



			if (buffer.readableBytes() < 4) { // 3 letter long B64 length + 2 letter long B64 header
				channel.close();
				return null;
			}	

			Player player = (Player) ctx.getChannel().getAttachment();

			if (player == null) {
				return null;
			}
			
			byte[] message_length = buffer.readBytes(4).array();
			byte[] message = buffer.readBytes(Integer.parseInt(new String(message_length).replaceAll("[^0-9]",""))).array();
			
			String content = new String(message);
			
			String request = null;
			String header = null;
			
			if (content.contains(" ")) {
				header = content.split(" ")[0];
				request = content.substring(header.length() + 1);
			} else {
				header = content;
				request = "";
			}
			
			return new NettyRequest(header, request);

		} catch (Exception e){

		}


		return null;
	}
}