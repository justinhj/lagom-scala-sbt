package org.justinhj.lagomhellostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.justinhj.lagomhellostream.api.LagomhelloStreamService
import org.justinhj.lagomhello.api.LagomhelloService

import scala.concurrent.Future

/**
  * Implementation of the LagomhelloStreamService.
  */
class LagomhelloStreamServiceImpl(lagomhelloService: LagomhelloService) extends LagomhelloStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagomhelloService.hello(_).invoke()))
  }
}
