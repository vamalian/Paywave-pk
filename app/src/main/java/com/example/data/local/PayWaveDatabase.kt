package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        WalletEntity::class,
        LedgerEntryEntity::class,
        TransactionEntity::class,
        KycDocumentEntity::class,
        BeneficiaryEntity::class,
        SupportTicketEntity::class,
        DeviceSessionEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PayWaveDatabase : RoomDatabase() {

    abstract fun walletDao(): WalletDao

    companion object {
        @Volatile
        private var INSTANCE: PayWaveDatabase? = null

        fun getDatabase(context: Context): PayWaveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PayWaveDatabase::class.java,
                    "paywave_pk_secure.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
