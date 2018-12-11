package org.alexdev.roseau.game.item;

/**
 * A set of booleans representing the behaviour of a item definition.
 * 
 * @author Nillus
 */
public class ItemBehaviour
{
	/**
	 * True if this item is 'stuff'. (a 'touchable' substantial non-flat object like a table or a chair)
	 */
	private boolean STUFF;
	/**
	 * True if this item is 'item'. (a flat non-STUFF item, such as a poster, post.it, photo etc)
	 */
	private boolean ITEM;
	
	/**
	 * True if this item can be placed on the floor of a space.
	 */
	private boolean onFloor;
	/**
	 * True if this item can be placed on the wall of a space.
	 */
	private boolean onWall;
	/**
	 * True if this item is a passive object (OBJECT), such as wall parts etc.
	 */
	private boolean isPassiveObject;
	/**
	 * True if this item is invisible to clients.
	 */
	private boolean isInvisible;
	/**
	 * True if this item triggers some action when a SpaceUser 'steps' on it.
	 */
	private boolean isTrigger;
	
	/**
	 * True if this item can be placed on the floor and allows space users to sit on it.
	 */
	private boolean canSitOnTop;
	/**
	 * True if this item can be placed on the floor and allows room users to lay on it. (beds etc)
	 */
	private boolean canLayOnTop;
	/**
	 * True if this item can be placed on the floor and allows room users to stand on top of it.
	 */
	private boolean canStandOnTop;
	/**
	 * True if other items can be stacked on top of this item.
	 */
	private boolean canStackOnTop;
	
	/**
	 * True if this item requires a room user to have room rights to interact with the item.
	 */
	private boolean requiresRightsForInteraction;
	/**
	 * True if this item can be placed on the floor and requires a room user to stand one tile removed from the item to interact with the item.
	 */
	private boolean requiresTouchingForInteraction;
	
	/**
	 * True if this item can be used to decorate the walls/floor of a user flat.
	 */
	private boolean isDecoration;
	/**
	 * True if this item is a wall item and behaves as a post.it item. ('sticky')
	 */
	private boolean isPostIt;
	/**
	 * True if this item is a photo taken by camera item and who's image is saved in database.
	 */
	private boolean isPhoto;
	/**
	 * True if this item can be placed on the floor and can be opened/closed. Open items allow room users to walk through them, closed items do not.
	 */
	private boolean isDoor;
	/**
	 * True if this item can be placed on the floor and can teleport room users to other teleporter items. BEAM ME UP SCOTTY FFS!!11oneone
	 */
	private boolean isTeleporter;
	/**
	 * True if this item can be placed on the floor and can be opened and closed, and 'rolled' to a random number.
	 */
	private boolean isDice;
	/**
	 * True if this item can be placed on the floor and can hold a user-given message as 'inscription'.
	 */
	private boolean isPrizeTrophy;
	
	/**
	 * Returns a String representing the flags that are true in this ItemBehaviour.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(3);

		if (this.STUFF) {
			sb.append('S');
		}
		if (this.ITEM) {
			sb.append('I');
		}
		if (this.onFloor) {
			sb.append('F');
		}
		if (this.onWall) {
			sb.append('W');
		}
		if (this.canSitOnTop) {
			sb.append('C');
		}
		if (this.canLayOnTop) {
			sb.append('B');
		}
		if (this.canStandOnTop) {
			sb.append('K');
		}
		if (this.isPassiveObject) {
			sb.append('P');
		}
		if (this.isInvisible) {
			sb.append('E');
		}
		if (this.isTrigger) {
			sb.append('M');
		}

		if (this.requiresRightsForInteraction) {
			sb.append('G');
		}
		if (this.requiresTouchingForInteraction) {
			sb.append('T');
		}

		if (this.canStackOnTop) {
			sb.append('H');
		}
		if (this.isDecoration) {
			sb.append('V');
		}
		if (this.isPostIt) {
			sb.append('J');
		}
		if (this.isPhoto) {
			sb.append('N');
		}
		if (this.isDoor) {
			sb.append('D');
		}
		if (this.isTeleporter) {
			sb.append('X');
		}
		if (this.isDice) {
			sb.append('L');
		}
		if (this.isPrizeTrophy) {
			sb.append('Y');
		}

		return sb.toString();
	}
	
	/**
	 * Parses a String of flags to a ItemBehaviour object.
	 * 
	 * @param s The String of flags to parse.
	 * @return The ItemBehaviour with the flags flipped to their real states etc.
	 */
	public static ItemBehaviour parse(String s)
	{
		// Create empty behaviour
		ItemBehaviour behaviour = new ItemBehaviour();
		
		// Process all flags
		for (char flag : s.toCharArray())
		{
			// Determine flag
			switch (flag)
			{
				case 'S':
					behaviour.STUFF = true;
					break;
				
				case 'I':
					behaviour.ITEM = true;
					break;
				
				case 'F':
					behaviour.onFloor = true;
					break;
				
				case 'W':
					behaviour.onWall = true;
					break;
				
				case 'P':
					behaviour.isPassiveObject = true;
					break;
				
				case 'E':
					behaviour.isInvisible = true;
					break;
					
				case 'M':
					behaviour.isTrigger = true;
					break;
					
				case 'C':
					behaviour.canSitOnTop = true;
					break;
				
				case 'B':
					behaviour.canLayOnTop = true;
					break;
				
				case 'K':
					behaviour.canStandOnTop = true;
					break;
				
				case 'G':
					behaviour.requiresRightsForInteraction = true;
					break;
				
				case 'T':
					behaviour.requiresTouchingForInteraction = true;
					break;
				
				case 'H':
					behaviour.canStackOnTop = true;
					break;
				
				case 'V':
					behaviour.isDecoration = true;
					break;
				
				case 'J':
					behaviour.isPostIt = true;
					break;
				
				case 'N':
					behaviour.isPhoto = true;
					break;
				
				case 'D':
					behaviour.isDoor = true;
					break;
				
				case 'X':
					behaviour.isTeleporter = true;
					break;
				
				case 'L':
					behaviour.isDice = true;
					break;
				
				case 'Y':
					behaviour.isPrizeTrophy = true;
					break;
			}
		}
		
		// Return the parsed behaviour
		return behaviour;
	}

	public boolean isSTUFF() {
		return STUFF;
	}

	public boolean isITEM() {
		return ITEM;
	}

	public boolean isOnFloor() {
		return onFloor;
	}

	public boolean isOnWall() {
		return onWall;
	}

	public boolean isPassiveObject() {
		return isPassiveObject;
	}

	public boolean isInvisible() {
		return isInvisible;
	}

	public boolean isTrigger() {
		return isTrigger;
	}

	public boolean isCanSitOnTop() {
		return canSitOnTop;
	}

	public boolean isCanLayOnTop() {
		return canLayOnTop;
	}

	public boolean isCanStandOnTop() {
		return canStandOnTop;
	}

	public boolean isCanStackOnTop() {
		return canStackOnTop;
	}

	public boolean getRequiresRightsForInteraction() {
		return requiresRightsForInteraction;
	}

	public boolean getRequiresTouchingForInteraction() {
		return requiresTouchingForInteraction;
	}

	public boolean isDecoration() {
		return isDecoration;
	}

	public boolean isPostIt() {
		return isPostIt;
	}

	public boolean isPhoto() {
		return isPhoto;
	}

	public boolean isDoor() {
		return isDoor;
	}

	public boolean isTeleporter() {
		return isTeleporter;
	}

	public boolean isDice() {
		return isDice;
	}

	public boolean isPrizeTrophy() {
		return isPrizeTrophy;
	}
}