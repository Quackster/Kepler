package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.game.commandqueue.CommandTemplate;

public interface Command {
    void executeCommand(CommandTemplate commandArgs);

}
