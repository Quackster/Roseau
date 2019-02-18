package org.alexdev.roseau.server.netty.readers;

import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class NettyResponse implements Response {

	private String header;
	private boolean finalised;
	private StringBuilder buffer;

	public NettyResponse() { }

	@Override
	public void init(String header) {
		this.finalised = false;
		this.header = header;
		this.buffer = new StringBuilder();

		this.buffer.append('#');
		this.append(header);
	}

	@Override
	public void append(Object s) {
		String data = String.valueOf(s);
		data = data.replace('#', '*');
		this.buffer.append(data);
	}

	@Override
	public void appendArgument(Object arg) {
		appendArgument(arg.toString(), ' ');
	}

	@Override
	public void appendNewArgument(Object arg) {
		appendArgument(arg, (char)13);
	}

	@Override
	public void appendPartArgument(Object arg) {
		appendArgument(arg, '/');
	}

	@Override
	public void appendTabArgument(Object arg) {
		appendArgument(arg, (char)9);
	}

	@Override
	public void appendKVArgument(Object key, Object value) {
		this.append((char)13);
		this.append(key);
		this.append('=');
		this.append(value);
	}

	@Override
	public void appendKV2Argument(Object key, Object value) {
		this.append((char)13);
		this.append(key);
		this.append(':');
		this.append(value);
	}

	@Override
	public void appendArgument(Object arg, char delimiter) {
		this.append(delimiter);
		this.append(arg);	
	}


	@Override
	public void appendObject(SerializableObject obj) {
		obj.serialise(this);
	}

	@Override
	public String getBodyString() {
		String str = this.get();
		for (int i = 0; i < 14; i++) { 
			str = str.replace(Character.toString((char)i), "[" + i + "]");
		}
		return str;
	}

	@Override
	//# @type \r data\r##
	public String get() {

		if (!this.finalised) {
			this.buffer.append('#');
			this.buffer.append('#');
			this.finalised = true;
		}

		return this.buffer.toString();
	}

	public String getHeader() {
		return header;
	}

	public boolean isFinalised() {
		return finalised;
	}


	public void setFinalised(boolean finalised) {
		this.finalised = finalised;
	}	
}
