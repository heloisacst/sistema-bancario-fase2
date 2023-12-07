## Instruções para Compilação e Execução da Aplicação Bancária em Java com Neo4j

### Pré-requisitos:

Certifique-se de ter o JDK (Java Development Kit) instalado em sua máquina.
Faça o download e instale o Neo4j em neo4j.com.


Passos:

### 1.  Clone o Repositório:

Clone o repositório que contém o código-fonte da aplicação bancária.

```
git clone -b neo4j https://github.com/heloisacst/sistema-bancario-fase2.git
``` 
### 2. Configuração do Banco de Dados Neo4j:

Inicie o Neo4j e certifique-se de que o banco de dados esteja em execução.
Altere as configurações de conexão no código Java no caminho `main > java connection` na classe `ConnectionManager` para refletir a sua instância do Neo4j (por exemplo, URL, usuário, senha).

### 3. Execução do programa:

Navegue até a classe principal _(main)_.
No arquivo fonte, localize o método main e clique com o botão direito sobre ele.
Selecione a opção "Run" ou "Run As" na IDE e escolha "Java Application" para executar o programa.

### 4. Interaja com o Sistema Bancário:

Siga as instruções no console da aplicação para interagir com o sistema bancário. Isso pode incluir depósitos, consultas de saldo, etc.

### 5. Encerramento:

Após interagir com a aplicação, encerre o programa e feche a conexão com o Neo4j.



#### *Observações:*

Certifique-se de que o Neo4j esteja em execução e acessível antes de iniciar a aplicação Java.

