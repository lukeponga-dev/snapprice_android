package dev.lukeponga.pricesnap.network

import com.google.gson.Gson
import dev.lukeponga.pricesnap.model.ValuationRequest
import dev.lukeponga.pricesnap.model.ValuationResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class AppraisalRepositoryTest {
    private class FakeService : PriceSnapApiService {
        var request: ValuationRequest? = null
        var cancel = false
        var result: Response<ValuationResponse> = Response.success(Gson().fromJson("""
            {"ok":true,"status":"insufficient_evidence","warnings":[],
             "item":{"name":"Chair","category":"Furniture"},
             "condition":{"grade":"Good","score":80,"notes":[]},
             "valuation":{"currency":"NZD","estimatedValue":null,"low":null,"high":null},
             "confidence":{"score":0,"level":"low"},"comparables":[],"generatedAt":"2026-09-29"}
        """, ValuationResponse::class.java))
        override suspend fun ping(): Response<PingResponse> = Response.success(PingResponse("ok", "pricesnap-backend"))
        override suspend fun connection(): Response<ConnectionResponse> = Response.success(
            ConnectionResponse(true, EngineStatus("configured", true, "gemini-flash-latest", "internal-1.0.0", false, null))
        )
        override suspend fun valuate(request: ValuationRequest): Response<ValuationResponse> {
            if (cancel) throw CancellationException("cancelled")
            this.request = request
            return result
        }
    }

    @Test fun sendsCanonicalRequestAndAcceptsUnpricedResult() = runTest {
        val service = FakeService()
        val repository = AppraisalRepository(service)
        assertTrue(repository.checkConnection().isSuccess)
        val result = repository.analyzeImage("data:image/png;base64,aGVsbG8=").getOrThrow()
        assertEquals(ValuationRequest("aGVsbG8=", "image/png"), service.request)
        assertFalse(result.hasUsablePrice)
        assertNull(result.valuation.estimatedValue)
    }

    @Test fun acceptsHeuristicResultsFromServer() = runTest {
        val service = FakeService()
        service.result = Response.success(service.result.body()!!.copy(status = "heuristic"))
        val result = AppraisalRepository(service).analyzeImage("aGVsbG8=").getOrThrow()
        assertTrue(result.isHeuristic)
        assertFalse(result.hasUsablePrice)
    }

    @Test fun parsesProductionPingResponse() {
        val ping = Gson().fromJson(
            """{"status":"ok","service":"pricesnap-backend","timestamp":1790819863643}""",
            PingResponse::class.java
        )
        assertTrue(ping.ok)
        assertEquals("pricesnap-backend", ping.service)
    }

    @Test fun decodesSafeBackendErrorCode() = runTest {
        val service = FakeService()
        service.result = Response.error(429,
            """{"code":"PROVIDER_RATE_LIMIT","error":"provider secret"}""".toResponseBody("application/json".toMediaType()))
        val error = AppraisalRepository(service).analyzeImage("aGVsbG8=").exceptionOrNull()
        assertEquals("PriceSnap is busy. Please try again shortly.", error?.message)
    }

    @Test fun cancellationIsNotConvertedToFailure() = runTest {
        val service = FakeService().apply { cancel = true }
        try {
            AppraisalRepository(service).analyzeImage("aGVsbG8=")
            fail("Expected cancellation")
        } catch (_: CancellationException) { }
    }
}
