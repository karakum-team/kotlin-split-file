package node.fs

fun RmdirOptions(recursive: Boolean) = object : RmdirOptions {
    override val recursive = recursive
}
