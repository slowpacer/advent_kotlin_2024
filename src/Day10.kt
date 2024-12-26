typealias Matrix = List<List<Int>>
typealias Coordinates = Pair<Int, Int>
typealias PeaksWithRoutes = Pair<Int, MutableSet<Coordinates>>

fun main() {
    Day10().execute()
}


class Day10 : ContestDay<List<List<Int>>, Int>("Day10") {

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

        val variations = entryPoints.map { pathVariations(it, input) }
        val uniquePeaks = variations.sumOf { it.second.size }
        return uniquePeaks
    }

    private fun pathVariations(
        entry: Pair<Int, Int>,
        matrix: List<List<Int>>,
        nextStop: Int = 0,
        routesCache: MutableMap<Coordinates, PeaksWithRoutes> = mutableMapOf(),
    ): PeaksWithRoutes {
        if (nextStop == 9) {
            return 1 to mutableSetOf(entry)
        }
        var uniqueRoutes = 0 to mutableSetOf<Coordinates>()
        val nextSteps = matrix.exploreSteps(entry, nextStop + 1)
        if (nextSteps.isNotEmpty()) {
            nextSteps.forEach { step ->
                uniqueRoutes += routesCache[step] ?: pathVariations(step, matrix, nextStop + 1, routesCache).apply {
                    routesCache[step] = this
                }

            }
        }
        return uniqueRoutes
    }

    infix operator fun PeaksWithRoutes.plus(other: PeaksWithRoutes): PeaksWithRoutes {
        return this.first + other.first to this.second.apply { addAll(other.second) }
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

