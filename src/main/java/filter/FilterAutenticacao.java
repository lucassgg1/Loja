package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import connection.SingleConnectionBanco;



//aula 2.15
// Tudo que vier da pasta "principal" ser� interceptado
@WebFilter(urlPatterns = { "/principal/*" }) /* Intercepta todas as requisi��es que vierem do projeto ou mapeamento */
public class FilterAutenticacao extends HttpFilter implements Filter {

	private static final long serialVersionUID = 1L;

	private static Connection connection; /* chama a conex�o [Class SingleConnectionBanco] */

	public FilterAutenticacao() {

	}

	// Encerra os processos quando o servidor � parado
	// finalizaria os processos de conex�o com o DB
	public void destroy() {
		try {
			connection.close(); // **Fecha a conex�o do DB/
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// Intercepta as requisi��es e d� as respostas no sistema
	// Tudo que fizer no sistema passar� por aqui
	/*
	 * Valida��o de autentica��o; Dar commit e rolback de transa��es no DB Validar e
	 * fazer redirecionamento de p�ginas
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpSession session = req.getSession();

			String usuarioLogado = (String) session.getAttribute("usuario");

			String urlParaAutenticar = req.getServletPath();/* URL que est� sendo acessada */

			// Validar se est� logado
			/*
			 * Se n�o estiver logado, e a URL que est� acessando for diferente da URL de
			 * login, redireciona para tela de login
			 */
			if (usuarioLogado == null || (usuarioLogado != null && usuarioLogado.isEmpty()) /* aula 2.15 */
					&& !urlParaAutenticar.contains("/principal/ServeletLogin")) { /* N�o est� logado */

				/*
				 * mando para url de login, e depois de logado redireciono para a url que ele
				 * estava tentando acessar
				 */
				RequestDispatcher redireciona = request.getRequestDispatcher("/login.jsp?url=" + urlParaAutenticar);
				// mando a mensagem
				request.setAttribute("msg", "Por favor realize o login!");
				// executo o redirecionamento
				redireciona.forward(request, response);
				return; // Para a execu��o e redireciona
			} else {
				chain.doFilter(request, response);
			}

			connection.commit(); /* se deu certo, executa/ salva as a��es solicitadas no DB */

		} catch (Exception e) {
			System.out.println("Erro na Class FilterAutenticacao/ doFilter");			
			
			//envia o erro para p�gina de erro
			RequestDispatcher redirecionar = request.getRequestDispatcher("/erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redirecionar.forward(request, response);

			try {
				connection.rollback(); /* desfaz as alterações feitas no DB - PESQUISAR SOBRE */
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	// Inicia os processos ou recursos quando o servidor sobe o projeto
	// iniciar a conex�o com o DB
	public void init(FilterConfig fConfig) throws ServletException {
		connection = SingleConnectionBanco.getConnection(); /* Aqui efetivamente � iniciada a conex�o ao DB */
	}

}
