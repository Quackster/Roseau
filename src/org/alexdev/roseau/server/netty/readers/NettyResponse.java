package org.alexdev.roseau.server.netty.readers;

import java.io.IOException;
import java.nio.charset.Charset;

import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.encoding.Base64Encoding;
import org.alexdev.roseau.server.encoding.WireEncoding;
import org.alexdev.roseau.server.messages.Response;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

public class NettyResponse implements Response
{

	private int id;
	private boolean finalised;
	private ChannelBufferOutputStream bodystream;
	private ChannelBuffer body;

	public NettyResponse() {
		this.id = -1;
		this.finalised = false;
		this.body = ChannelBuffers.dynamicBuffer();
		this.bodystream = new ChannelBufferOutputStream(body);
	}
	
	public NettyResponse(int id) {
		this.init(id);
	}

	@Override
	public void init(int id) {

		this.id = id;
		this.finalised = false;
		this.body = ChannelBuffers.dynamicBuffer();
		this.bodystream = new ChannelBufferOutputStream(body);
		
		try {	
			byte[] header = Base64Encoding.EncodeInt32(id, 2);
			this.bodystream.write(header, 0, header.length);

		} catch (Exception e) {
			Log.exception(e);
		}
	}

	@Override
	public void appendString(Object obj) {

		if (obj == null) {
			obj = "";
		}
		
		byte[] str = obj.toString().getBytes();
		
		try {
			bodystream.write(str, 0, str.length);
			bodystream.write((char)2);
		} catch (IOException e) {
			Log.exception(e);
		}
	}

	@Override
	public void appendInt32(Integer obj) {
		try {
			this.bodystream.write(WireEncoding.EncodeInt32(obj), 0, 2);
		} catch (IOException e) {
			Log.exception(e);
		}
	}

	@Override
	public void appendInt32(Boolean obj) {
		try {	
			this.bodystream.write(WireEncoding.EncodeInt32(obj ? 1 : 0), 0, 2);
			
		} catch (IOException e) {
			Log.exception(e);
		}
	}

	@Override
	public void appendBoolean(Boolean obj) {
		try {
			bodystream.writeByte(obj ? WireEncoding.POSITIVE : WireEncoding.NEGATIVE);
		} catch (IOException e) {
			Log.exception(e);
		}
	}

	public String getBodyString() {
		
		String str = new String(this.get().toString(Charset.defaultCharset()));
		
		for (int i = 0; i < 14; i++) { 
			str = str.replace(Character.toString((char)i), "[" + i + "]");
		}

		return str;
	}
	
	public ChannelBuffer get() {

		if (!this.finalised) {
			try {
				this.bodystream.write((char)1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.finalised = true;
		}
		
		return this.body;
	}

	public int getHeader() {
		return this.id;
	}	
}
