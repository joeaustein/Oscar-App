# Oscar App

O **Oscar App** é um aplicativo Android nativo desenvolvido em Kotlin para a disciplina de Desenvolvimento para Dispositivos Móveis (DS151). O objetivo do app é permitir que entusiastas do cinema participem da votação do Oscar de forma intuitiva, consumindo serviços de uma API REST centralizada.

---

## Funcionalidades Implementadas

- **Autenticação Segura**: Tela de login com validação de campos e integração com a API.
- **Sessão de Usuário**: Gerenciamento de sessão com `SharedPreferences` para persistir o token e dados do usuário.
- **Boas-Vindas Dinâmico**: Exibição do token de votação gerado pelo servidor e navegação centralizada.
- **Votação em Filmes**: 
    - Listagem com `RecyclerView` e `ProgressBar`.
    - Carregamento assíncrono de pôsteres via **Glide**.
    - Tela de detalhes com registro de voto local.
- **Votação em Diretores**: 
    - Interface dinâmica com `RadioGroup` e `RadioButton` gerados via código (adaptável a qualquer quantidade de diretores vindos do JSON).
- **Confirmação e Bloqueio**: 
    - Revisão final dos votos com inserção manual do token.
    - **Bloqueio Pós-Voto**: Após a confirmação bem-sucedida, o app impede novas alterações, permitindo apenas a visualização das escolhas.
- **Tratamento de Erros Semânticos**: O app interpreta os códigos de status da API (401, 403, 409, etc.) para fornecer mensagens precisas ao usuário.

---

## Tecnologias Utilizadas

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **Network:** [Retrofit 2](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson)
- **Imagens:** [Glide](https://github.com/bumptech/glide)
- **UI:** ViewBinding, ConstraintLayout, CardView, Material Design.

---

## Configuração e Integração com a API

Para que o App funcione corretamente, ele precisa se comunicar com a **Oscar API**. O endereço da `BASE_URL` deve ser configurado no arquivo:  
`app/src/main/java/com/example/oscar_app/api/OscarApiService.kt`

### Cenário 1: Testando via Emulador
O Android Studio mapeia o endereço do seu PC (onde a API está rodando) para um IP especial.
1. Certifique-se de que a API está rodando em `localhost:8080`.
2. No App, defina: `private const val BASE_URL = "http://10.0.2.2:8080/"`.

### Cenário 2: Testando via Celular Físico
O computador e o celular devem estar na **mesma rede Wi-Fi**.
1. Descubra o IP do seu computador (Windows: `ipconfig` | Linux/Mac: `ifconfig`). Exemplo: `192.168.1.15`.
2. No App, defina: `private const val BASE_URL = "http://192.168.1.15:8080/"`.
3. Certifique-se de que o Firewall do Windows permita conexões na porta 8080.

---

## Passo a Passo para Teste

### 1. Preparação
- Inicie a **Oscar API** (verifique se o banco `oscar.db` foi criado).
- No Android Studio, execute o **Oscar App** no dispositivo escolhido.

### 2. Fluxo de Novo Voto (Ex: user2)
1. **Login**: Use login `user2` e senha `pass2`.
2. **Boas-Vindas**: Anote o **Token** exibido na tela (ex: `87`).
3. **Escolha**:
   - Vá em "Votar Filme", selecione um filme e clique em "Votar".
   - Vá em "Votar Diretor", selecione um e clique em "Confirmar".
4. **Finalização**: 
   - Vá em "Confirmar Voto". 
   - Verifique se as escolhas estão corretas.
   - Digite o **Token** anotado anteriormente.
   - Clique em "Confirmar Voto".
   - Você deve receber um feedback de sucesso.

### 3. Teste de Bloqueio (Ex: user1)
1. Faça login com `user1` / `pass1` (usuário que já possui voto no seed da API).
2. Tente votar em um filme ou diretor.
3. Observe que o botão de voto estará desabilitado com a mensagem "Voto Confirmado".

---

## Dados de Teste (API Seed)
- Há **20** usuários já cadastrados no BD
- **Logins:** `user1` até `user20`
- **Senhas:** `pass1` até `pass20`
- **Importante:** `user1` já inicia com voto confirmado para testar a trava de segurança.
