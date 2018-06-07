package fr.edjaz.cqrs

class QuackCounter() {
    var value: Int = 0
        private set

    fun handle(evt: MessageQuacked) {
        value++
    }


    fun handle(evt: MessageDeleted) {
        value--
    }

}

data class TimeLineMessage(private var content: String) {

}

class TimeLine() : IEventSubscriber<MessageQuacked> {
    var messages: List<TimeLineMessage> = ArrayList()
        private set

    override fun handle(evt: MessageQuacked) {
        messages += TimeLineMessage(evt.content)
    }
}

class Message(publisher: IEventsPublisher) {
    private var projection: DecisionProjection

    private class DecisionProjection(publisher: EventsBus) {
        var deleted: Boolean = false
            private set

        fun apply(evt: MessageDeleted) {
            deleted = true
        }

        init {
            publisher.stream.getEvents().forEach { if (it is MessageDeleted) apply(it) }
        }

    }

    fun delete(publisher: IEventsPublisher) {
        if (projection.deleted) return
        publishAndApply(publisher, MessageDeleted())
    }

    private fun publishAndApply(publisher: IEventsPublisher, messageDeleted: MessageDeleted) {
        publisher.publish(messageDeleted)
        projection.apply(messageDeleted)
    }

    companion object {
        fun quake(publisher: IEventsPublisher, content: String) {
            publisher.publish(MessageQuacked(content))
        }
    }

    init {
        projection = DecisionProjection(publisher as EventsBus)
    }
}