package karakum.splitfile

import node.fs.readFileSync

fun main() {
    println(readFileSync("kotlin/dependentFunctions.js", "utf8"))
}
