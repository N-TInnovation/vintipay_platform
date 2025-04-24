package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPermissaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPermissaoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GerenciaPermissaoUseCaseImpl implements GerenciaPermissaoUseCase {

    private final GerenciaPermissaoPort gerenciaPermissaoPort;

    @Override
    @Transactional
    public Permissao criar(Permissao permissao) {
        log.info("Criando nova permissão: {}", permissao.getNome());
        
        if (gerenciaPermissaoPort.existePorCodigo(permissao.getCodigo())) {
            throw new IllegalArgumentException("Já existe uma permissão com o código: " + permissao.getCodigo());
        }
        
        permissao.setCreatedAt(LocalDateTime.now());
        permissao.setUpdatedAt(LocalDateTime.now());
        
        return gerenciaPermissaoPort.salvar(permissao);
    }

    @Override
    @Transactional
    public Permissao atualizar(Long id, Permissao permissao) {
        log.info("Atualizando permissão ID: {}", id);
        
        Permissao permissaoExistente = gerenciaPermissaoPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada com ID: " + id));
        
        // Verificar se o código já existe e não pertence a esta permissão
        if (!permissaoExistente.getCodigo().equals(permissao.getCodigo()) && 
            gerenciaPermissaoPort.existePorCodigo(permissao.getCodigo())) {
            throw new IllegalArgumentException("Já existe uma permissão com o código: " + permissao.getCodigo());
        }
        
        permissaoExistente.setNome(permissao.getNome());
        permissaoExistente.setDescricao(permissao.getDescricao());
        permissaoExistente.setCodigo(permissao.getCodigo());
        permissaoExistente.setUpdatedAt(LocalDateTime.now());
        
        return gerenciaPermissaoPort.salvar(permissaoExistente);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo permissão ID: {}", id);
        
        if (!gerenciaPermissaoPort.buscarPorId(id).isPresent()) {
            throw new IllegalArgumentException("Permissão não encontrada com ID: " + id);
        }
        
        gerenciaPermissaoPort.excluir(id);
    }

    @Override
    public Optional<Permissao> buscarPorId(Long id) {
        return gerenciaPermissaoPort.buscarPorId(id);
    }

    @Override
    public Optional<Permissao> buscarPorCodigo(String codigo) {
        return gerenciaPermissaoPort.buscarPorCodigo(codigo);
    }

    @Override
    public List<Permissao> listarTodas() {
        return gerenciaPermissaoPort.listarTodas();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return gerenciaPermissaoPort.existePorCodigo(codigo);
    }
}
