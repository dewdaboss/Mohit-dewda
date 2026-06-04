package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Entity(tableName = "portfolio_items")
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Photography", "Reels", "Cars", "Commercial", "Products", "Podcast", "YouTube", "Brand Content"
    val mediaUrl: String, 
    val clientName: String,
    val description: String,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val serviceName: String, // "🎬 Reel Shoot", "📸 Photography", "🎙 Podcast Production", etc.
    val packageName: String, // "Starter", "Creator Pro", "Cinematic Ultimate"
    val shootDate: String,
    val shootTime: String,
    val requirements: String,
    val status: String = "Lead", // "Lead", "Discussion", "Booked", "Shoot Scheduled", "Shoot Completed", "Editing", "Delivered", "Closed"
    val googleDriveLink: String = "",
    val invoiceAmount: Double = 0.0,
    val isAdvancePaid: Boolean = false,
    val isBalancePaid: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),

    // --- Extended Operations & Spreadsheet Mapping ---
    val businessName: String = "",
    val instagramId: String = "",
    val bookingDate: String = "",
    val deliveryDate: String = "",
    val advanceReceivedValue: Double = 0.0,
    val pendingAmountValue: Double = 0.0,
    val location: String = "Indore regional agency base",
    val assignedTeam: String = "Mohit Dewda, Vinay S (Cam 2)",
    val equipmentRequired: String = "Sony FX3, DJI Ronin RS3, Rode Wireless Duo"
)

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_items ORDER BY timestamp DESC")
    fun getAllPortfolio(): Flow<List<PortfolioItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(item: PortfolioItem)

    @Update
    suspend fun updatePortfolio(item: PortfolioItem)

    @Delete
    suspend fun deletePortfolio(item: PortfolioItem)

    @Query("DELETE FROM portfolio_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Database(entities = [PortfolioItem::class, Booking::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "creative_production_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class AppRepository(private val db: AppDatabase) {
    val portfolioItems: Flow<List<PortfolioItem>> = db.portfolioDao().getAllPortfolio()
    val bookings: Flow<List<Booking>> = db.bookingDao().getAllBookings()

    private val portfolioDao = db.portfolioDao()
    private val bookingDao = db.bookingDao()

    suspend fun insertPortfolio(item: PortfolioItem) = portfolioDao.insertPortfolio(item)
    suspend fun updatePortfolio(item: PortfolioItem) = portfolioDao.updatePortfolio(item)
    suspend fun deletePortfolio(item: PortfolioItem) = portfolioDao.deletePortfolio(item)
    suspend fun deletePortfolioById(id: Int) = portfolioDao.deleteById(id)

    suspend fun insertBooking(booking: Booking) = bookingDao.insertBooking(booking)
    suspend fun updateBooking(booking: Booking) = bookingDao.updateBooking(booking)
    suspend fun deleteBooking(booking: Booking) = bookingDao.deleteBooking(booking)
    suspend fun deleteBookingById(id: Int) = bookingDao.deleteById(id)

    // Prepopulate some rich demo data on a background coroutine if empty
    fun prepopulateIfEmpty(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val existing = portfolioItems.firstOrNull()
            if (existing.isNullOrEmpty()) {
                val items = listOf(
                    PortfolioItem(
                        title = "BMW M4 Editorial Shoot",
                        category = "Cars",
                        mediaUrl = "cars_bmw",
                        clientName = "Indore Premium Cars",
                        description = "Sleek, high-contrast night aesthetics at Indore Bypass Road showcasing lines and performance.",
                        dateString = "2026-05-15"
                    ),
                    PortfolioItem(
                        title = "Kore Food Commercial v1",
                        category = "Commercial",
                        mediaUrl = "comm_food",
                        clientName = "Silver Oak Bistro Indore",
                        description = "Gourmet slow motion cinematic shots highlighting smoke, textures, and craft cocktails.",
                        dateString = "2026-05-20"
                    ),
                    PortfolioItem(
                        title = "Influencer Brand Launch Reels",
                        category = "Reels",
                        mediaUrl = "reel_influencer",
                        clientName = "Srishti Creation",
                        description = "Viral transition-heavy vertical video that garnered over 500k views on Instagram within 48 hours.",
                        dateString = "2026-05-28"
                    ),
                    PortfolioItem(
                        title = "Capitalist Minds Podcast Episode 4",
                        category = "Podcast",
                        mediaUrl = "pod_capitalist",
                        clientName = "Mohit Dewda Talks",
                        description = "Full technical multi-camera setup, crisp Rode mic audio, and automated wide-to-tight camera switches.",
                        dateString = "2026-06-01"
                    ),
                    PortfolioItem(
                        title = "Aesthetic Perfume Ad",
                        category = "Products",
                        mediaUrl = "prod_perfume",
                        clientName = "Scents of Indore",
                        description = "Macro studio lens shoot detailing product typography, clean liquid splashes, and luxury lighting.",
                        dateString = "2026-06-02"
                    )
                )
                for (item in items) {
                    portfolioDao.insertPortfolio(item)
                }

                // Add sample bookings to populate the dashboard instantly for beautiful user immersion
                val sampleBookings = listOf(
                    Booking(
                        clientName = "Amit Sharma",
                        clientPhone = "+91 9876543210",
                        clientEmail = "amit@bistrolife.com",
                        serviceName = "🎬 Reel Shoots",
                        packageName = "Standard Reel Package",
                        shootDate = "2026-06-10",
                        shootTime = "02:00 PM",
                        requirements = "Need 5 Instagram reels with trend audio. Location: Indore Cafe Bistro.",
                        status = "Shoot Scheduled",
                        googleDriveLink = "https://drive.google.com/drive/folders/demo1",
                        invoiceAmount = 15000.0,
                        isAdvancePaid = true,
                        isBalancePaid = false,
                        businessName = "Bistro Life Indore",
                        instagramId = "@bistro.life.indore",
                        bookingDate = "2026-06-01",
                        deliveryDate = "2026-06-12",
                        advanceReceivedValue = 7500.0,
                        pendingAmountValue = 7500.0,
                        location = "Bistro Life Club, Indore Bypass",
                        assignedTeam = "Mohit Dewda (Cinematographer), Vinay S (Cam 2)",
                        equipmentRequired = "Sony FX3, DJI Ronin RS3, Sony 24-70mm f/2.8 GM II"
                    ),
                    Booking(
                        clientName = "Nisha Patel",
                        clientPhone = "+91 9012345678",
                        clientEmail = "nisha@scentsofindore.com",
                        serviceName = "📦 Product Shoots",
                        packageName = "Edited product photography",
                        shootDate = "2026-06-15",
                        shootTime = "10:30 AM",
                        requirements = "Commercial photography of 12 luxury candles.",
                        status = "Booked",
                        googleDriveLink = "",
                        invoiceAmount = 25000.0,
                        isAdvancePaid = true,
                        isBalancePaid = false,
                        businessName = "Scents of Indore",
                        instagramId = "@scents.of.indore",
                        bookingDate = "2026-06-02",
                        deliveryDate = "2026-06-18",
                        advanceReceivedValue = 12500.0,
                        pendingAmountValue = 12500.0,
                        location = "Studio Room 4B, Indore Agency Base",
                        assignedTeam = "Mohit Dewda (Director), Abhishek K (Lighting Tech)",
                        equipmentRequired = "Sony A7R V, Sony Macro 90mm f/2.8, 3x Godox FV150 lights"
                    ),
                    Booking(
                        clientName = "Rakesh Dewda",
                        clientPhone = "+91 9755550380",
                        clientEmail = "rakesh.show@gmail.com",
                        serviceName = "🎙 Podcasts",
                        packageName = "Podcast Studio Shoot",
                        shootDate = "2026-05-24",
                        shootTime = "04:00 PM",
                        requirements = "Indore local startup guest episode recording and editing.",
                        status = "Delivered",
                        googleDriveLink = "https://drive.google.com/drive/folders/completed_demo",
                        invoiceAmount = 18000.0,
                        isAdvancePaid = true,
                        isBalancePaid = true,
                        businessName = "The Indore Capitalist Show",
                        instagramId = "@indore.capitalist.show",
                        bookingDate = "2026-05-20",
                        deliveryDate = "2026-05-26",
                        advanceReceivedValue = 9000.0,
                        pendingAmountValue = 0.0,
                        location = "Podcast Studio Area, Indore Main Base",
                        assignedTeam = "Mohit Dewda (Visual Director), Sandeep M (Acoustic Sound Engineer)",
                        equipmentRequired = "3x Sony FX3, Shure SM7B Microphones, Zoom H8 Audio Console"
                    )
                )
                for (booking in sampleBookings) {
                    bookingDao.insertBooking(booking)
                }
            }
        }
    }
}
