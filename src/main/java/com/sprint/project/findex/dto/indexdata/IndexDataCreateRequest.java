package com.sprint.project.findex.dto.indexdata;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "지수 데이터 생성 요청")
public record IndexDataCreateRequest(
    //todo @Postive 같은 제약 조건은 임의로 넣어둔 상태임
    @NotNull
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @NotNull
    @Schema(description = "기준 일자", example = "2023-01-01")
    LocalDate baseDate,
    @Positive
    @NotNull
    @Schema(description = "시가", example = "2800.25")
    Double marketPrice,
    @Positive
    @NotNull
    @Schema(description = "종가", example = "2850.75")
    Double closingPrice,
    @Positive
    @NotNull
    @Schema(description = "고가", example = "2870.5")
    Double highPrice,
    @Positive
    @NotNull
    @Schema(description = "저가", example = "2795.3")
    Double lowPrice,
    @NotNull
    @Schema(description = "전일 대비 등락", example = "50.5")
    Double versus,
    @NotNull
    @Schema(description = "전일 대비 등락률", example = "1.8")
    Double fluctuationRate,
    @PositiveOrZero
    @NotNull
    @Schema(description = "거래량", example = "1250000")
    Long tradingQuantity,
    @PositiveOrZero
    @NotNull
    @Schema(description = "거래대금", example = "3500000000")
    BigInteger tradingPrice,
    @Positive
    @NotNull
    @Schema(description = "상장 시가 총액", example = "450000000000")
    BigInteger marketTotalAmount
) {

}
