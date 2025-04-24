package cv.ntinnovation.vintipay.application.core.usecase;

import cv.ntinnovation.vintipay.application.core.domain.entity.Papel;
import cv.ntinnovation.vintipay.application.core.domain.entity.Permissao;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPapelUseCase;
import cv.ntinnovation.vintipay.application.port.in.GerenciaPermissaoUseCase;
import cv.ntinnovation.vintipay.application.port.out.GerenciaPapelPort;
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
public class GerenciaPapelUseCaseImpl implements GerenciaPapelUseCase {

    private final GerenciaPapelPort gerenciaPapelPort;
    private final GerenciaPermissaoUseCase gerenciaPermissaoUseCase;

    @Override
    @Transactional
    public Papel criar(Papel papel) {
        log.info("Criando novo papel: {}", papel.getNome());
        
        if (gerenciaPapelPort.existePorCodigo(papel.getCodigo())) {
            throw new IllegalArgumentException("Já existe um papel com o código: " + papel.getCodigo());
        }
        
        papel.setCreatedAt(LocalDateTime.now());
        papel.setUpdatedAt(LocalDateTime.now());
        
        return gerenciaPapelPort.salvar(papel);
    }

    @Override
    @Transactional
    public Papel atualizar(Long id, Papel papel) {
        log.info("Atualizando papel ID: {}", id);
        
        Papel papelExistente = gerenciaPapelPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado com ID: " + id));
        
        // Verificar se o código já existe e não pertence a este papel
        if (!papelExistente.getCodigo().equals(papel.getCodigo()) && 
            gerenciaPapelPort.existePorCodigo(papel.getCodigo())) {
            throw new IllegalArgumentException("Já existe um papel com o código: " + papel.getCodigo());
        }
        
        papelExistente.setNome(papel.getNome());
        papelExistente.setDescricao(papel.getDescricao());
        papelExistente.setCodigo(papel.getCodigo());
        papelExistente.setPadrao(papel.isPadrao());
        
        // Se este papel for definido como padrão, remover o status de padrão de outros papéis
        if (papel.isPadrao()) {
            Optional<Papel> papelPadraoAtual = gerenciaPapelPort.buscarPapelPadrao();
            if (papelPadraoAtual.isPresent() && !papelPadraoAtual.get().getId().equals(id)) {
                Papel papelPadrao = papelPadraoAtual.get();
                papelPadrao.setPadrao(false);
                gerenciaPapelPort.salvar(papelPadrao);
            }
        }
        
        papelExistente.setUpdatedAt(LocalDateTime.now());
        
        return gerenciaPapelPort.salvar(papelExistente);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        log.info("Excluindo papel ID: {}", id);
        
        Papel papel = gerenciaPapelPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado com ID: " + id));
        
        // Não permitir excluir o papel padrão
        if (papel.isPadrao()) {
            throw new IllegalArgumentException("Não é possível excluir o papel padrão");
        }
        
        gerenciaPapelPort.excluir(id);
    }

    @Override
    public Optional<Papel> buscarPorId(Long id) {
        return gerenciaPapelPort.buscarPorId(id);
    }

    @Override
    public Optional<Papel> buscarPorCodigo(String codigo) {
        return gerenciaPapelPort.buscarPorCodigo(codigo);
    }

    @Override
    public Optional<Papel> buscarPapelPadrao() {
        return gerenciaPapelPort.buscarPapelPadrao();
    }

    @Override
    public List<Papel> listarTodos() {
        return gerenciaPapelPort.listarTodos();
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        return gerenciaPapelPort.existePorCodigo(codigo);
    }

    @Override
    @Transactional
    public Papel adicionarPermissao(Long papelId, Long permissaoId) {
        log.info("Adicionando permissão ID: {} ao papel ID: {}", permissaoId, papelId);
        
        Papel papel = gerenciaPapelPort.buscarPorId(papelId)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado com ID: " + papelId));
        
        Permissao permissao = gerenciaPermissaoUseCase.buscarPorId(permissaoId)
                .orElseThrow(() -> new IllegalArgumentException("Permissão não encontrada com ID: " + permissaoId));
        
        // Verificar se a permissão já está associada ao papel
        boolean permissaoJaExiste = papel.getPermissoes().stream()
                .anyMatch(p -> p.getId().equals(permissaoId));
        
        if (!permissaoJaExiste) {
            papel.getPermissoes().add(permissao);
            papel.setUpdatedAt(LocalDateTime.now());
            return gerenciaPapelPort.salvar(papel);
        }
        
        return papel;
    }

    @Override
    @Transactional
    public Papel removerPermissao(Long papelId, Long permissaoId) {
        log.info("Removendo permissão ID: {} do papel ID: {}", permissaoId, papelId);
        
        Papel papel = gerenciaPapelPort.buscarPorId(papelId)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado com ID: " + papelId));
        
        // Verificar se a permissão está associada ao papel
        boolean permissaoExiste = papel.getPermissoes().removeIf(p -> p.getId().equals(permissaoId));
        
        if (permissaoExiste) {
            papel.setUpdatedAt(LocalDateTime.now());
            return gerenciaPapelPort.salvar(papel);
        }
        
        return papel;
    }
}
