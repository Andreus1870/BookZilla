package com.bookzilla.booking.infrastructure.persistance.provider;

import com.bookzilla.booking.domain.Provider;
import com.bookzilla.booking.infrastructure.persistence.provider.JpaProviderRepository;
import com.bookzilla.booking.infrastructure.persistence.provider.JpaProviderRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JpaProviderRepositoryAdapterTest {

    @Mock
    private JpaProviderRepository jpaProviderRepository;

    @InjectMocks
    private JpaProviderRepositoryAdapter jpaProviderRepositoryAdapter;

    @Test
    void shouldSave() {
        //arrange
        Provider provider = new Provider(12L);
        when(jpaProviderRepository.save(provider)).thenReturn(provider);

        //act
        Provider result = jpaProviderRepositoryAdapter.save(provider);

        //assert
        verify(jpaProviderRepository).save(provider);

        assertThat(provider).isSameAs(result);
    }

    @Test
    void shouldFindByUserId() {
        //arrange
        Long userId = 12L;
        Provider provider = new Provider(userId);

        when(jpaProviderRepository
                .findByUserId(userId))
                .thenReturn(Optional.of(provider));

        //act
        Optional<Provider> result = jpaProviderRepositoryAdapter.findByUserId(userId);

        //assert
        verify(jpaProviderRepository).findByUserId(userId);

        assertThat(result).containsSame(provider);
    }

}
