package pro.skyjava.course2.examinerservice.service;

import org.springframework.stereotype.Service;
import pro.skyjava.course2.examinerservice.domain.Question;
import pro.skyjava.course2.examinerservice.exception.BadRequestException;

import java.util.*;

@Service
public class JavaQuestionService implements QuestionService {

    private final Set<Question> questions = new LinkedHashSet<>();
    private final Random random = new Random();

    public JavaQuestionService() {
        add("Какое нужно использовать слово, чтобы реализовать интерфейс?", "Implements");
        add("На конструкции каких трех слов построена обработка исключений в Java?", "try, catch и finally");
        add("Какая аннотация отмечает классы-контроллеры, которые возвращают пользователю объект в формате JSON/XML?", "@RestController");
    }

    @Override
    public Question add(String question, String answer) {
        if (question == null || question.isBlank() || answer == null || answer.isBlank()) {
            throw new BadRequestException("Вопрос и ответ не могут быть пустыми");
        }
        return add(new Question(question, answer));
    }

    @Override
    public Question add(Question question) {
        if (!questions.add(question)) {
            throw new BadRequestException("Такой вопрос уже существует");
        }
        return question;
    }

    @Override
    public Question remove(Question question) {
        if (questions.remove(question)) {
            return question;
        }
        return null;
    }

    @Override
    public Collection<Question> getAll() {
        List<Question> list = new ArrayList<>(questions);
        return Collections.unmodifiableList(list);
    }

    @Override
    public Question getRandomQuestion() {
        if (questions.isEmpty()) {
            throw new BadRequestException("В хранилище нет вопросов");
        }

        List<Question> snapshot = new ArrayList<>(questions);
        int index = random.nextInt(snapshot.size());
        return snapshot.get(index);
    }
}