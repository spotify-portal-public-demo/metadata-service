package com.spotify.demo.metadata;

import org.springframework.stereotype.Service;
import com.spotify.demo.metadata.proto.HelloReply;
import com.spotify.demo.metadata.proto.HelloRequest;
import com.spotify.demo.metadata.proto.SimpleGrpc;
import io.grpc.stub.StreamObserver;

@Service
class SimpleGrpcService extends SimpleGrpc.SimpleImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello: " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
