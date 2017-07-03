package CC1.T2;

import java.util.List;

public class AnalisadorSemantico extends LuazinhaBaseVisitor<Void> {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    /** programa : trecho **/
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

    /** trecho : (comando ';'?)* (ultimocomando ';'?)? **/
    @Override
    public Void visitTrecho(LuazinhaParser.TrechoContext ctx) {
        // Continua a analise nos comandos que compoem o trecho (caso exista algum)
        if (ctx.comando() != null) {
            List<LuazinhaParser.ComandoContext> comandos = ctx.comando();
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

    /** comando pode derivar regras distintas (que estão sendo tratadas no corpo do metodo)**/
    @Override
    public Void visitComando(LuazinhaParser.ComandoContext ctx) {
        // comando :  listavar '=' listaexp
        if (ctx.listavar() != null) {
            // Adquire todos os nomes de variaveis utilizadas
            List<String> nomes = ctx.listavar().nomes;

            // faz a analise do lado direito da atribuicao '=' para verificar
            visitListaexp(ctx.listaexp());
            for (String var : nomes) {
                if (!pilhaDeTabelas.existeSimbolo(var)) {
                    pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
                }
            }
        }
        // comando: 'function' nomedafuncao corpodafuncao
        else if (ctx.nomedafuncao() != null) {

            pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.nomedafuncao().nome));

            if (ctx.nomedafuncao().metodo) {
                pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro");
            }

            visitCorpodafuncao(ctx.corpodafuncao());

            pilhaDeTabelas.desempilhar();
        }
        // comando: 'for' forNome=NOME '=' exp ',' exp (',' exp)? 'do' bloco 'end'
        else if (ctx.forNome != null) {
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.forNome.getText(), "variavel");
            super.visitComando(ctx);
            pilhaDeTabelas.desempilhar();
        }
        // comando: 'for' forListadenomes=listadenomes 'in' listaexp 'do' forBloco=bloco 'end'
        else if (ctx.forListadenomes != null) {
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));

            visitListaexp(ctx.listaexp());

            for (String nome : ctx.forListadenomes.nomes) {
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel");
            }

            visitBloco(ctx.forBloco);

            pilhaDeTabelas.desempilhar();
        }
        // comando: 'local' 'function' localFunction=NOME corpodafuncao
        else if (ctx.localFunction != null) {
            String nomeFuncao = ctx.localFunction.getText();

            if (nomeFuncao.contains(":")) {
                nomeFuncao = nomeFuncao.replace(":", ".");

                pilhaDeTabelas.empilhar(new TabelaDeSimbolos(nomeFuncao));

                pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro");
            } else {
                pilhaDeTabelas.empilhar(new TabelaDeSimbolos(nomeFuncao));
            }

            visitCorpodafuncao(ctx.corpodafuncao());

            pilhaDeTabelas.desempilhar();
        }
        // comando: 'local' localListadenomes=listadenomes ('=' listaexp)?
        else if (ctx.localListadenomes != null) {
            List<String> nomes = ctx.localListadenomes.nomes;
            visitListaexp(ctx.listaexp());
            for (String var : nomes) {
                pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
            }
        }
        // Realiza a análise nas demais alternativas do camando
        else {
            super.visitComando(ctx);
        }
        return null;
    }
    /** expprefixo2 : var | chamadadefuncao | '(' exp ')' **/
    @Override
    public Void visitExpprefixo2(LuazinhaParser.Expprefixo2Context ctx) {
        if (ctx.var() != null) {
            if (!pilhaDeTabelas.existeSimbolo(ctx.var().nome)) {
                Mensagens.erroVariavelNaoExiste(ctx.var().linha, ctx.var().coluna, ctx.var().nome);
            }
        } else if (ctx.chamadadefuncao() != null) {
            visitChamadadefuncao(ctx.chamadadefuncao());
        } else {
            visitExp(ctx.exp());
        }

        return null;
    }

    /** listapar : listadenomes (',' '...')? | '...' **/
    @Override
    public Void visitListapar(LuazinhaParser.ListaparContext ctx) {

        List<String> nomes = ctx.listadenomes().nomes;

        for (String nome : nomes) {
            if (!pilhaDeTabelas.topo().existeSimbolo(nome)) {
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "parametro");
            }
        }
        return null;
    }

}
