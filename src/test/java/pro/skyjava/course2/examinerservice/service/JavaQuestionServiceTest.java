package pro.skyjava.course2.examinerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.skyjava.course2.examinerservice.domain.Question;
import pro.skyjava.course2.examinerservice.exception.BadRequestException;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaQuestionServiceTest {

    private JavaQuestionService out;

    @BeforeEach
    void setUp() {
        out = new JavaQuestionService();

        out.getAll().forEach(out::remove);
    }

    @Test
    void add_shouldAddAndReturnQuestion() {
        Question added = out.add(
                "Какое нужно использовать слово, чтобы реализовать интерфейс?",
                "Implements"
        );

        assertThat(added.getQuestion()).isEqualTo("Какое нужно использовать слово, чтобы реализовать интерфейс?");
        assertThat(added.getAnswer()).isEqualTo("Implements");

        Collection<Question> all = out.getAll();
        assertThat(all).hasSize(1);
        assertThat(all).containsExactly(added);
    }

    @Test
    void add_shouldThrowOnDuplicate() {
        String q = "Какое нужно использовать слово, чтобы реализовать интерфейс?";
        String a = "Implements";

        out.add(q, a);

        assertThatThrownBy(() -> out.add(q, a))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Такой вопрос уже существует");
    }

    @Test
    void remove_shouldRemoveExistingQuestionAndReturnIt() {
        Question question = out.add(
                "Какое нужно использовать слово, чтобы реализовать интерфейс?",
                "Implements"
        );

        Question removed = out.remove(question);

        assertThat(removed).isEqualTo(question);
        assertThat(out.getAll()).isEmpty();
    }

    @Test
    void remove_shouldReturnNullIfQuestionNotFound() {
        Question nonExisting = new Question("Нет такого вопроса", "Нет такого ответа");

        Question result = out.remove(nonExisting);

        assertThat(result).isNull();
        assertThat(out.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnUnmodifiableCopy() {
        out.add("Q1", "A1");

        Collection<Question> all = out.getAll();

        assertThatThrownBy(() -> all.add(new Question("Q2", "A2")))
                .isInstanceOf(UnsupportedOperationException.class);

        assertThat(out.getAll()).hasSize(1);
    }

    @Test
    void getAll_shouldReturnEmptyCollectionWhenStorageIsEmpty() {
        assertThat(out.getAll()).isEmpty();
    }

    @Test
    void getRandomQuestion_shouldReturnOneOfStoredQuestions() {
        Question q1 = out.add("Вопрос 1", "Ответ 1");
        Question q2 = out.add("Вопрос 2", "Ответ 2");
        Question q3 = out.add("Вопрос 3", "Ответ 3");

        Question random = out.getRandomQuestion();

        assertThat(random).isIn(q1, q2, q3);
        assertThat(random).isNotNull();
    }

    @Test
    void getRandomQuestion_shouldThrowIfStorageIsEmpty() {
        assertThatThrownBy(out::getRandomQuestion)
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("В хранилище нет вопросов");
    }
}
