package org.alexdev.kepler.game.item.interactors;

import org.alexdev.kepler.game.games.triggers.BattleShipsTrigger;
import org.alexdev.kepler.game.games.triggers.ChessTrigger;
import org.alexdev.kepler.game.games.triggers.PokerTrigger;
import org.alexdev.kepler.game.games.triggers.TicTacToeTrigger;
import org.alexdev.kepler.game.item.interactors.types.*;
import org.alexdev.kepler.game.item.interactors.types.wobblesquabble.WobbleSquabbleJoinQueue;
import org.alexdev.kepler.game.item.interactors.types.wobblesquabble.WobbleSquabbleQueueTile;
import org.alexdev.kepler.game.item.interactors.types.wobblesquabble.WobbleSquabbleTileStart;
import org.alexdev.kepler.game.triggers.GenericTrigger;

public enum InteractionType {
    DEFAULT(new DefaultInteractor()),
    BED(new BedInteractor()),
    CHAIR(new ChairInteractor()),
    TELEPORT(new DefaultInteractor()),
    VENDING_MACHINE(new DefaultInteractor()),
    LERT(new DefaultInteractor()),
    SCOREBOARD(new DefaultInteractor()),
    FORTUNE(new DefaultInteractor()),
    PET_NEST(new PetNestInteractor()),

    TOTEM_LEG(new DefaultInteractor()),
    TOTEM_HEAD(new DefaultInteractor()),
    TOTEM_PLANET(new DefaultInteractor()),

    POOL_ENTER(new PoolLadderInteractor()),
    POOL_EXIT(new PoolLadderInteractor()),

    POOL_BOOTH(new PoolBoothInteractor()),
    POOL_LIFT(new PoolLiftInteractor()),
    POOL_QUEUE(new PoolQueueInteractor()),
    GAME_TIC_TAC_TOE(new TicTacToeTrigger()),
    GAME_CHESS(new ChessTrigger()),
    GAME_BATTLESHIPS(new BattleShipsTrigger()),
    GAME_POKER(new PokerTrigger()),

    WS_JOIN_QUEUE(new WobbleSquabbleJoinQueue()),
    WS_QUEUE_TILE(new WobbleSquabbleQueueTile()),
    WS_TILE_START(new WobbleSquabbleTileStart());

    private final GenericTrigger genericTrigger;

    InteractionType(GenericTrigger genericTrigger) {
        this.genericTrigger = genericTrigger;
    }

    public GenericTrigger getTrigger() {
        return genericTrigger;
    }
}
