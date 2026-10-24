package org.zerock.mallapi.service;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.mallapi.dto.TodoDTO;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class TodoServiceTests {
    @Autowired
    private TodoService todoService;

    @Disabled 
    @Test
    public void testRegister() {
        TodoDTO todoDTO = TodoDTO.builder()
                .title("서비스 테스트")
                .writer("tester")
                .dueDate(LocalDate.of(2026, 10, 23))
                .build();
        Long tno = todoService.register(todoDTO);
        log.info("TNO: " + tno);
    }

    @Disabled 
    @Test
    public void testGet() {
        Long tno = 101L;
        TodoDTO todoDTO = todoService.get(tno);
        log.info(todoDTO);
    }

    @Disabled 
    @Test 
    public void testModify() {
        TodoDTO todoDTO = TodoDTO.builder()
        .tno(105L)
        .title("Test Update Title")
        .complete(true)
        .dueDate(LocalDate.of(2026,12,31))
        .build();

        todoService.modify(todoDTO);
        
    }

    @Test 
    public void testRemove() {
        Long tno = 105L;

        todoService.remove(tno);
    }

}