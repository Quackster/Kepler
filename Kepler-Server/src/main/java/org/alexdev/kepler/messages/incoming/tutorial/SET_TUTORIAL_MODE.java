package org.alexdev.kepler.messages.incoming.tutorial;

import org.alexdev.kepler.dao.mysql.TutorialDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SET_TUTORIAL_MODE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int tutorialMode = reader.readInt();

        if (tutorialMode != 0 && tutorialMode != 1) {
            tutorialMode = 0;
        }

        boolean finishedTutorial = (tutorialMode == 0);

        player.getDetails().setTutorialFinished(finishedTutorial);
        TutorialDao.updateTutorialMode(player.getDetails().getId(), finishedTutorial);
    }
}
