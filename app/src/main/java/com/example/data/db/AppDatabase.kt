package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Board
import com.example.data.model.PhraseHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {
    @Query("SELECT * FROM custom_boards")
    fun getAllBoards(): Flow<List<Board>>

    @Query("SELECT * FROM custom_boards WHERE id = :id LIMIT 1")
    suspend fun getBoardById(id: String): Board?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoard(board: Board)

    @Query("DELETE FROM custom_boards WHERE id = :id")
    suspend fun deleteBoardById(id: String)
}

@Dao
interface PhraseHistoryDao {
    @Query("SELECT * FROM phrase_history ORDER BY timestamp DESC LIMIT 50")
    fun getHistory(): Flow<List<PhraseHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(phrase: PhraseHistory)

    @Query("DELETE FROM phrase_history")
    suspend fun clearHistory()
}

@Database(entities = [Board::class, PhraseHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun boardDao(): BoardDao
    abstract fun phraseHistoryDao(): PhraseHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vocal_flair_aac.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
