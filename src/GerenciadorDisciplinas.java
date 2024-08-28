import java.io.*;
import java.util.*;

public class GerenciadorDisciplinas {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu Principal:");
            System.out.println("1. - Criar arquivo de respostas dos alunos");
            System.out.println("2. - Gerar resultados da disciplina");
            System.out.println("3. - Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    criarArquivoRespostas(scanner);
                    break;
                case "2":
                    gerarResultadoDisciplina(scanner);
                    break;
                case "3":
                    System.out.println("Encerrando o programa.");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void criarArquivoRespostas(Scanner scanner) {
        System.out.print("Digite o nome da disciplina: ");
        String nomeDisciplina = scanner.nextLine().trim();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Disciplinas/" + nomeDisciplina + ".txt"))) {
            while (true) {
                System.out.print("Digite as respostas do aluno (V ou F) e o nome do aluno, separados por tab (ou 'sair' para terminar): ");
                String linha = scanner.nextLine().trim();
                if (linha.equalsIgnoreCase("sair")) {
                    break;
                }
                writer.write(linha);
                writer.newLine();
            }
            System.out.println("Arquivo " + nomeDisciplina + ".txt criado com sucesso!\n");
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo: " + e.getMessage());
        }
    }

    private static void gerarResultadoDisciplina(Scanner scanner) {
        System.out.print("Digite o nome da disciplina: ");
        String nomeDisciplina = scanner.nextLine().trim();
        File arquivoDisciplina = new File("Disciplinas/" + nomeDisciplina + ".txt");
        if (!arquivoDisciplina.exists()) {
            System.out.println("Arquivo " + nomeDisciplina + ".txt não encontrado.");
            return;
        }

        System.out.print("Digite o caminho do arquivo de gabarito: ");
        String caminhoGabarito = scanner.nextLine().trim();
        File arquivoGabarito = new File("Gabaritos/" + caminhoGabarito + ".txt");
        if (!arquivoGabarito.exists()) {
            System.out.println("Arquivo de gabarito " + caminhoGabarito + " não encontrado.");
            return;
        }

        try {
            String gabarito = new BufferedReader(new FileReader(arquivoGabarito)).readLine().trim();
            List<Aluno> alunos = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoDisciplina))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    String[] partes = linha.split("\t");
                    String respostas = partes[0];
                    String nomeAluno = partes[1];

                    int pontos = 0;
                    if (!(respostas.equals("VVVVVVVVVV") || respostas.equals("FFFFFFFFFF"))) {
                        for (int i = 0; i < 10; i++) {
                            if (respostas.charAt(i) == gabarito.charAt(i)) {
                                pontos++;
                            }
                        }
                    }

                    alunos.add(new Aluno(nomeAluno, pontos));
                }
            }

            alunos.sort(Comparator.comparing(Aluno::getNome));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Resultados/Alfabetico/" + nomeDisciplina + ".txt"))) {
                for (Aluno aluno : alunos) {
                    writer.write(aluno.getNome() + "\t" + aluno.getPontos());
                    writer.newLine();
                }
            }

            alunos.sort(Comparator.comparing(Aluno::getPontos).reversed());
            double totalPontos = alunos.stream().mapToInt(Aluno::getPontos).sum();
            double media = !alunos.isEmpty() ? totalPontos / alunos.size() : 0;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Resultados/Pontuacao/" + nomeDisciplina + ".txt"))) {
                for (Aluno aluno : alunos) {
                    writer.write(aluno.getNome() + "\t" + aluno.getPontos());
                    writer.newLine();
                }
                writer.write("\nMédia da turma: " + String.format("%.2f", media));
            }

            System.out.println("\nResultado gerado com sucesso!");
            visualizarResultado(nomeDisciplina);

        } catch (IOException e) {
            System.err.println("Erro ao processar arquivos: " + e.getMessage());
        }
    }

    private static void visualizarResultado(String nomeDisciplina) {
        System.out.println("\nResultados ordenados por nome:");
        exibirArquivo("Resultados/Alfabetico/" + nomeDisciplina + ".txt");

        System.out.println("\nResultados ordenados por pontuação:");
        exibirArquivo("Resultados/Pontuacao/" + nomeDisciplina + ".txt");
    }

    private static void exibirArquivo(String nomeArquivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                System.out.println(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
