package com.target.question_service.service;

import com.target.question_service.doa.QuestionDao;
import com.target.question_service.model.Question;
import com.target.question_service.model.QuestionWrapper;
import com.target.question_service.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionDao doa;

    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            logger.info("Fetching all questions from the database.");
            List<Question> questions = doa.findAll();
            logger.info("Successfully fetched {} questions.", questions.size());
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all questions", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            logger.info("Fetching questions for category: {}", category);
            List<Question> questions = doa.findByCategory(category);
            if (questions.isEmpty()) {
                logger.warn("No questions found for category: {}", category);
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
            }
            logger.info("Successfully fetched {} questions for category: {}", questions.size(), category);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching questions for category: {}", category, e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> addQuestion(Question question) {
        try {
            logger.info("Adding new question: {}", question);
            doa.save(question);
            logger.info("Successfully added question with id: {}", question.getId());
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while adding question: {}", question, e);
            return new ResponseEntity<>("Error adding question", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String category, int numQ) {
        try {
            logger.info("Generating {} random questions for category: {}", numQ, category);
            List<Integer> questionIds = doa.findRandomQuestionsByCategory(category, numQ);
            logger.info("Successfully generated {} question IDs for the quiz.", questionIds.size());
            return new ResponseEntity<>(questionIds, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while generating questions for quiz in category: {}", category, e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionFromId(List<Integer> questionsIds) {
        try {
            logger.info("Fetching questions for IDs: {}", questionsIds);
            List<QuestionWrapper> wrappers = new ArrayList<>();
            List<Question> questions = new ArrayList<>();
            for (Integer id : questionsIds) {
                Question question = doa.findById(id).orElse(null);
                if (question != null) {
                    questions.add(question);
                } else {
                    logger.warn("No question found for ID: {}", id);
                }
            }

            for (Question q : questions) {
                QuestionWrapper wrapper = new QuestionWrapper();
                wrapper.setId(q.getId());
                wrapper.setQuestionTitle(q.getQuestion());
                wrapper.setOption1(q.getOption1());
                wrapper.setOption2(q.getOption2());
                wrapper.setOption3(q.getOption3());
                wrapper.setOption4(q.getOption4());
                wrappers.add(wrapper);
            }

            logger.info("Successfully fetched {} questions by ID.", wrappers.size());
            return new ResponseEntity<>(wrappers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching questions by IDs", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        try {
            logger.info("Calculating score for the given responses.");
            int score = 0;
            for (Response response : responses) {
                Question question = doa.findById(response.getId()).orElse(null);
                if (question != null && response.getResponse().equals(question.getAnswer())) {
                    score++;
                } else if (question == null) {
                    logger.warn("Question with ID {} not found for scoring.", response.getId());
                }
            }
            logger.info("Successfully calculated score: {}", score);
            return new ResponseEntity<>(score, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while calculating score", e);
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
