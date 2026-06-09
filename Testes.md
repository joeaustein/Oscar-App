# Plano de Testes - Oscar App

Este documento descreve os cenários de teste necessários para validar todos os requisitos do projeto.

## 1. Autenticação e Tela Inicial
- **Campos Vazios**: Tentar clicar em "Entrar" com login ou senha em branco. 
    - *Esperado*: Mensagem de erro e bloqueio do envio.
- **Usuário Inválido**: Tentar logar com credenciais não cadastradas na API.
    - *Esperado*: AlertDialog informando erro de autenticação (401).
- **Login com Sucesso**: Logar com um usuário válido (ex: `user2` / `pass2`).
    - *Esperado*: Transição para a tela de Boas-Vindas.

## 2. Tela de Boas-Vindas
- **Identidade Visual**: Verificar se a imagem do troféu do Oscar está visível e centralizada.
- **Token de Votação**: Verificar se um número inteiro (0-100) é exibido na tela após o login.
- **Navegação**: Testar os botões "Votar Filme", "Votar Diretor", "Confirmar Voto" e "Sair".
    - *Esperado*: Cada botão deve levar à sua respectiva Activity ou voltar ao Login (Sair).

## 3. Votar em Filme
- **Carregamento Assíncrono**: Abrir a tela de filmes e observar o ProgressBar.
    - *Esperado*: ProgressBar visível enquanto os dados carregam e desaparece após a listagem.
- **Lista de Filmes**: Verificar se cada item exibe pôster (via URL), nome e gênero.
- **Detalhes**: Clicar em um filme da lista.
    - *Esperado*: Abrir tela com foto ampliada e botão "Votar".
- **Voto Local**: Clicar em "Votar" na tela de detalhes.
    - *Esperado*: Toast de confirmação local e retorno à lista/menu.

## 4. Votar em Diretor
- **Interface Dinâmica**: Verificar se os RadioButtons são gerados automaticamente com base no JSON da API.
- **Seleção Única**: Tentar selecionar mais de um diretor.
    - *Esperado*: O RadioGroup deve permitir apenas uma seleção por vez.
- **Voto Local**: Selecionar um diretor e clicar em confirmar.
    - *Esperado*: Armazenamento local da escolha para revisão posterior.

## 5. Confirmar Voto
- **Revisão de Votos**: Abrir a tela de confirmação sem ter votado em nada.
    - *Esperado*: Indicação de "Não selecionado" nos campos de filme e diretor.
- **Validação de Token**: Tentar confirmar o voto com o campo de token vazio ou com valor incorreto.
    - *Esperado*: Feedback de erro (Toast ou AlertDialog).
- **Sucesso no Registro**: Inserir o token correto e clicar em "Confirmar Voto".
    - *Esperado*: AlertDialog de sucesso e bloqueio de novas alterações.
- **Bloqueio Pós-Voto**: Após sucesso, tentar votar novamente em um filme ou diretor.
    - *Esperado*: Botões de votação desabilitados e mensagem "Voto Confirmado".

## 6. Sair (Logout)
- **Limpeza de Sessão**: Clicar em "Sair" e tentar voltar usando o botão físico "Back".
    - *Esperado*: O app deve permanecer na tela de login e exigir nova autenticação.
