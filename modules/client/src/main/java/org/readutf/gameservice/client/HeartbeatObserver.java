package org.readutf.gameservice.client;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;

import org.readutf.gameservice.proto.DiscoveryServiceOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatObserver implements StreamObserver<DiscoveryServiceOuterClass.HeartbeatResponse> {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatObserver.class);

    private final CountDownLatch countDownLatch;

    public HeartbeatObserver(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onNext(DiscoveryServiceOuterClass.HeartbeatResponse value) {
    }

    @Override
    public void onError(Throwable t) {
        // Log the error or handle it as needed
        log.error("Heartbeat error", t);
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {
        countDownLatch.countDown();
    }
}
