package node

import kotlinext.js.ReadonlyArray

external object process {
    val argv: ReadonlyArray<String>
}
