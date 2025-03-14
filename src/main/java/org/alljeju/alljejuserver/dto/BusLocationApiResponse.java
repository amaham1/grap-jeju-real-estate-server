package org.alljeju.alljejuserver.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "response")
public class BusLocationApiResponse {
    
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
        @JacksonXmlProperty(localName = "localX")
        private String localX;
        
        @JacksonXmlProperty(localName = "localY")
        private String localY;
        
        @JacksonXmlProperty(localName = "lowPlateTp")
        private String lowPlateTp;
        
        @JacksonXmlProperty(localName = "mobiNum")
        private String mobiNum;
        
        @JacksonXmlProperty(localName = "plateNo")
        private String plateNo;
        
        @JacksonXmlProperty(localName = "routeId")
        private String routeId;
        
        @JacksonXmlProperty(localName = "routeNm")
        private String routeNm;
        
        @JacksonXmlProperty(localName = "routeSubNm")
        private String routeSubNm;
        
        @JacksonXmlProperty(localName = "routeTp")
        private String routeTp;
        
        @JacksonXmlProperty(localName = "stationId")
        private String stationId;
        
        @JacksonXmlProperty(localName = "stationNm")
        private String stationNm;
        
        @JacksonXmlProperty(localName = "stationOrd")
        private String stationOrd;
        
        @JacksonXmlProperty(localName = "updnDir")
        private String updnDir;
    }
}