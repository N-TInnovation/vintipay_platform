package cv.ntinnovation.gateway_payment.application.core.usecase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.InfoContaPagamento;
import cv.ntinnovation.gateway_payment.application.port.in.CriaContaPagamentoUseCase;
import cv.ntinnovation.gateway_payment.application.port.out.BuscaContaPagamentoPorEmail;
import cv.ntinnovation.gateway_payment.application.port.out.SalvaContaPagamento;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriaContaPagamentoUseCaseImpl implements CriaContaPagamentoUseCase {

    private final BuscaContaPagamentoPorEmail buscaContaPagamentoPorEmail;
    private final SalvaContaPagamento salvaContaPagamento;

    @Override
    public ContaPagamento criarOuBuscarPorEmail(String email, String billAddrCity, String billAddrCountry, String billAddrLine1, String billAddrPostCode) {
        // Validar email
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }
        
        // Verificar se já existe uma conta com este email
        ContaPagamento contaExistente = buscaContaPagamentoPorEmail.buscarPorEmail(email);
        
        if (contaExistente != null) {
            // Verifica se houve mudança no endereço
            boolean enderecoMudou = !contaExistente.getBillAddrCity().equals(billAddrCity) ||
                                  !contaExistente.getBillAddrCountry().equals(billAddrCountry) ||
                                  !contaExistente.getBillAddrLine1().equals(billAddrLine1) ||
                                  !contaExistente.getBillAddrPostCode().equals(billAddrPostCode);
            
            if (enderecoMudou) {
                contaExistente.setBillAddrCity(billAddrCity);
                contaExistente.setBillAddrCountry(billAddrCountry);
                contaExistente.setBillAddrLine1(billAddrLine1);
                contaExistente.setBillAddrPostCode(billAddrPostCode);
                return salvaContaPagamento.salvar(contaExistente);
            }
            
            return contaExistente;
        }
        
        // Criar uma nova conta de pagamento
        ContaPagamento novaConta = ContaPagamento.builder()
                .email(email)
                .billAddrCity(billAddrCity)
                .billAddrCountry(billAddrCountry)
                .billAddrLine1(billAddrLine1)
                .billAddrPostCode(billAddrPostCode)
                .acctInfo(criarInfoContaPadrao())
                .build();
        
        return salvaContaPagamento.salvar(novaConta);
    }
    
    private InfoContaPagamento criarInfoContaPadrao() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String dataFormatada = agora.format(formatter);
        
        return InfoContaPagamento.builder()
                .acctID(gerarAcctID())
                .chAccAgeInd("01") // Menos de 30 dias
                .chAccChange(dataFormatada)
                .chAccDate(dataFormatada)
                .chAccPwChange(dataFormatada)
                .chAccPwChangeInd("01") // Menos de 30 dias
                .suspiciousAccActivity("01") // Sem atividade suspeita
                .build();
    }
    
    private String gerarAcctID() {
        // Implementação simplificada para gerar um ID de conta
        return "ACC" + System.currentTimeMillis();
    }
}
