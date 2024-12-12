fun main() {
    Day05().execute()
}

class Day05 : ContestDay<Pair<List<Pair<Int, Int>>, List<List<Int>>>, Int>("Day05") {

    override fun partOne(input: Pair<List<Pair<Int, Int>>, List<List<Int>>>): Int {
        val ruleMap = mutableMapOf<Int, MutableSet<Int>>()
        input.first.forEach { ruleMap.getOrPut(it.first, ::mutableSetOf).add(it.second) }
        val updates = input.second
        var sum = 0
        var brokenUpdates = updates.size
        updates.forEach { update ->
            if (isValidUpdate(update, ruleMap)) {
                brokenUpdates--
                val midElement = update.lastIndex / 2
                sum += update[midElement]
            }
        }
        return sum
    }

    private fun isValidUpdate(update: List<Int>, ruleMap: Map<Int, MutableSet<Int>>): Boolean {
        var amountOfIterations = 0
        for (i in update.lastIndex downTo 1) {
            for (j in i - 1 downTo 0) {
                ruleMap[update[i]]?.let {
                    if (it.contains(update[j])) {
                        return false
                    }
                    amountOfIterations++
                }
            }
//            println(amountOfIterations)
        }
        return true
    }

    override fun partTwo(input: Pair<List<Pair<Int, Int>>, List<List<Int>>>): Int {
        val updates = input.second
        var sum = 0
        for (update in updates) {
            val priority = buildPriority(update, input.first)

            if (update != priority) {
                sum += priority[priority.lastIndex/2]
            }
        }


//        val rules = allRules.toMutableList()
//        val rules = LinkedList(allRules.toMutableList())
        // individual element approach
//        var startingPoint = 0

//        while (startingPoint < input.first.lastIndex) {
//            var currentElementIndex = startingPoint
//            for (i in currentElementIndex..rules.lastIndex) {
//                if (ruleMap[rules[currentElementIndex]]?.contains(rules[i]) != true && ruleMap[rules[i]]?.contains(rules[currentElementIndex]) == true) {
//                    val removed = rules.removeAt(i)
//                    rules.add(currentElementIndex, removed)
//                    currentElementIndex = i
//                }
//            }
//            startingPoint++
//        }

//        while (startingPoint < rules.lastIndex) {
//            var currentElementIndex = startingPoint
//            for (i in currentElementIndex..rules.lastIndex) {
//                if (ruleMap[rules[currentElementIndex]]?.contains(rules[i]) != true && ruleMap[rules[i]]?.contains(rules[currentElementIndex]) == true) {
//                    val removed = rules.removeAt(i)
//                    rules.add(currentElementIndex, removed)
//                    currentElementIndex = i
//                }
//            }
//            startingPoint++
//        }
        //bubble approach
//        for (i in 0 until rules.lastIndex) {
//            var noMoves = true
//            for (j in 0 until rules.lastIndex) {
//                if (ruleMap[rules[j + 1]]?.contains(rules[j]) != false) {
//                    noMoves = false
//                    val switcher = rules[j + 1]
//                    rules[j + 1] = rules[j]
//                    rules[j] = switcher
//
//                }
//            }
//            if (noMoves) {
//                break
//            }
//        }
//        val rulesPriority = mutableMapOf<Int, Int>()
//        priority.forEachIndexed { index, value -> rulesPriority[value] = index }
//        val brokenUpdates = updates.filterNot { isValidUpdateByPriority(it, rulesPriority) }
//        val fixedUpdates = brokenUpdates.map { update ->
//            val fixedUpdate = update.sortedWith { a, b -> rulesPriority[a]!! - rulesPriority[b]!! }
//            val midElement = fixedUpdate[fixedUpdate.lastIndex / 2]
//            println("sum = $sum and middle element $midElement")
//            sum += midElement
//            fixedUpdate
//        }

        return sum
    }

    private fun buildPriority(updates: List<Int>, rules: List<Pair<Int, Int>>):List<Int> {
        val ruleMap = mutableMapOf<Int, MutableSet<Int>>()
        val relatedRules = rules.filter { it.first in updates && it.second in updates }
        relatedRules.forEach {
            ruleMap.getOrPut(it.first, ::mutableSetOf).add(it.second)
            ruleMap.getOrPut(it.second, ::mutableSetOf)
        }

        // solution with using a DAG and Khan's algorithm
        // build graph
        val DAG = ruleMap
        val vertices = DAG.keys
        val inDegree = mutableMapOf<Int, Int>()
        val queue = ArrayDeque<Int>()
        // building in degree
        for (vertex in vertices) {
            val edges = ruleMap[vertex]
            inDegree[vertex] = inDegree.getOrDefault(vertex, 0)
            edges?.takeIf { it.isNotEmpty() }?.let {
                for (to in edges) {
                    inDegree[to] = inDegree.getOrDefault(to, 0) + 1
                }
            }
        }
        // adding to queue
        inDegree.filter { it.value == 0 }.forEach { queue.add(it.key) }
        val priority = mutableListOf<Int>()

        while (queue.isNotEmpty()) {
            val vertex = queue.removeFirst()
            priority.add(vertex)

            ruleMap[vertex]?.forEach {
                inDegree[it] = inDegree[it]!! - 1
                if (inDegree[it]!! == 0) {
                    queue.add(it)
                }
            }
        }


        return priority
    }

    private fun isValidUpdateByPriority(updates: List<Int>, rulesPriority: Map<Int, Int>): Boolean {
        var highestPriority = -1
        for (update in updates) {
            val currentPriority = rulesPriority[update] ?: Int.MIN_VALUE
            if (currentPriority > highestPriority) {
                highestPriority = currentPriority
            } else return false
        }
        return true

    }

    override fun transformInput(input: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val (rules, updates) = input.partition { it.contains('|') }
        val pageRules = rules.map {
            val (a, b) = it.split("|", limit = 2)
            a.toInt() to b.toInt()
        }
        val pageUpdate = updates.toMutableList().apply { removeAt(0) }.map {
            numberRegexp.findAll(it).map { match -> match.value.toInt() }.toList()
        }
        return pageRules to pageUpdate
    }

}