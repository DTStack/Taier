package com.dtstack.learning.webapp;

import com.dtstack.learning.api.ApplicationContext;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class App {
  final ApplicationContext context;

  @Inject
  App(ApplicationContext context) {
    this.context = context;
  }
}
