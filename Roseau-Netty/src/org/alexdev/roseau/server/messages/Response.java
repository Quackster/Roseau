package org.alexdev.roseau.server.messages;

public interface Response {

	public void init(int id);
	public void appendString(Object obj);
	public void appendInt32(Integer obj);
	public void appendInt32(Boolean obj);
	public void appendBoolean(Boolean obj);
	public String getBodyString();
	public Object get();

	public int getHeader();

}
