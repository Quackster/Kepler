package org.alexdev.kepler.game.guides;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.tutorial.GUIDE_FOUND;
import org.alexdev.kepler.messages.outgoing.tutorial.INVITING_COMPLETED;
import org.alexdev.kepler.util.DateUtil;

import java.util.List;

public class GuideInviteTask implements Runnable {
    @Override
    public void run() {
        List<Player> waitingForGuides = GuideManager.getInstance().getAvaliableBeginners();

        for (Player beginner : waitingForGuides) {
            beginner.getGuideManager().getInvited().removeIf(userId -> PlayerManager.getInstance().getPlayerById(userId) == null);

            for (Player guide : GuideManager.getInstance().getGuidesAvailable()) {
                if (!guide.getGuideManager().isWaitingForInvitations()) {
                    continue;
                }

                if (guide.getGuideManager().hasInvite(beginner.getDetails().getId())) {
                    continue;
                }

                if (beginner.getGuideManager().hasInvited(guide.getDetails().getId())) {
                    continue;
                }

                beginner.send(new GUIDE_FOUND(guide.getDetails().getId()));

                guide.getGuideManager().addInvite(beginner.getDetails().getId(), beginner.getDetails().getName());
                beginner.getGuideManager().addInvited(guide.getDetails().getId());
            }

            if (beginner.getGuideManager().getInvited().isEmpty()) {
                if (DateUtil.getCurrentTimeSeconds() > beginner.getGuideManager().getStartedForWaitingGuidesTime()) {
                    beginner.getGuideManager().setWaitingForGuide(false);
                    beginner.getGuideManager().setStartedForWaitingGuidesTime(0);
                    beginner.send(new INVITING_COMPLETED(INVITING_COMPLETED.InvitationResult.FAILURE));
                }
            }
        }
    }
}
