package omar.cucumber.ingest.test

import java.security.Permission

class SecurityManagerCheck extends SecurityManager {
  @Override public void checkExit(int status) {
    throw new SecurityException();
  }

  @Override public void checkPermission(Permission perm) {
      // Allow other activities by default
  }
}