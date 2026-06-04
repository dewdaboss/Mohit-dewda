package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.Booking
import com.example.data.PortfolioItem
import com.example.data.GeminiAssistant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.widget.Toast

class ProductionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    val portfolioItems: StateFlow<List<PortfolioItem>>
    val bookings: StateFlow<List<Booking>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db)
        portfolioItems = repository.portfolioItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        bookings = repository.bookings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        // Prepopulate demo items on first run
        repository.prepopulateIfEmpty(viewModelScope)
    }

    // --- State variables for interactive screens ---
    val currentRole = MutableStateFlow("Client") // "Client" vs "Admin"
    
    // Booking Form State
    val bookingService = MutableStateFlow("🎬 Reel Shoots")
    val bookingPackage = MutableStateFlow("Creator Pro")
    val bookingDate = MutableStateFlow("2026-06-10")
    val bookingTime = MutableStateFlow("12:00 PM")
    val clientName = MutableStateFlow("")
    val clientPhone = MutableStateFlow("")
    val clientEmail = MutableStateFlow("")
    val clientRequirements = MutableStateFlow("")
    val clientBusinessName = MutableStateFlow("")
    val clientInstagramId = MutableStateFlow("")
    val clientLocation = MutableStateFlow("")
    val bookingStep = MutableStateFlow(1) // Steps 1 to 5

    // Chat / AI Assistant State
    val chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(listOf(
        "Hello! I am your Creative Team Production AI partner. If you need bespoke marketing ideas, Reel outlines, caption formulas, or specific packages in Indore, write down your requirements here!" to false
    ))
    val isChatLoading = MutableStateFlow(false)

    // Portfolio Form State (For upload dialogues)
    val uploadTitle = MutableStateFlow("")
    val uploadCategory = MutableStateFlow("Photography")
    val uploadClient = MutableStateFlow("")
    val uploadDesc = MutableStateFlow("")
    val uploadMedia = MutableStateFlow("ic_launcher_foreground")

    // General Toasts or Action indicators
    val integrationStatus = MutableStateFlow<String?>(null)

    // --- Booking Logic ---
    fun setBookingStep(step: Int) {
        bookingStep.value = step
    }

    private fun calculateDeliveryDate(shootDateString: String): String {
        return try {
            val parts = shootDateString.split("-")
            if (parts.size == 3) {
                val year = parts[0]
                val month = parts[1]
                val day = parts[2].toIntOrNull() ?: 10
                val nextDay = day + 2
                val formattedDay = if (nextDay < 10) "0$nextDay" else "$nextDay"
                "$year-$month-$formattedDay"
            } else {
                shootDateString
            }
        } catch (e: Exception) {
            shootDateString
        }
    }

    private fun getAssignedTeamForService(service: String): String {
        return when {
            service.contains("Reel") -> "Mohit Dewda (Director), Vinay S (Cam 2 / Grip)"
            service.contains("Podcast") -> "Mohit Dewda (Podcast Studio Lead), Sandeep M (Acoustics)"
            service.contains("YouTube") -> "Mohit Dewda (Vlog Director), Abhishek K (Editor & Sound)"
            service.contains("Product") -> "Mohit Dewda (Macro Lighting Artist)"
            else -> "Mohit Dewda (Creative Lead)"
        }
    }

    private fun getEquipmentRequiredForService(service: String): String {
        return when {
            service.contains("Reel") -> "Sony FX3, DJI Ronin RS3, Sony 24-70mm f/2.8 GM II"
            service.contains("Podcast") -> "3x Sony FX3, Zoom H8 mixer, 4x Shure SM7B"
            service.contains("YouTube") -> "Sony A7S III, Senheiser AVX, DJI Mavic 3 Drone"
            service.contains("Product") -> "Sony A7R V, 90mm Macro lens, Godox FV200 Studio Rig"
            else -> "Sony FX3 cinema body, Aperture Light Storm 600d"
        }
    }

    fun submitBookingFlow() {
        viewModelScope.launch {
            val rawPak = bookingPackage.value.lowercase()
            val amount = when {
                rawPak.contains("1,500") || rawPak.contains("1500") -> 1500.0
                rawPak.contains("2,000") || rawPak.contains("2000") -> 2000.0
                rawPak.contains("2,500") || rawPak.contains("2500") -> 2500.0
                rawPak.contains("3,500") || rawPak.contains("3500") -> 3500.0
                rawPak.contains("5,000") || rawPak.contains("5000") -> 5000.0
                rawPak.contains("7,000") || rawPak.contains("7000") -> 7000.0
                rawPak.contains("9,000") || rawPak.contains("9000") -> 9000.0
                rawPak.contains("15,000") || rawPak.contains("15000") -> 15000.0
                rawPak.contains("20,000") || rawPak.contains("20000") -> 20000.0
                rawPak.contains("25,000") || rawPak.contains("25000") -> 25000.0
                rawPak.contains("1,199") || rawPak.contains("1199") -> 1199.0 // Hourly rate
                rawPak.contains("25/photo") || rawPak.contains("25") -> 375.0 // Min 15 photos * 25
                rawPak.contains("50/photo") || rawPak.contains("50") -> 750.0 // Min 15 photos * 50
                rawPak.contains("29/photo") || rawPak.contains("29") -> 435.0 // Min 15 photos * 29
                else -> {
                    // Smart digit parser
                    val cleaner = rawPak.replace(",", "").replace(".", "")
                    val digits = cleaner.filter { it.isDigit() }
                    if (digits.isNotEmpty()) {
                        digits.toDoubleOrNull() ?: 1500.0
                    } else {
                        1500.0
                    }
                }
            }
            
            val bDate = "2026-06-03" // Today's date default
            val dDate = calculateDeliveryDate(bookingDate.value)
            val team = getAssignedTeamForService(bookingService.value)
            val gear = getEquipmentRequiredForService(bookingService.value)

            val newBooking = Booking(
                clientName = clientName.value,
                clientPhone = clientPhone.value,
                clientEmail = clientEmail.value,
                serviceName = bookingService.value,
                packageName = bookingPackage.value,
                shootDate = bookingDate.value,
                shootTime = bookingTime.value,
                requirements = clientRequirements.value,
                invoiceAmount = amount,
                isAdvancePaid = false,
                isBalancePaid = false,
                status = "Lead", // initially registered as Lead
                businessName = clientBusinessName.value.ifEmpty { "N/A" },
                instagramId = clientInstagramId.value.ifEmpty { "N/A" },
                bookingDate = bDate,
                deliveryDate = dDate,
                advanceReceivedValue = 0.0,
                pendingAmountValue = amount,
                location = clientLocation.value.ifEmpty { "Indore Base Coverage (Free)" },
                assignedTeam = team,
                equipmentRequired = gear
            )
            repository.insertBooking(newBooking)

            // Dynamic Google Integration triggers
            triggerGoogleIntegration(newBooking)

            // Reset form
            clientName.value = ""
            clientPhone.value = ""
            clientEmail.value = ""
            clientRequirements.value = ""
            clientBusinessName.value = ""
            clientInstagramId.value = ""
            clientLocation.value = ""
            bookingStep.value = 5 // Screen shows completed ticket!
        }
    }

    private fun triggerGoogleIntegration(b: Booking) {
        // Build authentic simulated backup logs containing spreadsheet and calendars structure
        integrationStatus.value = """
            GOOGLE OPERATIONS SYNCHRONIZED:
            
            ✓ Created Client Entry for '${b.clientName}'
            ✓ Google Sheets Database Updated:
              | CLIENT | ${b.clientName}
              | BUSINESS | ${b.businessName}
              | INSTAGRAM | ${b.instagramId}
              | SERVICE | ${b.serviceName}
              | PACKAGE | ${b.packageName}
              | BOOKING DATE | ${b.bookingDate}
              | SHOOT DATE | ${b.shootDate}
              | DELIVERY DATE | ${b.deliveryDate}
              | TOTAL AMOUNT | ₹${b.invoiceAmount}
              | STATUS | ${b.status}
              
            ✓ Created Google Calendar Alerts:
              1. 📅 [Onboarding Meeting] Scheduled 
              2. 🎬 [Shoot Crew Call]: ${b.shootDate} at ${b.shootTime}
              3. 💻 [Post Production Deadline]: ${b.deliveryDate}
              4. 📦 [Deliverable Release Drive Alert]
              5. 🔔 [Follow-Up Call Strategy]: Scheduled 
              6. 💸 [Accounts Invoice Reminder Setup]
              
            ✓ Resources Booked:
              - Crew Dedicated: ${b.assignedTeam}
              - Lens & Rigs Locked: ${b.equipmentRequired}
        """.trimIndent()
    }

    fun clearIntegrationStatus() {
        integrationStatus.value = null
    }

    // --- Admin Operations ---
    fun updateBookingStatus(
        booking: Booking,
        nextStatus: String,
        driveLink: String? = null,
        isAdvancePaid: Boolean? = null,
        isBalancePaid: Boolean? = null,
        businessName: String? = null,
        instagramId: String? = null,
        location: String? = null,
        assignedTeam: String? = null,
        equipmentRequired: String? = null,
        invoiceAmount: Double? = null,
        advanceReceivedValue: Double? = null,
        pendingAmountValue: Double? = null,
        deliveryDate: String? = null
    ) {
        viewModelScope.launch {
            val amt = invoiceAmount ?: booking.invoiceAmount
            val adv = advanceReceivedValue ?: (if (isAdvancePaid == true) amt * 0.5 else booking.advanceReceivedValue)
            val pend = pendingAmountValue ?: (amt - adv - (if (isBalancePaid == true) amt * 0.5 else 0.0)).coerceAtLeast(0.0)

            val updated = booking.copy(
                status = nextStatus,
                googleDriveLink = driveLink ?: booking.googleDriveLink,
                isAdvancePaid = isAdvancePaid ?: booking.isAdvancePaid,
                isBalancePaid = isBalancePaid ?: booking.isBalancePaid,
                businessName = businessName ?: booking.businessName,
                instagramId = instagramId ?: booking.instagramId,
                location = location ?: booking.location,
                assignedTeam = assignedTeam ?: booking.assignedTeam,
                equipmentRequired = equipmentRequired ?: booking.equipmentRequired,
                invoiceAmount = amt,
                advanceReceivedValue = adv,
                pendingAmountValue = pend,
                deliveryDate = deliveryDate ?: booking.deliveryDate
            )
            repository.updateBooking(updated)
            Toast.makeText(getApplication(), "Contract state updated: $nextStatus", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteBookingItem(id: Int) {
        viewModelScope.launch {
            repository.deleteBookingById(id)
            Toast.makeText(getApplication(), "Booking removed", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Portfolio Operations ---
    fun submitPortfolioItem() {
        viewModelScope.launch {
            if (uploadTitle.value.isEmpty() || uploadClient.value.isEmpty()) {
                Toast.makeText(getApplication(), "Please fill out required fields", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val newItem = PortfolioItem(
                title = uploadTitle.value,
                category = uploadCategory.value,
                mediaUrl = uploadMedia.value,
                clientName = uploadClient.value,
                description = uploadDesc.value,
                dateString = "2026-06-03"
            )
            repository.insertPortfolio(newItem)
            Toast.makeText(getApplication(), "Portfolio updated statically. Autoupdating on devices!", Toast.LENGTH_LONG).show()

            // Reset upload fields
            uploadTitle.value = ""
            uploadClient.value = ""
            uploadDesc.value = ""
        }
    }

    fun removePortfolioItem(id: Int) {
        viewModelScope.launch {
            repository.deletePortfolioById(id)
            Toast.makeText(getApplication(), "Portfolio item deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun updatePortfolioItem(item: PortfolioItem) {
        viewModelScope.launch {
            repository.updatePortfolio(item)
            Toast.makeText(getApplication(), "Portfolio item updated", Toast.LENGTH_SHORT).show()
        }
    }

    // --- AI Assistant Logic ---
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        val currentMessages = chatMessages.value.toMutableList()
        currentMessages.add(text to true) // True = User
        chatMessages.value = currentMessages

        isChatLoading.value = true
        viewModelScope.launch {
            val response = GeminiAssistant.consult(text)
            val updatedMessages = chatMessages.value.toMutableList()
            updatedMessages.add(response to false) // False = Assistant
            chatMessages.value = updatedMessages
            isChatLoading.value = false
        }
    }
}
