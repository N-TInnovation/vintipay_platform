package cv.ntinnovation.gateway_payment.application.core.usecase;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.MetodoPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaMetodoPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaMetodoPagamentoPort;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaMetodoPagamentoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CriaMetodoPagamentoUseCaseImpl implements CriaMetodoPagamentoUseCase {

    private final SalvaMetodoPagamentoPort salvaMetodoPagamentoPort;
    private final BuscaMetodoPagamentoPort buscaMetodoPagamentoPort;

    @Override
    @Transactional
    public MetodoPagamento criar(MetodoPagamento metodoPagamento) {
        validarMetodoPagamento(metodoPagamento);
        verificarSeJaExiste(metodoPagamento);

        return salvaMetodoPagamentoPort.salvar(
            MetodoPagamento.builder()
                .name(metodoPagamento.getName().toUpperCase())
                .build()
        );
    }

    private void validarMetodoPagamento(MetodoPagamento metodoPagamento) {
        if (Objects.isNull(metodoPagamento)) {
            throw new IllegalArgumentException("Método de pagamento não pode ser nulo");
        }
        if (Objects.isNull(metodoPagamento.getName()) || metodoPagamento.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do método de pagamento é obrigatório");
        }
    }

    private void verificarSeJaExiste(MetodoPagamento metodoPagamento) {
        buscaMetodoPagamentoPort.buscarPorNome(metodoPagamento.getName())
            .ifPresent(existente -> {
                throw new IllegalStateException("Já existe um método de pagamento com este nome");
            });
    }
}
