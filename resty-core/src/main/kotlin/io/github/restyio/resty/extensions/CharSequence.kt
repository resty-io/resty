package io.github.restyio.resty.extensions

class CharSequenceEnvelope(private val source: () -> CharSequence) : CharSequence {
    private val value by lazy { this.source() }

    override val length: Int
        get() = this.value.length

    override fun get(index: Int): Char {
        return this.value.get(index)
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return this.value.subSequence(startIndex, endIndex)
    }
}

fun CharSequence.buildString(): String {
    val seq = this
    return buildString(this.length) { this.append(seq) }
}
