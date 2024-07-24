package io.github.restyio.resty

import io.github.restyio.resty.extensions.CharSequenceEnvelope
import io.github.restyio.resty.extensions.buildString
import java.util.Base64

/**
 * HTTP message header.
 *
 * @since 0.1
 */
interface Header : Iterable<CharSequence> {
    /**
     * @param name The name of the field to retrieve
     * values from.
     *
     * @return An iterator over the values assigned to
     * the field [name], or an empty iterator if the
     * field does not exist.
     *
     * @since 0.1
     */
    fun field(name: CharSequence): Iterator<CharSequence>
}

/**
 * @since 0.1
 */
class PreparedHeader(
    private val map: Map<CharSequence, Iterable<CharSequence>>,
) : Header {
    override fun field(name: CharSequence): Iterator<CharSequence> {
        return (this.map[name] ?: emptyList()).iterator()
    }

    override fun iterator(): Iterator<CharSequence> {
        return this.map.keys.iterator()
    }
}

/**
 * @since 0.1
 */
class ExtendedHeader(
    private val origin: Header,
    private val field: CharSequence,
    private val value: Iterable<CharSequence>,
) : Header {
    override fun field(name: CharSequence): Iterator<CharSequence> {
        return iterator {
            this.yieldAll(this@ExtendedHeader.origin.field(name))
            if (name == this@ExtendedHeader.field) {
                this.yieldAll(this@ExtendedHeader.value)
            }
        }
    }

    override fun iterator(): Iterator<CharSequence> {
        return iterator {
            var exists = false
            for (field in this@ExtendedHeader.origin) {
                this.yield(field)
                if (field == this@ExtendedHeader.field) {
                    exists = true
                }
            }
            if (!exists) {
                this.yield(this@ExtendedHeader.field)
            }
        }
    }
}

class FilteredHeader(
    private val origin: Header,
    private val field: CharSequence,
) : Header {
    override fun field(name: CharSequence): Iterator<CharSequence> {
        if (name == this.field) {
            return emptyArray<CharSequence>().iterator()
        }
        return this.origin.field(name)
    }

    override fun iterator(): Iterator<CharSequence> {
        return iterator {
            for (field in this@FilteredHeader.origin) {
                if (field == this@FilteredHeader.field) {
                    continue
                }
                this.yield(field)
            }
        }
    }
}

object EmptyHeader : Header by PreparedHeader(emptyMap())

abstract class Field(value: CharSequence) : CharSequence by value {
    override fun equals(other: Any?): Boolean {
        if (other is Field && other === this) {
            return true
        }
        if (other is CharSequence) {
            return other.contentEquals(this, true)
        }
        return false
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    object AIM : Field("A-IM")

    object UserAgent : Field("User-Agent")

    object ContentLength : Field("Content-Length")
}

class Authorization(
    kind: CharSequence,
    credential: () -> CharSequence,
) : CharSequence by CharSequenceEnvelope(
    {
        buildString {
            append(kind)
            append(" ")
            append(credential())
        }
    },
) {
    class Basic(
        username: CharSequence,
        password: CharSequence,
    ) : CharSequence by Authorization(
        "Basic",
        {
            Base64
                .getEncoder()
                .encodeToString(
                    buildString {
                        this.append(username)
                        this.append(":")
                        this.append(password)
                    }.encodeToByteArray(),
                )
        },
    )

    class Bearer(token: CharSequence) : CharSequence by Authorization("Bearer", {
        token
    })
}

fun Header.with(field: CharSequence, value: Iterable<CharSequence>): Header {
    return ExtendedHeader(this, field, value)
}

fun Header.with(field: CharSequence, value: CharSequence): Header {
    return ExtendedHeader(this, field, listOf(value))
}

fun Header.without(field: CharSequence): Header {
    return FilteredHeader(this, field)
}

fun Header.replace(field: CharSequence, value: Iterable<CharSequence>): Header {
    return this.without(field).with(field, value)
}

fun Header.replace(field: CharSequence, value: CharSequence): Header {
    return this.without(field).with(field, value)
}

fun Header.contentLength(): Int {
    for (value in this.field(Field.ContentLength)) {
        return value.buildString().toInt()
    }
    return -1
}
