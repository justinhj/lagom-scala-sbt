package org.justinhj.lagomhellostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import org.justinhj.lagomhellostream.api.LagomhelloStreamService
import org.justinhj.lagomhello.api.LagomhelloService
import com.softwaremill.macwire._

class LagomhelloStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomhelloStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomhelloStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[LagomhelloStreamService])
}

abstract class LagomhelloStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[LagomhelloStreamService](wire[LagomhelloStreamServiceImpl])

  // Bind the LagomhelloService client
  lazy val lagomhelloService: LagomhelloService = serviceClient.implement[LagomhelloService]
}
