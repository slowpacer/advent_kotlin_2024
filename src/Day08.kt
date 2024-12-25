import java.util.*

fun main() {
    Day08().execute()
}

class Day08 : ContestDay<List<CharArray>, Int>("Day08") {
    private var calculatePartTwo = false
    private val antennasAsAntiNodes = mutableSetOf<Pair<Int, Int>>()


    override fun transformInput(input: List<String>): List<CharArray> {
        return input.map { row -> row.toCharArray() }
    }

    override fun partOne(input: List<CharArray>): Int {
        val antennas = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()
        input.forEachIndexed { i, row ->
            row.forEachIndexed { j, element ->
                if (element != '.') {
                    antennas.getOrPut(element) { mutableListOf() }.add(i to j)
                }
            }
        }
        var antiNodes = 0
        antennas.filter { it.value.size >= 1 }.forEach { (_, value) ->
            val queue = ArrayDeque<Pair<Int, Int>>(value.size)
            queue.addAll(value)
            var current = queue.removeFirst()
            while (queue.isNotEmpty()) {
                val checkAgainstQueue = queue.toMutableList()
                while (checkAgainstQueue.isNotEmpty()) {
                    val next = checkAgainstQueue.removeFirst()
                    antiNodes += findAntiNodes(current, next, input)
                }
                current = queue.removeFirst()
            }
        }

        return antiNodes
    }

    override fun partTwo(input: List<CharArray>): Int? {
        calculatePartTwo = true
        val antennas = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()
        input.forEachIndexed { i, row ->
            row.forEachIndexed { j, element ->
                if (element != '.') {
                    antennas.getOrPut(element) { mutableListOf() }.add(i to j)
                }
            }
        }
        var antiNodes = 0
        antennas.filter { it.value.size >= 1 }.forEach { (_, value) ->
            val queue = ArrayDeque<Pair<Int, Int>>(value.size)
            queue.addAll(value)
            var current = queue.removeFirst()
            while (queue.isNotEmpty()) {
                val checkAgainstQueue = queue.toMutableList()
                while (checkAgainstQueue.isNotEmpty()) {
                    val next = checkAgainstQueue.removeFirst()
                    antiNodes += findAntiNodes(current, next, input)
                }
                current = queue.removeFirst()
            }
        }

//        printResultingMap(input)
        return antiNodes + antennasAsAntiNodes.count()
    }

    private fun printResultingMap(input: List<CharArray>) {
        input.forEachIndexed { i, row ->
            print("\n")
            row.forEachIndexed { j, element ->
                print(element)
            }
        }
    }

    private fun findAntiNodes(antennaA: Pair<Int, Int>, antennaB: Pair<Int, Int>, map: List<CharArray>): Int {
        val firstDirection = antennaA.first - antennaB.first to antennaA.second - antennaB.second
        val secondDirection = firstDirection.first * -1 to firstDirection.second * -1
        val antiNodes = countAntiNodes(antennaA, firstDirection, map) + countAntiNodes(antennaB, secondDirection, map)
        return antiNodes

    }

    private fun countAntiNodes(startingPoint: Pair<Int, Int>, direction: Pair<Int, Int>, map: List<CharArray>): Int {
        var nodes = 0
        var i = startingPoint.first + direction.first
        var j = startingPoint.second + direction.second
        antennasAsAntiNodes.add(startingPoint)
        if (calculatePartTwo) {
            while (map.withinBounds(i, j)) {

                if (map[i][j] == '.') {
                    nodes++
                    map[i][j] = '#'
                } else if (map[i][j] != '#') {
                    antennasAsAntiNodes.add(i to j)
                }
                i += direction.first
                j += direction.second
            }

        } else {
            if (map.withinBounds(i, j) && map[i][j] != '#') {
                nodes++
                if (map[i][j] == '.') {
                    map[i][j] = '#'
                }

            }
        }

        return nodes
    }

    private fun List<CharArray>.withinBounds(i: Int, j: Int) =
        i >= 0 && i <= this.lastIndex && j >= 0 && j <= this[i].lastIndex
}