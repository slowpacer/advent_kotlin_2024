import kotlin.math.abs

fun main() {
    Day04().execute()
}

class Day04 : ContestDay<List<String>, Int>("Day04") {

    val xmas = "XMAS"
    val samx = xmas.reversed()
    val chars = setOf('M', 'A', 'S')
    val opposites = mapOf(
        'M' to 'S',
        'S' to 'M',
        'X' to 'Y'
    )

    override fun partOne(input: List<String>): Int? {
        val rows = input

        val columns = mutableListOf<String>()
        for (i in rows[0].indices) {
            var column = buildString {
                for (j in rows.indices) {
                    append(rows[j][i])
                }
            }
            columns.add(column)
        }

        val diagonals = mutableListOf<String>()
        // \ diagonals
        val amountOfDiagonals = 1 + (rows.size - xmas.length) * 2
        var startingPoint = rows.size - xmas.length
        var iterations = amountOfDiagonals
        while (iterations > 0) {
            val diagonal = buildString {
                for (i in 0 until rows.size - abs(startingPoint)) {
                    if (startingPoint < 0) {
                        append(rows[i][abs(startingPoint) + i])
                    } else {
                        append(rows[startingPoint + i][i])
                    }

                }
            }
            diagonals.add(diagonal)
            --startingPoint
            iterations--
        }

        // / diagonals
        iterations = amountOfDiagonals
        startingPoint = rows.size - xmas.length
        while (iterations > 0) {
            val diagonal = buildString {
                for (i in 0 until rows.size - abs(startingPoint)) {
                    if (startingPoint < 0) {
                        append(rows[i][rows.lastIndex + startingPoint - i])
                    } else {
                        append(rows[startingPoint + i][rows.lastIndex - i])
                    }

                }
            }
            diagonals.add(diagonal)
            --startingPoint
            iterations--
        }
        val allVariations = rows + columns + diagonals
        val xmasRegex = xmas.toRegex()
        val samxRegex = samx.toRegex()
        val xmasCount = allVariations.sumOf { xmasRegex.findAll(it).count() + samxRegex.findAll(it).count() }

        return xmasCount
    }

    override fun partTwo(input: List<String>): Int? {
        // basically a BFS that looks through each possibility
        val matrix = input.map { it.toCharArray() }
        var xmasCount = 0
        for (i in 1..matrix.lastIndex) {
            for (j in 1..matrix[i].lastIndex) {
                if (matrix[i][j] == 'A' && isX(matrix, i, j)) xmasCount++
            }
        }
        return xmasCount
    }

    private fun isX(matrix: List<CharArray>, i: Int, j: Int): Boolean {
        if (i + 1 > matrix.lastIndex || i - 1 < 0) return false
        if (j + 1 > matrix[i].lastIndex || j - 1 < 0) return false
        val xmas =
            opposites[matrix[i - 1][j - 1]] == matrix[i + 1][j + 1] && opposites[matrix[i - 1][j + 1]] == matrix[i + 1][j - 1]
        return xmas

    }


    override fun transformInput(input: List<String>): List<String> {
        return input
    }


}