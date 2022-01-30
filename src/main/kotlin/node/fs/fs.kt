@file:JsModule("fs")
package node.fs

external fun readFileSync(path: String, encoding: String): String

external fun writeFileSync(path: String, data: String)

external interface RmdirOptions {
    val recursive: Boolean
}

external fun rmdirSync(path: String, options: RmdirOptions)

external fun mkdirSync(path: String)
