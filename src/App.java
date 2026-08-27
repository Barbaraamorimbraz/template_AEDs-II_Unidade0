import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

public class App {

	private static final String ARQUIVO_DADOS = "dadosProdutos.csv";
	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private static Produto[] produtos = new Produto[0];
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		produtos = lerProdutos(ARQUIVO_DADOS);
		IO.println(produtos.length + " produto(s) carregado(s) de \"" + ARQUIVO_DADOS + "\".");

		int opcao;
		do {
			exibirMenu();
			opcao = lerOpcaoMenu();

			switch (opcao) {
				case 1:
					listarTodosOsProdutos();
					break;
				case 2:
					cadastrarProduto();
					break;
				case 3:
					localizarProdutos();
					break;
				case 4:
					salvarProdutos(ARQUIVO_DADOS);
					IO.println("Dados salvos em \"" + ARQUIVO_DADOS + "\".");
					break;
				case 0:
					salvarProdutos(ARQUIVO_DADOS);
					IO.println("Dados salvos. Encerrando o programa...");
					break;
				default:
					IO.println("Opção inválida. Tente novamente.");
			}
			IO.println();

		} while (opcao != 0);

		scanner.close();
	}

	private static void exibirMenu() {
		IO.println("===== MENU =====");
		IO.println("1 - Listar todos os produtos");
		IO.println("2 - Cadastrar novo produto");
		IO.println("3 - Localizar produto");
		IO.println("4 - Salvar produtos no arquivo");
		IO.println("0 - Salvar e sair");
		IO.println("Escolha uma opção: ");
	}

	private static int lerOpcaoMenu() {
		try {
			int opcao = Integer.parseInt(scanner.nextLine().trim());
			return opcao;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Lê os dados de um arquivo-texto e retorna um vetor de produtos. Arquivo-texto
	 * no formato:
	 * N (quantidade de produtos) <br/>
	 * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
	 * Deve haver uma linha para cada um dos produtos.
	 * Retorna um vetor vazio em caso de problemas com a leitura do arquivo.
	 * 
	 * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
	 * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de
	 *         leitura.
	 */
	static Produto[] lerProdutos(String nomeArquivoDados) {

		try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivoDados))) {

			int quantidade = Integer.parseInt(leitor.readLine().trim());
			Produto[] vetor = new Produto[quantidade];

			for (int i = 0; i < quantidade; i++) {
				String linha = leitor.readLine();
				try {
					vetor[i] = Produto.criarDoTexto(linha);
				} catch (Exception e) {
					IO.println("Linha inválida ignorada: " + linha);
					vetor[i] = null;
				}
			}

			return vetor;

		} catch (IOException | NumberFormatException e) {
			System.out
					.println("Não foi possível ler o arquivo \"" + nomeArquivoDados + "\". Iniciando com vetor vazio.");
			return new Produto[0];
		}
	}

	/**
	 * Localiza um produto no vetor de produtos cadastrados, a partir do nome de
	 * produto informado pelo usuário,
	 * e imprime seus dados.
	 * A busca não é sensível ao caso. No caso de não encontrar o produto, imprime
	 * uma mensagem padrão
	 */
	static void localizarProdutos() {

		IO.println("Digite a descrição (ou parte dela) do produto a localizar: ");
		String busca = scanner.nextLine().trim();

		boolean encontrou = false;

		for (Produto produto : produtos) {
			if (produto != null && produto.getDescricao().toLowerCase().contains(busca.toLowerCase())) {
				IO.println(produto);
				encontrou = true;
			}
		}

		if (!encontrou) {
			IO.println("Nenhum produto encontrado com essa descrição.");
		}
	}

	/**
	 * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve
	 * todo o conteúdo do arquivo.
	 * 
	 * @param nomeArquivo Nome do arquivo a ser gravado.
	 */
	static void salvarProdutos(String nomeArquivo) {

		try (PrintWriter escritor = new PrintWriter(new FileWriter(nomeArquivo))) {

			long quantidadeValida = Arrays.stream(produtos).filter(p -> p != null).count();
			escritor.println(quantidadeValida);

			for (Produto produto : produtos) {
				if (produto != null) {
					escritor.println(produto.gerarDadosTexto());
				}
			}

		} catch (IOException e) {
			IO.println("Erro ao salvar o arquivo \"" + nomeArquivo + "\": " + e.getMessage());
		}
	}

	/** Lista todos os produtos cadastrados, numerados, um por linha */
	static void listarTodosOsProdutos() {

		if (produtos.length == 0) {
			IO.println("Não há produtos cadastrados.");
			return;
		}

		for (int i = 0; i < produtos.length; i++) {
			if (produtos[i] != null) {
				IO.println((i + 1) + " - " + produtos[i]);
			}
		}
	}

	/**
	 * Rotina para cadastro de um novo produto: pergunta ao usuário o tipo do
	 * produto, lê os dados correspondentes,
	 * cria o objeto adequado de acordo com seu tipo, e inclui o produto no vetor.
	 */
	static void cadastrarProduto() {

		IO.println("Tipo do produto (1 - não perecível, 2 - perecível): ");
		String tipo = scanner.nextLine().trim();

		IO.println("Descrição: ");
		String descricao = scanner.nextLine().trim();

		double precoCusto = lerDouble("Preço de custo: ");
		double margemLucro = lerDouble("Margem de lucro (ex.: 0.20 para 20%): ");

		Produto novoProduto;

		try {
			if (tipo.equals("2")) {
				LocalDate validade = lerData("Data de validade (dd/MM/yyyy): ");
				novoProduto = new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
			} else if (tipo.equals("1")) {
				novoProduto = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
			} else {
				IO.println("Tipo inválido. Cadastro cancelado.");
				return;
			}
		} catch (IllegalArgumentException e) {
			IO.println("Não foi possível cadastrar o produto: " + e.getMessage());
			return;
		}

		produtos = Arrays.copyOf(produtos, produtos.length + 1);
		produtos[produtos.length - 1] = novoProduto;

		IO.println("Produto cadastrado com sucesso!");
	}

	private static double lerDouble(String mensagem) {
		while (true) {
			IO.println(mensagem);
			try {
				return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
			} catch (NumberFormatException e) {
				IO.println("Valor inválido. Digite um número (ex.: 12.50).");
			}
		}
	}

	private static LocalDate lerData(String mensagem) {
		while (true) {
			IO.println(mensagem);
			try {
				return LocalDate.parse(scanner.nextLine().trim(), FORMATO_DATA);
			} catch (DateTimeParseException e) {
				IO.println("Data inválida. Use o formato dd/MM/yyyy.");
			}
		}
	}
}
