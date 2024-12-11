package com.example.test

import java.math.BigInteger
import kotlin.math.pow

fun toBinary32(n: Long): String {
    return String.format("%32s", java.lang.Long.toBinaryString(n)).replace(" ", "0")
}

fun Skobka(a: Long): Long { // Используем Long для промежуточных вычислений
    var aSk = 0L // Используем Long для промежуточных вычислений
    val aBin = toBinary32(a)
    val aMas = mutableListOf<Long>()

    // Разделение бинарной строки на части по 8 бит
    for (i in aBin.indices step 8) {
        aMas.add(binaryToLong(aBin.substring(i, i + 8)))
    }

    // Преобразование в Long для промежуточных вычислений
    for (i in aMas.indices) {
        aSk += (aMas[i] * 256.0.pow(3 - i).toLong())
    }

    // Выполняем операцию сдвига в 32 бита и возвращаем результат как Long
    return aSk % (1L shl 32)
}

fun beltHash(X: String): String {
    val r = Skobka(splitAndReverse(X.length.toLong() * 4).toLong())
    var rBig = BigInteger.valueOf(r)
    var rBigStr = rBig.toString(16)
    while (rBigStr.length != 32) {
        rBigStr+="0"
        rBig = BigInteger(rBigStr, 16)
    }
    rBig = BigInteger(rBigStr, 16)

    var s = BigInteger.ZERO
    val h = "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D"
    val Xn = mutableListOf<String>()
    var tempX = X

    while (tempX.isNotEmpty()) {
        while (tempX.length % 64 != 0) {
            tempX += "0"
        }
        Xn.add(tempX.take(64))
        tempX = tempX.drop(64)
    }

    var t: String
    var hTemp = h
    for (xi in Xn) {
        t = beltCompress(xi + hTemp).first
        val temp = BigInteger(t, 16)
        s = s.xor(temp)
        hTemp = beltCompress(xi + hTemp).second
    }

    val Y = beltCompress(rBig.toString(16) + s.toString(16) + hTemp).second
    return Y
}

fun beltCompress(x: String): Pair<String, String> {
    var paddedX = x
    while (paddedX.length < 128) {
        paddedX = "0" + paddedX
    }

    val X1 = paddedX.substring(0, 32)
    val X2 = paddedX.substring(32, 64)
    val X3 = paddedX.substring(64, 96)
    val X4 = paddedX.substring(96, 128)

    val x1 = BigInteger(X1, 16)
    val x2 = BigInteger(X2, 16)
    val x3 = BigInteger(X3, 16)
    val x4 = BigInteger(X4, 16)

    val S1 = BigInteger(beltBlockEncrypt(x3.xor(x4).toString(16), X1 + X2), 16)
    val S = x3.xor(S1.xor(x4))
    val ed = BigInteger("1".repeat(128), 2)

    val xorN = S.xor(ed).toString(16)
    val T = BigInteger(beltBlockEncrypt(X2, xorN + X3), 16)
    val Y2 = x2.xor(T)

    val S1Final = BigInteger(beltBlockEncrypt(X1, S.toString(16) + X4), 16)
    val Y1 = S1Final.xor(x1)

    val Y1TXT = Y1.toString(16).padStart(32, '0')
    val Y2TXT = Y2.toString(16).padStart(32, '0')



    return Pair(S.toString(16), Y1TXT + Y2TXT)
}

fun beltBlockEncrypt(X: String, K: String): String {

    if(X.length>32){
        return blocksEncrypt(X, K)
    }
    var x = X.padStart(32, '0')
    var k = K.padStart(64, '0')

    val xParts = split(x, 4)
    val kParts = split(k, 8)

    var x1 = splitAndReverse(xParts[0])
    var x2 = splitAndReverse(xParts[1])
    var x3 = splitAndReverse(xParts[2])
    var x4 = splitAndReverse(xParts[3])

    val keySchedule = LongArray(56) { splitAndReverse(kParts[it % 8]) }

    var a = x1
    var b = x2
    var c = x3
    var d = x4
    var e: Long

    for (i in 0 until 8) {
        b = b xor g(squareSum(a, keySchedule[7 * (i + 1) - 6 - 1]), 5)
        c = c xor g(squareSum(d, keySchedule[7 * (i + 1) - 5 - 1]), 21)
        a = squareMinus(a, g(squareSum(b, keySchedule[7 * (i + 1) - 4 - 1]), 13))
        e = g(squareSum(b, squareSum(c, keySchedule[7 * (i + 1) - 3 - 1])), 21) xor (i.toLong() + 1)
        b = squareSum(b, e)
        c = squareMinus(c, e)
        d = squareSum(d, g(squareSum(c, keySchedule[7 * (i + 1) - 2 - 1]), 13))
        b = b xor g(squareSum(a, keySchedule[7 * (i + 1) - 1 - 1]), 21)
        c = c xor g(squareSum(d, keySchedule[7 * (i + 1) - 1]), 5)

        // Swap operations
        a = b.also { b = a }
        c = d.also { d = c }
        b = c.also { c = b }
    }

    return "${splitAndReverse(b).toString(16).padStart(8, '0')}" +
            "${splitAndReverse(d).toString(16).padStart(8, '0')}" +
            "${splitAndReverse(a).toString(16).padStart(8, '0')}" +
            "${splitAndReverse(c).toString(16).padStart(8, '0')}"
}
fun blocksEncrypt(X: String, K: String): String {
    val blocks = mutableListOf<String>()
    var x = X

    // Добавляем нули, чтобы длина X была кратна 32
    while (x.length % 32 != 0) {
        x += '0'
    }

    // Разделяем строку на блоки по 32 символа
    for (i in x.indices step 32) {
        blocks.add(x.substring(i, i + 32))
    }

    var result = ""
    for (block in blocks) {
        result += beltBlockEncrypt(block, K)
    }

    return result
}


fun blocksDecrypt(X: String, K: String): String {
    val blocks = mutableListOf<String>()
    var x = X

    // Добавляем нули, чтобы длина X была кратна 32
    while (x.length % 32 != 0) {
        x += '0'
    }

    // Разделяем строку на блоки по 32 символа
    for (i in x.indices step 32) {
        blocks.add(x.substring(i, i + 32))
    }

    var result = ""
    for (block in blocks) {
        result += beltBlockDecrypt(block, K)
    }

    return result
}



fun beltBlockDecrypt(X: String, K: String): String {

    if(X.length>32){
        return blocksDecrypt(X, K)
    }
    var x = X.padStart(32, '0')
    var k = K.padStart(64, '0')

    val xParts = split(x, 4)
    val kParts = split(k, 8)

    var x1 = splitAndReverse(xParts[0])
    var x2 = splitAndReverse(xParts[1])
    var x3 = splitAndReverse(xParts[2])
    var x4 = splitAndReverse(xParts[3])

    val keySchedule = LongArray(56) { splitAndReverse(kParts[it % 8]) }

    var a = x1
    var b = x2
    var c = x3
    var d = x4
    var e: Long

    for (i in 7 downTo 0) {
        b = b xor g(squareSum(a, keySchedule[7 * (i + 1) - 1]), 5)
        c = c xor g(squareSum(d, keySchedule[7 * (i + 1) - 1 - 1]), 21)
        a = squareMinus(a, g(squareSum(b, keySchedule[7 * (i + 1) - 2 - 1]), 13))
        e = g(squareSum(b, squareSum(c, keySchedule[7 * (i + 1) - 3 - 1])), 21) xor (i.toLong() + 1)
        b = squareSum(b, e)
        c = squareMinus(c, e)
        d = squareSum(d, g(squareSum(c, keySchedule[7 * (i + 1) - 4 - 1]), 13))
        b = b xor g(squareSum(a, keySchedule[7 * (i + 1) - 5 - 1]), 21)
        c = c xor g(squareSum(d, keySchedule[7 * (i + 1) - 6-1]), 5)

        // Swap operations
        a = b.also { b = a }
        c = d.also { d = c }
        a = d.also { d = a }
    }

    return "${splitAndReverse(c).toString(16).padStart(8, '0')}" +
            "${splitAndReverse(a).toString(16).padStart(8, '0')}" +
            "${splitAndReverse(d).toString(16).padStart(8, '0')}" +
            "${splitAndReverse(b).toString(16).padStart(8, '0')}"
}

// Utility functions
fun split(input: String, parts: Int): List<Long> =
    (0 until parts).map { input.substring(it * 8, (it + 1) * 8).toLong(16) }

fun splitAndReverse(n: Long): Long {
    // Преобразуем число в строку в шестнадцатеричном формате с ведущими нулями
    val hexStr = String.format("%08x", n)

    // Создаем список для хранения частей
    val parts = mutableListOf<String>()

    // Разбиваем строку на части по два символа, начиная с конца
    var i = hexStr.length
    while (i > 0) {
        parts.add(hexStr.substring(i - 2, i))
        i -= 2
    }

    // Собираем результат из частей
    var resultStr = ""
    for (part in parts) {
        resultStr += part
    }

    // Преобразуем строку обратно в число
    val resultLong = resultStr.toLong(16)

    // Возвращаем результат как целое число
    return resultLong
}

fun squareSum(a: Long, b: Long): Long =
    (a + b) % (1L shl 32)

fun squareMinus(a: Long, b: Long): Long =
    ((a - b + (1L shl 32)) % (1L shl 32))

fun g(u: Long, r: Int): Long {
    val uBin = u.toString(2).padStart(32, '0')
    val uParts = uBin.chunked(8).map { binaryToLong(it) }
    val transformed = uParts.joinToString("") { H[(it shr 4).toInt()][(it and 0xF).toInt()] }
    val b = rotHi(transformed.toLong(16), r)
    return b
}

fun rotHi(u: Long, n: Int): Long {
    var res = u
    for (i in 0 until n) {
        res = shHi(res, 1) xor shLo(res, 31)
    }
    return res % (1L shl 32)
}

fun shLo(u: Long, n: Int): Long {
    var result = u
    for (i in 0 until n) {
        result /= 2
    }
    return result % (1L shl 32)
}

fun shHi(u: Long, n: Int): Long {
    var result = u
    for (i in 0 until n) {
        result *= 2
    }
    return result % (1L shl 32)
}



fun binaryToLong(binary: String): Long =
    binary.toLong(2)

val H = arrayOf(
    arrayOf("B1", "94", "BA", "C8", "0A", "08", "F5", "3B", "36", "6D", "00", "8E", "58", "4A", "5D", "E4"),
    arrayOf("85", "04", "FA", "9D", "1B", "B6", "C7", "AC", "25", "2E", "72", "C2", "02", "FD", "CE", "0D"),
    arrayOf("5B", "E3", "D6", "12", "17", "B9", "61", "81", "FE", "67", "86", "AD", "71", "6B", "89", "0B"),
    arrayOf("5C", "B0", "C0", "FF", "33", "C3", "56", "B8", "35", "C4", "05", "AE", "D8", "E0", "7F", "99"),
    arrayOf("E1", "2B", "DC", "1A", "E2", "82", "57", "EC", "70", "3F", "CC", "F0", "95", "EE", "8D", "F1"),
    arrayOf("C1", "AB", "76", "38", "9F", "E6", "78", "CA", "F7", "C6", "F8", "60", "D5", "BB", "9C", "4F"),
    arrayOf("F3", "3C", "65", "7B", "63", "7C", "30", "6A", "DD", "4E", "A7", "79", "9E", "B2", "3D", "31"),
    arrayOf("3E", "98", "B5", "6E", "27", "D3", "BC", "CF", "59", "1E", "18", "1F", "4C", "5A", "B7", "93"),
    arrayOf("E9", "DE", "E7", "2C", "8F", "0C", "0F", "A6", "2D", "DB", "49", "F4", "6F", "73", "96", "47"),
    arrayOf("06", "07", "53", "16", "ED", "24", "7A", "37", "39", "CB", "A3", "83", "03", "A9", "8B", "F6"),
    arrayOf("92", "BD", "9B", "1C", "E5", "D1", "41", "01", "54", "45", "FB", "C9", "5E", "4D", "0E", "F2"),
    arrayOf("68", "20", "80", "AA", "22", "7D", "64", "2F", "26", "87", "F9", "34", "90", "40", "55", "11"),
    arrayOf("BE", "32", "97", "13", "43", "FC", "9A", "48", "A0", "2A", "88", "5F", "19", "4B", "09", "A1"),
    arrayOf("7E", "CD", "A4", "D0", "15", "44", "AF", "8C", "A5", "84", "50", "BF", "66", "D2", "E8", "8A"),
    arrayOf("A2", "D7", "46", "52", "42", "A8", "DF", "B3", "69", "74", "C5", "51", "EB", "23", "29", "21"),
    arrayOf("D4", "EF", "D9", "B4", "3A", "62", "28", "75", "91", "14", "10", "EA", "77", "6C", "DA", "1D")
)

fun stringToHex(input: String): String {
    return input.toByteArray(Charsets.UTF_8).joinToString("") { byte -> "%02x".format(byte) }
}

fun hexToString(hex: String): String {
    val bytes = ByteArray(hex.length / 2)
    for (i in 0 until hex.length step 2) {
        bytes[i / 2] = hex.substring(i, i + 2).toInt(16).toByte()
    }
    return String(bytes, Charsets.UTF_8)
}


