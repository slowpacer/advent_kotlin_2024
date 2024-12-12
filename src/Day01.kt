import kotlin.math.abs

fun main() {
    Day01().execute()
}

class Day01 : ContestDay<Pair<List<Int>, List<Int>>, Int>("Day01") {

    override fun partOne(input: Pair<List<Int>, List<Int>>): Int? {
        val firstList = input.first.sorted()
        val secondList = input.second.sorted()
        var result = 0
        for (i in 0..firstList.lastIndex) {
            val diff = abs(firstList[i] - secondList[i])
            println("${firstList[i]} - ${secondList[i]} = $diff")
            result += diff

        }
        return result
    }

    override fun partTwo(input: Pair<List<Int>, List<Int>>): Int? {
        val numberOccurrences = HashMap<Int, Int>(input.second.size)
        input.second.forEach {
            var occurrences = numberOccurrences.getOrDefault(it, 0)
            numberOccurrences[it] = ++occurrences

        }
        val similarityScore = input.first.sumOf {
            it * numberOccurrences.getOrDefault(it, 0)
        }
        return similarityScore
    }

    override fun transformInput(input: List<String>): Pair<List<Int>, List<Int>> {
        val firstList = mutableListOf<Int>()
        val secondList = mutableListOf<Int>()
        input.forEach {
            val numbers = numberRegexp.findAll(it).toList()
            firstList.add(numbers[0].value.toInt())
            secondList.add(numbers[1].value.toInt())
        }
        return firstList to secondList
    }

}
