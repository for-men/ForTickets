package com.fortickets.concertservice.domain.repository;

import com.fortickets.concertservice.domain.entity.Concert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcertRepositoryTest {
    @Mock
    private ConcertRepository concertRepository;

    private Concert concert1;
    private Concert concert2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        concert1 = Concert.of(
                1L,
                "공연A",
                120,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                100000L,
                "imageA.jpa"
        );

        concert2 = Concert.of(
                2L,
                "공연B",
                150,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                50000L,
                "imageB.jpa"
        );
    }

    @Test
    @DisplayName("0. findById(ID로 공연 조회)")
    void findById() {
        // given
        when(concertRepository.findById(concert1.getId())).thenReturn(Optional.of(concert1));

        // when
        Optional<Concert> foundConcert = concertRepository.findById(concert1.getId());

        // then
        assertThat(foundConcert).isPresent();
        assertThat(foundConcert.get().getConcertName()).isEqualTo("공연A");

        verify(concertRepository, times(1)).findById(concert1.getId());
    }

    @Test
    @DisplayName("1. findByUserId(사용자 ID로 공연 조회)")
    void findByUserId() {
        // given
        when(concertRepository.findByUserId(1L)).thenReturn(List.of(concert1));

        // when
        List<Concert> concerts = concertRepository.findByUserId(1L);

        // then
        assertThat(concerts).hasSize(1);
        assertThat(concerts.get(0).getConcertName()).isEqualTo("공연A");

        verify(concertRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("2. findByIdIn(공연 ID 목록으로 공연 목록 조회)")
    void findByIdIn() {
        // given
        List<Long> concertIds = Arrays.asList(1L, 2L, 3L);
        when(concertRepository.findByIdIn(concertIds)).thenReturn(List.of(concert1));

        // when
        List<Concert> concerts = concertRepository.findByIdIn(concertIds);

        // then
        assertThat(concerts).hasSize(1);
        assertThat(concerts.get(0).getId()).isEqualTo(concert1.getId());

        verify(concertRepository, times(1)).findByIdIn(concertIds);
    }

    @Test
    @DisplayName("3. findByUserIdAndConcertNameContaining(사용자 ID와 공연 이름이 포함된 공연 조회)")
    void findByUserIdAndConcertNameContaining() {
        // given
        String searchKeyword = "공연";
        when(concertRepository.findByUserIdAndConcertNameContaining(1L, searchKeyword))
                .thenReturn(List.of(concert1));

        // when
        List<Concert> concerts = concertRepository.findByUserIdAndConcertNameContaining(1L, searchKeyword);

        // then
        assertThat(concerts).hasSize(1);
        assertThat(concerts.get(0).getConcertName()).contains(searchKeyword);

        verify(concertRepository, times(1)).findByUserIdAndConcertNameContaining(1L, searchKeyword);
    }

    @Test
    @DisplayName("4. findByConcertNameContaining(공연 이름이 포함된 공연 조회)")
    void findByConcertNameContaining() {
        // given
        String searchKeyword = "공연";
        when(concertRepository.findByConcertNameContaining(searchKeyword)).thenReturn(List.of(concert1, concert2));

        // when
        List<Concert> concerts = concertRepository.findByConcertNameContaining(searchKeyword);

        // then
        assertThat(concerts).hasSize(2);
        assertThat(concerts.get(0).getConcertName()).contains(searchKeyword);

        verify(concertRepository, times(1)).findByConcertNameContaining(searchKeyword);
    }
}
