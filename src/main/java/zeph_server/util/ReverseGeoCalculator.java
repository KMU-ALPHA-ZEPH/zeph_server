package zeph_server.util;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zeph_server.course.dto.KaKaoResponse;

@Component
@RequiredArgsConstructor
public class ReverseGeoCalculator {
    @Value("${KAKAO_API_KEY}")
    private String apiKey;

    private final KaKaoLocalClient kaKaoLocalClient;

    public String extractRegion(KaKaoResponse response) {
        if (response == null || response.documents() == null || response.documents().isEmpty()) {
            return null;
        }

        KaKaoResponse.Document doc = response.documents().get(0);
        KaKaoResponse.Address addr = doc.address();

        if (addr == null) {
            return null;
        }

        return addr.region_1depth_name() + " " + addr.region_2depth_name();
    }

    public String getRegion(Double lat, Double lng) {
        String authHeader = "KakaoAK " + apiKey;
        KaKaoResponse response = kaKaoLocalClient.getAddressByCoords(authHeader, lng, lat);
        return extractRegion(response);
    }


}
