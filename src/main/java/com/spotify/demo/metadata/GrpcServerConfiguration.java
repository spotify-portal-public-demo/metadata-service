package com.spotify.demo.metadata;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.protobuf.services.HealthStatusManager;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(GrpcServerProperties.class)
class GrpcServerConfiguration {

  @Bean
  GrpcServerLifecycle grpcServerLifecycle(GrpcServerProperties properties,
      List<BindableService> services) {
    return new GrpcServerLifecycle(properties, services);
  }

}

@ConfigurationProperties(prefix = "grpc.server")
class GrpcServerProperties {

  private String host = "0.0.0.0";

  private int port = 9090;

  private Duration shutdownGracePeriod = Duration.ofSeconds(30);

  String determineHost() {
    if (!StringUtils.hasText(this.host) || "*".equals(this.host)) {
      return "0.0.0.0";
    }
    return this.host;
  }

  public String getHost() {
    return this.host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public Duration getShutdownGracePeriod() {
    return this.shutdownGracePeriod;
  }

  public void setShutdownGracePeriod(Duration shutdownGracePeriod) {
    this.shutdownGracePeriod = shutdownGracePeriod;
  }

}

final class GrpcServerLifecycle implements SmartLifecycle {

  private final GrpcServerProperties properties;

  private final List<BindableService> services;

  private final HealthStatusManager healthStatusManager = new HealthStatusManager();

  private volatile Server server;

  private volatile boolean running;

  GrpcServerLifecycle(GrpcServerProperties properties, List<BindableService> services) {
    this.properties = properties;
    this.services = services;
  }

  @Override
  public synchronized void start() {
    if (this.running) {
      return;
    }

    try {
      var builder = NettyServerBuilder.forAddress(
          new InetSocketAddress(this.properties.determineHost(), this.properties.getPort()));
      builder.addService(this.healthStatusManager.getHealthService());
      builder.addService(ProtoReflectionService.newInstance());

      for (var service : this.services) {
        builder.addService(service);
      }

      this.server = builder.build().start();
      this.healthStatusManager.setStatus("", ServingStatus.SERVING);
      for (var service : this.services) {
        this.healthStatusManager.setStatus(
            service.bindService().getServiceDescriptor().getName(),
            ServingStatus.SERVING);
      }
      this.running = true;
    }
    catch (IOException exception) {
      throw new IllegalStateException("Failed to start gRPC server", exception);
    }
  }

  @Override
  public synchronized void stop() {
    if (!this.running || this.server == null) {
      return;
    }

    this.healthStatusManager.enterTerminalState();
    this.server.shutdown();
    try {
      if (!this.server.awaitTermination(this.properties.getShutdownGracePeriod().toMillis(),
          java.util.concurrent.TimeUnit.MILLISECONDS)) {
        this.server.shutdownNow();
        this.server.awaitTermination(this.properties.getShutdownGracePeriod().toMillis(),
            java.util.concurrent.TimeUnit.MILLISECONDS);
      }
    }
    catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      this.server.shutdownNow();
    }
    finally {
      this.server = null;
      this.running = false;
    }
  }

  @Override
  public void stop(Runnable callback) {
    stop();
    callback.run();
  }

  @Override
  public boolean isRunning() {
    return this.running;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public int getPhase() {
    return Integer.MAX_VALUE;
  }

}
