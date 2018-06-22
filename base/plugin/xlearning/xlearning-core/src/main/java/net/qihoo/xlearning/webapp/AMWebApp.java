package net.qihoo.xlearning.webapp;


import net.qihoo.xlearning.webapp.AMParams;
import net.qihoo.xlearning.webapp.AppController;
import org.apache.hadoop.yarn.webapp.WebApp;

public class AMWebApp extends WebApp implements AMParams {

  @Override
  public void setup() {
    route("/", AppController.class);
    route("/savedmodel", AppController.class, "savedmodel");
  }
}
