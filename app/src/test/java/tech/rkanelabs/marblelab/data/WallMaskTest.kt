package tech.rkanelabs.marblelab.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WallMaskTest {

    @Test
    fun `none has no walls`() {
        assertFalse(WallMask.None.has(WallMask.Up))
        assertFalse(WallMask.None.has(WallMask.Right))
        assertFalse(WallMask.None.has(WallMask.Down))
        assertFalse(WallMask.None.has(WallMask.Left))
    }

    @Test
    fun `single masks report their own side`() {
        assertTrue(WallMask.Up.has(WallMask.Up))
        assertFalse(WallMask.Up.has(WallMask.Right))

        assertTrue(WallMask.Right.has(WallMask.Right))
        assertFalse(WallMask.Right.has(WallMask.Down))
    }

    @Test
    fun `combined masks include each component`() {
        val combo = WallMask.Up.with(WallMask.Left)
        assertTrue(combo.has(WallMask.Up))
        assertTrue(combo.has(WallMask.Left))
        assertFalse(combo.has(WallMask.Down))
    }

    @Test
    fun `all mask includes every side`() {
        assertTrue(WallMask.All.has(WallMask.Up))
        assertTrue(WallMask.All.has(WallMask.Right))
        assertTrue(WallMask.All.has(WallMask.Down))
        assertTrue(WallMask.All.has(WallMask.Left))
    }

    @Test
    fun `has matches bitwise contract for every combination`() {
        val masks = (0 until 16).map(::WallMask)

        masks.forEach { subject ->
            masks.forEach { query ->
                val expected = (subject.bits and query.bits) != 0
                assertEquals(
                    "Mask ${subject.bits.toString(2)} has ${query.bits.toString(2)}",
                    expected,
                    subject.has(query)
                )
            }
        }
    }
}
