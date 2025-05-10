    package com.madlab.aichatbot

    import android.os.Bundle
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.activity.enableEdgeToEdge
    import android.widget.*
    import com.google.android.material.floatingactionbutton.FloatingActionButton
    import okhttp3.*
    import com.google.gson.*
    import okhttp3.MediaType.Companion.toMediaTypeOrNull
    import android.view.LayoutInflater
    import android.view.View
    import android.widget.*
    import okhttp3.*
    import com.google.gson.*
    import okhttp3.MediaType.Companion.toMediaTypeOrNull
    import java.io.IOException

    class MainActivity : AppCompatActivity() {
        private val apiKey = "AIzaSyCISuC959rgGWuQeGjBWUGb7DK2eU0TARM"
        private lateinit var chatContainer: LinearLayout
        private lateinit var userInputEditText: EditText
        private lateinit var scrollView: ScrollView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            chatContainer = findViewById(R.id.chatContainer)
            userInputEditText = findViewById(R.id.userInputEditText)
            scrollView = findViewById(R.id.scrollView)
            val sendButton: FloatingActionButton = findViewById(R.id.sendButton)

            sendButton.setOnClickListener {
                val userText = userInputEditText.text.toString()
                if (userText.isNotEmpty()) {
                    addMessageToChat(userText, true) // true for user message
                    userInputEditText.text.clear()
                    sendToGemini(userText)
                }
            }
        }

        private fun addMessageToChat(message: String, isUser: Boolean): View {
            val inflater = LayoutInflater.from(this)
            val bubbleView = inflater.inflate(R.layout.layout_message_bubble, chatContainer, false)

            val bubbleLayout = bubbleView.findViewById<LinearLayout>(R.id.bubbleLayout)
            val messageText = bubbleView.findViewById<TextView>(R.id.messageText)

            messageText.text = message

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            if (isUser) {
                bubbleLayout.setBackgroundResource(R.drawable.bubble_user)
                layoutParams.gravity = android.view.Gravity.END
            } else {
                bubbleLayout.setBackgroundResource(R.drawable.bubble_bot)
                layoutParams.gravity = android.view.Gravity.START
            }

            bubbleView.layoutParams = layoutParams
            chatContainer.addView(bubbleView)
            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }

            return bubbleView
        }

        private fun sendToGemini(userText: String) {
            val typingMessage = addMessageToChat("Typing...", false)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-04-17:generateContent?key=$apiKey"

            val json = """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "$userText"
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        chatContainer.removeView(typingMessage)
                        addMessageToChat("Error: ${e.message}", false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val gson = Gson()
                    val result = gson.fromJson(body, GeminiResponse::class.java)
                    val reply = result.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No reply"

                    runOnUiThread {
                        chatContainer.removeView(typingMessage)
                        addMessageToChat(reply, false)
                    }
                }
            })
        }

        data class GeminiResponse(
            val candidates: List<Candidate>?
        )

        data class Candidate(
            val content: Content
        )

        data class Content(
            val parts: List<Part>
        )

        data class Part(
            val text: String
        )
    }