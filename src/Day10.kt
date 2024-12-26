import java.util.Queue

typealias Matrix = List<List<Int>>
typealias Coordinates = Pair<Int, Int>
typealias Route = Pair<Int, Int>

fun main() {
    Day10().execute()
}


class Day10 : ContestDay<List<List<Int>>, Int>("Day10_test") {

    override fun transformInput(input: List<String>): List<List<Int>> {
        return input.map { it.toCharArray().map { charNum -> charNum - '0' } }
    }

    override fun partOne(input: List<List<Int>>): Int? {
        val entryPoints = buildList {
            input.forEachIndexed { index, row ->
                row.forEachIndexed { col, num ->
                    if (num == 0) {
                        add(index to col)
                    }
                }
            }
        }

        return entryPoints.sumOf { pathVariations(it, input) }
    }

    private fun pathVariations(
        entry: Pair<Int, Int>,
        matrix: List<List<Int>>,
        nextStop: Int = 0
    ): Int {
        if (nextStop == 9) return 1
        var uniqueRoutes = 0
        val nextSteps = matrix.exploreSteps(entry, nextStop + 1)
        if (nextSteps.isNotEmpty()) {
            nextSteps.forEach { step ->
                uniqueRoutes += pathVariations(step, matrix, nextStop + 1)
            }
        }
        return uniqueRoutes
    }

}

private fun Matrix.exploreSteps(standingAt: Coordinates, nextStep: Int): List<Coordinates> {
    val potentialNextSteps = matchingSteps(standingAt)
    return potentialNextSteps.filter { this[it.first][it.second] == nextStep }
}

private fun Matrix.matchingSteps(standingAt: Coordinates): List<Coordinates> {
    val (i, j) = standingAt
    val moves = mutableListOf(
        i + 1 to j,
        i - 1 to j,
        i to j + 1,
        i to j - 1
    )
    return moves.filter { this.withinBounds(it.first, it.second) }
}

