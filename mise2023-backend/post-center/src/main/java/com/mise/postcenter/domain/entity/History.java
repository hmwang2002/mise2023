package com.mise.postcenter.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "histories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    @Id
    private Long historyId;
    private Long userId;
    private Long postId;
    private Date visitTime;
}
