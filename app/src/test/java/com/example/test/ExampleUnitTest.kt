package com.example.test

import org.junit.Test
import org.junit.Assert.*

class BeltCryptographyTest {

    @Test
    fun testBeltBlockEncrypt() {
        val keyEnc = "E9DEE72C8F0C0FA62DDB49F46F73964706075316ED247A3739CBA38303A98BF6"
        val xEnc = "B194BAC80A08F53B366D008E584A5DE4"
        val expected = "69CCA1C93557C9E3D66BC3E0FA88FA6E".lowercase()
        val actual = beltBlockEncrypt(xEnc, keyEnc).lowercase()

        assertEquals("Test failed for BeltBlockEncrypt", expected, actual)
    }

    @Test
    fun testBeltBlockDecrypt() {
        val keyEnc = "92BD9B1CE5D141015445FBC95E4D0EF2682080AA227D642F2687F93490405511"
        val xEnc = "E12BDC1AE28257EC703FCCF095EE8DF1"
        val expected = "0DC5300600CAB840B38448E5E993F421".lowercase()
        val actual = beltBlockDecrypt(xEnc, keyEnc).lowercase()

        assertEquals("Test failed for BeltBlockDecrypt", expected, actual)
    }

    @Test
    fun testBeltHash() {
        val x1 = "B194BAC80A08F53B366D008E58"
        val expectedY1 = "ABEF9725D4C5A83597A367D14494CC2542F20F659DDFECC961A3EC550CBA8C75".lowercase()
        val actualY1 = beltHash(x1).lowercase()
        assertEquals("Test failed for input x1", expectedY1, actualY1)

        val x2 = "B194BAC80A08F53B366D008E584A5DE48504FA9D1BB6C7AC252E72C202FDCE0D5BE3D61217B96181FE6786AD716B890B"
        val expectedY2 = "9D02EE446FB6A29FE5C982D4B13AF9D3E90861BC4CEF27CF306BFB0B174A154A".lowercase()
        val actualY2 = beltHash(x2).lowercase()
        assertEquals("Test failed for input x2", expectedY2, actualY2)
    }

}
