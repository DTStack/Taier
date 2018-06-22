package net.qihoo.xlearning.webapp;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import net.qihoo.xlearning.api.ApplicationContext;

@RequestScoped
public class App {
  final ApplicationContext context;

  @Inject
  App(ApplicationContext context) {
    this.context = context;
  }
}
