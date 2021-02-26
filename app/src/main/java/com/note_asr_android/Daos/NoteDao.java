package com.note_asr_android.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.note_asr_android.Models.Notes;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM Notes ")
    List<Notes> getAll();

    /*
     * Insert the object in database
     * @param note, object to be inserted
     */
    @Insert
    void insert(Notes note);

    /*
     * update the object in database
     * @param note, object to be updated
     */
    @Update
    void update(Notes repos);

    /*
     * delete the object from database
     * @param note, object to be deleted
     */
    @Delete
    void delete(Notes note);

    /*
     * delete list of objects from database
     * @param note, array of objects to be deleted
     */
    @Delete
    void delete(Notes... note);      // Note... is varargs, here note is an array

}
