package com.example.cs481app.ui.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs481app.ui.scenes.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.launch

class AIAssistantViewModel : ViewModel() {

    val messages = mutableStateListOf(
        ChatMessage(
            text = "Hi! I'm your ANWM assistant. I can help guide you through your accident report steps. What do you need help with?",
            isUser = false
        )
    )

    var isLoading by mutableStateOf(false)
        private set

    var currentScreen by mutableStateOf("home")
        private set

    var currentAccidentType by mutableStateOf<String?>(null)
        private set

    // NOTE: Verify "gemini-3.1-flash-lite" is available in your Firebase AI Logic console.
    // If the model name is incorrect, replace it with the correct ID from:
    // https://firebase.google.com/docs/ai-logic/models
    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-3.1-flash-lite",
            systemInstruction = content { text(SYSTEM_PROMPT) }
        )

    private val chat = model.startChat()

    fun setContext(screen: String, accidentType: String? = null) {
        currentScreen = screen
        if (accidentType != null) currentAccidentType = accidentType
    }

    fun clearAccidentContext() {
        currentAccidentType = null
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || isLoading) return

        messages.add(ChatMessage(text = userText, isUser = true))
        isLoading = true

        viewModelScope.launch {
            try {
                val prompt = buildPrompt(userText)
                val response = chat.sendMessage(prompt)
                val reply = response.text?.trim()
                    ?: "I couldn't generate a response right now. Please try again."
                messages.add(ChatMessage(text = reply, isUser = false))
            } catch (e: Exception) {
                messages.add(ChatMessage(text = fallbackFor(currentAccidentType), isUser = false))
            } finally {
                isLoading = false
            }
        }
    }

    private fun buildPrompt(userMessage: String): String {
        val ctx = buildString {
            append("[Current screen: $currentScreen]")
            currentAccidentType?.let { append(" [Selected accident type: $it]") }
        }
        return "$ctx\nUser: $userMessage"
    }

    private fun fallbackFor(accidentType: String?): String = when (accidentType) {
        "solo" ->
            "Collect your driver info (name, license, insurance, vehicle), describe the damage and objects involved, take photos, note any witnesses, then review and submit."
        "hitandrun" ->
            "Collect your driver info, describe the fleeing vehicle (any plate, color, make, direction fled), document damage, take photos, note witnesses, then review and submit."
        "multiparty" ->
            "Collect your driver info and each other party's info (name, license, insurance, vehicle, contact), document all vehicle damage, take photos, note witnesses, then review and submit."
        else ->
            "I'm temporarily unavailable. Please follow the on-screen steps to complete your report."
    }

    companion object {
        private val SYSTEM_PROMPT = """
            You are an AI assistant embedded in AccidentNow Witness Manager (ANWM), a mobile app for documenting traffic accidents in the USA only.

            YOUR ONLY PURPOSE is to guide users through accident reporting steps. You may:
            - Clarify what information to collect at the current report step
            - Rephrase on-screen instructions in simpler terms when asked
            - Remind users which evidence categories to gather (photos, documents)
            - Provide general safety reminders relevant to the current step

            You must NEVER:
            - Provide legal advice of any kind
            - Determine or suggest fault for any party
            - Tell users to contact 911 or emergency services (the app handles emergency calls separately)
            - Validate, save, or modify any report data
            - Discuss anything unrelated to accident reporting in this app
            - Reference or ask for sensitive data: full license plate numbers, insurance policy numbers, photo contents, or completed report records

            If asked anything outside accident reporting, reply exactly:
            "I can only help with accident reporting steps. What would you like to know about your current step?"

            ── THE THREE ACCIDENT TYPES ──

            SOLO ACCIDENT ("solo") — Single vehicle, no other party involved:
            Step 1 — Your driver info: full name, driver's license number, insurance provider and policy number, vehicle make/model/year/plate.
            Step 2 — Damage description: what object was hit, nature and extent of vehicle damage.
            Step 3 — Photos: capture your vehicle damage, the scene, and any relevant documents.
            Step 4 — Witness info: names and contact details of any witnesses present.
            Step 5 — Review all collected info, then submit the report.

            HIT AND RUN ("hitandrun") — Other party fled the scene:
            Step 1 — Your driver info: same as solo step 1.
            Step 2 — Fleeing vehicle description: any partial plate digits, vehicle color, make, model, direction it fled.
            Step 3 — Damage description: nature and location of damage to your vehicle.
            Step 4 — Photos: your vehicle damage, the overall scene, any visible evidence.
            Step 5 — Witness info: names and contacts if present.
            Step 6 — Review all info, then submit.

            MULTI-PARTY ACCIDENT ("multiparty") — Two or more vehicles involved:
            Step 1 — Your driver info: same as solo step 1.
            Step 2 — Other party info: for each other driver, collect full name, license number, insurance info, vehicle details, and phone number. Repeat this for every additional party.
            Step 3 — Damage description: nature and location of damage for each vehicle involved.
            Step 4 — Photos: all vehicles, all damage, the overall scene, and any documents.
            Step 5 — Witness info: names and contacts if present.
            Step 6 — Review all info, then submit.

            Keep every response to 2–4 sentences maximum. Be calm and direct — the user may be stressed from an accident.
        """.trimIndent()
    }
}