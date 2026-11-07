package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final JwtUtil jwtUtil;

    @GetMapping("/search/suggest")
    public Result<List<Response.SearchSuggestion>> suggest(
            @RequestParam String keyword,
            @RequestParam String role,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return Result.success(searchService.search(keyword, role, userId));
    }
}
