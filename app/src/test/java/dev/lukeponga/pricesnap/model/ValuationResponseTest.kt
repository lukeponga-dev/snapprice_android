package dev.lukeponga.pricesnap.model

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class ValuationResponseTest {
    private val gson = Gson()

    @Test
    fun testValuationResponseParsing() {
        val json = """
            {
              "item": {
                "name": "iPhone 13 Pro",
                "brand": "Apple",
                "model": "13 Pro",
                "category": "Phones",
                "attributes": {
                  "storage": "256GB"
                }
              },
              "condition": {
                "grade": "Excellent",
                "score": 90,
                "notes": ["No scratches", "Like new"]
              },
              "valuation": {
                "currency": "NZD",
                "estimatedValue": 1050.0,
                "low": 1000.0,
                "high": 1100.0,
                "method": "market_evidence"
              },
              "confidence": {
                "score": 95,
                "level": "high"
              },
              "comparables": [
                {
                  "title": "iPhone 13 Pro 256GB Trade Me",
                  "price": 1020.0,
                  "currency": "NZD",
                  "source": "Trade Me",
                  "url": "https://trademe.co.nz/123"
                }
              ],
              "generatedAt": "2026-09-28T12:00:00Z"
            }
        """.trimIndent()

        val response = gson.fromJson(json, ValuationResponse::class.java)
        
        assertNotNull(response)
        assertEquals("iPhone 13 Pro", response.item.name)
        assertEquals("Apple", response.item.brand)
        assertEquals("13 Pro", response.item.model)
        assertEquals("Phones", response.item.category)
        assertEquals("256GB", response.item.attributes["storage"])

        assertEquals("Excellent", response.condition.grade)
        assertEquals(90, response.condition.score)
        assertEquals(2, response.condition.notes.size)

        assertEquals("NZD", response.valuation.currency)
        assertEquals(1050.0, response.valuation.estimatedValue, 0.0)
        assertEquals(1000.0, response.valuation.low, 0.0)
        assertEquals(1100.0, response.valuation.high, 0.0)
        assertEquals("market_evidence", response.valuation.method)

        assertEquals(95, response.confidence.score)
        assertEquals("high", response.confidence.level)

        assertEquals(1, response.comparables.size)
        assertEquals("iPhone 13 Pro 256GB Trade Me", response.comparables[0].title)
        assertEquals(1020.0, response.comparables[0].price, 0.0)
        assertEquals("NZD", response.comparables[0].currency)
        assertEquals("Trade Me", response.comparables[0].source)
        assertEquals("https://trademe.co.nz/123", response.comparables[0].url)

        assertEquals("2026-09-28T12:00:00Z", response.generatedAt)
    }
}
