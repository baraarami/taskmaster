package taskmaster.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.taskmaster.model.Task;

import java.util.List;

import amplifyframework.datastore.generated.model.Task;

@Dao
public interface TaskDao {

    @Insert
    void insertOne(Task task);
    @Query("SELECT * FROM task WHERE id like : id")
    Task findById(long id);


    @Query("SELECT * FROM Task ")
    List<Task> findAll();

    @Delete
    void delete(Task task);
}
