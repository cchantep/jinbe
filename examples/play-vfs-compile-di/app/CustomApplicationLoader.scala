package io.github.cchantep.jinbe.demo

import play.api.ApplicationLoader.Context
import play.api.routing.Router
import play.api.{ Application, ApplicationLoader, LoggerConfigurator }
import play.filters.HttpFiltersComponents

import _root_.controllers.AssetsComponents
import router.Routes

import io.github.cchantep.jinbe.demo.controllers.JinbeController

import play.modules.jinbe.JinbeFromContext

class CustomApplicationLoader extends ApplicationLoader {

  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new CustomComponents(context).application
  }
}

class CustomComponents(context: Context)
    extends JinbeFromContext(context)
    with AssetsComponents
    with HttpFiltersComponents {

  implicit val ec = actorSystem.dispatcher

  lazy val applicationController =
    new JinbeController(controllerComponents, jinbe)

  lazy val router: Router =
    new Routes(httpErrorHandler, applicationController, assets)
}
