package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    private List<Object> input;
    private Object expected;
}