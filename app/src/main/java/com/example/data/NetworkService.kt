package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

// --- Common Data Classes ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content
)

// --- Retrofit Setup ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiAssistant {
    private const val SYSTEM_PROMPT = """
        You are the official AI representative for "Creative Team Production", a world-class premium photography, videography, content creation, and social media marketing agency based in Indore, Madhya Pradesh, India.
        Founder: Mohit Dewda
        Contact/WhatsApp: +91 9755550380
        Email: creative.team.production.official@gmail.com
        Instagram: https://www.instagram.com/creative_team_production_
        
        Our core services include:
        - 🎬 Reel Shoots (Instagram, Brand, Commercial, Car Reels)
        - 📸 Photography (Commercial, Product, Portrait, Social Media)
        - 🎙 Podcast Production (Recording, multi-cam setups, edit, clips)
        - 🎥 YouTube Production (Shoots, vlogs, business/educational videos)
        - 📦 Product Shoots (Commercial Ads, macro product videography)
        - 🎞 Animation Videos (Product, brand animation)
        - 📢 Social Media Marketing (Strategy, content planning, brand growth)

        Your goal:
        - Act as a professional representative.
        - Understand client requirements.
        - Suggest services and creative packages:
          * Starter Pack: Perfect for upcoming creators (Reels/Photos setup)
          * Creator Pro Pack: Production grade audio/video, active content strategy
          * Brand Cinematic: Ultra high-end cameras, 3D elements, premium commercial grade
        - Suggest viral Reel Ideas, monthly content planning grids, hashtag recommendations, and marketing strategies.
        - Guide clients to book a shoot within the app or contact Mohit Dewda directly on WhatsApp (+91 9755550380).
        - Maintain an incredibly premium, luxury, modern, editorial, and elite agency tone. Never sound like a cheap freelancer.
    """

    suspend fun consult(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey == "MY_GEMINI_API_KEY" || apiKey.isEmpty()) {
            return@withContext "API Configuration Notice: Please configure your GEMINI_API_KEY in the AI Studio Secrets panel. This premium representative is running in offline intelligence mode and recommends contacting Mohit Dewda directly on WhatsApp (+91 9755550380) to guide your luxury production!"
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = SYSTEM_PROMPT))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "I am here to elevate your ideas. Can you tell me more about your brand or production requirements in Indore?"
        } catch (e: Exception) {
            Log.e("GeminiAssistant", "Error calling Gemini", e)
            "Our creative servers are highly active. Mohit Dewda is available at +91 9755550380 (WhatsApp) to discuss your elite commercial shoot directly! Direct error: ${e.localizedMessage}"
        }
    }
}
