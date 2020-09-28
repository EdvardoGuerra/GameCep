# GameCep
A ideia do programa é criar um Servidor e um Cliente que vão trocar informações de nome e CEP para eles tentarem se localizar.
A cada erro, a pontuação inicial diminui. Ganha quem achar o CEP do adversário e tiver mais pontos restantes.

Telas:
1 - Splash com o nome do jogo
2 - Identificação: O jogador informa seu nome e CEP. Clica em "Verificar localização". O app vai buscar no site viacep se o 
    CEP é válido. Depois escolhe se quer ser Servidor ou Cliente. Se for ser cliente, é necessário preencher o ip fornecido
    pelo servidor.
3 - Tela de jogo (semelhante para o servidor e o cliente): Mostra as informações do jogador e do adversário. O jogador preenche 
    os 3 primeiros números do CEP do adversário para tentar localizá-lo. A cada erro, um ponto é debitado da pontual total.
4 - Tela final a fazer: mostra se ganhou ou perdeu e indica o endereço no adversário.

Problemas:
1 - erro de comunicação
2 - não consegui atualizar alguns texto em TextViews
