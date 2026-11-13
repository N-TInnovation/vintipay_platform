# Consulta de Recibo por UUID

## spec

```yaml
spec:
  id: consulta-recibo-uuid
  tipo: feature
  agente: backend-architect
  skills:
    - add-hexagonal-feature
    - run-tests
```

## titulo
Criar endpoint GET /api/recibos/{uuid} para consulta pública de recibo.

## descricao
Precisamos expor um endpoint REST que permita consultar um recibo pelo UUID sem autenticação. O retorno deve conter os dados mínimos do recibo (identificador, valor, status, data, pagador e link de confirmação). Em caso de UUID inexistente, deve retornar HTTP 404 com exceção de domínio clara.

## escopo

- `src/main/java/cv/ntinnovation/vintipay/application/port/in/ConsultarReciboPorUuidUseCase.java`
- `src/main/java/cv/ntinnovation/vintipay/application/port/out/ReciboRepositoryPort.java` (se não existir)
- `src/main/java/cv/ntinnovation/vintipay/application/core/usecase/ConsultarReciboPorUuidUseCaseImpl.java`
- `src/main/java/cv/ntinnovation/vintipay/adapter/out/persistence/ReciboJpaRepository.java` (se não existir)
- `src/main/java/cv/ntinnovation/vintipay/adapter/in/web/controller/ReciboController.java` (adicionar endpoint)
- `src/main/java/cv/ntinnovation/vintipay/adapter/in/web/dto/ReciboResumidoDTO.java`
- `src/main/java/cv/ntinnovation/vintipay/adapter/in/web/mapper/ReciboMapper.java` (se necessário)
- `src/main/java/cv/ntinnovation/vintipay/application/core/domain/exception/ReciboNaoEncontradoException.java` (se não existir)
- `src/test/java/cv/ntinnovation/vintipay/application/core/usecase/ConsultarReciboPorUuidUseCaseImplTest.java`

## criterios_aceite
- [ ] Endpoint `GET /api/recibos/{uuid}` responde 200 com `ReciboResumidoDTO` quando o UUID existe.
- [ ] Endpoint responde 404 quando o UUID não existe.
- [ ] Use case lança `ReciboNaoEncontradoException` (ou exceção de domínio equivalente).
- [ ] Nenhuma query SQL escrita fora do repository.
- [ ] Nenhum arquivo criado com mais de 500 linhas.
- [ ] `mvn test` passa.

## restricoes
- Não expor dados sensíveis do pagador (documento, endereço, etc.).
- Não alterar tabelas existentes; usar entidade/recurso já mapeado.
- Manter compatibilidade com Flyway existente.
- Seguir nomenclatura e pacotes hexagonais.

## saida
Código funcional, testado e documentado no spec.
