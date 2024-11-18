package com.target.quiz_service.service;

import com.target.quiz_service.doa.QuizDoa;
import com.target.quiz_service.feign.QuizInterface;
import com.target.quiz_service.model.QuestionWrapper;
import com.target.quiz_service.model.Quiz;
import com.target.quiz_service.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizDoa quizDoa;

    @Autowired
    private QuizInterface quizInterface;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        try {
            logger.info("Creating quiz for category: {} with {} questions and title: {}", category, numQ, title);
            List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
            if (questions == null || questions.isEmpty()) {
                logger.warn("No questions received for category: {}", category);
                return new ResponseEntity<>("No questions found for the specified category", HttpStatus.NOT_FOUND);
            }

            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestionsIds(questions);
            quizDoa.save(quiz);

            logger.info("Successfully created quiz with title: {}", title);
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while creating quiz for category: {}", category, e);
            return new ResponseEntity<>("Error creating quiz", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        try {
            logger.info("Fetching questions for quiz with ID: {}", id);
            Quiz quiz = quizDoa.findById(id).orElse(null);
            if (quiz == null) {
                logger.warn("Quiz with ID {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<Integer> questionIds = quiz.getQuestionsIds();
            ResponseEntity<List<QuestionWrapper>> questions = quizInterface.getQuestionsFromId(questionIds);
            logger.info("Successfully fetched {} questions for quiz ID: {}", questionIds.size(), id);
            return questions;
        } catch (Exception e) {
            logger.error("Error occurred while fetching questions for quiz ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        try {
            logger.info("Calculating result for quiz ID: {}", id);
            return quizInterface.getScore(responses);
        } catch (Exception e) {
            logger.error("Error occurred while calculating result for quiz ID: {}", id, e);
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}