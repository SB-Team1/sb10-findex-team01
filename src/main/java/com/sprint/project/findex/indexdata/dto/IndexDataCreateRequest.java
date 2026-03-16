package com.sprint.project.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "지수 데이터 생성 요청")
public record IndexDataCreateRequest(
    @NotNull
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @NotNull
    @Schema(description = "기준 일자", example = "2023-01-01")
    LocalDate baseDate,
    @Positive
    @Schema(description = "시가", example = "2800.25")
    Double marketPrice,
    @Positive
    @Schema(description = "종가", example = "2850.75")
    Double closingPrice,
    @Positive
    @Schema(description = "고가", example = "2870.5")
    Double highPrice,
    @Positive
    @Schema(description = "저가", example = "2795.3")
    Double lowPrice,
    @Positive
    @Schema(description = "전일 대비 등락", example = "50.5")
    Double versus,
    @Positive
    @Schema(description = "전일 대비 등락률", example = "1.8")
    Double fluctuationRate,
    @Positive
    @Schema(description = "거래량", example = "1250000")
    Double tradingQuantity,
    @Positive
    @Schema(description = "거래대금", example = "3500000000")
    BigInteger tradingPrice,
    @Positive
    @Schema(description = "상장 시가 총액", example = "450000000000")
    BigInteger marketTotalAmount
) {

}
