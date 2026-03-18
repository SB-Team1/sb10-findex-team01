package com.sprint.project.findex.service;

import com.sprint.project.findex.dto.indexdata.IndexDataCsvExportRequest;
import com.sprint.project.findex.dto.indexdata.IndexDataCsvHeader;
import com.sprint.project.findex.entity.IndexData;
import com.sprint.project.findex.global.exception.ApiException;
import com.sprint.project.findex.global.exception.ErrorCode;
import com.sprint.project.findex.repository.IndexDataRepository;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvExportService {

  private final IndexDataRepository indexDataRepository;

  public void exportToCsv(Writer writer, IndexDataCsvExportRequest request) {

    CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
        .setHeader(IndexDataCsvHeader.getHeaderArray())
        .build();

    List<IndexData> dataList = indexDataRepository.findAllForExport(request);

    try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
      for (IndexData indexData : dataList) {
        printer.printRecord(
            indexData.getBaseDate(),
            indexData.getMarketPrice(),
            indexData.getClosingPrice(),
            indexData.getHighPrice(),
            indexData.getLowPrice(),
            indexData.getVersus(),
            indexData.getFluctuationRate(),
            indexData.getTradingQuantity(),
            indexData.getTradingPrice(),
            indexData.getMarketTotalAmount()
        );
      }
      printer.flush();
    } catch (IOException e) {
      throw new ApiException(ErrorCode.FILE_EXPORT_FAILED);
    }
  }
}
