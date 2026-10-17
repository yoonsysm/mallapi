package org.zerock.mallapi.repository.search;

import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.domain.QTodo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Repository
@RequiredArgsConstructor
@Log4j2
public class TodoSearchImpl implements TodoSearch {

    private final JPQLQueryFactory queryFactory;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDTO<TodoDTO> search(String keyword, PageRequestDTO pageRequest) {

        QTodo todo = QTodo.todo;

        JPQLQuery<Todo> query = queryFactory.selectFrom(todo);

        query.where(todo.title.contains(keyword));

        // tno역순으로 정렬
        query.orderBy(todo.tno.desc());

        // 페이징 처리
        Pageable pageable = PageRequest.of(pageRequest.getPage() - 1, pageRequest.getSize());
        query.offset(pageable.getOffset()); //시작조건
        query.limit(pageable.getPageSize());

        log.info("========================================");
        log.info(query);

        java.util.List<Todo> todoList = query.fetch();

        log.info(todoList);

        long count = query.fetchCount();

        java.util.List<TodoDTO> dtoList = todoList.stream().map(todo1 -> modelMapper.map(todo1, TodoDTO.class))
                .toList();

        return PageResponseDTO.<TodoDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequest).totalCount(count).build();
    }
}
