package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizStartMessage {
    private QuizProblem quizProblem;
    private String startTime;
}