abstract class ContestDay<Input, Output>(private val inputPath: String) {

    fun execute() {
        val input = readInput(inputPath)
        measureTimeTaken { partOne(transformInput(input)).println() }
        measureTimeTaken { partTwo(transformInput(input)).println() }
    }


    open fun partOne(input: Input): Output? {
        return null
    }

    open fun partTwo(input: Input): Output? {
        return null
    }

    internal abstract fun transformInput(input: List<String>): Input
}