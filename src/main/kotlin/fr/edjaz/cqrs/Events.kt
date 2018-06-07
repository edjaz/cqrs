package fr.edjaz.cqrs

interface DomainEvent

data class MessageQuacked(val content: String) : DomainEvent

class MessageDeleted() : DomainEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}