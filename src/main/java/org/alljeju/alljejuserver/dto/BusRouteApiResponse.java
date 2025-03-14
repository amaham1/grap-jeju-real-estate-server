package org.alljeju.alljejuserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusRouteApiResponse {
    
    @JacksonXmlProperty(localName = "header")
    private Header header;
    
    @JacksonXmlProperty(localName = "body")
    private Body body;
    
    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;
        
        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }
    
    @Data
    public static class Body {
        @JacksonXmlProperty(localName = "items")
        private Items items;
        
        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;
        
        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;
        
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;
    }
    
    @Data
    public static class Items {
        @JacksonXmlProperty(localName = "item")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Item> itemList;
    }
    
    @Data
    public static class Item {
        @JacksonXmlProperty(localName = "dstStationId")
        private String dstStationId;
        
        @JacksonXmlProperty(localName = "govNm")
        private String govNm;
        
        @JacksonXmlProperty(localName = "orgtStationId")
        private String orgtStationId;
        
        @JacksonXmlProperty(localName = "routeColor")
        private String routeColor;
        
        @JacksonXmlProperty(localName = "routeId")
        private String routeId;
        
        @JacksonXmlProperty(localName = "routeLen")
        private String routeLen;
        
        @JacksonXmlProperty(localName = "routeNm")
        private String routeNm;
        
        @JacksonXmlProperty(localName = "routeNum")
        private String routeNum;
        
        @JacksonXmlProperty(localName = "routeSubNm")
        private String routeSubNm;
        
        @JacksonXmlProperty(localName = "routeTp")
        private String routeTp;
        
        @JacksonXmlProperty(localName = "stationCnt")
        private String stationCnt;
        
        @JacksonXmlProperty(localName = "upd")
        private String upd;
        
        @JacksonXmlProperty(localName = "useYn")
        private String useYn;
        
        @JacksonXmlProperty(localName = "firstTm")
        private String firstTm;
        
        @JacksonXmlProperty(localName = "lastTm")
        private String lastTm;
    }
}
