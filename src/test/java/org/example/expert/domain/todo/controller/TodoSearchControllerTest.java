package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class) // 컨트롤러 테스트용 어노테이션
public class TodoSearchControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc 사용

    @MockBean
    private TodoService todoService; // Service를 Mock 처리

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환용

    @Test
    void testSearchTodos() throws Exception {
        // given
        TodoSearchResponse response1 = new TodoSearchResponse("Test Title 1", 3, 5);
        TodoSearchResponse response2 = new TodoSearchResponse("Test Title 2", 2, 4);
        Page<TodoSearchResponse> pageResult = new PageImpl<>(Arrays.asList(response1, response2), PageRequest.of(0, 10), 2);

        // when
        Mockito.when(todoService.searchTodos(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any()))
                .thenReturn(pageResult);

        // then
        mockMvc.perform(get("/todos/search")
                        .param("page", "1")
                        .param("size", "10")
                        .param("title", "Test")
                        .param("managerNickname", "Manager")
                        .param("startDate", LocalDateTime.now().minusDays(1).toString())
                        .param("endDate", LocalDateTime.now().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 4. 상태 코드 확인
                .andExpect(content().json(objectMapper.writeValueAsString(pageResult))); // 5. 응답 JSON과 일치하는지 확인
    }
}
