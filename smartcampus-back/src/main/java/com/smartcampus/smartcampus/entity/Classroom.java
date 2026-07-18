package com.smartcampus.smartcampus.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name="classroom")
public class Classroom {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="room_name",nullable=false,length=50) private String roomName;
    @Column(length=50) private String building;
    private Integer capacity;
    @Column(name="room_type",length=20) private String roomType;
    @Column(length=10) private String status;
    @Column(name="created_time",nullable=false,updatable=false) private LocalDateTime createdTime;
    @PrePersist void onCreate(){createdTime=LocalDateTime.now();}
}
