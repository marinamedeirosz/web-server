# Servidor Web Multithread em Java

Este projeto é um servidor web simples implementado em Java que:

- Escuta requisições HTTP via socket TCP na porta configurável (padrão 8080)
- Atende múltiplos clientes simultaneamente usando multithreading
- Serve arquivos estáticos HTML e TXT a partir da pasta `wwwroot`
- Previne ataques de path traversal para garantir segurança básica
- Retorna respostas HTTP corretas, incluindo códigos 200, 403, 404 e 400

---

## Como usar

### Preparação

1. Clone ou baixe o projeto.
2. Crie uma pasta chamada `wwwroot` na raiz do projeto.
3. Coloque seus arquivos HTML ou TXT dentro da pasta `wwwroot`. Por exemplo, crie um arquivo `index.html`:

```html
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Meu Servidor</title>
</head>
<body>
  <h1>Olá, Mundo!</h1>
  <p>Servidor Java funcionando.</p>
</body>
</html>
```

---

### Compilar

Compile os arquivos Java (assumindo que estão na pasta `src/`):

```bash
javac src/*.java -d out
```

Isso criará os arquivos `.class` na pasta `out/`.

---

### Executar

Execute o servidor:

```bash
java -cp out MultiThreadedWebServer [porta]
```

- `[porta]` é opcional (padrão: 8080)

Exemplo para porta padrão:

```bash
java -cp out MultiThreadedWebServer
```

Exemplo para porta 80 (requer permissão de administrador/root):

```bash
java -cp out MultiThreadedWebServer 80
```

---

### Testar

Abra o navegador e acesse:

```
http://localhost:8080/
```

Você deve ver o conteúdo do arquivo `index.html`.

Para acessar outros arquivos, use a URL com o nome do arquivo, por exemplo:

```
http://localhost:8080/team.html
```

---

## Estrutura do projeto

```
/web-server/
├── wwwroot/
│   ├── index.html
│   ├── team.html
│   └── about.html
├── src/
│   ├── MultiThreadedWebServer.java
│   └── ClientHandler.java
├── out/                   ← gerado após compilação
└── README.md
```

---

## Detalhes técnicos

- O servidor aceita conexões TCP na porta informada.
- Cada conexão é atendida por uma nova thread (`ClientHandler`).
- Requisições HTTP são parseadas manualmente.
- O caminho solicitado é mapeado para a pasta `wwwroot/` com segurança contra path traversal (`..`).
- Arquivos `.html` e `.txt` são suportados.
- São tratados os seguintes status HTTP:
  - `200 OK` para arquivos encontrados
  - `403 Forbidden` para extensões não permitidas
  - `404 Not Found` para arquivos inexistentes
  - `400 Bad Request` para requisições malformadas

---

## Possíveis melhorias

- Suporte a mais tipos de conteúdo (CSS, imagens, JavaScript, etc)
- Melhor gerenciamento de erros e logging
- Suporte a métodos HTTP além do GET (POST, PUT, etc)
- Cache para arquivos acessados com frequência
- Timeout para conexões inativas
- Interface de configuração por arquivo `.properties`