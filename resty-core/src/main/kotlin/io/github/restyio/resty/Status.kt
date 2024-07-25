package io.github.restyio.resty

abstract class Status(private val code: Number) :
    Number(),
    Comparable<Number> {
    override fun toByte(): Byte {
        return this.code.toByte()
    }

    override fun toDouble(): Double {
        return this.code.toDouble()
    }

    override fun toLong(): Long {
        return this.code.toLong()
    }

    override fun toFloat(): Float {
        return this.code.toFloat()
    }

    override fun toInt(): Int {
        return this.code.toInt()
    }

    override fun toShort(): Short {
        return this.code.toShort()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Number) {
            return other.toInt() == this.toInt()
        }
        return false
    }

    override fun compareTo(other: Number): Int {
        return other.toInt().compareTo(this.toInt())
    }

    override fun hashCode(): Int {
        return this.code.hashCode()
    }

    object Continue : Status(100)

    object OK : Status(200)

    object MultipleChoices : Status(300)

    object BadRequest : Status(400)

    object TooManyRequests : Status(429)

    object InternalServerError : Status(500)
}
