package org.alljeju.alljejuserver.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alljeju.alljejuserver.mapper.RealEstateMapper;
import org.alljeju.alljejuserver.model.RealEstate;
import org.alljeju.alljejuserver.model.RealEstateList;
import org.alljeju.alljejuserver.model.RealEstateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class RealEstateService {
    @Autowired
    private XmlClientService xmlClientService;
    @Autowired
    private RealEstateMapper realEstateMapper;

    @Value("${real.estate.api.service-key}")
    private String serviceKey;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final String API_URL = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptTradeDev";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(RealEstateService.class);

    public void insertRealEstateData() throws JsonMappingException, JsonProcessingException {
        logger.info("부동산 데이터 삽입 시작");

        int startYear = java.time.Year.now().getValue();
        int startMonth = java.time.LocalDate.now().getMonthValue()-1;
        int endYear = java.time.Year.now().getValue();
        int endMonth = java.time.LocalDate.now().getMonthValue();
        for (int year = startYear; year <= endYear; year++) {
            for (int month = 0; month <= endMonth; month++) {
                if (year == endYear && month > endMonth) {
                    break;
                }

                String dealYmd = String.format("%04d%02d", year, month);
                String url = API_URL + "?serviceKey=" + serviceKey + "&LAWD_CD=50110&DEAL_YMD=" + dealYmd + "&pageNo=1&numOfRows=200";
                ResponseEntity<String> response = xmlClientService.getXmlData(url);
                String responseBody = response.getBody();

                if (responseBody == null || responseBody.isEmpty()) {
                    System.out.println("API 응답이 비어있습니다.");
                    continue;
                }

                try {
                    ObjectMapper xmlMapper = new XmlMapper();
                    JsonNode rootNode = xmlMapper.readTree(responseBody);
                    JsonNode itemsNode = rootNode.path("body").path("items").path("item");

                    if (itemsNode.isArray()) { // itemsNode가 배열인지 확인
                        for (JsonNode itemNode : itemsNode) { // 각 item에 대해 반복
                            RealEstate realEstate = new RealEstate();
                            realEstate.setAptDong(itemNode.path("aptDong").asText());
                            realEstate.setAptNm(itemNode.path("aptNm").asText());
                            realEstate.setAptSeq(itemNode.path("aptSeq").asText());
                            realEstate.setBonbun(itemNode.path("bonbun").asText());
                            realEstate.setBubun(itemNode.path("bubun").asText());
                            realEstate.setBuildYear(Integer.parseInt(itemNode.path("buildYear").asText()));
                            realEstate.setBuyerGbn(itemNode.path("buyerGbn").asText());
                            realEstate.setCdealDay(itemNode.path("cdealDay").asText());
                            realEstate.setCdealType(itemNode.path("cdealType").asText());
                            realEstate.setDealAmount(Integer.parseInt(itemNode.path("dealAmount").asText().replaceAll(",", "")));
                            
                            realEstate.setDealYear(Integer.parseInt(itemNode.path("dealYear").asText()));
                            realEstate.setDealMonth(Integer.parseInt(itemNode.path("dealMonth").asText()));
                            realEstate.setDealDay(Integer.parseInt(itemNode.path("dealDay").asText()));
                            realEstate.setDealingGbn(itemNode.path("dealingGbn").asText());
                            realEstate.setEstateAgentSggNm(itemNode.path("estateAgentSggNm").asText());
                            realEstate.setExcluUseAr(Double.parseDouble(itemNode.path("excluUseAr").asText()));
                            realEstate.setFloor(Integer.parseInt(itemNode.path("floor").asText()));
                            realEstate.setJibun(itemNode.path("jibun").asText());
                            realEstate.setLandCd(itemNode.path("landCd").asText());
                            realEstate.setLandLeaseholdGbn(itemNode.path("landLeaseholdGbn").asText());
                            realEstate.setRoadNm(itemNode.path("roadNm").asText());
                            realEstate.setRoadNmBonbun(itemNode.path("roadNmBonbun").asText());
                            realEstate.setRoadNmBubun(itemNode.path("roadNmBubun").asText());
                            realEstate.setRoadNmCd(itemNode.path("roadNmCd").asText());
                            realEstate.setRoadNmSeq(itemNode.path("roadNmSeq").asText());
                            realEstate.setRoadNmSggCd(itemNode.path("roadNmSggCd").asText());
                            realEstate.setRoadNmbCd(itemNode.path("roadNmbCd").asText());
                            realEstate.setSggCd(itemNode.path("sggCd").asText());
                            realEstate.setSlerGbn(itemNode.path("slerGbn").asText());
                            realEstate.setUmdCd(itemNode.path("umdCd").asText());
                            realEstate.setUmdNm(itemNode.path("umdNm").asText());
                            realEstate.generateChecksum();

                            // 주소 생성
                            String estateAgentSggNm = itemNode.path("estateAgentSggNm").asText();
                            if (estateAgentSggNm == null || estateAgentSggNm.trim().isEmpty()) {
                                estateAgentSggNm = "제주 제주시";
                            }
                            String address = String.format("%s %s %s-%s",
                                estateAgentSggNm,
                                itemNode.path("roadNm").asText(),
                                itemNode.path("roadNmBonbun").asText(),
                                itemNode.path("roadNmBubun").asText());

                            // 위경도 조회
                            String coordinates = getCoordinates(address);
                            if (coordinates != null) {
                                String[] parts = coordinates.split(",");
                                realEstate.setLongitude(parts[0]);
                                realEstate.setLatitude(parts[1]);
                            }

                            realEstateMapper.insertOrUpdate(realEstate);

                            // 로깅
                            logger.info("부동산 데이터 처리: {}", realEstate);
                        }
                    } else {
                        System.out.println("아이템 데이터가 없거나 예상치 못한 형식입니다.");
                    }

                } catch (Exception e) {
                    System.out.println("XML 파싱 중 예상치 못한 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


    // @Cacheable(value = "realEstateData", key = "'realEstateData-' + #params['startDealYear'] + '-' + #params['startDealMonth'] + '-' + #params['endDealYear'] + '-' + #params['endDealMonth'] + '-' + #params['itemNum'] + '-' + #params['itemSizePerPage'] + '-' + #params['searchKey'] + '-' + #params['searchType'] + '-' + #params['sortType'] + '-' + #params['sortOrder']")
    public RealEstateResponse getAllRealEstates(Map<String, Object> params) {
        logger.info("=== Service Layer: getAllRealEstates ===");
        logger.info("Start Year-Month: {}-{}", params.get("startDealYear"), params.get("startDealMonth"));
        logger.info("End Year-Month: {}-{}", params.get("endDealYear"), params.get("endDealMonth"));
        logger.info("Pagination: {} items from index {}", params.get("itemSizePerPage"), params.get("itemNum"));
        if (params.get("searchKey") != null) {
            logger.info("Search: {} = {}", params.get("searchType"), params.get("searchKey"));
        }
        if (params.get("sortType") != null) {
            logger.info("Sort: {} {}", params.get("sortType"), params.get("sortOrder"));
        }
        logger.info("===================================");

        try {
            List<RealEstate> realEstates = realEstateMapper.selectAllRealEstates(params);
            int totalCount = realEstateMapper.selectAllRealEstatesCount(params);
            logger.info("Query executed successfully. Found {} results", totalCount);
            return new RealEstateResponse(realEstates, totalCount);
        } catch (Exception e) {
            logger.error("Error getting real estates: ", e);
            return null;
        }
    }

    /**
     * ID로 단일 부동산 데이터를 조회합니다.
     * @param id 조회할 부동산 데이터의 ID
     * @return 조회된 부동산 데이터, 없을 경우 null
     */
    public RealEstate getRealEstateById(Map<String, Object> params) {
        return realEstateMapper.selectRealEstateById(params);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCacheAfterStartup() {
        logger.info("서버 시작 후 캐시 로딩 시작");
        Map<String, Object> params = new HashMap<>();
        Integer startDealYear = java.time.Year.now().getValue(); // 현재 년으로 기본값 설정
        Integer startDealMonth = java.time.LocalDate.now().minusMonths(1).getMonthValue(); // 현재 월 기준 전월로 기본값 설정
        Integer endDealYear = java.time.Year.now().getValue(); // 현재 년으로 기본값 설정
        Integer endDealMonth = java.time.LocalDate.now().getMonthValue(); // 현재 월 기준 전월로 기본값 설정

        params.put("startDealYear", startDealYear);
        params.put("startDealMonth", startDealMonth);
        params.put("endDealYear", endDealYear);
        params.put("endDealMonth", endDealMonth);
        getAllRealEstates(params);

        logger.info("캐시 로딩 완료");
    }

    public List<RealEstate> getTopMonthlyTransactions(String dealDate) {
        return realEstateMapper.selectTopMonthlyTransactions(dealDate);
    }

    private String getCoordinates(String address) throws RestClientException, UnsupportedEncodingException {
        String kakaoApiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(kakaoApiUrl)
                .queryParam("query", address); // 주소를 쿼리 파라미터로 추가

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                URLDecoder.decode(builder.toUriString(), StandardCharsets.UTF_8.toString()),
                HttpMethod.GET,
                entity,
                String.class);

        String responseBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("responseBody: " + responseBody);
        try {
            JsonNode root = mapper.readTree(responseBody);
            JsonNode documents = root.path("documents");
            if (documents.isArray() && documents.size() > 0) {
                JsonNode firstResult = documents.get(0);
                String longitude = firstResult.path("x").asText();
                String latitude = firstResult.path("y").asText();
                return longitude + "," + latitude;
            }
        } catch (JsonProcessingException e) {
            logger.error("JSON 파싱 오류", e);
        }
        return null;
    }


}