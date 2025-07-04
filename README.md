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
          "parameters": [1, 5],
          "parameterTypes": ["int", "int"]
        }' | jq
```