package org.alexdev.roseau.server.messages;

public interface Response {

	public void init(String header);
	public void append(Object s);
	public void appendArgument(Object arg);
	public void appendNewArgument(Object arg);
	public void appendPartArgument(Object arg);
	public void appendTabArgument(Object arg);
	public void appendKVArgument(Object key, Object value);
	public void appendKV2Argument(Object key, Object value);
	public void appendArgument(Object arg, char delimiter);
	public void appendObject(SerializableObject obj);
	public String getBodyString();
	public Object get();

	public String getHeader();

}
