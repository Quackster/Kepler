package net.h4bbo.kepler.game.item.interactors;

import net.h4bbo.kepler.game.games.triggers.BattleShipsTrigger;
import net.h4bbo.kepler.game.games.triggers.ChessTrigger;
import net.h4bbo.kepler.game.games.triggers.PokerTrigger;
import net.h4bbo.kepler.game.games.triggers.TicTacToeTrigger;
import net.h4bbo.kepler.game.item.interactors.types.*;
import net.h4bbo.kepler.game.item.interactors.types.wobblesquabble.WobbleSquabbleJoinQueue;
import net.h4bbo.kepler.game.item.interactors.types.wobblesquabble.WobbleSquabbleQueueTile;
import net.h4bbo.kepler.game.item.interactors.types.wobblesquabble.WobbleSquabbleTileStart;
import net.h4bbo.kepler.game.triggers.GenericTrigger;

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

    PET_FOOD(new PetFoodInteractor()),
    PET_WATER_BOWL(new PetWaterBowlInteractor()),
    PET_TOY(new PetToyInteractor()),
    TOTEM_LEG(new DefaultInteractor()),
    TOTEM_HEAD(new DefaultInteractor()),
    TOTEM_PLANET(new DefaultInteractor()),

    POOL_ENTER(new PoolLadderInteractor()),
    POOL_EXIT(new PoolLadderInteractor()),

    POOL_BOOTH(new PoolBoothInteractor()),
    POOL_LIFT(new PoolLiftInteractor()),
    QUEUE_TILE(new QueueTileInteractor()),
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
