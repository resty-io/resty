package io.github.restyio.resty

abstract class Method(value: CharSequence) : CharSequence by value {
    override fun equals(other: Any?): Boolean {
        if (other is Method && other === this) {
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

    object Get : Method("GET")

    object Put : Method("PUT")

    object Post : Method("POST")

    object Delete : Method("DELETE")

    object Patch : Method("PATCH")

    object Head : Method("HEAD")

    object Options : Method("OPTIONS")

    object Trace : Method("TRACE")

    object Connect : Method("CONNECT")
}
