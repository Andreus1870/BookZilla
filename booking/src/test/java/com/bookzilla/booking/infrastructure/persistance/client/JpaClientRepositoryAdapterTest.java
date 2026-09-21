package com.bookzilla.booking.infrastructure.persistance.client;

import com.bookzilla.booking.domain.Client;
import com.bookzilla.booking.infrastructure.persistence.client.JpaClientRepository;
import com.bookzilla.booking.infrastructure.persistence.client.JpaClientRepositoryAdapter;
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
public class JpaClientRepositoryAdapterTest {
    @Mock
    private JpaClientRepository jpaClientRepository;

    @InjectMocks
    private JpaClientRepositoryAdapter jpaClientRepositoryAdapter;

    @Test
    void shouldSave() {
        //arrange
        Long userId = 12L;
        Client client = new Client(userId);
        when(jpaClientRepository.save(client)).thenReturn(client);

        //act
        Client result = jpaClientRepositoryAdapter.save(client);

        //assert
        verify(jpaClientRepository).save(client);
        assertThat(result).isSameAs(client);
    }

    @Test
    void shouldFindByUserId() {
        //arrange
        Long userId = 12L;
        Client client = new Client(userId);

        when(jpaClientRepository
                .findByUserId(userId))
                .thenReturn(Optional.of(client));

        //act
        Optional<Client> result = jpaClientRepositoryAdapter.findByUserId(userId);

        //assert
        verify(jpaClientRepository).findByUserId(userId);

        assertThat(result).containsSame(client);
    }

}
