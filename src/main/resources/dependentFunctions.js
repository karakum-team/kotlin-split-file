export function firstFn() {
    console.log("hello 1")
}

export function secondFn() {
    firstFn()
    console.log("hello 2")
}

export default function() {
    firstFn()
    secondFn()
    console.log("hello default")
}
