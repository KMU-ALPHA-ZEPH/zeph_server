package zeph_server.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import zeph_server.course.dto.KaKaoResponse;

@FeignClient(name = "KaKaoLocalClient", url = "https://dapi.kakao.com")
public interface KaKaoLocalClient {
    @GetMapping(value = "/v2/local/geo/coord2address.json")
    KaKaoResponse getAddressByCoords(
            @RequestHeader("Authorization") String apiKey,
            @RequestParam("x") Double x,
            @RequestParam("y") Double y
    );
}
