package com.gameet.match.dto.request;

import com.gameet.match.enums.ReportReason;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchReportRequest(

        @NotNull(message = "매칭 방 아이디는 필수입니다.")
        Long matchRoomId,

        @NotEmpty(message = "신고 사유는 필수입니다.")
        List<ReportReason> reportReasons

) {
}
