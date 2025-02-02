package org.alljeju.alljejuserver.scheduler;

import org.alljeju.alljejuserver.service.RealEstateService; // 실제 서비스 클래스 이름으로 변경해주세요
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private final RealEstateService realEstateService; // 실제 서비스 클래스 이름으로 변경해주세요

    @Autowired
    public Scheduler(RealEstateService realEstateService) {
        this.realEstateService = realEstateService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void runDailyQuery() {
        //realEstateService.executeYourQuery();  실제 서비스 메소드 이름으로 변경해주세요
    }
}