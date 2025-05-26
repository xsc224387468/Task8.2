package com.example.sit305_study_assistant;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface StudyPlanDao {
    @Insert
    void insert(StudyPlan plan);

    @Query("SELECT * FROM StudyPlan ORDER BY createdAt DESC")
    List<StudyPlan> getAllPlans();
} 