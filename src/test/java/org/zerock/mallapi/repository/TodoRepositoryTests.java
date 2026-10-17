package org.zerock.mallapi.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QSort;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.domain.QTodo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TodoRepositoryTests {

        private static final Logger log = LoggerFactory.getLogger(TodoRepositoryTests.class);

        @Autowired
        private TodoRepository todoRepository;

        @Autowired
        private JPAQueryFactory queryFactory;

        @Disabled
        @Test
        void testCreate() {
                log.info("Running testCreate");
                List<Todo> savedTodos = todoRepository.saveAll(List.of(
                                createTodo("Repository create test 1"),
                                createTodo("Repository create test 2"),
                                createTodo("Repository create test 3")));

                assertEquals(3, savedTodos.size());
                savedTodos.forEach(todo -> {
                        assertNotNull(todo.getTno());
                        log.info("Created todo: tno={}, title={}", todo.getTno(), todo.getTitle());
                });
        }

        @Disabled
        @Test
        void testRead() {
                log.info("Running testRead");
                Todo savedTodo = todoRepository.save(createTodo("Repository read test"));

                Todo foundTodo = todoRepository.findById(savedTodo.getTno())
                                .orElseThrow(() -> new AssertionError("Saved todo was not found"));

                assertEquals("Repository read test", foundTodo.getTitle());
                assertEquals("tester", foundTodo.getWriter());
                assertFalse(foundTodo.isComplete());
                log.info("Read todo: tno={}, title={}", foundTodo.getTno(), foundTodo.getTitle());
        }

        @Disabled
        @Test
        void testUpdate() {
                log.info("Running testUpdate");
                Todo savedTodo = todoRepository.save(createTodo("Repository update test"));
                Todo updatedTodo = Todo.builder()
                                .tno(savedTodo.getTno())
                                .title("Updated repository test")
                                .writer(savedTodo.getWriter())
                                .complete(true)
                                .dueDate(savedTodo.getDueDate())
                                .build();

                todoRepository.save(updatedTodo);

                Todo foundTodo = todoRepository.findById(savedTodo.getTno())
                                .orElseThrow(() -> new AssertionError("Updated todo was not found"));
                assertEquals("Updated repository test", foundTodo.getTitle());
                assertTrue(foundTodo.isComplete());
                log.info("Updated todo: tno={}, title={}", foundTodo.getTno(), foundTodo.getTitle());
        }

        @Disabled
        @Test
        void testDelete() {
                log.info("Running testDelete");
                Todo savedTodo = todoRepository.save(createTodo("Repository delete test"));

                todoRepository.deleteById(savedTodo.getTno());

                assertFalse(todoRepository.existsById(savedTodo.getTno()));
                log.info("Deleted todo: tno={}", savedTodo.getTno());
        }

        @Disabled
        @Test
        void testPaging() {
                log.info("Running testPaging");
                todoRepository.saveAll(List.of(
                                createTodo("Repository paging test 1"),
                                createTodo("Repository paging test 2"),
                                createTodo("Repository paging test 3"),
                                createTodo("Repository paging test 4"),
                                createTodo("Repository paging test 5")));

                Page<Todo> page = todoRepository.findAll(
                                PageRequest.of(0, 3, new QSort(QTodo.todo.tno.asc())));
                Page<Todo> secondPage = todoRepository.findAll(
                                PageRequest.of(1, 3, new QSort(QTodo.todo.tno.asc())));

                assertEquals(5, page.getTotalElements());
                assertEquals(2, page.getTotalPages());
                assertEquals(3, page.getContent().size());
                assertEquals(2, secondPage.getContent().size());
                assertTrue(page.getContent().getLast().getTno() < secondPage.getContent().getFirst().getTno());
                log.info("Paging result: page={}, size={}, totalElements={}, totalPages={}",
                                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
                page.getContent().forEach(
                                todo -> log.info("Page todo: tno={}, title={}", todo.getTno(), todo.getTitle()));
                log.info("Paging result: page={}, size={}", secondPage.getNumber(), secondPage.getSize());
                secondPage.getContent().forEach(
                                todo -> log.info("Page todo: tno={}, title={}", todo.getTno(), todo.getTitle()));
        }

        private Todo createTodo(String title) {
                return Todo.builder()
                                .title(title)
                                .writer("tester")
                                .complete(false)
                                .dueDate(LocalDate.of(2026, 10, 31))
                                .build();
        }

        @Disabled
        @Test
        void testQuerydslSearchByTitle() {
                log.info("Running testQuerydslSearchByTitle");
                todoRepository.saveAll(List.of(
                                createTodo("Task 11 - first match"),
                                createTodo("Task 211 - second match"),
                                createTodo("Task without match")));

                QTodo todo = QTodo.todo;
                List<Todo> results = queryFactory.selectFrom(todo)
                                .where(todo.title.contains("11"))
                                .orderBy(todo.tno.asc())
                                .fetch();

                log.info("Querydsl results: {}", results);

                assertEquals(2, results.size());
                assertTrue(results.stream().allMatch(result -> result.getTitle().contains("11")));
                results.forEach(result -> log.info("Querydsl search result: tno={}, title={}", result.getTno(),
                                result.getTitle()));
        }

        @Test
        public void testSearch3() {

                String keyword = "99";
                PageRequestDTO requestDTO = PageRequestDTO.builder().build();

                // TodoRepository의 search
                PageResponseDTO<TodoDTO> responseDTO = todoRepository.search(keyword, requestDTO);

                log.info("Search response: {}", responseDTO);

        }
}
