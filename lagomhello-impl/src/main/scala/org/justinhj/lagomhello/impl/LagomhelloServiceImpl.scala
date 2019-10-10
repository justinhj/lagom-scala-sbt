package org.justinhj.lagomhello.impl

import org.justinhj.lagomhello.api
import org.justinhj.lagomhello.api.LagomhelloService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the LagomhelloService.
  */
class LagomhelloServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomhelloService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the lagomhello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomhelloEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the lagomhello entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomhelloEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }


  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(LagomhelloEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[LagomhelloEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}
