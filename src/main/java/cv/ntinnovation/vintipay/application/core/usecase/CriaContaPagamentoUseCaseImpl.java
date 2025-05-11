package cv.ntinnovation.vintipay.application.core.usecase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContatoContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.enums.AtividadesSuspeitasEnum;
import cv.ntinnovation.vintipay.application.core.domain.enums.ShAccAgeIndEnum;
import org.springframework.stereotype.Service;

import cv.ntinnovation.vintipay.application.core.domain.entity.ContaPagamento;
import cv.ntinnovation.vintipay.application.core.domain.entity.InfoContaPagamento;
import cv.ntinnovation.vintipay.application.port.in.CriaContaPagamentoUseCase;
import cv.ntinnovation.vintipay.application.port.out.BuscaContaPagamentoPorEmail;
import cv.ntinnovation.vintipay.application.port.out.SalvaContaPagamento;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriaContaPagamentoUseCaseImpl implements CriaContaPagamentoUseCase {

    private final BuscaContaPagamentoPorEmail buscaContaPagamentoPorEmail;
    private final SalvaContaPagamento salvaContaPagamento;

    @Override
    public ContaPagamento criarOuBuscarPorEmail(String name, String email, String billAddrCity, String billAddrState, String billAddrCountry, String billAddrLine1, String billAddrPostCode, String phoneCode, String phoneNumber) {
        // Validar email
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }
        
        // Verificar se já existe uma conta com este email
        ContaPagamento contaExistente = buscaContaPagamentoPorEmail.buscarPorEmail(email);
        
        if (contaExistente != null) {
            name = contaExistente.getName();
            // Verifica se houve mudança no endereço
            boolean enderecoMudou = !contaExistente.getBillAddrCity().equals(billAddrCity) || !contaExistente.getBillAddrCountry().equals(billAddrCountry) || !contaExistente.getBillAddrLine1().equals(billAddrLine1) || !contaExistente.getBillAddrPostCode().equals(billAddrPostCode) || !contaExistente.getMobilePhone().getSubscriber().equals(phoneNumber);
            
            if (enderecoMudou) {
                contaExistente.setBillAddrCity(billAddrCity);
                contaExistente.setBillAddrCountry(billAddrCountry);
                contaExistente.setBillAddrLine1(billAddrLine1);
                contaExistente.setBillAddrState(billAddrState);
                contaExistente.setBillAddrPostCode(billAddrPostCode);
                contaExistente.setMobilePhone(criarContato(phoneCode, phoneNumber));
                contaExistente.setName(name);
                return salvaContaPagamento.salvar(contaExistente);
            }
            
            return contaExistente;
        }
        
        // Criar uma nova conta de pagamento
        ContaPagamento novaConta = ContaPagamento.builder()
                .name(name)
                .email(email)
                .addrMatch("N")
                .billAddrCity(billAddrCity)
                .billAddrCountry(billAddrCountry)
                .billAddrLine1(billAddrLine1)
                .billAddrPostCode(billAddrPostCode)
                .billAddrState(billAddrState)
                .acctInfo(criarInfoContaPadrao())
                .mobilePhone(criarContato(phoneCode, phoneNumber))
                .build();
        
        return salvaContaPagamento.salvar(novaConta);
    }
    
    private InfoContaPagamento criarInfoContaPadrao() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dataFormatada = agora.format(formatter);
        
        return InfoContaPagamento.builder()
                .chAccAgeInd(ShAccAgeIndEnum.SEM_CONTA.getAgeInd())
                .chAccChange(dataFormatada)
                .chAccDate(dataFormatada)
                .chAccPwChange(dataFormatada)
                .chAccPwChangeInd(ShAccAgeIndEnum.SEM_CONTA.getAgeInd()) // Menos de 30 dias
                .suspiciousAccActivity(AtividadesSuspeitasEnum.NAO_SUSPEITO.getActivityInd())// Sem atividade suspeita
                .build();
    }

    private ContatoContaPagamento criarContato(String cc, String subscriber) {
        return ContatoContaPagamento.builder()
                .cc(cc)
                .subscriber(subscriber)
                .build();
    }

}
