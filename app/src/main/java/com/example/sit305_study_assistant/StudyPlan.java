package com.example.sit305_study_assistant;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StudyPlan {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String planJson; // 存储完整的 JSON 字符串
    public String createdAt; // 存储保存时间
    public String learningGoal; // 学习目标
    public String courses; // 课程名称
} 