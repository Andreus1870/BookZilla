package com.bookzilla.booking.infrastructure.persistence.client;

import com.bookzilla.booking.domain.Client;
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
public class JpaClientRepositoryAdapterTest {
    @Mock
    private JpaClientRepository jpaClientRepository;

    @InjectMocks
    private JpaClientRepositoryAdapter jpaClientRepositoryAdapter;

    @Test
    void shouldSave() {
        //arrange
        UUID id = UUID.randomUUID();
        Client client = new Client(id);
        when(jpaClientRepository.save(client)).thenReturn(client);

        //act
        Client result = jpaClientRepositoryAdapter.save(client);

        //assert
        verify(jpaClientRepository).save(client);
        assertThat(result).isSameAs(client);
    }

}
