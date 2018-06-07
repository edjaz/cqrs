package fr.edjaz.cqrs

import org.assertj.core.api.Assertions
import org.junit.Test


class MixterShould {
    @Test
    fun display_message_in_timeline_when_quackmessage() {

        var eventsBus = EventsBus(MemoryEventStream())
        var timeline = TimeLine()
        eventsBus.subsribe(timeline)

        Message.quake(eventsBus, "Hello")

        Assertions.assertThat(timeline.messages).containsExactly(TimeLineMessage("Hello"))
    }
}


class EventBusShould {
    @Test
    fun storeevents_when_publish_event() {
        var history = MemoryEventStream()
        var eventsBus = EventsBus(history)

        eventsBus.publish(MessageQuacked("Hello"))
        Assertions.assertThat(history.getEvents()).contains(MessageQuacked("Hello"))
    }


    @Test
    fun call_each_handler_when_publish_event() {
        var history = MemoryEventStream()
        var eventsBus = EventsBus(history)


        val eventSubscriber1 = EventSubscriber<MessageQuacked>()
        eventsBus.subsribe(eventSubscriber1)
        val eventSubscriber2 = EventSubscriber<MessageQuacked>()
        eventsBus.subsribe(eventSubscriber2)
        val eventSubscriber3 = EventSubscriber<MessageDeleted>()
        eventsBus.subsribe(eventSubscriber3)

        eventsBus.publish(MessageQuacked("Hello"))

        Assertions.assertThat(eventSubscriber1.called).isTrue()
        Assertions.assertThat(eventSubscriber2.called).isTrue()
        Assertions.assertThat(eventSubscriber3.called).isTrue()
    }
}

class TimelineShould {
    @Test
    fun display_message_when_messagequaked() {
        var timeline: TimeLine = TimeLine()

        timeline.handle(MessageQuacked("Hello"))

        Assertions.assertThat(timeline.messages).contains(TimeLineMessage("Hello"))

    }
}


class QuackCounterShould {
    @Test
    fun increment_when_messagequacked() {
        var counter = QuackCounter()

        counter.handle(MessageQuacked("Hello"))

        Assertions.assertThat(counter.value).isEqualTo(1)
    }

    @Test
    fun decrement_when_messagequacked() {
        var counter = QuackCounter()

        counter.handle(MessageQuacked("Hello"))
        counter.handle(MessageDeleted())

        Assertions.assertThat(counter.value).isEqualTo(0)
    }
}


class MessageShould {
    private var stream: MemoryEventStream = MemoryEventStream();
    private var eventsBus = EventsBus(stream)


    @Test
    fun raise_message_quacked_when_quake_message() {

        Message.quake(eventsBus, "Hello")

        Assertions.assertThat(stream.getEvents()).contains(MessageQuacked("Hello"))
    }

    @Test
    fun raise_message_deleted_when_delete_message() {
        stream.add(MessageQuacked("Hello"))

        var message = Message(eventsBus)

        message.delete(eventsBus)

        Assertions.assertThat(stream.getEvents()).contains(MessageDeleted())
    }

    @Test
    fun not_raise_messagedeleted_when_delete_deletedmessage() {
        stream.add(MessageQuacked("Hello"))
        stream.add(MessageDeleted())

        var message = Message(eventsBus)
        message.delete(eventsBus)
        Assertions.assertThat(stream.getEvents().filter { any -> any is MessageDeleted }).hasSize(1);
    }


    @Test
    fun not_raise_messagedeleted_when_twice_deletemessage() {
        stream.add(MessageQuacked("Hello"))

        var message = Message(eventsBus)
        message.delete(eventsBus)
        message.delete(eventsBus)

        Assertions.assertThat(stream.getEvents().filter { any -> any is MessageDeleted }).hasSize(1);
    }

}


