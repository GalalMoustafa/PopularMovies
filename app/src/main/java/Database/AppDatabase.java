package Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.example.popularmovies.Models.Movie;



@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String Log_TAG = AppDatabase.class.getSimpleName();
    private static final Object Lock = new Object();
    private static final String DATABASE_NAME = "Movies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if (sInstance == null){
            synchronized (Lock){
                Log.d(Log_TAG, "Creating new Database...");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(Log_TAG, "Getting the Database...");
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
