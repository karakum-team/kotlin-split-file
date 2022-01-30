package karakum.splitfile

import kotlinext.js.ReadonlyArray
import kotlinext.js.jso
import node.fs.mkdirSync
import node.fs.rmdirSync
import node.fs.writeFileSync
import typescript.Node
import typescript.createProgram
import typescript.isFunctionDeclaration

fun main() {
    val (filePath) = getArgs()

    val program = createProgram(
        arrayOf(
            filePath
        ),
        jso {
            lib = emptyArray()
            types = emptyArray()
            allowJs = true
        }
    )

    val checker = program.getTypeChecker()

    val (sourceFile) = program.getSourceFiles()

    rmdirSync("target", jso { recursive = true })
    mkdirSync("target")

    // TODO: fix array inheritance
    val statements = sourceFile.statements.unsafeCast<ReadonlyArray<Node>>()

    val rootNodes = statements.filter { isFunctionDeclaration(it) }

    for (node in statements) {
        if (isFunctionDeclaration(node)) {
            val dependencies = mutableSetOf<Node>()

            traverse(node) {
                val symbol = checker.getSymbolAtLocation(it)
                val valueDeclaration = symbol?.valueDeclaration

                if (
                    valueDeclaration != null &&
                    rootNodes.contains(valueDeclaration) &&
                    valueDeclaration != node
                ) {
                    dependencies.add(valueDeclaration)
                }
            }

            val fileName = node.name?.text ?: "default"
            var fileContent = ""

            for (dependency in dependencies) {
                if (isFunctionDeclaration(dependency)) {
                    val dependencyName = dependency.name?.text ?: "default"

                    fileContent += """import {$dependencyName} from "./${dependencyName}.js""""
                    fileContent += "\n"
                }
            }

            if (dependencies.isNotEmpty()) {
                fileContent += "\n"
            }

            fileContent += node.getText(sourceFile)

            writeFileSync("target/$fileName.js", fileContent)
        }
    }
}

private fun getArgs(): List<String> =
    js("process.argv")
        .unsafeCast<Array<String>>()
        .drop(2)

private fun traverse(node: Node, callback: (current: Node) -> Unit) {
    callback(node)

    for (current in node.getChildren(node.getSourceFile())) {
        traverse(current, callback)
    }
}
