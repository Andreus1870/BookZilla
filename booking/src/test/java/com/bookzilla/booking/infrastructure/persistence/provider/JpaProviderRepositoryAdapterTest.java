package com.bookzilla.booking.infrastructure.persistence.provider;

import com.bookzilla.booking.domain.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
        UUID id = UUID.randomUUID();
        Provider provider = new Provider(id);
        when(jpaProviderRepository.save(provider)).thenReturn(provider);

        //act
        Provider result = jpaProviderRepositoryAdapter.save(provider);

        //assert
        verify(jpaProviderRepository).save(provider);

        assertThat(provider).isSameAs(result);
    }

}
