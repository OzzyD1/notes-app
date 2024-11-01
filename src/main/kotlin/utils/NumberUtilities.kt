package utils

fun validRange(numberToCheck: Int, min: Int, max: Int): Boolean {
    return numberToCheck in min..max
}

fun validRangeDouble(numberToCheck: Double, min: Double, max: Double): Boolean {
    return numberToCheck in min..max
}