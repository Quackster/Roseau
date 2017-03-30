package org.alexdev.roseau.server.netty.readers;

import java.io.IOException;
import java.nio.charset.Charset;

import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.server.encoding.Base64Encoding;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;

public class NettyResponse implements Response {

	private String header;
	private boolean finalised;
	private ChannelBufferOutputStream bodystream;
	private ChannelBuffer body = null;

	public NettyResponse() { }

	@Override
	public void init(String header) {

		this.finalised = false;
		this.body = ChannelBuffers.dynamicBuffer();
		this.bodystream = new ChannelBufferOutputStream(body);
		this.header = header;

		try {
			this.bodystream.write('#');
			this.bodystream.write(header.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void append(String s) {
		try {
			this.bodystream.write(s.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void appendArgument(String arg) {
		appendArgument(arg, ' ');
	}

	@Override
	public void appendNewArgument(String arg) {
		appendArgument(arg, (char)13);
	}

	@Override
	public void appendPartArgument(String arg) {
		appendArgument(arg, '/');
	}

	@Override
	public void appendTabArgument(String arg) {
		appendArgument(arg, (char)9);
	}

	@Override
	public void appendKVArgument(String key, String value) {
		try {
			this.bodystream.write((char)13);
			this.bodystream.write(key.getBytes());
			this.bodystream.write('=');
			this.bodystream.write(value.getBytes());	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void appendKV2Argument(String key, String value) {
		try {

			this.bodystream.write((char)13);
			this.bodystream.write(key.getBytes());
			this.bodystream.write(':');
			this.bodystream.write(value.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void appendArgument(String arg, char delimiter) {
		try {
			this.bodystream.write(delimiter);
			this.bodystream.write(arg.getBytes());	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void appendObject(SerializableObject obj) {
		obj.serialise(this);
	}

	@Override
	public String getBodyString() {
		String str = new String(this.get().toString(Charset.defaultCharset()));
		for (int i = 0; i < 14; i++) { 
			str = str.replace(Character.toString((char)i), "[" + i + "]");
		}
		return str;
	}

	@Override
	public ChannelBuffer get() {

		if (!this.finalised) {
			try {
				this.bodystream.write('#');
				this.bodystream.write('#');
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.finalised = true;
		}

		return this.body;
	}

	public String getHeader() {
		return header;
	}	
}
