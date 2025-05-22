package com.unasp.atmosweatherapp

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import com.unasp.atmosweatherapp.service.RetrofitClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit

abstract class BaseApiTest {
    protected lateinit var mockWebServer: MockWebServer
    protected val idlingResource = CountingIdlingResource("ApiCall")

    @Before
    open fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        IdlingRegistry.getInstance().register(idlingResource)

        // Configura o Retrofit para usar o MockWebServer
        RetrofitClient.setBaseUrl(mockWebServer.url("/").toString())

        // Configura o IdlingResource no Retrofit
        RetrofitClient.setIdlingResource(idlingResource)
    }

    @After
    open fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(idlingResource)
        RetrofitClient.clearIdlingResource()
    }

    protected fun enqueueMockResponse(
        code: Int = 200,
        body: String = "{}",
        delayMs: Long = 0
    ) {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(code)
                .setBody(body)
                .setBodyDelay(delayMs, TimeUnit.MILLISECONDS)
        )
    }
}