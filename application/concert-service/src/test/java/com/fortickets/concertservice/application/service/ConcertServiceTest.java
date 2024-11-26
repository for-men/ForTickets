package com.fortickets.concertservice.application.service;

import com.fortickets.common.exception.GlobalException;
import com.fortickets.common.util.ErrorCase;
import com.fortickets.concertservice.application.dto.request.CreateConcertReq;
import com.fortickets.concertservice.application.dto.request.UpdateConcertReq;
import com.fortickets.concertservice.application.dto.response.CreateConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertRes;
import com.fortickets.concertservice.application.dto.response.GetConcertsRes;
import com.fortickets.concertservice.domain.entity.Concert;
import com.fortickets.concertservice.domain.mapper.ConcertMapper;
import com.fortickets.concertservice.domain.repository.ConcertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertMapper concertMapper;

    @Test
    @DisplayName("1. 공연 생성 - 성공")
    void createConcert_success() {
        // given
        Long userId = 1L;
        CreateConcertReq request = new CreateConcertReq(
                "공연 이름",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                50000L,
                "image.jpg"
        );

        // 위에 값들로 공연 객체 생성
        Concert concert = Concert.of(userId, request.concertName(), request.runTime(), request.startDate(), request.endDate(),
                request.price(), request.concertImage());

        // save 가 될 때 concert 객체 반환
        when(concertRepository.save(any(Concert.class))).thenReturn(concert);
        // toCreateConcertRes 가 호출될 때 공연 상세 정보 반환
        when(concertMapper.toCreateConcertRes(any(Concert.class))).thenAnswer(invocation -> {
            Concert savedConcert = invocation.getArgument(0);
            return new CreateConcertRes(
                    1L,
                    savedConcert.getUserId(),
                    savedConcert.getConcertName(),
                    savedConcert.getRuntime(),
                    savedConcert.getStartDate(),
                    savedConcert.getEndDate(),
                    savedConcert.getPrice(),
                    savedConcert.getConcertImage()
            );
        });

        // when
        // 실제 호출
        CreateConcertRes result = concertService.createConcert(userId, request);

        // then
        // save 가 호출이 되었는지 검증
        verify(concertRepository).save(any(Concert.class));
        // 반환 값 확인
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.concertName()).isEqualTo("공연 이름");
        assertThat(result.runtime()).isEqualTo(120);
    }

    @Test
    @DisplayName("2. 모든 공연 조회 - 성공")
    void getAllConcerts_success() {
        // given
        Page<Concert> concertPage = new PageImpl<>(List.of(
                Concert.of(1L, "공연1", 120, LocalDate.now(), LocalDate.now().plusDays(10), 50000L, "image1.jpg"),
                Concert.of(2L, "공연2", 100, LocalDate.now(), LocalDate.now().plusDays(20), 40000L, "image2.jpg")
        ));
        Pageable pageable = PageRequest.of(0, 10);

        when(concertRepository.findAll(pageable)).thenReturn(concertPage);
        when(concertMapper.toGetConcertsRes(any(Concert.class)))
                .thenAnswer(invocation -> {
                    Concert concert = invocation.getArgument(0);
                    GetConcertsRes response = new GetConcertsRes();
                    response.setId(concert.getId());
                    response.setConcertName(concert.getConcertName());
                    response.setPrice(concert.getPrice());
                    return response;
                });

        // when
        Page<GetConcertsRes> result = concertService.getAllConcerts(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getConcertName()).isEqualTo("공연1");
    }

    @Test
    @DisplayName("3. 특정 공연 조회 - 성공")
    void getConcertById_success() {
        // given
        Concert concert = Concert.of(
                1L,
                "공연 이름",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                50000L,
                "image.jpg"
        );

        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(concertMapper.toGetConcertRes(any(Concert.class))).thenAnswer(invocationOnMock -> {
            Concert savedConcert = invocationOnMock.getArgument(0);
            GetConcertRes response = new GetConcertRes();
            response.setId(savedConcert.getId());
            response.setConcertName(savedConcert.getConcertName());
            response.setRuntime(savedConcert.getRuntime());
            response.setStartDate(savedConcert.getStartDate());
            response.setEndDate(savedConcert.getEndDate());
            response.setPrice(savedConcert.getPrice());
            response.setConcertImage(savedConcert.getConcertImage());
            return response;
        });

        // when
        GetConcertRes result = concertService.getConcertById(1L);

        // then
        assertThat(result.getConcertName()).isEqualTo("공연 이름");
        verify(concertRepository).findById(1L);
    }

    @Test
    @DisplayName("3. 특정 공연 조회 - 실패(존재하지 않는 공연)")
    void getConcertById_failure_notFound() {
        // given
        when(concertRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> concertService.getConcertById(1L))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCase.NOT_EXIST_CONCERT.getMessage());
    }

    @Test
    @DisplayName("4. 특정 공연 수정 - 성공")
    void updateConcertById_success() {
        // given
        Long userId = 1L;
        String role = "ROLE_MANAGER";
        Long concertId = 1L;
        UpdateConcertReq updateConcertReq = new UpdateConcertReq(
                "새 공연 이름",
                null,
                null,
                null,
                null,
                100000L
        );

        Concert concert = Concert.of(
                userId,
                "공연 이름",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                50000L,
                "image.jpg"
        );

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(concertMapper.toGetConcertRes(any(Concert.class))).thenAnswer(invocationOnMock -> {
            Concert savedConcert = invocationOnMock.getArgument(0);
            GetConcertRes response = new GetConcertRes();
            response.setId(savedConcert.getId());
            response.setConcertName(savedConcert.getConcertName());
            response.setRuntime(savedConcert.getRuntime());
            response.setStartDate(savedConcert.getStartDate());
            response.setEndDate(savedConcert.getEndDate());
            response.setPrice(savedConcert.getPrice());
            response.setConcertImage(savedConcert.getConcertImage());
            return response;
        });

        // when
        GetConcertRes result = concertService.updateConcertById(userId, role, concertId, updateConcertReq);

        // then
        assertThat(result.getConcertName()).isEqualTo("새 공연 이름");
        assertThat(result.getPrice()).isEqualTo(100000L);
    }

    @Test
    @DisplayName("4. 특정 공연 수정 - 실패(권한 부족)")
    void updateConcertById_failure_unauthorized() {
        // given
        Long userId = 2L;
        String role = "ROLE_USER";
        Long concertId = 1L;
        UpdateConcertReq updateConcertReq = new UpdateConcertReq(
                "새 공연 이름",
                null,
                null,
                null,
                null,
                100000L
        );

        Concert concert = Concert.of(
                1L,
                "공연 이름",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                50000L,
                "image.jpg"
        );

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        // when & then
        assertThatThrownBy(() -> concertService.updateConcertById(userId, role, concertId, updateConcertReq))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCase.NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("5. 공연 삭제 - 성공")
    void deleteConcertById_success() {
        // given
        Long userId = 1L;
        String role = "ROLE_MANAGER";
        String email = "manager@example.com";
        Long concertId = 1L;

        Concert concert = Concert.of(
                userId,
                "공연 이름",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                50000L,
                "image.jpg"
        );

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        // when
        concertService.deleteConcertById(userId, role, email, concertId);

        // then
        verify(concertRepository).findById(concertId);
        assertThat(concert.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("5. 공연 삭제 - 실패(권한 부족)")
    void deleteConcertById_failure_unauthorized() {
        // given
        Long userId = 2L;
        String role = "ROLE_USER";
        String email = "user@example.com";
        Long concertId = 1L;

        Concert concert = Concert.of(
                1L,
                "공연 이름",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                50000L,
                "image.jpg"
        );

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        // when & then
        assertThatThrownBy(() -> concertService.deleteConcertById(userId, role, email, concertId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCase.NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("5. 공연 삭제 - 실패(공연 존재하지 않음)")
    void deleteConcertById_failure_notFound() {
        // given
        Long userId = 1L;
        String role = "ROLE_MANAGER";
        String email = "manager@example.com";
        Long concertId = 1L;

        when(concertRepository.findById(concertId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> concertService.deleteConcertById(userId, role, email, concertId))
                .isInstanceOf(GlobalException.class)
                .hasMessageContaining(ErrorCase.NOT_EXIST_CONCERT.getMessage());
    }

}
