-- Este arquivo será executado pelo Flyway para importar os dados de localização
-- Ele vai carregar os dados dos arquivos SQL externos

-- Nota: Os comandos abaixo dependem da implementação específica do banco de dados
-- Para MySQL, podemos usar o comando SOURCE
-- SOURCE /path/to/countries.sql;
-- SOURCE /path/to/states.sql;
-- SOURCE /path/to/cities.sql;

-- Como não podemos usar SOURCE diretamente aqui, vamos delegar a importação para o Java
-- usando a classe DataImportConfig que criamos
