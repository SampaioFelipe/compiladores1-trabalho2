package CC1.T2;

import java.util.List;

public class AnalisadorSemantico extends LuazinhaBaseVisitor<Void> {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    /*Visitor da regra inicial "programa"
    *Quando o analisador semântico passa pelo nó da regra programa esse método é chamado
    *e uma TabelaDeSimbolos é empilhada na pilha ainda vazia de tabelas,
    *representando o escopo global. Depois o visitor da regra seguinte (trecho) é chamado.
    *Após a execução deste e de suas ramificações, a tabela global é desempilhada.*/
    @Override
    public Void visitPrograma(LuazinhaParser.ProgramaContext ctx) {
        // Criacao do escopo global
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));
        // Continua a analise no seu filho "trecho"
        visitTrecho(ctx.trecho());
        // Destruicao do escopo global
        pilhaDeTabelas.desempilhar();
        return null;
    }

    /*Visitor da regra "trecho"
    *Neste método, conta-se o número de comandos que derivam da regra "trecho"
    *e o visitor da regra comando é chamado para cada comando, que podem ser de diversos tipos*/
    /** trecho : (comando ';'?)* (ultimocomando ';'?)? **/
    @Override
    public Void visitTrecho(LuazinhaParser.TrechoContext ctx) {
        // Continua a analise nos comandos que compoem o trecho (caso exista algum)
        if (ctx.comando() != null) {//se a lista que guarda os comandos não estiver vazio
            List<LuazinhaParser.ComandoContext> comandos = ctx.comando();
            //laço que percorre todos os comandos, um por um
            for (LuazinhaParser.ComandoContext comando : comandos) {
                visitComando(comando);
            }
        }

        // Apos a analise dos comandos e feita analise do ultimocomando (caso exista)
        if (ctx.ultimocomando() != null) {
            visitUltimocomando(ctx.ultimocomando());
        }
        return null;
    }
    /*Visitor da regra "comando", que pode derivar diferentes regras, tratadas no corpo do método.
     *É verificado a primeira regra de cada possibilidade, pois é o que as diferencia.*/
    @Override
    public Void visitComando(LuazinhaParser.ComandoContext ctx) {
        // Se a regra for comando :  listavar '=' listaexp
        if (ctx.listavar() != null) {
            // Adquire todos os nomes de variaveis utilizadas
            List<String> nomes = ctx.listavar().nomes;

            // faz a analise do lado direito da atribuicao '=' para verificar se as expressões são válidas
            visitListaexp(ctx.listaexp());
            for (String var : nomes) {
              /*Se o símbolo não existir na tabela de símbolos que está no topo da pilha, é
               *adicionado na tabela, com seu nome e tipo "variável"*/
                if (!pilhaDeTabelas.existeSimbolo(var)) {
                    pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
                }
            }
        }
        //se a regra for comando: 'function' nomedafuncao corpodafuncao
        else if (ctx.nomedafuncao() != null) {
            //cria uma nova tabela de símbolos com o nome passado para ctx.nomedafuncao()
            //Isto resulta em um novo escopo
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.nomedafuncao().nome));

            /*se for um metodo, é colocado na pilha o valor "self", utilizado para referir-se ao
             *próprio método*/
            if (ctx.nomedafuncao().metodo) {
                pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro");
            }
            //tratamento de corpodafuncao, regra seguinte à nome da função
            visitCorpodafuncao(ctx.corpodafuncao());

            pilhaDeTabelas.desempilhar();
        }
        // se a regra for comando: 'for' forNome=NOME '=' exp ',' exp (',' exp)? 'do' bloco 'end'
        else if (ctx.forNome != null) {
            //empilha uma nova tabela de símbolos com o nome for
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));

            //insere na tabela de símbolos a variável declarada para percorrer o laço
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.forNome.getText(), "variavel");

            //Analisa os comandos que estão dentro do for
            super.visitComando(ctx);

            pilhaDeTabelas.desempilhar();
        }

        //se a regra for comando: 'for' forListadenomes=listadenomes 'in' listaexp 'do' forBloco=bloco 'end'
        else if (ctx.forListadenomes != null) {
            //empilha nova tabela de símbolos com o nome "for"
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));

            //analisa a lista de expressões passadas depois de 'in'
            visitListaexp(ctx.listaexp());

            //para cada nome em lista de nomes declarado após 'for'
            for (String nome : ctx.forListadenomes.nomes) {
                //insere uma nova entrada na tabela de símbolo no topo com nome e tipo "variável"
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel");
            }

            //analisa bloco de comandos
            visitBloco(ctx.forBloco);

            pilhaDeTabelas.desempilhar();
        }

        //Se a regra for comando: 'local' 'function' localFunction=NOME corpodafuncao
        else if (ctx.localFunction != null) {
            //pega o nome da função do contexto e atribui a nomeFuncao
            String nomeFuncao = ctx.localFunction.getText();

            //verifica se há dois pontos no nome da função, o que a caracteriza como método
            if (nomeFuncao.contains(":")) {
                nomeFuncao = nomeFuncao.replace(":", ".");

                //Empilha nova tabela com o nome da função
                pilhaDeTabelas.empilhar(new TabelaDeSimbolos(nomeFuncao));
                //Empilha "self", que se refere ao próprio método
                pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro");
            } else {
                //senão não é método e não precisa se "self"
                pilhaDeTabelas.empilhar(new TabelaDeSimbolos(nomeFuncao));
            }

            //analisa corpo da função
            visitCorpodafuncao(ctx.corpodafuncao());

            pilhaDeTabelas.desempilhar();
        }

        //Se a regra for comando: 'local' localListadenomes=listadenomes ('=' listaexp)?
        else if (ctx.localListadenomes != null) {
            List<String> nomes = ctx.localListadenomes.nomes;

            //Analisa lista de expressões
            visitListaexp(ctx.listaexp());
            //Insere variável nome por nome na tabela de símbolos correntes
            for (String var : nomes) {
                pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
            }
        }
        // Realiza a análise nas demais alternativas do comando
        else {
            super.visitComando(ctx);
        }
        return null;
    }

    /*Visitor da regra expprefixo2*/
    /** expprefixo2 : var | chamadadefuncao | '(' exp ')' **/
    @Override
    public Void visitExpprefixo2(LuazinhaParser.Expprefixo2Context ctx) {
        if (ctx.var() != null) {
          //Se não existe a variável na tabela de símbolos do topo da pilha
            if (!pilhaDeTabelas.existeSimbolo(ctx.var().nome)) {
              //erro
                Mensagens.erroVariavelNaoExiste(ctx.var().linha, ctx.var().coluna, ctx.var().nome);
            }
            //se for chamada de função
        } else if (ctx.chamadadefuncao() != null) {
          //trata chamada de função
            visitChamadadefuncao(ctx.chamadadefuncao());
        } else {
          //analisa expressão
            visitExp(ctx.exp());
        }
        return null;
      }

    /*Visitor da regra listapar*/
    /** listapar : listadenomes (',' '...')? | '...' **/
    @Override
    public Void visitListapar(LuazinhaParser.ListaparContext ctx) {
      //coloca o nome dos parâmetros na lista nomes
        List<String> nomes = ctx.listadenomes().nomes;

        /*laço que percorre a lista "nomes" item por item e se este não existir
         *o símbolo é adicionado com seu nome e tipo "parâmetro"*/
        for (String nome : nomes) {
            if (!pilhaDeTabelas.topo().existeSimbolo(nome)) {
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "parametro");
            }
        }
        return null;
    }

}
