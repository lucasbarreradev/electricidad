package com.sistema.controller;

import com.sistema.model.Cliente;
import com.sistema.model.CondicionIva;
import com.sistema.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTests {

    @Mock
    private ClienteService clienteService;

    private ClienteController controller;

    @BeforeEach
    void setUp() {
        controller = new ClienteController(clienteService);
    }

    @Test
    void altaRapidaDevuelveClienteParaSeleccionarloEnElRemito() {
        when(clienteService.saveCliente(any(Cliente.class)))
                .thenAnswer(invocation -> {
                    Cliente cliente = invocation.getArgument(0);
                    cliente.setId(25L);
                    return cliente;
                });

        Map<String, Object> respuesta = controller.guardarRapido(
                "  Lucas  ",
                "  Barrera ",
                " 1234 ",
                " 30111222 ",
                " lucas@example.com ",
                " Calle 123 ",
                CondicionIva.CONSUMIDOR_FINAL);

        assertThat(respuesta)
                .containsEntry("id", 25L)
                .containsEntry("nombre", "Lucas")
                .containsEntry("apellido", "Barrera")
                .containsEntry("direccion", "Calle 123");

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        org.mockito.Mockito.verify(clienteService).saveCliente(captor.capture());
        assertThat(captor.getValue().getCondicionIva())
                .isEqualTo(CondicionIva.CONSUMIDOR_FINAL);
    }
}
