package com.example.housebuddy.data.remote

import kotlin.test.Test
import kotlin.test.assertEquals

class SdmxGenericDataParserTest {
    @Test
    fun parseObservations_extractsPeriodAndValue() {
        val xml = """
            <generic:Obs>
            <generic:ObsDimension value="1999-01-04"/>
            <generic:ObsValue value="1.1789"/>
            </generic:Obs>
            <generic:Obs>
            <generic:ObsDimension value="1999-01-05"/>
            <generic:ObsValue value="1.179"/>
            </generic:Obs>
        """.trimIndent()

        val observations = SdmxGenericDataParser.parseObservations(xml)

        assertEquals(2, observations.size)
        assertEquals("1999-01-04", observations[0].period)
        assertEquals(1.1789, observations[0].value)
        assertEquals("1999-01-05", observations[1].period)
        assertEquals(1.179, observations[1].value)
    }
}
