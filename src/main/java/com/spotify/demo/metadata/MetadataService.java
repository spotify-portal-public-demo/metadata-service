package com.spotify.demo.metadata;

import org.springframework.stereotype.Service;
import com.spotify.demo.metadata.proto.GetAlbumsRequest;
import com.spotify.demo.metadata.proto.GetAlbumsResponse;
import com.spotify.demo.metadata.proto.GetArtistsRequest;
import com.spotify.demo.metadata.proto.GetArtistsResponse;
import com.spotify.demo.metadata.proto.GetTracksRequest;
import com.spotify.demo.metadata.proto.GetTracksResponse;
import com.spotify.demo.metadata.proto.MetadataServiceGrpc.MetadataServiceImplBase;
import io.grpc.stub.StreamObserver;

@Service
class MetadataService extends MetadataServiceImplBase {

  @Override
  public void getTracks(GetTracksRequest req, StreamObserver<GetTracksResponse> responseObserver) {
    var reply = GetTracksResponse.newBuilder().build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

  @Override
  public void getAlbums(GetAlbumsRequest req, StreamObserver<GetAlbumsResponse> responseObserver) {
    var reply = GetAlbumsResponse.newBuilder().build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

  @Override
  public void getArtists(GetArtistsRequest req,
      StreamObserver<GetArtistsResponse> responseObserver) {
    var reply = GetArtistsResponse.newBuilder().build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

}
