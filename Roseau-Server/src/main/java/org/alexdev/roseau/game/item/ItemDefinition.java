package org.alexdev.roseau.game.item;

public class ItemDefinition {

	private int id;
	private String sprite;
	private String color;
	private int length;
	private int width;
	private double height;
	private String stringBehaviour;
	private ItemBehaviour behaviour;
	private String name;
	private String description;
	private String dataClass;
	
	public ItemDefinition(int id, String sprite, String color, int length, int width, double height, String behaviour, String name, String description, String dataClass) {
		this.id = id;
		this.sprite = sprite;
		this.color = color;
		this.length = length;
		this.width = width;
		this.height = height;
		
		this.stringBehaviour = behaviour;
		this.name = name;
		this.description = description;
		this.dataClass = dataClass;
		this.behaviour = ItemBehaviour.parse(this.stringBehaviour);
	}

	public int getID() {
		return id;
	}
	
	public String getSprite() {
		return sprite;
	}
	
	public String getColor() {
		return color;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public ItemBehaviour getBehaviour() {
		return behaviour;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public String getDataClass() {
		return dataClass;
	}

	
}
