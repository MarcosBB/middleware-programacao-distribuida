# Middleware for Distributed Systems

Implementação de um MiddleWare com um pequena aplicação de calculadora como exemplo

Para compilar o .jar do middleware usando maven com as dependencias embutidas e também compilar classes da aplicação:

```
mvn clean package
```

Para executar a aplicação após a compilação:

```
java -cp out/app:target/middleware-1.0-SNAPSHOT-jar-with-dependencies.jar application.MainApplication
```

Se estiver usando o vs code com o plugin de java basta clicar no "run" da classe MainApplication para executar as tarefas acima automaticamente (também pode usar o botão "play" da ide)

### EndPoints

Os seguintes comandos podem ser usados no terminal linux para teste dos endpoints:

Pode-se usar o endpoint `invoke`, indicando as referencias das anotacoes de `RemoteComponent (object)` e de `RemoteMethod (method)` no Json 

```
curl -X POST http://localhost:8080/invoke \
     -H "Content-Type: application/json" \
     -d '{
          "clientId": "4102676a-0f0b-49fb-b6ed-18f0eac64c01",
          "object": "calculator",
          "method": "subtract",
          "parameters": [1, 5],
          "parameterTypes": ["int", "int"]
        }' | jq
```

Ou utilizando o caminho `http://localhost:8080/<object>/<method> `

```
curl -X POST http://localhost:8080/calculator/add \
     -H "Content-Type: application/json" \
     -d '{
          "clientId": "4102676a-0f0b-49fb-b6ed-18f0eac64c01",
          "parameters": [1, 5],
          "parameterTypes": ["int", "int"]
        }' | jq
```

Pode-se omitir o `clientId` na primeira chamada, a resposta da requisição vem no seguinte formato:

```
{
  "result": 6,
  "clientId": "4102676a-0f0b-49fb-b6ed-18f0eac64c01",
  "requestId": "93548ac2-3d96-48f5-a461-ef02f87cd1b4"
}
```

Assim é possível resgatar o `clientId`, e usá-lo nas proximas requisições, isso gera coesão nas entidades que declaradas como `PER_CLIENT`

A aplicação desenvolvida com o MiddleWare é uma calculadora e ela possue três entidades remotas:
- `calculator` - A calculadora, com as quatro operações matematicas básicas para inteiros, essa entidade foi declarada como `PER_REQUEST`;
- `history` - Um logger de histórico das operações da calculadora, foi declarado como `PER_CLIENT`;
- `logger` - Um logger global que observa todas as chamadas remotas e calcula cada tempo de execução, foi declaro como `STATIC`

Exemplo de requisição para histórico:

```
curl -X GET http://localhost:8080/history/getall \
     -H "Content-Type: application/json" \
     -d '{
          "clientId": "4102676a-0f0b-49fb-b6ed-18f0eac64c01",
          "parameters": [],
          "parameterTypes": []
        }' | jq
```

Exemplo de requisição para logger:

```
curl -X GET http://localhost:8080/logger/all \
     -H "Content-Type: application/json" \
     -d '{
          "clientId": "4102676a-0f0b-49fb-b6ed-18f0eac64c01",
          "parameters": [],
          "parameterTypes": []
        }' | jq
```
