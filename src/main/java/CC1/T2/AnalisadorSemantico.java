package CC1.T2;

import java.util.List;

public class AnalisadorSemantico extends LuazinhaBaseVisitor<String> {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    @Override
    public String visitPrograma(LuazinhaParser.ProgramaContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        visitTrecho(ctx.trecho());

        pilhaDeTabelas.desempilhar();
        return null;
    }

    @Override
    public String visitTrecho(LuazinhaParser.TrechoContext ctx) {

        if (ctx.comando() != null) {
            List<LuazinhaParser.ComandoContext> comandos = ctx.comando();
            for (LuazinhaParser.ComandoContext comando : comandos) {
                visitComando(comando);
            }
        }
        if(ctx.ultimocomando() != null){
            visitUltimocomando(ctx.ultimocomando());
        }
        return null;
    }

    @Override
    public String visitBloco(LuazinhaParser.BlocoContext ctx) {
        return super.visitBloco(ctx);
    }

    @Override
    public String visitComando(LuazinhaParser.ComandoContext ctx) {

        if (ctx.listavar() != null) {
            List<String> nomes = ctx.listavar().nomes;
            visitListaexp(ctx.listaexp());

            for (String var : nomes) {
                if (!pilhaDeTabelas.existeSimbolo(var)) {
                    pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
                }
            }
        } else if (ctx.nomedafuncao() != null) {

            String nomeFuncao = ctx.nomedafuncao().nome;

            if (nomeFuncao.contains(":")) {
                nomeFuncao = nomeFuncao.replace(":", ".");

                pilhaDeTabelas.empilhar(new TabelaDeSimbolos(nomeFuncao));

                pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro");
            } else {
                pilhaDeTabelas.empilhar(new TabelaDeSimbolos(nomeFuncao));
            }

            visitCorpodafuncao(ctx.corpodafuncao());

            pilhaDeTabelas.desempilhar();
        } else if (ctx.forNome != null) {
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.forNome.getText(), "variavel");
            super.visitComando(ctx);
            pilhaDeTabelas.desempilhar();
        } else if (ctx.forListadenomes != null) {
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));

            visitListaexp(ctx.listaexp());

            for (String nome : ctx.forListadenomes.nomes) {
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel");
            }

            visitBloco(ctx.forBloco);

            pilhaDeTabelas.desempilhar();
        } else if (ctx.localFunction != null) {
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
        } else if (ctx.localListadenomes != null) {
            System.out.println("Local aqui");
            List<String> nomes = ctx.localListadenomes.nomes;
            visitListaexp(ctx.listaexp());
            for (String var : nomes) {
                pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
            }
        } else {
            super.visitComando(ctx);
        }
        return null;
    }

    @Override
    public String visitUltimocomando(LuazinhaParser.UltimocomandoContext ctx) {
        return super.visitUltimocomando(ctx);
    }

    @Override
    public String visitNomedafuncao(LuazinhaParser.NomedafuncaoContext ctx) {
        return super.visitNomedafuncao(ctx);
    }

    @Override
    public String visitListavar(LuazinhaParser.ListavarContext ctx) {
        return super.visitListavar(ctx);
    }

    @Override
    public String visitVar(LuazinhaParser.VarContext ctx) {
        return super.visitVar(ctx);
    }

    @Override
    public String visitListadenomes(LuazinhaParser.ListadenomesContext ctx) {
        return super.visitListadenomes(ctx);
    }

    @Override
    public String visitListaexp(LuazinhaParser.ListaexpContext ctx) {
        return super.visitListaexp(ctx);
    }

    @Override
    public String visitExp(LuazinhaParser.ExpContext ctx) {
        return super.visitExp(ctx);
    }

    @Override
    public String visitExpprefixo(LuazinhaParser.ExpprefixoContext ctx) {
        return super.visitExpprefixo(ctx);
    }

    @Override
    public String visitExpprefixo2(LuazinhaParser.Expprefixo2Context ctx) {
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

    @Override
    public String visitChamadadefuncao(LuazinhaParser.ChamadadefuncaoContext ctx) {
        return super.visitChamadadefuncao(ctx);
    }

    @Override
    public String visitArgs(LuazinhaParser.ArgsContext ctx) {
        return super.visitArgs(ctx);
    }

    @Override
    public String visitFuncao(LuazinhaParser.FuncaoContext ctx) {
        return super.visitFuncao(ctx);
    }

    @Override
    public String visitCorpodafuncao(LuazinhaParser.CorpodafuncaoContext ctx) {

        return super.visitCorpodafuncao(ctx);
    }

    @Override
    public String visitListapar(LuazinhaParser.ListaparContext ctx) {

        List<String> nomes = ctx.listadenomes().nomes;

        for (String nome : nomes) {
            if (!pilhaDeTabelas.topo().existeSimbolo(nome)) {
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "parametro");
            }
        }
        return null;
    }

    @Override
    public String visitConstrutortabela(LuazinhaParser.ConstrutortabelaContext ctx) {
        return super.visitConstrutortabela(ctx);
    }

    @Override
    public String visitListadecampos(LuazinhaParser.ListadecamposContext ctx) {
        return super.visitListadecampos(ctx);
    }

    @Override
    public String visitCampo(LuazinhaParser.CampoContext ctx) {
        return super.visitCampo(ctx);
    }

    @Override
    public String visitSeparadordecampos(LuazinhaParser.SeparadordecamposContext ctx) {
        return super.visitSeparadordecampos(ctx);
    }

    @Override
    public String visitOpbin(LuazinhaParser.OpbinContext ctx) {
        return super.visitOpbin(ctx);
    }

    @Override
    public String visitOpunaria(LuazinhaParser.OpunariaContext ctx) {
        return super.visitOpunaria(ctx);
    }
}
