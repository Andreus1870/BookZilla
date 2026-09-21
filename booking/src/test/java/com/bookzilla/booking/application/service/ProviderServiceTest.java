package com.bookzilla.booking.application.service;

import com.bookzilla.booking.application.port.out.ProviderRepository;
import com.bookzilla.booking.domain.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private ProviderService providerService;

    @Test
    void shouldCreateProviderSuccessfully() {
        // arrange
        Long userId = 17L;

        Provider savedProvider = new Provider(userId);

        when(providerRepository.save(any(Provider.class))).thenReturn(savedProvider);

        // act
        Provider result = providerService.createProvider(userId);

        // assert
        ArgumentCaptor<Provider> providerCaptor = ArgumentCaptor.forClass(Provider.class);

        verify(providerRepository).save(providerCaptor.capture());

        Provider provider = providerCaptor.getValue();

        assertThat(provider.getUserId()).isEqualTo(userId);
        assertThat(result).isSameAs(savedProvider);

    }
}
