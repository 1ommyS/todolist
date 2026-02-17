package com.indistudia.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Todo {
    private UUID id;
    private String title;
    private boolean isCompleted;
    private Long userId;
}
