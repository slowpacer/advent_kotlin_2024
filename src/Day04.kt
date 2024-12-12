fun main() {
    Day03().execute()
}

class Day03 : ContestDay<String, Long>("Day03") {

    private val allowed = "do()"
    private val disallowed = "don't()"

    override fun partOne(input: String): Long? {
        val potentialMultiplications = input.split("mul")
        var product = 0L
        for (i in 1..potentialMultiplications.lastIndex) {
            val inputs = potentialMultiplications[i].split(",")
            if (inputs.size == 1) {
                continue
            }
            val firstNumber = if (inputs[0][0] == '(') inputs[0].substring(1).toLongOrNull() ?: 0 else 0
            val secondNumber = inputs[1].substringBefore(")").toLongOrNull() ?: 0
            product += firstNumber * secondNumber
        }
        return product

    }

    override fun partTwo(input: String): Long? {
        val potentialMultiplications = input.split("mul")
        var product = 0L
        var operationAllowed = true
        for (i in 0..potentialMultiplications.lastIndex) {
            val inputs = potentialMultiplications[i].split(",")
            if (inputs[0].contains(allowed) || inputs[0].contains(disallowed)) {
                operationAllowed = inputs[0].contains(allowed) || !inputs[0].contains(disallowed)
            }
            if (inputs.size == 1) {
                continue
            }
            val firstNumber = if (inputs[0][0] == '(') inputs[0].substring(1).toLongOrNull() ?: 0 else 0
            val secondNumber = inputs[1].substringBefore(")").toLongOrNull() ?: 0
            if (operationAllowed) {
                product += firstNumber * secondNumber
            }
            val allowedIndex = potentialMultiplications[i].lastIndexOf(allowed)
            val disallowedIndex = potentialMultiplications[i].lastIndexOf(disallowed)
            if (allowedIndex != -1 || disallowedIndex != -1) {
                operationAllowed = allowedIndex > disallowedIndex
            }
        }
        return product
    }

    override fun transformInput(input: List<String>): String {
        return input.joinToString()
    }
}