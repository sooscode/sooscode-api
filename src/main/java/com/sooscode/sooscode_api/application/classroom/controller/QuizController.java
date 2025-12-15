package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.QuizEndMessage;
import com.sooscode.sooscode_api.application.classroom.dto.QuizStartMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QuizController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 퀴즈 시작
     * 클라이언트가 /app/quiz/{classId}/start로 메시지를 보내면
     * /topic/quiz/{classId}/start를 구독한 모든 클라이언트에게 브로드캐스트
     */
    @MessageMapping("/quiz/{classId}/start")
    public void startQuiz(@DestinationVariable String classId, QuizStartMessage quizStartMessage) {
        messagingTemplate.convertAndSend("/topic/quiz/" + classId + "/start", quizStartMessage);
    }

    /**
     * 퀴즈 종료
     * 클라이언트가 /app/quiz/{classId}/end로 메시지를 보내면
     * /topic/quiz/{classId}/end를 구독한 모든 클라이언트에게 브로드캐스트
     */
    @MessageMapping("/quiz/{classId}/end")
    public void endQuiz(@DestinationVariable String classId, QuizEndMessage quizEndMessage) {
        messagingTemplate.convertAndSend("/topic/quiz/" + classId + "/end", quizEndMessage);
    }
}