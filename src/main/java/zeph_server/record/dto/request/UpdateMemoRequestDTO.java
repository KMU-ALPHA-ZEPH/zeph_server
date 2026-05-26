package zeph_server.record.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMemoRequestDTO {

    @Size(max = 500)
    private String memo;
}
