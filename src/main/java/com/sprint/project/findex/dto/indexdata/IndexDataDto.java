package com.sprint.project.findex.dto.indexdata;

import com.sprint.project.findex.entity.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigInteger;
import java.time.LocalDate;

@Schema(description = "지수 데이터 DTO")
public record IndexDataDto(
    @Schema(description = "지수 데이터 ID", example = "1")
    Long id,
    @Schema(description = "지수 정보 ID", example = "1")
    Long indexInfoId,
    @Schema(description = "기준 일자", example = "2023-01-01")
    LocalDate baseDate,
    @Schema(description = "출처 (사용자, Open API)", example = "OPEN_API")
    SourceType sourceType,
    @Schema(description = "시가", example = "2800.25")
    Double marketPrice,
    @Schema(description = "종가", example = "2850.75")
    Double closingPrice,
    @Schema(description = "고가", example = "2870.5")
    Double highPrice,
    @Schema(description = "저가", example = "2795.3")
    Double lowPrice,
    @Schema(description = "전일 대비 등락", example = "50.5")
    Double versus,
    @Schema(description = "전일 대비 등락률", example = "1.8")
    Double fluctuationRate,
    @Schema(description = "거래량", example = "1250000")
    Long tradingQuantity,
    @Schema(description = "거래대금", example = "3500000000")
    BigInteger tradingPrice,
    @Schema(description = "상장 시가 총액", example = "450000000000")
    BigInteger marketTotalAmount
) {

}
