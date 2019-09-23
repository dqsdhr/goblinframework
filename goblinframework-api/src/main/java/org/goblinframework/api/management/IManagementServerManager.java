package org.goblinframework.api.management;

import org.goblinframework.api.common.Internal;
import org.goblinframework.api.common.Lifecycle;
import org.jetbrains.annotations.Nullable;

@Internal(uniqueInstance = true)
public interface IManagementServerManager extends Lifecycle {

  @Nullable
  static IManagementServerManager instance() {
    return ManagementServerManagerInstaller.INSTALLED;
  }
}
