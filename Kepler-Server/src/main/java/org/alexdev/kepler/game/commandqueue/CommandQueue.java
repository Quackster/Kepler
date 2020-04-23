package org.alexdev.kepler.game.commandqueue;

public class CommandQueue {
    private int id;
    private boolean executed;
    private String command;
    private String arguments;

    public CommandQueue(int id, boolean executed, String command, String arguments) {
        this.id = id;
        this.executed = executed;
        this.command = command;
        this.arguments = arguments;

    }

    /**
     * Get the id
     *
     * @return returns id
     */
    public int getId() {
        return this.id;
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
