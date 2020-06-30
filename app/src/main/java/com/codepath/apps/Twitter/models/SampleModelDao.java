package com.codepath.apps.Twitter.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SampleModelDao {

    // @Query annotation requires knowing SQL syntax
    // See http://www.sqltutorial.org/

    @Query("SELECT * FROM SampleModel WHERE id = :id")
    com.codepath.apps.Twitter.models.SampleModel byId(long id);

    @Query("SELECT * FROM SampleModel ORDER BY ID DESC LIMIT 300")
    List<com.codepath.apps.Twitter.models.SampleModel> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(com.codepath.apps.Twitter.models.SampleModel... sampleModels);
}