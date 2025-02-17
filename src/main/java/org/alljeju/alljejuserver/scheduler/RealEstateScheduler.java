package org.alljeju.alljejuserver.scheduler;

import org.alljeju.alljejuserver.service.RealEstateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RealEstateScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(RealEstateScheduler.class);
    
    @Autowired
    private RealEstateService realEstateService;
    
    // 매일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduledInsertRealEstateData() {
        logger.info("Starting scheduled real estate data insertion at 3 AM");
        try {
            realEstateService.insertRealEstateData();
            logger.info("Scheduled real estate data insertion completed successfully");
        } catch (Exception e) {
            logger.error("Error during scheduled real estate data insertion: ", e);
        }
    }
}
