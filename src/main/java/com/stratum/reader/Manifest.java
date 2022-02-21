package com.stratum.reader;

import java.util.Map;

public class Manifest {

  String app;
  Map<String, String> info;
  Map<String, String> launch;

  public String getAppIndexFile() {
    String userHomeDir = System.getProperty("user.home");
    String appDir = String.format("%s/stratum/apps/%s/", userHomeDir, app);
    String fileName = launch.get("command").split(" ")[1];

    return appDir + fileName;
  }

  public String getWorkingDirectory() {
    String userHomeDir = System.getProperty("user.home");
    String appDir = String.format("%s/stratum/apps/%s/", userHomeDir, app);

    return appDir;
  }

  public String getFirstCommand() {
    return launch.get("command").split(" ")[0];
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public Map<String, String> getInfo() {
    return info;
  }

  public void setInfo(Map<String, String> info) {
    this.info = info;
  }

  public Map<String, String> getLaunch() {
    return launch;
  }

  public void setLaunch(Map<String, String> launch) {
    this.launch = launch;
  }
}
