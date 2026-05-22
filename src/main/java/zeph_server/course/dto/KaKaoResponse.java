package zeph_server.course.dto;

import java.util.List;

public record KaKaoResponse(
        List<Document> documents
) {
    public record Document(
            Address address
    ) {
    }

    public record Address(
            String region_1depth_name,
            String region_2depth_name
    ) {
    }
}
