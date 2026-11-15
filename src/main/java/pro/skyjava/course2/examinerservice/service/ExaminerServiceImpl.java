package pro.skyjava.course2.examinerservice.service;

import org.springframework.stereotype.Service;
import pro.skyjava.course2.examinerservice.domain.Question;
import pro.skyjava.course2.examinerservice.exception.BadRequestException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ExaminerServiceImpl implements ExaminerService {

    private final QuestionService questionService;

    public ExaminerServiceImpl(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public Collection<Question> getQuestions(int amount) {

        List<Question> allQuestions = new ArrayList<>(questionService.getAll());
        int totalSize = allQuestions.size();

        if (amount <= 0) {
            throw new BadRequestException("Количество вопросов должно быть больше 0");
        }

        if (totalSize == 0) {
            throw new BadRequestException("В системе нет вопросов для выдачи");
        }

        int countToTake = Math.min(amount, totalSize);

        Collections.shuffle(allQuestions);

        return allQuestions.subList(0, countToTake);
    }
}