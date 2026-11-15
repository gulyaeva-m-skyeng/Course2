package pro.skyjava.course2.examinerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {ExaminerServiceImpl.class, JavaQuestionService.class})
class ExaminerServiceImplTest {

    @Autowired
    private ExaminerService examinerService;

    @Autowired
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionService.getAll().forEach(questionService::remove);
        assertThat(questionService.getAll()).isEmpty();
    }

    @Test
    void getQuestions_amountZero_shouldThrowBadRequest() {
        assertThatThrownBy(() -> examinerService.getQuestions(0))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("больше 0");
    }

    @Test
    void getQuestions_negativeAmount_shouldThrowBadRequest() {
        assertThatThrownBy(() -> examinerService.getQuestions(-5))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("больше 0");
    }

    @Test
    void getQuestions_emptyCollection_shouldThrowBadRequest() {
        assertThatThrownBy(() -> examinerService.getQuestions(1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("нет вопросов");
    }

    @Test
    void getQuestions_moreThanAvailable_shouldReturnAllAvailable() {
        questionService.add("Единственный вопрос", "Единственный ответ");

        Collection<Question> result = examinerService.getQuestions(5);

        assertThat(result).hasSize(1);
        List<String> texts = result.stream()
                .map(Question::getQuestion)
                .toList();
        assertThat(texts).containsExactly("Единственный вопрос");
    }

    @Test
    void getQuestions_validAmount_shouldReturnUniqueRandomQuestions() {
        questionService.add("Какое нужно использовать слово, чтобы реализовать интерфейс?", "Implements");
        questionService.add("На конструкции каких трех слов построена обработка исключений в Java?", "try, catch и finally");
        questionService.add("Какая аннотация отмечает классы-контроллеры?", "@RestController");
        questionService.add("Что такое Map?", "хранилище пар ключ-значение");

        Collection<Question> result = examinerService.getQuestions(3);

        assertThat(result).hasSize(3);

        List<String> allTexts = questionService.getAll().stream()
                .map(Question::getQuestion)
                .collect(Collectors.toList());

        List<String> resultTexts = result.stream()
                .map(Question::getQuestion)
                .collect(Collectors.toList());

        assertThat(resultTexts).containsOnlyElementsOf(allTexts);
        assertThat(resultTexts).hasNoDuplicates();
    }

    @Test
    void getQuestions_exactAmount_shouldReturnAll() {
        questionService.add("Q1", "A1");
        questionService.add("Q2", "A2");
        questionService.add("Q3", "A3");

        Collection<Question> result = examinerService.getQuestions(3);

        assertThat(result).hasSize(3);

        List<String> expectedTexts = List.of("Q1", "Q2", "Q3");
        List<String> actualTexts = result.stream()
                .map(Question::getQuestion)
                .collect(Collectors.toList());

        assertThat(actualTexts).containsExactlyInAnyOrderElementsOf(expectedTexts);
        assertThat(actualTexts).hasNoDuplicates();
    }
}