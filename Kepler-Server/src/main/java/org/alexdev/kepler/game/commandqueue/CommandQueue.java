package org.alexdev.kepler.game.commandqueue;

public class CommandQueue {
    private String command;
    private String arguments;

    public CommandQueue(String command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }


    /**
     * Get the command
     *
     * @return returns command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Get the arguments
     *
     * @return returns arguments
     */
    public String getArguments() {
        return this.arguments;
    }


}
