package karakum.splitfile

import kotlinext.js.jso
import node.fs.mkdirSync
import node.process
import node.fs.rmdirSync
import node.fs.writeFileSync
import typescript.*

fun main() {
    val (filePath) = process.argv.drop(2)

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

    val statements = sourceFile.statements.asArray()

    val rootNodes: List<Node> = statements.filter { isFunctionDeclaration(it) }

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

private fun traverse(node: Node, callback: (current: Node) -> Unit) {
    callback(node)

    for (current in node.getChildren(node.getSourceFile())) {
        traverse(current, callback)
    }
}
