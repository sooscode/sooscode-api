package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizProblem {
    private String id;
    private String title;
    private String description;
    private String difficulty;  // EASY, MEDIUM, HARD
    private String initialCode;
    private List<QuizExample> examples;
    private List<String> constraints;
    private List<TestCase> testCases;
}
