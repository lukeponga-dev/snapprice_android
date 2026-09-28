package dev.lukeponga.pricesnap.model

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class AppraisalResponseTest {
    private val gson = Gson()

    private fun response(status: String, estimate: String, sources: String) = gson.fromJson(
        """{
          "ok":true,"status":"$status",
          "product":{"name":"Camera","brand":"Example","category":"Electronics","summary":"Used camera",
            "condition":{"score":7,"grade":"B","defects":[]}},
          "valuation":{"estimatedValue":$estimate,"lowEstimate":null,"highEstimate":null,"currency":"NZD"},
          "confidence":{"score":0.4,"percentage":40,"level":"LOW"},
          "evidence":{"sources":$sources},"market":{"best_platform":"Trade Me"},"warnings":[]
        }""".trimIndent(), AppraisalResponse::class.java
    )

    @Test fun noEvidenceIsUnpriced() {
        val result = response("insufficient_evidence", "null", "[]")
        assertFalse(result.isPriced)
        assertNull(result.valuation.estimatedValue)
        assertTrue(result.evidence.sources.isEmpty())
    }

    @Test fun evidenceBackedResultParsesCanonicalPrice() {
        val result = response("success", "250", """[{"title":"Used camera","priceNZD":250,"platform":"Trade Me","url":"https://example.com/item"}]""")
        assertTrue(result.isPriced)
        assertEquals(250.0, result.valuation.estimatedValue!!, 0.0)
        assertEquals("Trade Me", result.evidence.sources.single().platform)
    }
}
