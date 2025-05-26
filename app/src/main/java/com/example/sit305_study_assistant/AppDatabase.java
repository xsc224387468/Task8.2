package com.example.sit305_study_assistant;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {StudyPlan.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudyPlanDao studyPlanDao();
} 