package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(queryFactory
                .select(todo)
                .from(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(
                        todoIdEq(todoId)
                ).fetchOne());
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(
            String titleKeyword,
            String managerNickname,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {


        List<TodoSearchResponse> results = queryFactory.
                select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        todo.managers.size(),
                        todo.comments.size()))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .where(
                    titleContains(titleKeyword),
                        managerNicknameContains(managerNickname),
                        creationDateBetween(startDate, endDate)
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset()) // 페이지 시작 위치
                .limit(pageable.getPageSize()) // 한 페이지에 몇 개의 항목을 보여줄지 지정
                .fetch();

        long total = Optional.ofNullable(queryFactory
                        .select(todo.count())
                        .from(todo)
                        .fetchOne())
                .orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

    private BooleanExpression titleContains(String titleKeyword) {
        return StringUtils.hasText(titleKeyword) ? todo.title.contains(titleKeyword) : null;
    }

    private BooleanExpression managerNicknameContains(String managerNickname) {
        return StringUtils.hasText(managerNickname) ? manager.nickname.contains(managerNickname) : null;
    }

    private BooleanExpression creationDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return todo.createdAt.between(startDate, endDate);
        } else if (startDate != null) {
            return todo.createdAt.goe(startDate); // "greater than or equal"(크거나 같다)
        } else if (endDate != null) {
            return todo.createdAt.loe(endDate); // "less than or equal"(작거나 같다)
        } else {
            return null;
        }
    }
}
