package org.alexdev.kepler.server.rcon;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import com.github.emansom.retrorcon.*;
import com.github.emansom.retrorcon.StarterRoomRequest.StarterRoomTheme;
import com.github.emansom.retrorcon.RconGrpc.RconImplBase;
import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;

public class RconImpl extends RconImplBase {

    /**
     * Check if emulator is still alive
     *
     * @param request Empty request
     * @param responseObserver Response
     */
    @Override
    public void ping(Empty request, StreamObserver<Response> responseObserver) {
        boolean online = !Kepler.isShuttingdown();
        Response reply = Response.newBuilder().setOk(online).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Get online users count
     *
     * @param request Empty request
     * @param responseObserver Response
     */
    @Override
    public void getOnlineCount(Empty request, StreamObserver<OnlineCountResponse> responseObserver) {
        int onlineCount = PlayerManager.getInstance().getPlayers().size();
        OnlineCountResponse reply = OnlineCountResponse.newBuilder().setOnlineCount(onlineCount).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Refresh a user's look and motto
     *
     * @param request RefreshAppearanceRequest
     * @param responseObserver Response
     */
    @Override
    public void isUserOnline(UserOnlineRequest request, StreamObserver<UserOnlineResponse> responseObserver) {
        Player user = null;
        String username = request.getUsername();
        int userId = request.getUserId();

        if (username.length() > 0) {
            user = PlayerManager.getInstance().getPlayerByName(username);
        } else if (userId > 0) {
            user = PlayerManager.getInstance().getPlayerById(userId);
        }

        UserOnlineResponse reply = UserOnlineResponse.newBuilder().setOnline(user != null).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Refresh a user's look and motto
     *
     * @param request RefreshAppearanceRequest
     * @param responseObserver Response
     */
    @Override
    public void refreshAppearance(RefreshAppearanceRequest request, StreamObserver<Response> responseObserver) {
        Player user = PlayerManager.getInstance().getPlayerById(request.getUserId());

        if (user != null) {
            user.getRoomUser().refreshAppearance();
        }

        Response reply = Response.newBuilder().setOk(true).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Create a starter room
     *
     * @param request StarterRoomRequest
     * @param responseObserver Response
     */
    @Override
    public void createStarterRoom(StarterRoomRequest request, StreamObserver<Response> responseObserver) {
        // TODO: implement
        // use switch on request.getTheme(), checking for e.g. StarterRoomTheme.ORANGE

        Response reply = Response.newBuilder().setOk(true).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
