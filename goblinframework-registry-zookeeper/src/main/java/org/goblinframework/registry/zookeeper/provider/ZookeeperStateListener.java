package org.goblinframework.registry.zookeeper.provider;

import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher;
import org.goblinframework.api.registry.RegistryState;
import org.goblinframework.api.registry.RegistryStateListener;
import org.goblinframework.core.conversion.ConversionService;
import org.jetbrains.annotations.NotNull;

final public class ZookeeperStateListener implements IZkStateListener {

  private final RegistryStateListener listener;

  ZookeeperStateListener(@NotNull RegistryStateListener listener) {
    this.listener = listener;
  }

  @Override
  public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
    if (state == null) {
      return;
    }
    RegistryState rs = ConversionService.INSTANCE.convert(state, RegistryState.class);
    if (rs == null) {
      return;
    }
    listener.onStateChanged(rs);
  }

  @Override
  public void handleNewSession() {
  }

  @Override
  public void handleSessionEstablishmentError(Throwable error) {
  }
}
