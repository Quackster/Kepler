package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class SHOWPROGRAM extends OutgoingMessageComposer {

	private String[] parameters;

	public SHOWPROGRAM(String[] parameters) {
		this.parameters = parameters;
	}

	@Override
	public void write() {
		response.init("SHOWPROGRAM");
			response.appendNewArgument(parameters[0]);

			if (parameters.length > 1) {
				for (int i = 1; i < parameters.length; i++) {	
					String parameter = parameters[i];
					response.appendArgument(parameter);

				}
		}
	}
}
