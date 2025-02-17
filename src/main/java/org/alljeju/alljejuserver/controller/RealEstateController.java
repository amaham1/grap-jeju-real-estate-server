package org.alljeju.alljejuserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.alljeju.alljejuserver.model.RealEstate;
import org.alljeju.alljejuserver.model.RealEstateList;
import org.alljeju.alljejuserver.model.RealEstateResponse;
import org.alljeju.alljejuserver.service.RealEstateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.cache.Cache;
import java.util.ArrayList;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/api/real-estate")
public class RealEstateController {

    private static final Logger logger = LoggerFactory.getLogger(RealEstateController.class);

    @Autowired
    private RealEstateService realEstateService;

    @Autowired
    private CacheManager cacheManager;

    // @GetMapping("/cached")
    // public ResponseEntity<List<RealEstate>> getCachedRealEstates() {
    //     Cache cache = cacheManager.getCache("realEstateData");
    //     if (cache != null) {
    //         List<RealEstate> cachedData = new ArrayList<>();
    //         if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
    //             ((com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache()).asMap().values().forEach(value -> {
    //                 if (value instanceof List) {
    //                     cachedData.addAll((List<RealEstate>) value);
    //                     System.out.println("cachedData : " + cachedData.size());
    //                 }
    //             });
    //         }
    //         return ResponseEntity.ok(cachedData);
    //     }
    //     return ResponseEntity.notFound().build();
    // }

    @GetMapping("/detail/{jsApRSId}")
    public ResponseEntity<?> getRealEstateById(@PathVariable(required = true) String jsApRSId) {
        try {
            HashMap<String, Object> params = new HashMap<>();
            System.out.println("jsApRSId : " + jsApRSId);
            params.put("jsApRSId", jsApRSId);
            RealEstate realEstate = realEstateService.getRealEstateById(params);
            if (realEstate == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(realEstate);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("ID 값이 올바른 숫자 형식이 아닙니다.");
        }
    }

    @GetMapping(value = "/insert")
    public void insertRealEstateData() throws JsonMappingException, JsonProcessingException {
        realEstateService.insertRealEstateData();
    }

    @GetMapping("/all")
    public RealEstateResponse getAllRealEstatesController(
        @RequestParam(required = false) String jsApRSId,
        @RequestParam(value = "umdNm", required = false) String umdNm,
        @RequestParam(value = "aptSeq", required = false) String aptSeq,
        @RequestParam(value = "floor", required = false) String floor,
        @RequestParam(value = "searchWord", required = false) String searchWord,
        @RequestParam(required = false) String startDealYear,
        @RequestParam(required = false) String startDealMonth,
        @RequestParam(required = false) String endDealYear,
        @RequestParam(required = false) String endDealMonth,
        @RequestParam(defaultValue = "0") Integer itemNum,
        @RequestParam(defaultValue = "10") Integer itemSizePerPage,
        @RequestParam(required = false) String searchKey,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String sortType,
        @RequestParam(required = false) String sortOrder
    ) {
        logger.info("=== Real Estate Search Parameters ===");
        logger.info("Request Parameters: aptSeq=[{}], floor=[{}]", aptSeq, floor);
        logger.info("jsApRSId: {}", jsApRSId);
        logger.info("aptSeq: {}", aptSeq);
        logger.info("floor: {}", floor);
        logger.info("searchWord: {}", searchWord);
        logger.info("Start Date: {}-{}", startDealYear, startDealMonth);
        logger.info("End Date: {}-{}", endDealYear, endDealMonth);
        logger.info("Item Number: {}", itemNum);
        logger.info("Items Per Page: {}", itemSizePerPage);
        logger.info("Search Key: {}", searchKey);
        logger.info("Search Type: {}", searchType);
        logger.info("Sort Type: {}", sortType);
        logger.info("Sort Order: {}", sortOrder);
        logger.info("================================");

        // Empty string을 null로 변환
        aptSeq = (aptSeq != null && aptSeq.trim().isEmpty()) ? null : aptSeq;
        floor = (floor != null && floor.trim().isEmpty()) ? null : floor;

        // 시작 날짜 기본값 설정 (현재 달의 첫날)
        // Integer startDealYear = null;
        // Integer startDealMonth = null;

        // 종료 날짜 기본값 설정 (현재 날짜)
        // Integer endDealYear = null;
        // Integer endDealMonth = null;

        // startDate 파라미터가 있으면 파싱
        // if (startDate != null && !startDate.isEmpty()) {
        //     String[] startParts = startDate.split("-");
        //     startDealYear = Integer.parseInt(startParts[0]);
        //     startDealMonth = Integer.parseInt(startParts[1]);
        // }

        // // endDate 파라미터가 있으면 파싱
        // if (endDate != null && !endDate.isEmpty()) {
        //     String[] endParts = endDate.split("-");
        //     endDealYear = Integer.parseInt(endParts[0]);
        //     endDealMonth = Integer.parseInt(endParts[1]);
        // }

        // HashMap에 담기
        Map<String, Object> params = new HashMap<>();
        params.put("jsApRSId", jsApRSId);
        params.put("umdNm", umdNm);
        params.put("aptSeq", aptSeq);
        params.put("floor", floor);
        params.put("searchWord", searchWord);
        params.put("startDate", startDealYear);
        params.put("startMonth", startDealMonth);
        params.put("endDate", endDealYear);
        params.put("endMonth", endDealMonth);
        params.put("itemNum", itemNum);
        params.put("itemSizePerPage", itemSizePerPage);
        params.put("searchKey", searchKey);
        params.put("searchType", searchType);
        params.put("sortType", sortType);
        params.put("sortOrder", sortOrder);
        params.put("startDealYear", startDealYear);
        params.put("startDealMonth", startDealMonth);
        params.put("endDealYear", endDealYear);
        params.put("endDealMonth", endDealMonth);

        RealEstateResponse response = realEstateService.getAllRealEstates(params);
        logger.info("Total count of results: {}", response != null ? response.getTotalCount() : 0);
        
        return response;
    }

    @GetMapping("/detail-price-history")
    public RealEstateResponse getDetailPriceHistoryRealEstatesController(
        @RequestParam(required = false) String jsApRSId,
        @RequestParam(value = "aptSeq", required = false) String aptSeq,
        @RequestParam(value = "floor", required = false) String floor,
        @RequestParam(value = "searchWord", required = false) String searchWord,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(defaultValue = "0") Integer itemNum,
        @RequestParam(defaultValue = "10") Integer itemSizePerPage,
        @RequestParam(required = false) String searchKey,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String sortType,
        @RequestParam(required = false) String sortOrder
    ) {
        // Empty string을 null로 변환
        aptSeq = (aptSeq != null && aptSeq.trim().isEmpty()) ? null : aptSeq;
        floor = (floor != null && floor.trim().isEmpty()) ? null : floor;

        // 시작 날짜 기본값 설정 (현재 달의 첫날)
        Integer startDealYear = java.time.Year.now().getValue();
        Integer startDealMonth = java.time.LocalDate.now().getMonthValue();

        // 종료 날짜 기본값 설정 (현재 날짜)
        Integer endDealYear = java.time.Year.now().getValue();
        Integer endDealMonth = java.time.LocalDate.now().getMonthValue();

        // startDate 파라미터가 있으면 파싱
        if (startDate != null && !startDate.isEmpty()) {
            String[] startParts = startDate.split("-");
            startDealYear = Integer.parseInt(startParts[0]);
            startDealMonth = Integer.parseInt(startParts[1]);
        }

        // endDate 파라미터가 있으면 파싱
        if (endDate != null && !endDate.isEmpty()) {
            String[] endParts = endDate.split("-");
            endDealYear = Integer.parseInt(endParts[0]);
            endDealMonth = Integer.parseInt(endParts[1]);
        }

        // HashMap에 담기
        Map<String, Object> params = new HashMap<>();
        params.put("jsApRSId", jsApRSId);
        params.put("aptSeq", aptSeq);
        params.put("floor", floor);
        params.put("searchWord", searchWord);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("itemNum", itemNum);
        params.put("itemSizePerPage", itemSizePerPage);
        params.put("searchKey", searchKey);
        params.put("searchType", searchType);
        params.put("sortType", sortType);
        params.put("sortOrder", sortOrder);
        params.put("startDealYear", startDealYear);
        params.put("startDealMonth", startDealMonth);
        params.put("endDealYear", endDealYear);
        params.put("endDealMonth", endDealMonth);

        RealEstateResponse response = realEstateService.getAllRealEstates(params);
        logger.info("Total count of results: {}", response != null ? response.getTotalCount() : 0);
        
        return response;
    }

    @GetMapping("/top-monthly-real-estate")
    public ResponseEntity<List<RealEstate>> getTopMonthlyTransactions(
            @RequestParam String dealDate) {
        return ResponseEntity.ok(
                realEstateService.getTopMonthlyTransactions(dealDate)
        );
    }
}
