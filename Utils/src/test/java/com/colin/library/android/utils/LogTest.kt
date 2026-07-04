package com.colin.library.android.utils

import android.util.Log as AndroidLog
import com.colin.library.android.utils.config.UtilConfig
import com.colin.library.android.utils.helper.UtilHelper
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LogTest {

    private val mockConfig = mockk<UtilConfig>()

    @Before
    fun setUp() {
        mockkStatic(AndroidLog::class)
        mockkObject(UtilHelper)
        
        every { UtilHelper.getUtilConfig() } returns mockConfig
        every { mockConfig.isShowLog() } returns true
        every { mockConfig.getLogLevel() } returns AndroidLog.VERBOSE
        every { mockConfig.getLogTag() } returns "TestTag"

        // Mock Android Log methods to return 0
        every { AndroidLog.v(any(), any()) } returns 0
        every { AndroidLog.d(any(), any()) } returns 0
        every { AndroidLog.i(any(), any()) } returns 0
        every { AndroidLog.w(any(), any()) } returns 0
        every { AndroidLog.e(any(), any()) } returns 0
        every { AndroidLog.wtf(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testVerboseLog() {
        Log.v("test message")
        verify { AndroidLog.v("TestTag", "test message") }
    }

    @Test
    fun testDebugLogWithTag() {
        Log.d("test message", "CustomTag")
        verify { AndroidLog.d("CustomTag", "test message") }
    }

    @Test
    fun testLogDisabled() {
        every { mockConfig.isShowLog() } returns false
        val result = Log.v("should not log")
        assertEquals(INVALID, result)
        verify(exactly = 0) { AndroidLog.v(any(), any()) }
    }

    @Test
    fun testLogLevelFiltered() {
        every { mockConfig.getLogLevel() } returns AndroidLog.INFO
        val result = Log.d("debug message")
        assertEquals(INVALID, result)
        verify(exactly = 0) { AndroidLog.d(any(), any()) }
        
        Log.i("info message")
        verify { AndroidLog.i("TestTag", "info message") }
    }

    @Test
    fun testAutomaticTag() {
        // When tag is null in config and null in call, it should use stack trace
        every { mockConfig.getLogTag() } returns null
        Log.v("test message", null)
        // Verify that some tag was generated (usually containing the file name)
        verify { AndroidLog.v(match { it.contains("LogTest.kt") || it.contains(":") }, "test message") }
    }

    @Test
    fun testJsonLog() {
        mockkObject(FormatUtil)
        val json = "{\"name\":\"test\"}"
        val formattedJson = "{\n    \"name\": \"test\"\n}"
        every { FormatUtil.formatJson(json) } returns formattedJson
        
        Log.json(json)
        
        verify { AndroidLog.i("TestTag", formattedJson) }
    }

    @Test
    fun testXmlLog() {
        mockkObject(FormatUtil)
        val xml = "<root><item>test</item></root>"
        val formattedXml = "<root>\n    <item>test</item>\n</root>"
        every { FormatUtil.formatXml(xml) } returns formattedXml
        
        Log.xml(xml)
        
        verify { AndroidLog.i("TestTag", formattedXml) }
    }

    @Test
    fun testThrowableLog() {
        val throwable = RuntimeException("Test Error")
        mockkStatic(AndroidLog::class)
        every { AndroidLog.getStackTraceString(throwable) } returns "stacktrace"
        
        Log.log(throwable)
        
        verify { AndroidLog.e("TestTag", "stacktrace") }
    }
}
