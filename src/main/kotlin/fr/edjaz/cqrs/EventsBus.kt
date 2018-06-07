package fr.edjaz.cqrs

interface IEventsPublisher {
    fun <TEvent> publish(evt: TEvent) where TEvent : DomainEvent
}

class EventsBus(var stream: EventsStream) : IEventsPublisher {

    private var subscribers: List<IEventSubscriber<*>> = ArrayList()

    override fun <TEvent> publish(evt: TEvent) where TEvent : DomainEvent {
        stream.add(evt)
        subscribers.filter { it is IEventSubscriber<*> }.forEach { (it as IEventSubscriber<TEvent>).handle(evt) }
    }

    fun subsribe(evt: IEventSubscriber<*>) {
        subscribers += evt
    }

}


interface IEventSubscriber<TEvent> where TEvent : DomainEvent {
    fun handle(evt: TEvent)
}

class EventSubscriber<TEvent>() : IEventSubscriber<TEvent> where TEvent : DomainEvent {

    var called: Boolean = false
        private set

    override fun handle(evt: TEvent) {
        called = true
    }

}