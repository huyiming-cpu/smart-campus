package com.smartcampus.smartcampus.common;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 选课开关 - 全局共享，控制学生选课功能是否开放
 */
@Component
public class CourseSelectConfig {

    private final AtomicBoolean open = new AtomicBoolean(true);

    public boolean isOpen() {
        return open.get();
    }

    public boolean setOpen(boolean open) {
        this.open.set(open);
        return this.open.get();
    }

    public boolean toggle() {
        return !open.getAndSet(!open.get());
    }
}
