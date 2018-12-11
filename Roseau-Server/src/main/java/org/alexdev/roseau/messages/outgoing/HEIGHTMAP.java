package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class HEIGHTMAP extends OutgoingMessageComposer {

	private String heightMap;

	public HEIGHTMAP(String heightMap) {
		this.heightMap = heightMap;
	}

	@Override
	public void write() {
		response.init("HEIGHTMAP");
		response.appendNewArgument(this.heightMap);
	}

}
