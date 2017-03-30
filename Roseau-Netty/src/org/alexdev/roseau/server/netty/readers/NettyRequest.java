package org.alexdev.roseau.server.netty.readers;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.alexdev.roseau.server.encoding.*;
import org.alexdev.roseau.server.messages.ClientMessage;

public class NettyRequest implements ClientMessage {

	private int header;
	public ChannelBuffer buffer;
	
	public NettyRequest(int messageId, ChannelBuffer buffer) {
		super();
		
		this.header = messageId;
		this.buffer = (buffer == null || buffer.readableBytes() == 0) ? ChannelBuffers.EMPTY_BUFFER : buffer;
	}

	public Integer readInt() {
		try {
			
			int number = WireEncoding.DecodeInt32(this.readBytes(
					WireEncoding.MAX_INTEGER_BYTE_AMOUNT));
			
			return number;
		} catch (Exception e) {
			return -1;
		}
	}
	

	public boolean readIntAsBool() {
		try {
			return this.readInt() == 1;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean readBoolean()  {
		try {
			return buffer.readByte() == WireEncoding.POSITIVE;
		}
		catch (Exception e)	{
			return false;
		}
	}

	public String readString() {
		
		try {
			
			int length = Base64Encoding.DecodeInt32(this.readBytes(2));//this.readShort();
			byte[] data = this.buffer.readBytes(length).array();

			return new String(data);
			
		} catch (Exception e) {
			return null;
		}
	}
	

	public byte[] readBytes(int len) {
		
		try {
			
			return this.buffer.readBytes(len).array();
			
		} catch (Exception e) {
			return null;
		}
	}

	public String getMessageBody() {
		
		String consoleText = new String(buffer.toString(Charset.defaultCharset()));

		for (int i = 0; i < 13; i++) { 
			consoleText = consoleText.replace(Character.toString((char)i), "[" + i + "]");
		}

		return consoleText;
	}
	
	public ChannelBuffer getBuffer() {
		return buffer;
	}

	public int getMessageId() {
		return header;
	}
}
