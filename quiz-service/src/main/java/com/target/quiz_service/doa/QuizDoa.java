package com.target.quiz_service.doa;

import com.target.quiz_service.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizDoa extends JpaRepository<Quiz,Integer> {
}
