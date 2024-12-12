import kotlin.math.abs

fun main() {
    Day02().execute()
}

class Day02 : ContestDay<List<List<Int>>, Int>("Day02") {
    override fun partOne(input: List<List<Int>>): Int? {
        var safeRows = 0
        input.forEach { row ->
            var changedDirection = false
            var diff = row[0] - row[1]
            val positiveGrowth = diff > 0
            var fitsWithinBounds = true
            for (i in 2..row.size) {
                fitsWithinBounds = abs(diff) in 1..3
                changedDirection = positiveGrowth != diff > 0
                if (changedDirection || !fitsWithinBounds) {
                    return@forEach
                }
                if (i != row.size)
                    diff = row[i - 1] - row[i]
            }
            safeRows++
        }
        return safeRows
    }

    override fun partTwo(input: List<List<Int>>): Int? {
        var safeRows = 0
        input.forEach { row ->
            val isIncreasing = row[0] < row[1]
            var directionChanged = false
            var threshold = 1
            var dropIndex = -1
            var index = 1
            while (index < row.lastIndex) {
                if (index == dropIndex) {
                    index++
                    continue
                }
                val checkAgainstIndex = (if (index + 1 == dropIndex) dropIndex else index) + 1
                val diff = row[index] - row[checkAgainstIndex]
                val fitsWithinBounds = abs(diff) in 1..3
                directionChanged = isIncreasing != diff < 0
                if (directionChanged || !fitsWithinBounds) {
                    dropIndex = index
                    if (--threshold < 0) {
                        return@forEach
                    }
                    index--
                    continue
                }

                index++
            }
            safeRows++
        }
        return safeRows
    }

    override fun transformInput(input: List<String>): List<List<Int>> {
        return input.map { row -> numberRegexp.findAll(row).map { it.value.toInt() }.toList() }
    }

}