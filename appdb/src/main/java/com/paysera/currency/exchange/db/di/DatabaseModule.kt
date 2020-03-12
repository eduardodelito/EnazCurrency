package com.paysera.currency.exchange.db.di

import android.app.Application
import androidx.room.Room
import com.paysera.currency.exchange.db.AppDatabase
import com.paysera.currency.exchange.db.dao.PayseraDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by eduardo.delito on 3/11/20.
 */
@Module
class DatabaseModule(private val mApplication: Application) {

    private var appDatabase: AppDatabase

    init {
        synchronized(this) {
            val instance = Room.databaseBuilder(mApplication, AppDatabase::class.java, AppDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
            appDatabase = instance
            instance
        }
    }

    @Singleton
    @Provides
    fun providesRoomDatabase(): AppDatabase {
        return appDatabase
    }

    @Singleton
    @Provides
    fun providesPayseraDao(appDatabase: AppDatabase): PayseraDao {
        return appDatabase.payseraDao()
    }
}
