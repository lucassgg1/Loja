package connection;

import java.sql.Connection;
import java.sql.DriverManager;

//Aula 2.16
/*
 * Apenas uma conex�o por sistema
 * NÃO FICAR fechando e abrindo a conex�o com o DB. Isso gera muita lentid�o
 * o que abre e fecha s�o as sess�es e transa��es
 */
public class SingleConnectionBanco {

	/*
	 * static porque n�o vai mudar [?autoReconnect=true] caso caia a conex�o se
	 * reconecta automaticamente
	 */
	private static String banco = "jdbc:postgresql://dlei-servlet.postgres.database.azure.com:5432/dlei-servlet?autoReconnect=true";
	private static String user = "postgres";
	private static String senha = "Mypstdb@";
	private static Connection connection = null;

	/* retorna a conex�o existente */
	public static Connection getConnection() {
		return connection;
	}

	/* permite chamar a classe que faz a conex�o em qualquer local */
	static {
		conectar();
	}

	/* Quando houver uma inst�ncia ir� conectar */
	public SingleConnectionBanco() {
		conectar();
	}

	private static void conectar() {
		/* Se a conex�o for igual a nula, ou seja, se n�o houver conex�o */
		try {
			if (connection == null) {
				Class.forName("org.postgresql.Driver");/* Class respons�vel por carregar o driver de conex�o do DB */
				connection = DriverManager.getConnection(banco, user, senha);/* fornece os dados para conex�o ao DB */
				connection.setAutoCommit(false); /* Para n�o efetuar altera��o no DB sem nosso comando */
				System.out.println("\n--> Conex�o com o DB realizada com sucesso! <--"
						+ "\n==> DB: Azure/dlei-servlet <==\n");

			}
		} catch (Exception e) {
			System.out.println("\n--> Erro na conex�o com o DB <--"
					+ "\n==> DB: Azure/dlei-servlet <==\n"
					+ "--> VER Class SingleConnectionBanco <--\n");
			e.printStackTrace(); /* Mostrar qualquer erro no momento que conectar */
		}
	}
}
