package ua.at.tsvetkov.util.logger.okhttp3

import android.annotation.SuppressLint
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ua.at.tsvetkov.util.logger.Log
import ua.at.tsvetkov.util.logger.LogLong
import java.io.IOException

class Okhttp3Interceptor : Interceptor {

    val maxLogLength = 3000

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()
        try {
            Buffer().use { requestBuffer ->
                val body = request.body
                body?.writeTo(requestBuffer)
                var requestContentTypeLog = ""
                var requestContentLengthLog = ""
                if (body != null) {
                    if (body.contentType() != null) {
                        requestContentTypeLog = "Content-Type: ${body.contentType()}".trimIndent()
                    }
                    if (body.contentLength() != -1L) {
                        requestContentLengthLog = "Content-Length: ${body.contentLength()}".trimIndent()
                    }
                }
                Log.v(
                    ("--> Sending ${request.method} request ${request.url}\n" +
                            getHeadersLog(request) +
                            getContentLog(requestContentTypeLog) +
                            getContentLog(requestContentLengthLog) +
                            getBodyLog(requestBuffer, requestContentTypeLog))
                        .trimIndent()
                )
            }
        } catch (e: Exception) {
            Log.e(e)
        }

        val response = chain.proceed(request)
        val logHeader = getLogHeader(response, t1)
        val contentType = response.body.contentType()
        val content = response.body.string()

        if (contentType.toString().startsWith("application/json") && content.isNotBlank()) {
            if (content.startsWith("[")) {
                try {
                    val array = JSONArray(content)
                    if (content.length > maxLogLength)
                        LogLong.v(
                            (logHeader +
                                    "-------------------------- Body (Json) -------------------------\n" +
                                    array.toString(2)).trimIndent()
                        )
                    else {
                        Log.v(
                            (logHeader +
                                    "-------------------------- Body (Json) -------------------------\n" +
                                    array.toString(2)).trimIndent()
                        )
                    }
                } catch (e: JSONException) {
                    Log.e(e)
                }
            } else {
                try {
                    val obj = JSONObject(content)
                    if (content.length > maxLogLength)
                        LogLong.v(
                            (logHeader +
                                    "-------------------------- Body (Json) -------------------------\n" +
                                    obj.toString(2)).trimIndent()
                        )
                    else
                        Log.v(
                            (logHeader +
                                    "-------------------------- Body (Json) -------------------------\n" +
                                    obj.toString(2)).trimIndent()
                        )
                } catch (e: JSONException) {
                    Log.e(e)
                }
            }
        } else {
            if (content.length > maxLogLength)
                LogLong.v(
                    (logHeader +
                            "-------------------------- Body (Text/Html) -------------------------\n" +
                            content).trimIndent()
                )
            else
                Log.v(
                    (logHeader +
                            "-------------------------- Body (Text/Html) -------------------------\n" +
                            content).trimIndent()
                )
        }
        val wrappedBody = content.toResponseBody(contentType)
        return response.newBuilder().body(wrappedBody).build()
    }

    private fun getBodyLog(requestBuffer: Buffer, requestContentTypeLog: String): String {
        val body = requestBuffer.readUtf8()
        return if (body.isBlank()) {
            "[ no body ]"
        } else {
            if (requestContentTypeLog.contains("application/json")) {
                "-------------------------- Body (Json) -------------------------\n" +
                if (body.startsWith("[")) {
                    JSONArray(body).toString(2).trimIndent()
                } else {
                    JSONObject(body).toString(2).trimIndent()
                }
            } else {
                "-------------------------- Body (Text/Html) -------------------------\n${body}"
            }
        }
    }

    private fun getContentLog(log: String): String {
        return if (log.isBlank()) {
            log
        } else {
            "$log\n"
        }
    }

    private fun getHeadersLog(request: Request): String {
        val headers = request.headers
        return if (headers.size == 0) {
            "[ no headers ]\n"
        } else {
            "[Headers:${headers.size}]\n${headers}[---]\n"
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getLogHeader(response: Response, time: Long) =
        "<-- Received response for ${response.request.method} ${response.request.url}\n" +
                "HTTP Code: ${response.code}\n" +
                "Execution time: ${(System.nanoTime() - time) / 1e6}ms\n" +
                "${response.headers}"

}
