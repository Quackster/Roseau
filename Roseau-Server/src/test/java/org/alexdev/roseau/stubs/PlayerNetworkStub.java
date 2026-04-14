package org.alexdev.roseau.stubs;

import com.google.common.collect.ImmutableList;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.server.IPlayerNetwork;

/**
 * A stub {@link IPlayerNetwork} that tracks all responses sent to it, as well as whether the network has been closed.
 */
public class PlayerNetworkStub extends IPlayerNetwork {

    private final ImmutableList.Builder<OutgoingMessageComposer> responses = ImmutableList.builder();
    private boolean closed = false;

    public PlayerNetworkStub() {
        this(/* connectionId= */ 0, /* serverPort= */ 1000);
    }

    public PlayerNetworkStub(int connectionId, int serverPort) {
        super(connectionId, serverPort);
    }

    @Override
    public void send(OutgoingMessageComposer response) {
        responses.add(response);
    }

    @Override
    public void close() {
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public ImmutableList<OutgoingMessageComposer> getResponses() {
        return responses.build();
    }
}
