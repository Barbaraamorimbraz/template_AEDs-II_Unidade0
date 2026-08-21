import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    @Override
    public String toString() {

        NumberFormat moeda = NumberFormat.getCurrencyInstance();

        try {
            return String.format("NOME: " + getDescricao() + ": " + moeda.format(valorVenda()));
        } catch (IllegalStateException e) {
            return String.format("NOME: " + getDescricao() + ": PRODUTO VENCIDO (validade: " + dataDeValidade + ")");
        }
    }

}
