package com.xs.services.subunit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsycTask {

    @Async
    public void run() {
        System.out.println("异步线程池执行任务.........");
    }

}
