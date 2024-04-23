package com.erolc.mrouter.route

val emptyArgs get() = Args()

val emptyArg get() = Arg("", "")

/**
 * 变量，用于在界面跳转间传递数据，基础数据类型会自动赋予默认值
 * @author erolc
 * @since 2023/10/26 15:07
 */
class Args : Iterable<Arg> {
    private val list = mutableSetOf<Arg>()

    fun getArgOrNull(key: String) = list.find { it.key == key }

    fun getArg(key: String) = getArgOrNull(key) ?: emptyArg

    fun addArg(arg: Arg) = list.add(arg)

    fun addArg(key: String, value: Any) = addArg(Arg(key, value))

    fun addArg(arg: Pair<String, Any>) = addArg(arg.first, arg.second)

    fun addAllArg(args: Map<String, Any>) = also { it += args.toArgs() }

    fun addAllArg(args: Collection<Arg>) = list.addAll(args)

    private fun addArgs(args: Args) = addAllArg(args.list)

    inline fun <reified T> getDataOrNull(key: String) = getArgOrNull(key)?.getDataOrNull<T>()

    inline fun <reified T> getData(key: String) = getArg(key).getData<T>()

    operator fun plusAssign(args: Args) {
        addArgs(args)
    }

    operator fun plusAssign(arg: Arg) {
        addArg(arg)
    }


    operator fun plus(arg: Arg) = also { addArg(arg) }

    operator fun plus(args: Args) = also { it.addArgs(args) }

    operator fun minus(arg: String) = also { list.removeAll { it.key == arg } }

    operator fun minus(arg: Arg) = also { it - arg.key }

    operator fun minus(args: Args) = also { list.removeAll(args.list) }

    fun copy() = also { list.map { it.copy() }.toArgs() }

    /**
     * 判断两个args 的内容是否相同
     * @param args 另外的args
     * @param strict 严格模式，如果为true则还需要保证位置一致
     */
    fun contentEquals(args: Args, strict: Boolean = false): Boolean {
        if (size != args.size)
            return false

        if (!strict) return list.containsAll(args.list)

        return toString() == args.toString()
    }


    fun contains(element: Arg) = list.contains(element)

    val size = list.size

    fun isEmpty() = list.isEmpty()

    fun isNotEmpty() = list.isNotEmpty()


    override fun iterator(): Iterator<Arg> {
        return list.iterator()
    }

    val range = IntRange(0, size - 1)

    fun toList() = list

    override fun toString(): String {
        val result = list.fold("") { acc, arg ->
            if (acc.isEmpty())
                "{$arg"
            else
                "$acc,$arg"
        }
        return if (result.isNotEmpty()) "$result}" else ""
    }
}


@SinceKotlin("1.0")
data class Arg(val key: String, val value: Any) {

    inline fun <reified T> getDataOrNull(): T? {
        return getBasicData<T>(value) ?: value as? T
    }

    inline fun <reified T> getData(): T {
        return getBasicData<T>(value) ?: value as T
    }


    operator fun plus(arg: Arg): Args {
        return emptyArgs.apply {
            this += this@Arg
            this += arg
        }
    }

    override fun toString(): String {
        return "\"$key\":\"$value\""
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Arg

        if (key != other.key) return false
        return value == other.value
    }
}

fun Collection<Arg>.toArgs(): Args {
    return emptyArgs.also {
        it.addAllArg(this)
    }
}

fun Pair<String, Any>.toArg(): Arg {
    return Arg(first, second)
}

fun Map<String, Any>.toArgs(): Args {
    return map { (key, value) ->
        Arg(key, value)
    }.toArgs()
}

fun Args.toMap(): Map<String, Any> = toList().associate { it.toPair() }

fun Arg.toPair() = key to value


fun Args?.isNullOrEmpty() = this == null || this.isEmpty()

inline fun Args.ifEmpty(defaultValue: () -> Args): Args =
    if (isEmpty()) defaultValue() else this

/**
 * 是否是基础数据类型
 */
fun isBasic(value: Any): Boolean {
    return when (value) {
        is String -> true
        is Int -> true
        is Boolean -> true
        is Short -> true
        is Long -> true
        is Byte -> true
        is Char -> true
        is Double -> true
        is Float -> true
        is UInt -> true
        is ULong -> true
        is UShort -> true
        is UByte -> true
        else -> false
    }
}

/**
 * 转换数据，如果不是基础数据类型或无法转换则返回null
 */
inline fun <reified T> getBasicData(value: Any): T? {
    return if (isBasic(value))
        value as? T ?: when (T::class) {
            Int::class -> when (value) {
                is String -> value.toIntOrNull() ?: 0
                is Boolean -> if (value) 1 else 0
                is Number -> value.toInt()
                is Char -> value.digitToInt()
                else -> 0
            }

            String::class -> value.toString()
            Boolean::class -> when (value) {
                is String -> value.toBooleanStrictOrNull() ?: false
                is Int -> value >= 1
                is Short -> value > 0
                is Long -> value > 0
                is Byte -> value > 0
                is Char -> value.code > 0
                is Double -> value > 0
                is Float -> value > 0
                else -> false
            }

            Short::class -> when (value) {
                is Number -> value.toShort()
                is String -> value.toShortOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toShort()
                else -> 0
            }

            Long::class -> when (value) {
                is Number -> value.toLong()
                is String -> value.toLongOrNull() ?: 0L
                is Boolean -> if (value) 1L else 0L
                is Char -> value.code.toLong()
                else -> 0L
            }

            Byte::class -> when (value) {
                is Number -> value.toByte()
                is String -> value.toByteOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toByte()
                is Char -> value.code.toByte()
                else -> 0
            }

            Char::class -> when (value) {
                is Number -> value.toInt().toChar()
                is Boolean -> if (value) 1.digitToChar() else 0.digitToChar()
                else -> '0'
            }

            Double::class -> when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull() ?: 0.0
                is Boolean -> if (value) 1.0 else 0.0
                is Char -> value.code.toDouble()
                else -> 0.0
            }

            Float::class -> when (value) {
                is Number -> value.toFloat()
                is String -> value.toFloatOrNull() ?: 0f
                is Boolean -> if (value) 1f else 0f
                is Char -> value.code.toFloat()
                else -> null
            }
            //        is UByte -> true
            UInt::class -> when (value) {
                is Number -> value.toInt().toUInt()
                is String -> value.toUIntOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toUInt()
                is Char -> value.code.toUInt()
                else -> 0
            }

            ULong::class -> when (value) {
                is Number -> value.toLong().toULong()
                is String -> value.toULongOrNull() ?: 0L
                is Boolean -> (if (value) 1 else 0).toULong()
                is Char -> value.code.toULong()
                else -> 0L
            }

            UShort::class -> when (value) {
                is Number -> value.toShort().toUShort()
                is String -> value.toUShortOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toUShort()
                is Char -> value.code.toUShort()
                else -> 0
            }

            UByte::class -> when (value) {
                is Number -> value.toByte().toUByte()
                is String -> value.toUByteOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toUByte()
                is Char -> value.code.toUByte()
                else -> 0
            }

            else -> null
        } as? T
    else null
}

