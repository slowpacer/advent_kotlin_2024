fun main() {
    val currentTime = System.currentTimeMillis()
    Day06().execute()
    println("time to solve ${System.currentTimeMillis() - currentTime}")
}

class Day06 : ContestDay<List<CharArray>, Int>("Day06") {

    // needed for solution 1
    private val visited = '*'
    private val guardPositions = setOf('<', '>', '^', 'v')
    private var uniqueMoves = 1

    // needed for solution 2
    val newObstacles = mutableSetOf<Pair<Int, Int>>()
    val matchingEntryPoints = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    var doNotAddEntryPoints = false
    var potentialObstaclesChecked = 0


    override fun partOne(input: List<CharArray>): Int? {
        var (startingCoordinates, direction) = findStartingPoint(input)
        var (row, column) = startingCoordinates
        while (withinMapBoundaries(row, column, input)) {
            val (newCoordinates, newDirection) = makeAMove(row, column, input, direction, true)
            row = newCoordinates.first
            column = newCoordinates.second
            direction = newDirection
        }
//        for (i in input.indices) {
//            for (j in input[i].indices) {
//                kotlin.io.print(input[i][j])
//            }
//            kotlin.io.println()
//        }

        return uniqueMoves
    }

    override fun partTwo(input: List<CharArray>): Int? {
        doNotAddEntryPoints = true
        var (startingCoordinates, direction) = findStartingPoint(input)
        var (row, column) = startingCoordinates
        val startingDirection = direction

        // building obstacles along the route
        while (withinMapBoundaries(row, column, input)) {
            val hypotheticalDirection = changeDirection(direction)
            val newObstacle = plantNewObstacle(hypotheticalDirection, row, column, input)
            newObstacle?.let {
                input[newObstacle.first][newObstacle.second] = '#'
                potentialObstaclesChecked++
                if (isThereALoop(startingCoordinates.first, startingCoordinates.second, startingDirection, input)) {
                    newObstacles.add(newObstacle)
                }
                input[newObstacle.first][newObstacle.second] = '.'
            }

            val (newCoordinates, newDirection) = makeAMove(row, column, input, direction)
            row = newCoordinates.first
            column = newCoordinates.second
            direction = newDirection
        }
//        while (guardIsVisible(row, column, input)) {
//            val (newCoordinates, newDirection) = makeAMove(row, column, input, direction)
//            row = newCoordinates.first
//            column = newCoordinates.second
//            direction = newDirection
//            //check if we can make a loop
//            val hypotheticalDirection = changeDirection(direction)
//
//            val entryPoints = matchingEntryPoints[hypotheticalDirection] ?: continue
//            val newObstacle = plantNewObstacle(hypotheticalDirection, row, column, entryPoints)
//            newObstacle?.let { newObstacles.add(it) }
//
//        }
        return newObstacles.size
    }

    private fun isThereALoop(row: Int, column: Int, direction: Int, input: List<CharArray>): Boolean {
        val visited = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
        var currentRow = row
        var currentColumn = column
        var currentDirection = direction
        while (withinMapBoundaries(currentRow, currentColumn, input)) {
            if (visited[currentRow to currentColumn]?.contains(currentDirection) == true) {
                return true
            }
            visited.getOrPut(currentRow to currentColumn, ::mutableListOf).add(currentDirection)
            val (newCoordinates, newDirection) = makeAMove(currentRow, currentColumn, input, currentDirection)
            currentRow = newCoordinates.first
            currentColumn = newCoordinates.second
            currentDirection = newDirection
        }
        return false
    }

    private fun plantNewObstacle(
        direction: Int, row: Int, column: Int, input: List<CharArray>
    ): Pair<Int, Int>? {
        val obstacle = when (direction) {
            0 -> {
                // we were going down
                row + 1 to column
            }

            1 -> {
                // we were going left
                row to column - 1
            }

            2 -> {
                // we were going up
                row - 1 to column
            }

            3 -> {
                // we were going right
                row to column + 1
            }

            else -> 0 to 0
        }
        val (newRow, newColumn) = obstacle

        if (!withinMapBoundaries(
                newColumn, newRow, input
            ) || input[newRow][newColumn] == '#' || input[newRow][newColumn] == '^'
        ) {
            return null
        }
        return obstacle
    }

//    private fun plantNewObstacle(
//        direction: Int,
//        row: Int,
//        column: Int,
//        matchingObstacles: List<Pair<Int, Int>>
//    ): Pair<Int, Int>? {
//        return when (direction) {
//            0 -> {
//                // we were going down
//                matchingObstacles.find { it.first == row && it.second < column }?.let { row + 1 to column }
//            }
//
//            1 -> {
//                // we were going left
//                matchingObstacles.find { it.second == column && it.first < row }?.let { row to column - 1 }
//            }
//
//            2 -> {
//                // we were going up
//                matchingObstacles.find { it.first == row && it.second > column }?.let { row - 1 to column }
//            }
//
//            3 -> {
//                // we were going right
//                matchingObstacles.find { it.second == column && it.first > row }?.let { row to column + 1 }
//            }
//
//            else -> null
//        }
//    }

    private fun withinMapBoundaries(col: Int, row: Int, matrix: List<CharArray>): Boolean {
        return row >= 0 && matrix.size > row && col >= 0 && matrix[row].size > col
    }

//    private fun changeDirection(
//        row: Int,
//        column: Int,
//        matrix: List<CharArray>,
//        direction: Int
//    ): Int {
//        if (matrix[row][column] == '#') {
//            // rotation happens here
//            // 0 -> l
//            // 1 -> up
//            // 2 - right
//            // 3 -> down
//            return (direction + 1) % 4
//        }
//        return direction
//    }

    private fun changeDirection(
        direction: Int
    ): Int {
        // rotation happens here
        // 0 -> l
        // 1 -> up
        // 2 - right
        // 3 -> down
        return (direction + 1) % 4
    }

    private fun findStartingPoint(matrix: List<CharArray>): Pair<Pair<Int, Int>, Int> {
        for (row in matrix.indices) {
            for (column in matrix[row].indices) {
                if (guardPositions.contains(matrix[row][column])) {
                    return (row to column) to mapDirection(matrix[row][column])

                }
            }
        }
        throw IllegalStateException("No position found")
    }

    private fun mapDirection(direction: Char): Int {
        return when (direction) {
            '<' -> 0
            '^' -> 1
            '>' -> 2
            'v' -> 3
            else -> -1

        }
    }

    /**
     * @return new coordinates and direction
     */
    private fun makeAMove(
        row: Int,
        col: Int,
        matrix: List<CharArray>,
        direction: Int,
        markVisited: Boolean = false,
    ): Pair<Pair<Int, Int>, Int> {
        var newDirection: Int
        var newRow = row
        var newCol = col
        when (direction) {
            0 -> {
                newCol--
                newDirection = moveToNewCell(newRow, newCol, matrix, direction, markVisited)
                if (newDirection != direction) {
                    newCol = col
                }
            }

            1 -> {
                newRow--
                newDirection = moveToNewCell(newRow, newCol, matrix, direction, markVisited)
                if (newDirection != direction) {
                    newRow = row
                }
            }

            2 -> {
                newCol++
                newDirection = moveToNewCell(newRow, newCol, matrix, direction, markVisited)
                if (newDirection != direction) {
                    newCol = col
                }

            }

            3 -> {
                newRow++
                newDirection = moveToNewCell(newRow, newCol, matrix, direction, markVisited)
                if (newDirection != direction) {
                    newRow = row
                }

            }

            else -> throw IllegalStateException("Wrong direction")
        }

        return ((newRow to newCol) to newDirection)
    }

    private fun moveToNewCell(
        row: Int, col: Int, matrix: List<CharArray>, direction: Int, markVisited: Boolean = false
    ): Int {
        if (withinMapBoundaries(row, col, matrix)) {
            if (matrix[row][col] == '#') {
                // we need to know what was the entry point
                if (!doNotAddEntryPoints) {
                    matchingEntryPoints.getOrPut(direction, ::mutableListOf).add(row to col)

                }
                return changeDirection(direction)
            } else if (matrix[row][col] == '.') {
                if (markVisited) {
                    matrix[row][col] = visited
                }
                uniqueMoves++
            }
        }
        return direction
    }

    override fun transformInput(input: List<String>): List<CharArray> {
        return input.map { row -> row.toCharArray() }
    }

}
