import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.project.findex.IndexInfo;import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigDto;
import com.sprint.project.findex.dto.autosyncconfig.AutoSyncConfigUpdateRequest;
import com.sprint.project.findex.entity.AutoSyncConfig;
import com.sprint.project.findex.mapper.AutoSyncConfigMapper;
import com.sprint.project.findex.repository.AutoSyncConfigRepository;
import com.sprint.project.findex.service.AutoSyncConfigService;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutoSyncConfigServiceTest {

  @InjectMocks
  private AutoSyncConfigService autoSyncConfigService;

  @Mock
  private AutoSyncConfigRepository autoSyncConfigRepository;

  @Mock
  private AutoSyncConfigMapper autoSyncConfigMapper;

  @Test
  @DisplayName("지수 등록 시 자동 연동 설정이 비활성화 상태로 정상 저장된다.")
  void create_Success() {
    // given (준비)
    IndexInfo mockIndex = new IndexInfo();
    mockIndex.setId(1); // Long(1L)에서 Integer(1)로 변경
    mockIndex.setIndexName("나스닥 100");

    // AutoSyncConfig 생성자 ID 파라미터도 Integer로 변경
    AutoSyncConfig savedEntity = new AutoSyncConfig(1, mockIndex);
    AutoSyncConfigDto expectedDto = AutoSyncConfigDto.builder().id(1).enabled(false).build();

    // 가짜 동작 세팅
    when(autoSyncConfigRepository.save(any(AutoSyncConfig.class))).thenReturn(savedEntity);
    when(autoSyncConfigMapper.toDto(savedEntity)).thenReturn(expectedDto);

    // when (실행)
    AutoSyncConfigDto result = autoSyncConfigService.create(mockIndex);

    // then (검증)
    assertThat(result).isNotNull();
    assertThat(result.isEnabled()).isFalse();
    verify(autoSyncConfigRepository).save(any(AutoSyncConfig.class));
  }

  @Test
  @DisplayName("지수 정보가 null일 경우 연동 설정 저장 시 예외가 발생한다.")
  void create_Fail_WhenIndexInfoIsNull() {
    // given
    IndexInfo nullIndex = null;

    // when & then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      autoSyncConfigService.create(nullIndex);
    });

    assertThat(exception.getMessage()).isEqualTo("지수 정보가 존재하지 않습니다.");
  }

  @Test
  @DisplayName("자동 연동 설정의 활성화 상태가 정상적으로 변경된다.")
  void update_Success() {
    // given
    Integer targetId = 1; // Integer 유지
    AutoSyncConfig existingEntity = new AutoSyncConfig(1, new IndexInfo());

    AutoSyncConfigUpdateRequest mockRequest = mock(AutoSyncConfigUpdateRequest.class);
    when(mockRequest.isEnabled()).thenReturn(true);

    AutoSyncConfigDto expectedDto = AutoSyncConfigDto.builder().id(1).enabled(true).build();

    when(autoSyncConfigRepository.findById(targetId)).thenReturn(Optional.of(existingEntity));
    when(autoSyncConfigMapper.toDto(existingEntity)).thenReturn(expectedDto);

    // when
    AutoSyncConfigDto result = autoSyncConfigService.update(targetId, mockRequest);

    // then
    assertThat(result.isEnabled()).isTrue();
    assertThat(existingEntity.isEnabled()).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 ID로 상태 변경을 요청하면 예외가 발생한다.")
  void update_Fail_WhenIdNotFound() {
    // given
    Integer invalidId = 999; // Integer 유지
    AutoSyncConfigUpdateRequest mockRequest = mock(AutoSyncConfigUpdateRequest.class);

    when(autoSyncConfigRepository.findById(invalidId)).thenReturn(Optional.empty());

    // when & then
    NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
      autoSyncConfigService.update(invalidId, mockRequest);
    });

    assertThat(exception.getMessage()).contains("존재하지 않는 자동 연동 설정입니다. ID: 999");
  }
}
