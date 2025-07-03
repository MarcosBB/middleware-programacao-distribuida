# Middleware for Distributed Systems

Implementação de um MiddleWare com um pequena aplicação de calculadora como exemplo

### EndPoints

Os seguintes comandos podem ser usados no terminal linux para teste dos endpoints:

Pode-se usar o endpoint `invoke`, indicando as referencias das anotacoes de `RemoteComponent (object)` e de `RemoteMethod (method)` no Json 

```
curl -X POST http://localhost:8080/invoke      -H "Content-Type: application/json"      -d '{
  "object": "calculator",
  "method": "subtract",
  "parameters": [1, 5],
  "parameterTypes": ["int", "int"]
}'
```

Ou utilizando o caminho `http://localhost:8080/<object>/<method> `

```
curl -X POST http://localhost:8080/calculator/add    -H "Content-Type: application/json"      -d '{
  "parameters": [1, 5],
  "parameterTypes": ["int", "int"]
}'
```