package fr.edjaz.cqrs

import java.util.stream.Stream


interface EventsStream {
    fun add(evt: DomainEvent)
    fun getEvents(): Stream<DomainEvent>
}


class MemoryEventStream : EventsStream {
    private var history: List<DomainEvent> = ArrayList()

    override fun add(evt: DomainEvent) {
        history += evt
    }

    override fun getEvents() = history.stream()
}







