fun main() {
    val currentTime = System.currentTimeMillis()
    Day07().execute()
    println("time to solve ${System.currentTimeMillis() - currentTime}")
}

class Day07 : ContestDay<List<Pair<Long, List<Long>>>, Long>("Day07") {

    override fun transformInput(input: List<String>): List<Pair<Long, List<Long>>> {
        return input.map { row ->
            row.split(":").let {
                it[0].toLong() to it[1].split(" ").drop(1).map { it.toLong() }
            }
        }
    }

    override fun partOne(input: List<Pair<Long, List<Long>>>): Long {
        return input.sumOf { (sum, elements) -> sum.takeIf { validCalibration(sum, elements) } ?: 0 }
    }

    override fun partTwo(input: List<Pair<Long, List<Long>>>): Long {
        return input.sumOf { (sum, elements) -> sum.takeIf { validCalibration(sum, elements, true) } ?: 0 }
    }

    private fun validCalibration(target: Long, elements: List<Long>, withConcat: Boolean = false): Boolean {
        return if (withConcat) matchesTheSumWithConcat(target, elements = elements) else matchesTheSum(
            target,
            elements = elements
        )
    }

    private fun matchesTheSum(
        target: Long,
        currentSum: Long = 0,
        indexOfElement: Int = 0,
        elements: List<Long>,
        cache: MutableMap<Long, Boolean> = mutableMapOf()
    ): Boolean {
        if (indexOfElement <= elements.lastIndex && currentSum > target) return false
        if (indexOfElement == elements.size) return currentSum == target
        val newIndex = indexOfElement + 1
        val currentSumFromAdding = currentSum + elements[indexOfElement]
        val additionResult = cache.getOrPut(currentSumFromAdding, {
            matchesTheSum(
                target,
                currentSumFromAdding,
                newIndex,
                elements,
                cache
            )
        })
        val currentSumFromMultiplication = currentSum * elements[indexOfElement]
        val multiplicationResult = cache.getOrPut(currentSumFromMultiplication, {
            if (currentSum > 0) matchesTheSum(
                target,
                currentSum * elements[indexOfElement],
                newIndex,
                elements,
                cache
            ) else false
        })

        return additionResult || multiplicationResult
    }

    private fun matchesTheSumWithConcat(
        target: Long,
        currentSum: Long = 0,
        indexOfElement: Int = 0,
        elements: List<Long>,
        cache: MutableMap<Long, Boolean> = mutableMapOf()
    ): Boolean {
//        if (indexOfElement <= elements.lastIndex && currentSum > target) return false
        if (indexOfElement == elements.size) return currentSum == target
        val newIndex = indexOfElement + 1
        val currentSumFromAdding = currentSum + elements[indexOfElement]
        val additionResult =
            matchesTheSumWithConcat(
                target,
                currentSumFromAdding,
                newIndex,
                elements,
                cache
            )
        val currentSumFromMultiplication = currentSum * elements[indexOfElement]
        val multiplicationResult =
            if (currentSum > 0) matchesTheSumWithConcat(
                target,
                currentSum * elements[indexOfElement],
                newIndex,
                elements,
                cache
            ) else false
        val numberWithConcatenation = "$currentSum${elements[indexOfElement]}".toLong()
        val concatResult =
            matchesTheSumWithConcat(
                target,
                numberWithConcatenation,
                newIndex,
                elements,
                cache
            )

        return additionResult || multiplicationResult || concatResult
    }
}