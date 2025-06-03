# 🌦️ AtmosWeather

**AtmosWeather** é um aplicativo completo de previsão do tempo que permite ao usuário:

- Consultar a previsão de qualquer cidade do mundo
- Comparar temperatura e umidade entre duas cidades
- Favoritar cidades para acesso rápido
- Visualizar a previsão estendida de até 5 dias

---

## 🚀 Funcionalidades

- 🔍 Pesquisa de cidades para previsão do tempo
- 📊 Comparação entre cidades (temperatura e umidade)
- ⭐ Sistema de favoritos
- 📅 Previsão estendida de até 5 dias

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Linguagem:** Java  
- **Framework:** Spring Boot  
- **Integração:** API do OpenWeatherMap  
- **Documentação:** Swagger (disponível em [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html))

### Frontend
- **Linguagens:** Java/Kotlin e XML  
- **IDE:** Android Studio

### Banco de Dados
- **Tipo:** PostgreSQL  
- **Hospedagem:** Supabase

### Testes
- **Frameworks:** JUnit, Mockito  
- **Cobertura:** **91%**

### Arquitetura
- MVVM (Model-View-ViewModel)

---

## 🧪 Como Rodar o Projeto

### Pré-requisitos

- Java 17 ou superior
- IntelliJ IDEA (para backend)
- Android Studio (para frontend)
- Conta com chave da API do [OpenWeatherMap](https://openweathermap.org/api) (já configurada no projeto)
- Conexão com banco de dados PostgreSQL (via Supabase, já configurada)

### Passo a Passo

#### 🔙 Backend (Spring Boot)

1. Abra o projeto no **IntelliJ IDEA**.
2. Verifique se o Maven baixou as dependências corretamente.
3. Rode o projeto pela classe `SpringWeatherDataApplication.java` (com `@SpringBootApplication`).
4. O backend estará disponível em:  
   [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

> ⚠️ A porta `8081` é definida no `application.properties`.

#### 📱 Frontend (Android)

1. Abra o projeto do app no **Android Studio**.
2. Certifique-se de que o emulador esteja ativo ou um dispositivo físico esteja conectado.
3. Clique em "Run" (ícone ▶️) para iniciar o aplicativo.
4. O app irá se comunicar com o backend local (ajuste o IP, se necessário, caso esteja testando no celular).

---

## 📚 Documentação da API

### Endpoints Disponíveis

| **Método** | **Rota**                        | **Descrição**                                                     |
|------------|---------------------------------|-------------------------------------------------------------------|
| POST       | `/auth/login`                   | Autentica o usuário e retorna um token de acesso.                 |
| GET        | `/weather/{city}`               | Retorna a previsão do tempo (atual e próximos 5 dias) para uma cidade. |
| GET        | `/compare?city1={cidade1}&city2={cidade2}` | Compara temperatura e umidade entre duas cidades.                   |
| GET        | `/favorites`                    | Lista as cidades favoritados pelo usuário.                        |
| POST       | `/favorites`                    | Adiciona uma cidade aos favoritos.                                |
| DELETE     | `/favorites/{city}`             | Remove uma cidade dos favoritos.                                  |

> **Observação:** As rotas e os parâmetros foram definidos conforme os controllers enviados:
> - **AuthenticationController**
> - **ForecastController**
> - **ComparisonController**
> - **FavoriteCityController**

---

## ⚙️ Configuração

A configuração da aplicação (porta, conexões, tokens, etc.) está definida no arquivo `application.properties`.

---

## 📦 Deploy

O backend está preparado para conexão com o Supabase, utilizando um banco de dados PostgreSQL.

---

## 👥 Colaboradores

| Nome            | Função                      | GitHub                 |
|-----------------|-----------------------------|------------------------|
| [Seu Nome]      | Backend Developer           | [@Sandriely1999](#)       |
| [Colaborador 2] | Frontend Developer          | [@GustavoMCF](#)     |
| ...             | ...                         | ...                    |

---

## 📄 Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).

---

## 🔧 Contribuição

Se quiser contribuir para o AtmosWeather, por favor, siga as etapas abaixo:

1. Faça um fork do repositório.
2. Crie uma branch para sua feature:  
   ```bash
   git checkout -b feature/nome-da-feature
   ```
3. Commit suas alterações e envie:
   ```bash
   git commit -m "Descrição da feature"
   git push origin feature/nome-da-feature
   ```
4. Abra um Pull Request descrevendo as alterações realizadas.

---

