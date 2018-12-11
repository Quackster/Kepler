package org.alexdev.roseau.server.messages;

public interface Response {

	public void init(String header);
	public void append(Object s);
	public void appendArgument(String arg);
	public void appendNewArgument(String arg);
	public void appendPartArgument(String arg);
	public void appendTabArgument(String arg);
	public void appendKVArgument(String key, String value);
	public void appendKV2Argument(String key, String value);
	public void appendArgument(String arg, char delimiter);
	public void appendObject(SerializableObject obj);
	public String getBodyString();
	public Object get();

	public String getHeader();

}
