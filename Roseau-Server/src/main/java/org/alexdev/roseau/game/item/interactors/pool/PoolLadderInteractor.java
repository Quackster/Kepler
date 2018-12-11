package org.alexdev.roseau.game.item.interactors.pool;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.pathfinder.Pathfinder;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;

public class PoolLadderInteractor extends Interaction {

	private boolean enterLadder;

	public PoolLadderInteractor(Item item, boolean enterLadder) {
		super(item);
		this.enterLadder = enterLadder;
	}

	@Override
	public void onTrigger(Player player) {
		
		if (this.enterLadder) {
			player.getRoomUser().setStatus("swim", "", true, -1);
			this.poolInteractor(player, "enter");
			return;
		}

		if (!this.enterLadder) {
			player.getRoomUser().removeStatus("swim");
			this.poolInteractor(player, "exit");
			return;
		}
	}
	
	public void poolInteractor(Player player, String program) {
		player.getRoomUser().setWalking(false);
		player.getRoomUser().setNext(null);
		player.getRoomUser().forceStopWalking();

		String[] positions = item.getCustomData().split(" ", 2);

		Position warp = new Position(positions[0]);
		Position goal = new Position(positions[1]);

		player.getRoomUser().getPosition().setX(warp.getX());
		player.getRoomUser().getPosition().setY(warp.getY());
		player.getRoomUser().getPosition().setZ(player.getRoomUser().getRoom().getMapping().getTile(warp.getX(), warp.getY()).getHeight());
		player.getRoomUser().setNeedUpdate(true);

		item.showProgram(program);

		player.getRoomUser().getGoal().setX(goal.getX());
		player.getRoomUser().getGoal().setY(goal.getY());
		player.getRoomUser().getGoal().setZ(player.getRoomUser().getRoom().getMapping().getTile(goal.getX(), goal.getY()).getHeight());
		player.getRoomUser().getPath().addAll(Pathfinder.makePath(player));

		player.getRoomUser().setWalking(true);
	}

	@Override
	public void onStoppedWalking(Player player) {
		// TODO Auto-generated method stub
		
	}

}
