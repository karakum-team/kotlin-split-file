package typescript

fun <T : Node> NodeArray<T>.asArray(): ReadonlyArray<T> =
    unsafeCast<ReadonlyArray<T>>()
