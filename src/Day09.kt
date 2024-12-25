import java.math.BigInteger
import java.util.LinkedList

fun main() {
    Day09().execute()
}


class Day09 : ContestDay<CharArray, BigInteger>("Day09") {

    override fun transformInput(input: List<String>): CharArray {
        return input[0].toCharArray()
    }

    override fun partOne(input: CharArray): BigInteger {
        val fileBlocks = input.filterIndexed { index, _ -> index % 2 == 0 }.map { it - '0' }.toMutableList()
        val memoryBlocks = input.filterIndexed { index, _ -> index % 2 != 0 }.map { it - '0' }.toMutableList()
        var pickShiftedBlock = false
        var shiftedBlockId = fileBlocks.lastIndex
        var checkSum = BigInteger.valueOf(0L)
        var currentId = 0
        var blockId = 0
        var blocksLeft = fileBlocks.size
        while (blocksLeft > 0) {
            if (pickShiftedBlock) {
                var memoryAvailable = true
                while (memoryAvailable && blocksLeft > 0) {
                    val memoryBlockId = blockId - 1
                    val block = fileBlocks[shiftedBlockId]
                    val memorySlotsAvailable = memoryBlocks[memoryBlockId]
                    checkSum +=
                        calculateBlockCheckSum(
                            shiftedBlockId,
                            block.coerceAtMost(memorySlotsAvailable),
                            currentId
                        )
                    if (memorySlotsAvailable >= block) {
                        fileBlocks[shiftedBlockId] = 0
                        currentId += block
                        memoryBlocks[memoryBlockId] = memorySlotsAvailable - block
                        shiftedBlockId--
                        blocksLeft--
                    } else {
                        fileBlocks[shiftedBlockId] -= memorySlotsAvailable
                        currentId += memorySlotsAvailable
                        memoryBlocks[memoryBlockId] = 0
                    }
                    memoryAvailable = memoryBlocks[memoryBlockId] != 0
                }
            } else {
                // if we have memory in the next slot to compact files from the end, otherwise continue building the checksum
                var nextMemorySlotIsWritable = false
                while (!nextMemorySlotIsWritable && blocksLeft > 0) {
                    val block = fileBlocks[blockId]
                    checkSum += calculateBlockCheckSum(blockId, block, currentId)
                    currentId += block
                    // if that the last memory slot it might be omitted so we want to check for that
                    if (blockId <= memoryBlocks.lastIndex && memoryBlocks[blockId] == 0) {
                        nextMemorySlotIsWritable = false
                    } else {
                        nextMemorySlotIsWritable = true
                    }
                    blockId++
                    blocksLeft--
                }
            }
            pickShiftedBlock = !pickShiftedBlock

        }
        kotlin.io.println()

        return checkSum
    }

    override fun partTwo(input: CharArray): BigInteger? {
        val maxPriority = 9
        val fragmentedMemory = LinkedHashSet<Pair<Int, Int>>()
        val fileBlocks = input.filterIndexed { index, _ -> index % 2 == 0 }.map { it - '0' }.toMutableList()
        val memoryBlocks = input.filterIndexed { index, _ -> index % 2 != 0 }.map { it - '0' }.toMutableList()
        // left to right approach where we were looking for file with according size to fit the memory
        // but that was wrong according to what was asked
//        val filePriority = mutableMapOf<Int, MutableList<Int>>()
//        for (index in fileBlocks.indices) {
//            if (index == 0) continue
//            var fitsInto = fileBlocks[index]
//            while (fitsInto <= maxPriority) {
//                filePriority.getOrPut(fitsInto) { mutableListOf() }.add(index)
//                fitsInto++
//            }
//        }
//        var fileIndex = 0
//        while (fragmentedMemory.size <= fileBlocks.size) {
//            if (!fragmentedMemory.contains(fileIndex to fileBlocks[fileIndex])) {
//                fragmentedMemory.add(fileIndex to fileBlocks[fileIndex])
//            }
//            val availableMemory = memoryBlocks[fileIndex]
//            fitFilesIntoAvailableMemory(fragmentedMemory, availableMemory, filePriority, fileBlocks)
//
//            fileIndex++
//        }
//        for (index in fileBlocks.indices) {
//            if (!fragmentedMemory.contains(index to fileBlocks[index])) {
//                fragmentedMemory.add(index to fileBlocks[index])
//            }
//            val availableMemory = memoryBlocks[index]
//            fitFilesIntoAvailableMemory(fragmentedMemory, availableMemory, filePriority, fileBlocks)
//        }
        // might be a better solution, was thinking about using queues
        // would need to remove from the memory queue from the end when the files queue reaches a certain point of intersection
        // UPD: ignored the idea with queues for now, will move to Hashmap with priorities again, but will shift to right to left approach
        val memorySlotsPriority = mutableMapOf<Int, LinkedHashSet<Int>>()
        // memory + priority map, would need to update it every time I fit a new item, hence hashset for linear removal time
        memoryBlocks.forEachIndexed { index, size ->
            var freeSpaceForFile = size
            while (freeSpaceForFile >= 1) {
                memorySlotsPriority.getOrPut(freeSpaceForFile) { LinkedHashSet() }.add(index)
                freeSpaceForFile--
            }
        }
        val movedFiles = mutableSetOf<Int>()
        val fittedMemory = MutableList<LinkedList<Int>>(memoryBlocks.size) { LinkedList() }
        //fitting/fragmentation logic
        for (fileIndex in fileBlocks.lastIndex downTo 1) {
            val memoryOccupiedByFile = fileBlocks[fileIndex]
            var memorySlotIndex = -1
            var appropriateSlot = memoryOccupiedByFile
            while (appropriateSlot <= 9) {
                if (!memorySlotsPriority.containsKey(appropriateSlot) || memorySlotsPriority[appropriateSlot]!!.isEmpty()) {
                    appropriateSlot++
                } else {
                    memorySlotIndex = memorySlotsPriority[appropriateSlot]!!.first()
                    break
                }
            }
            if (memorySlotIndex == -1 || memorySlotIndex >= fileIndex) continue
            fittedMemory[memorySlotIndex].add(fileIndex)
            movedFiles.add(fileIndex)
            val memoryLeftInSlot = memoryBlocks[memorySlotIndex] - memoryOccupiedByFile
            memoryBlocks[memorySlotIndex] = memoryLeftInSlot
            cleanupMemorySlots(memoryLeftInSlot, memorySlotsPriority, memorySlotIndex)
        }

        var checkSum = BigInteger.valueOf(0L)
        var currentId = 0
        for (fileIndex in 0..fileBlocks.lastIndex) {
            if (!movedFiles.contains(fileIndex)) {
                checkSum += calculateBlockCheckSum(fileIndex, fileBlocks[fileIndex], currentId)
            }
            currentId += fileBlocks[fileIndex]

            if (fileIndex <= fittedMemory.lastIndex) {
                for (fittedFileIndex in fittedMemory[fileIndex]) {
                    checkSum += calculateBlockCheckSum(fittedFileIndex, fileBlocks[fittedFileIndex], currentId)
                    currentId += fileBlocks[fittedFileIndex]
                }
                currentId += memoryBlocks[fileIndex]
            }
        }


        return checkSum
    }

    private fun cleanupMemorySlots(
        fittedMemory: Int,
        memorySlots: MutableMap<Int, LinkedHashSet<Int>>,
        memorySlotIndex: Int
    ) {
        for (index in 9 downTo fittedMemory + 1) {
            memorySlots[index]?.remove(memorySlotIndex)
        }
    }

    private fun fitFilesIntoAvailableMemory(
        fragmentedMemory: LinkedHashSet<Pair<Int, Int>>,
        availableMemory: Int,
        filePriority: MutableMap<Int, MutableList<Int>>,
        fileBlocks: MutableList<Int>
    ) {
        var matchingFileSize = availableMemory
        var memoryOccupied = 0
        while (matchingFileSize > 0 && memoryOccupied < availableMemory) {
            if (!filePriority.containsKey(matchingFileSize)) {
                matchingFileSize--
                continue
            }
            val matchingFile = filePriority[matchingFileSize]!!
            if (matchingFile.isEmpty()) continue
            val fileId = matchingFile.removeLast()
            matchingFileSize = fileBlocks[fileId]
            val fileToFillTheGap = fileId to matchingFileSize
            if (!fragmentedMemory.contains(fileToFillTheGap)) {
                fragmentedMemory.add(fileToFillTheGap)
            }
            memoryOccupied += matchingFileSize
            matchingFileSize = availableMemory - memoryOccupied
        }

    }

    private fun calculateBlockCheckSum(blockId: Int, block: Int, id: Int): BigInteger {
        var currentId = id
        var decreasingBlocks = block
        var checkSum = 0L
        while (decreasingBlocks != 0) {
            checkSum += blockId * currentId
            decreasingBlocks--
            currentId++
        }
        return BigInteger.valueOf(checkSum)
    }
}