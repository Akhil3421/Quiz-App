package com.target.question_service.controller;

import com.target.question_service.model.Question;
import com.target.question_service.model.QuestionWrapper;
import com.target.question_service.model.Response;
import com.target.question_service.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService service;

    @Autowired
    Environment environment;

    @GetMapping("/all-questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            logger.info("Fetching all questions");
            return service.getAllQuestions();
        } catch (Exception e) {
            logger.error("Error fetching all questions", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        try {
            logger.info("Fetching questions for category: {}", category);
            return service.getQuestionsByCategory(category);
        } catch (Exception e) {
            logger.error("Error fetching questions for category: {}", category, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question) {
        try {
            logger.info("Adding new question: {}", question);
            return service.addQuestion(question);
        } catch (Exception e) {
            logger.error("Error adding question", e);
            return ResponseEntity.status(500).body("Error adding question");
        }
    }

    @GetMapping("/generate")
    public ResponseEntity<List<Integer>> getQuestionsForQuiz(@RequestParam String category, @RequestParam int numQ) {
        try {
            logger.info("Generating {} questions for category: {}", numQ, category);
            return service.getQuestionsForQuiz(category, numQ);
        } catch (Exception e) {
            logger.error("Error generating questions for quiz", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/get-questions")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(@RequestBody List<Integer> questionsIds) {
        try {
            logger.info("Fetching questions for IDs: {}", questionsIds);
            return service.getQuestionFromId(questionsIds);
        } catch (Exception e) {
            logger.error("Error fetching questions for IDs", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/get-score")
    public ResponseEntity<Integer> getScore(@RequestBody List<Response> responses) {
        try {
            logger.info("Calculating score for responses: {}", responses);
            return service.getScore(responses);
        } catch (Exception e) {
            logger.error("Error calculating score", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
