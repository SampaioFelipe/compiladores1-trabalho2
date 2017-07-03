package CC1.T2;

import java.util.List;

public class AnalisadorSemantico extends LuazinhaBaseVisitor<String> {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    @Override
    public String visitPrograma(LuazinhaParser.ProgramaContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        visitTrecho(ctx.trecho());

        pilhaDeTabelas.desempilhar();
        return super.visitPrograma(ctx);
    }

    @Override
    public String visitTrecho(LuazinhaParser.TrechoContext ctx) {

        if(ctx.comando() != null){
            for(LuazinhaParser.ComandoContext comando : ctx.comando()){
                visitComando(comando);
            }
        }
        return super.visitTrecho(ctx);
    }

    @Override
    public String visitBloco(LuazinhaParser.BlocoContext ctx) {
        return super.visitBloco(ctx);
    }

    @Override
    public String visitComando(LuazinhaParser.ComandoContext ctx) {

        if (ctx.listavar() != null) {
            List<String> nomes = ctx.listavar().nomes;
        } else if (ctx.nomedafuncao() != null) {

            pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.nomedafuncao().nome));

            visitCorpodafuncao(ctx.corpodafuncao());

            pilhaDeTabelas.desempilhar();
        }
        return super.visitComando(ctx);
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
        return super.visitExpprefixo2(ctx);
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

        for (int i = 0; i < nomes.size(); i++) {
            if(!pilhaDeTabelas.topo().existeSimbolo(nomes.get(i))){
                pilhaDeTabelas.topo().adicionarSimbolo(nomes.get(i),"parametro");
            }
            else {
                System.out.println("Parametro Duplicado");
            }
        }

        return super.visitListapar(ctx);
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
