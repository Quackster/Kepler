package org.alexdev.kepler.game.commands;

import java.util.ArrayList;
import java.util.List;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.moderation.Fuseright;

public abstract class Command {
   
    protected List<Fuseright> permissions;
    protected List<String> arguments;
    
    public Command() {
        this.permissions = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.addPermissions();
        this.addArguments();
    }

    /**
     * Adds the permissions.
     */
    public abstract void addPermissions();
    
    /**
     * Adds the argument names, must be overridden
     */
    public void addArguments() { };
    
    /**
     * Handle command.
     *
     * @param entity the entity
     * @param message the message
     */
    public abstract void handleCommand(Entity entity, String message, String[] args);
    
    /**
     * Gets the description.
     *
     * @return the description
     */
    public abstract String getDescription();

    /**
     * Gets the permissions.
     *
     * @return the permissions
     */
    public List<Fuseright> getPermissions() {
        return this.permissions;
    }

    /**
     * Gets the arguments.
     *
     * @return the arguments
     */
    public String[] getArguments() {
        return this.arguments.parallelStream().toArray(String[]::new);
    }
}
