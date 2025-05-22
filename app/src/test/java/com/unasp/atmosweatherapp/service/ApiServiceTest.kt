package com.unasp.atmosweatherapp.service

import com.unasp.atmosweatherapp.model.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @Test
    fun `fazerLogin should make POST to correct endpoint`() {
        // Configura resposta mockada
        val mockResponse = MockResponse()
            .setBody("""{"token": "abc123"}""")
            .setHeader("Content-Type", "application/json")

        mockWebServer.enqueue(mockResponse)

        // Executa chamada
        val call = apiService.fazerLogin(LoginRequest("user@test.com", "senha123"))
        val response = call.execute()

        // Verifica
        assertEquals(200, response.code())
        assertEquals("/api/auth/login", mockWebServer.takeRequest().path)
        assertEquals("abc123", response.body()?.token)
    }

    @Test
    fun `getCurrentWeather should include city query parameter`() {
        val mockResponse = MockResponse()
            .setBody("""{"temp": 25.5, "condition": "Sunny"}""")

        mockWebServer.enqueue(mockResponse)

        val call = apiService.getCurrentWeather("São Paulo")
        call.execute()

        val request = mockWebServer.takeRequest()
        assertTrue(request.requestUrl?.queryParameter("city") == "São Paulo")
    }

    @Test
    fun `addFavoriteCity should include query parameters`() {
        mockWebServer.enqueue(MockResponse().setBody("""{"id": 1}"""))

        val call = apiService.addFavoriteCity("Rio de Janeiro", true)
        call.execute()

        val request = mockWebServer.takeRequest()
        assertEquals("Rio de Janeiro", request.requestUrl?.queryParameter("cityName"))
        assertEquals("true", request.requestUrl?.queryParameter("isDefault"))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}