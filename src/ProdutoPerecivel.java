import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ProdutoPerecivel extends Produto {

    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private LocalDate dataDeValidade;

    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);

        if (validade == null || validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior ao dia de hoje.");
        }
        this.dataDeValidade = validade;
    }

    public ProdutoPerecivel(String desc, double precoCusto, LocalDate validade) {
        super(desc, precoCusto);

        if (validade == null || validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior ao dia de hoje.");
        }
        this.dataDeValidade = validade;
    }

    @Override
    public double valorVenda() {

        if (dataDeValidade.isBefore(LocalDate.now())) {
            throw new IllegalStateException("Produto vencido: venda não permitida.");
        }

        double valor = precoCusto * (1.0 + margemLucro);

        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataDeValidade);
        if (diasRestantes <= PRAZO_DESCONTO) {
            valor = valor * (1.0 - DESCONTO);
        }

        return valor;
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto. Preço e margem de lucro são formatados com 2 casas decimais.
     * Data de validade é formatada no formato dd/mm/aaaa
     * @return Uma string no formato "2;descrição;preçoDeCusto;margemDeLucro;dataDeValidade"
     */
	@Override
    public String gerarDadosTexto() {
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		return String.format(Locale.forLanguageTag("pt-BR"), "2;%s;%.2f;%.2f;%s",
				getDescricao(), precoCusto, margemLucro, formato.format(dataDeValidade));
	}

    @Override
    public String toString() {

       DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        String dados = super.toString();
        dados += "\nVálido até " + formato.format(dataDeValidade);
        
        return dados;
    }

}
