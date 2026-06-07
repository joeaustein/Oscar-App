"
Oscar App
DS151 3 DESENVOLVIMENTO PARA DISPOSITIVOS MÓVEIS
A Academia Internacional de Cinema quer aproximar a premiação do Oscar ao grande público 4 e você, desenvolvedor, está no
centro dessa missão. Este documento detalha o projeto de desenvolvimento de um aplicativo Android de votação, integrando
autenticação segura, consumo de web services RESTful e uma experiência de usuário fluida e intuitiva.
Desenvolvimento de duas
aplicações distintas: o Oscar App (cliente Android) e o Sistema Central (API com serviços web).
Visão Geral do Projeto
O projeto é estruturado em duas camadas que se comunicam por meio de serviços web no padrão RESTful com mensagens
JSON. Entender essa arquitetura desde o início é fundamental para um desenvolvimento organizado e bem avaliado.
™
Oscar App
Aplicativo Android nativo. O usuário se autentica, navega
pelas categorias, registra seus votos localmente e os
confirma via web service. Toda a interface e lógica de
navegação entre activities ficam aqui.
A
Sistema Central
Backend responsável por expor os serviços web RESTful.
Gerencia autenticação, geração e validação de tokens, e
registro definitivo dos votos no banco de dados da
Academia.
A comunicação entre as duas aplicações ocorre exclusivamente por meio de chamadas HTTP aos endpoints REST. O app nunca
acessa o banco de dados diretamente — tudo passa pelo servidor. Isso garante segurança, centralização dos dados e
flexibilidade para escalar o sistema no futuro.
Autenticação e Tela Inicial
0,5 PONTO
Ao iniciar o Oscar App, a primeira tela apresentada ao usuário é a Activity de Autenticação. Essa tela é o portão de entrada do
aplicativo e deve ser simples, funcional e bem validada. Apenas usuários previamente cadastrados no banco de dados poderão
acessar o sistema.
O que a tela deve ter
Campo de texto para login (usuário)
Campo de senha (com caracteres ocultos)
Botão de entrar que dispara o request
Validação dos campos antes do envio
Feedback de erro caso login/senha estejam incorretos
Regras de negócio
Usuário não cadastrado não pode logar
A autenticação consome um web service no servidor
Em caso de sucesso, o app avança para a tela de boas
vindas
Cadastrar ao menos 5 usuários no BD (2 nunca usados)
Campos em branco devem ser bloqueados no envio
Valide todos os campos de entrada! Permitir dados incorretos no sistema é critério de desconto de nota. Nunca envie
requisição com campos vazios.
Tela de Boas-Vindas e Token de Votação
0,5 PONTO
Após a autenticação bem-sucedida, o usuário é redirecionado para a Tela de Boas-Vindas. Esta é a central de navegação do app
— de onde o usuário acessa todas as funcionalidades de votação. Ela deve ser visualmente atrativa, com a imagem icônica do
troféu do Oscar armazenada localmente no projeto Android (não buscada da internet).
Imagem do Troféu
Armazenada localmente no app (pasta
res/drawable). Deve ocupar posição de
destaque na tela, transmitindo a
identidade visual do Oscar.
Token de Votação
Número inteiro aleatório entre 0 e 100,
gerado pelo servidor no momento do
login. Exibido em um campo de texto na
tela. O servidor armazena o token
vinculado ao usuário no BD.
Menu de Opções
Quatro botões de ação: Votar Filme,
Votar Diretor, Confirmar Voto e Sair.
Cada um leva a uma activity específica.
O token é gerado no servidor de forma aleatória e enviado imediatamente após o login bem-sucedido. Ele é a chave de
validação dos votos — guarde-o corretamente na memória do app durante a sessão.
Votar em Filme
1,5 PONTO
A Activity de Votar Filme acessa os dados dos filmes indicados a partir do endpoint fornecido pelo servidor da Academia, exibe
uma lista com pôster, nome e gênero, e permite ao usuário selecionar seu candidato favorito.
Registrar Lista ProgressBar Votar
O fluxo completo envolve múltiplas camadas: a requisição assíncrona ao servidor, o tratamento do JSON retornado, a exibição
do ProgressBar durante o carregamento, a montagem da lista com imagens dos pôsteres (buscadas via URL), e finalmente a
navegação para a tela de detalhes ao clicar em um item.
Tela de Lista de Filmes
Buscar dados de: http://200.236.3.97/filme.json
Exibir ProgressBar durante o carregamento
Cada célula: foto do pôster + nome + gênero
Clique no item abre a tela de detalhes
Fotos buscadas via URL do JSON
Tela de Detalhes do Filme
Foto em tamanho maior
Nome e gênero do filme
Botão "Votar"
Voto registrado localmente
Pode ser alterado antes da confirmação final
Não carregar a foto do pôster é critério de desconto! Garanta que o carregamento assíncrono de imagens funcione
corretamente para todos os filmes da lista.
Exemplo arquivo Filmes
[
{
"id": "1",
"nome": "Piratas do Caribe",
"genero": "Aventura",
"foto": "http://200.236.3.97/imagens/piratas.jpeg"
},
{
"id": "10",
"nome": "La La Land",
"genero": "Musical",
"foto": "http://200.236.3.97/imagens/land.jpeg"
}
]
Teste sua Activity com 2, 5 ou N filmes — ela deve funcionar perfeitamente para qualquer quantidade retornada pelo
servidor, sem travar ou quebrar o layout.
Votar em Diretor
0,5 PONTO
A Activity de Votar Diretor segue um padrão visual diferente da tela de filmes. Em vez de uma lista com imagens, a interface
utiliza um RadioGroup com RadioButtons, onde cada botão representa o nome de um diretor indicado. Essa abordagem é
intencional: o usuário deve selecionar exatamente um diretor antes de confirmar.
Como funciona
Os dados dos diretores são carregados do arquivo
http://200.236.3.97/diretor.json. O app monta
dinamicamente o RadioGroup com base na lista retornada —
ou seja, se o número de diretores mudar no JSON, o app deve
refletir isso automaticamente sem necessidade de alteração
no código.
RadioGroup dinâmico baseado no JSON
Um RadioButton por diretor
Apenas uma seleção permitida
Botão de confirmação local do voto
Voto registrado localmente (não enviado ainda)
Estrutura do JSON de Diretores
[
{
"id": "1",
"nome": "James Cameron"
},
{
"id": "15",
"nome": "Steven Spielberg"
}
]
Teste com diferentes quantidades de diretores. A Activity
deve funcionar para 2, 5 ou N nomes, sem bugs.
Assim como no voto de filme, o voto no diretor é armazenado apenas localmente até que o usuário acesse a tela de
Confirmar Voto e envie ao servidor. O usuário pode trocar sua escolha quantas vezes quiser antes da confirmação.
Confirmar Voto
1,0 PONTO
A Activity de Confirmar Voto é o momento mais crítico do fluxo — onde tudo se consolida. O usuário visualiza seus dois votos
(filme e diretor escolhidos), informa o token recebido no login e, ao confirmar, os votos são enviados ao servidor via web service.
A resposta do servidor determina o próximo passo: sucesso ou erro.
1
Exibir os Votos
Mostrar claramente o filme votado e o diretor votado. O
usuário precisa revisar antes de confirmar
definitivamente e ainda pode trocar se desejar.
2
Inserir o Token
Campo de texto para o usuário digitar o token recebido na
tela de boas-vindas. O servidor irá validar esse token
antes de registrar os votos.
3
Enviar via Web Service
Ao confirmar, o app realiza uma chamada REST ao
servidor com os dados do voto e do token. O servidor
valida e registra no banco de dados.
4
AlertDialog de Feedback
O app exibe um AlertDialog informando sucesso ou falha.
Em caso de sucesso, os votos ficam bloqueados — o
usuário pode visualizá-los, mas não alterá-los.
Após o voto confirmado com sucesso, o app deve bloquear qualquer edição. O usuário ainda pode ver seus votos,
mas não pode mais alterá-los. Implemente essa lógica com cuidado — é critério avaliado!
Sistema Central — Web Services
O backend é a espinha dorsal do projeto. Ele deve expor um conjunto de serviços web seguindo o padrão RESTful, com
comunicação via JSON. O banco de dados deve ser modelado cuidadosamente para suportar todas as regras de negócio da
aplicação, incluindo unicidade de votos por usuário e vinculação do token ao usuário correto.
🔐
Serviço de Autenticação
Recebe login e senha via POST,
valida no BD e retorna sucesso ou
falha. Em caso de sucesso, também
dispara a geração do token.
🎟
Serviço de Token
Gera um número inteiro aleatório
entre 0 e 100, armazena no BD
vinculado ao usuário autenticado e
retorna ao app para exibição.
✅
Serviço de Registro de
Voto
Recebe os IDs do filme, do diretor e
o token. Valida o token, verifica se o
usuário já votou e registra os votos.
Retorna sucesso ou código de erro.
Regras Críticas do Backend
Apenas 1 voto por usuário — rejeitar tentativas
duplicadas
Token deve ser validado antes de registrar qualquer voto
Filmes e diretores servidos como JSON via arquivos
Todos os dados devem ser persistidos no BD
Respostas sempre em formato JSON
Boas Práticas REST
Usar verbos HTTP adequados (GET, POST)
Retornar códigos HTTP semânticos (200, 401, 400)
Mensagens de erro claras no JSON de resposta
Endpoints organizados e documentados
Nunca expor dados sensíveis na resposta
BANCO DE DADOS USUÁRIOS
Modelagem de Dados e Usuários
A modelagem do banco de dados é um item avaliado implicitamente em toda a funcionalidade do sistema. Um BD mal
estruturado causará problemas em cascata durante a defesa. Planeje com cuidado as tabelas, relacionamentos e restrições
antes de começar a codificar.
Restrições do Banco de Dados
Mínimo de 5 usuários cadastrados no BD
2 usuários nunca usados e sem votos registrados
Ao menos 1 usuário com voto já confirmado
Filmes e diretores carregados dos JSONs externos
Constraint de unicidade: 1 voto por usuário
Token deve ser único por sessão de login
Chaves estrangeiras para integridade referencial
Campos obrigatórios com NOT NULL
Índices nas colunas de login para performance
Requisitos de Cadastro Prévio
Antes da apresentação, o banco de dados deve
conter dados de teste pré-cadastrados. Isso
permite demonstrar todas as funcionalidades sem
depender de cadastros em tempo real durante a
defesa.
Restrições do Banco de Dados
Consistência, unicidade e integridade precisam
estar prontas para sustentar a demonstração.
Teste a aplicação com diferentes quantidades de filmes e diretores nos JSONs. O app deve se adaptar
automaticamente sem necessidade de alteração no código.
Critérios de Avaliação e Entrega
Conheça exatamente o que será avaliado durante a defesa. A pontuação total do trabalho representa 40% da nota final da
disciplina. Cada funcionalidade tem peso específico — planeje o desenvolvimento priorizando os itens de maior valor e
garantindo que nenhum item obrigatório fique de fora.
0,5
Autenticação
Activity + web service de login
funcionando com consumo correto do
endpoint
0,5
Boas-Vindas
Imagem local, token do servidor exibido,
navegação correta entre activities
1,5
Votar Filme
Maior peso! Lista, pôsteres, detalhes,
voto local e todos os serviços corretos
0,5
Votar Diretor
RadioGroup dinâmico, seleção única,
registro local do voto
1,0
Confirmar Voto
Web service, AlertDialog, validação de
token e bloqueio após confirmação

"


Tecnologia:

"App Android
Kotlin
Activities
ViewBinding
Retrofit
RecyclerView
Glide ou Coil

API
Kotlin + Ktor
SQLite (ou MySQL se o professor exigir um banco separado)

Motivos:

Continua tudo em Kotlin.
Curva de aprendizado menor.
Projeto pequeno.
Fácil de demonstrar.
Atende todos os requisitos REST."



A partir dessa descrição, foi criado o seguinte roadMap:

"Roadmap Definitivo
-------------------------------------------------
-------------------------------------------------
Fase 1 — Preparação do Projeto (API + APP)
Objetivo

Montar a estrutura mínima necessária.

Criar dois projetos
Projeto 1

OscarAPI

Responsável por:

autenticação
geração de token
registro de votos
acesso ao banco
Projeto 2

OscarApp

Responsável por:

interface
consumo da API
gerenciamento dos votos locais

-------------------------------------------------
Fase 2 — Banco de Dados (API)

SQLite

Fazer antes de qualquer código.

Tabela usuarios

Campos:

id
login
senha
token

Regras:

login único
login obrigatório
senha obrigatória
Tabela votos

Campos:

id
usuario_id
filme_id
diretor_id
token

Regras:

FK para usuário
UNIQUE(usuario_id)

Garantia:

apenas um voto por usuário
Massa de Teste

Cadastrar:

5 usuários

Garantir:

2 nunca utilizados
1 já votado

Esse requisito aparece explicitamente no enunciado.

-------------------------------------------------
Fase 3 — API REST (API)

Implementar toda a API antes do Android.

Endpoint Login
POST
/auth/login

Recebe:

login
senha

Valida:

usuário existe
senha correta

Se sucesso:

gera token aleatório (0 a 100)
salva token no banco
retorna token
Endpoint Registrar Voto
POST
/voto

Recebe:

usuarioId
filmeId
diretorId
token

Valida:

token correto
usuário já votou?
usuário existe?

Se válido:

registra voto

Se inválido:

retorna erro
Respostas

Sempre JSON.

Exemplos:

Sucesso
{
"success": true
}
Erro
{
"success": false,
"message": "Token inválido"
}

-------------------------------------------------
Fase 4 — Estrutura Android (APP)
Dependências

Adicionar:

Retrofit
Gson
Glide (ou Coil)
Pacotes

Estrutura simples:

activities
api
models
adapters
session

Não criar arquitetura complexa.

-------------------------------------------------
Fase 5 — Models (APP)

Criar:

Filme
id
nome
genero
foto
Diretor
id
nome
LoginRequest
login
senha
LoginResponse
success
token
VoteRequest
usuarioId
filmeId
diretorId
token

-------------------------------------------------
Fase 6 — Retrofit (APP)

Criar interface única.

Métodos:

Login
POST /auth/login
Confirmar voto
POST /voto
Buscar filmes
GET filme.json
Buscar diretores
GET diretor.json

-------------------------------------------------
Fase 7 — LoginActivity (APP)

Primeira tela do aplicativo.

Componentes
EditText login
EditText senha
Botão Entrar
Validações

Impedir envio quando:

login vazio
senha vazia
Fluxo

Sucesso:

guardar usuário
guardar token
abrir BoasVindasActivity

Erro:

Toast ou AlertDialog

-------------------------------------------------
Fase 8 — Sessão do Usuário (APP)

Criar SessionManager simples.

Guardar:

id usuário
login
token

Durante a sessão.

Não precisa implementar JWT.

Não precisa persistência complexa.

-------------------------------------------------
Fase 9 — BoasVindasActivity (APP)

Exibir
imagem local do Oscar
token recebido
Botões
Votar Filme
Votar Diretor
Confirmar Voto
Sair
Sair

Limpar sessão.

Voltar para login.

-------------------------------------------------
Fase 10 — Votar Filme (APP)

Maior peso da nota.

Tela ListaFilmesActivity

Componentes:

RecyclerView
ProgressBar
Fluxo

Abrir tela.

Mostrar:

ProgressBar

Buscar:

http://200.236.3.97/filme.json

Ocultar ProgressBar.

Popular RecyclerView.

Item da Lista

Mostrar:

pôster
nome
gênero
Imagens

Carregar com:

Glide ou Coil.

Esse requisito é obrigatório.

Clique

Abrir:

FilmeDetalheActivity

-------------------------------------------------
Fase 11 — FilmeDetalheActivity (APP)

Mostrar:

imagem grande
nome
gênero
Botão Votar

Salvar localmente:

id filme
nome filme

Não enviar para servidor.

Regra

Permitir trocar o filme enquanto voto não for confirmado.

-------------------------------------------------
Fase 12 — Votar Diretor (APP)

Buscar Diretores

Consumir:

http://200.236.3.97/diretor.json
Montar Interface

Criar RadioButtons dinamicamente.

Um para cada diretor recebido.

Seleção

Permitir apenas uma escolha.

RadioGroup já resolve isso.

Botão Confirmar

Salvar localmente:

id diretor
nome diretor
Regra

Permitir trocar até confirmação final.

-------------------------------------------------
Fase 13 — Gerenciamento dos Votos (APP)

Criar objeto simples:

VoteManager

Guardar:

filme escolhido
diretor escolhido

Em memória.

Nada mais.

-------------------------------------------------
Fase 14 — Confirmar Voto (APP)

Tela mais importante depois de Filmes.

Mostrar
filme escolhido
diretor escolhido
Campo

Token

O professor pede que o usuário digite.

Mesmo já possuindo o token na sessão.

Botão Confirmar

Enviar:

filmeId
diretorId
token

Para API.

-------------------------------------------------
Fase 15 — Resultado da Confirmação (APP)

Sucesso

Mostrar AlertDialog:

Voto registrado com sucesso
Erro

Mostrar:

token inválido
usuário já votou
erro servidor

-------------------------------------------------
Fase 16 — Bloqueio dos Votos (APP)

Requisito obrigatório.

Após sucesso:

usuarioJaVotou = true
Impedir

Nova escolha de filme.

Nova escolha de diretor.

Permitir

Visualizar votos.

-------------------------------------------------
Fase 17 — Tratamento de Erros (APP)

Adicionar em todas as requisições:

sem internet
timeout
servidor offline
JSON inválido

Mostrar mensagens amigáveis.

-------------------------------------------------
Fase 18 — Testes (API + APP)

Login
válido
inválido
vazio
Filmes
2 filmes
5 filmes
N filmes
Diretores
2 diretores
5 diretores
N diretores
Votação
token correto
token incorreto
usuário já votou


Ordem Exata de Desenvolvimento

OscarAPI

Criar projeto Ktor
Configurar SQLite
Criar tabela usuarios
Criar tabela votos
Inserir usuários de teste
Implementar POST /auth/login
Implementar POST /voto
Testar endpoints no Postman

OscarApp

Criar projeto Android
Configurar dependências
Criar Models
Configurar Retrofit
Criar SessionManager
Implementar LoginActivity
Implementar BoasVindasActivity
Implementar ListaFilmesActivity
Implementar Adapter dos filmes
Implementar FilmeDetalheActivity
Implementar DiretorActivity
Implementar VoteManager
Implementar ConfirmarVotoActivity
Integrar endpoint POST /voto
Implementar bloqueio pós-voto
Implementar tratamento de erros
Executar testes finais
Preparar apresentação


Resumo rápido

Fase					            Projeto
Fase 1 – Preparação			        API + APP
Fase 2 – Banco de Dados		        API
Fase 3 – API REST			        API
Fase 4 – Estrutura Android	        APP
Fase 5 – Models			            APP
Fase 6 – Retrofit			        APP
Fase 7 – LoginActivity			    APP
Fase 8 – Sessão do Usuário		    APP
Fase 9 – BoasVindasActivity		    APP
Fase 10 – Votar Filme			    APP
Fase 11 – FilmeDetalheActivity		APP
Fase 12 – Votar Diretor			    APP
Fase 13 – VoteManager			    APP
Fase 14 – Confirmar Voto		    APP
Fase 15 – Feedback da Votação		APP
Fase 16 – Bloqueio Pós-Voto		    APP
Fase 17 – Tratamento de Erros		APP
Fase 18 – Testes			        API + APP


Essa sequência entrega primeiro toda a infraestrutura necessária e depois implementa exatamente os itens que valem nota, sem adicionar nenhuma camada ou tecnologia que não seja exigida pelo enunciado."