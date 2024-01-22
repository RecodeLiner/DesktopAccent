import org.junit.Assert.assertArrayEquals
import kotlin.test.Test

internal class Test {
    @Test
    fun testConvert() {
        val actualString = "0xff2311e8"
        val expected = intArrayOf(255,35,17,232)
        assertArrayEquals(expected, getRGBA(actualString))
    }
}