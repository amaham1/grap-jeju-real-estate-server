import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "response")
public class RealEstate {
    @JacksonXmlProperty(localName = "item")
    private List<Item> items;

    @Data
    public static class Item {
        private String aptDong;
        private String aptNm;
        private String dealAmount;
    }
}