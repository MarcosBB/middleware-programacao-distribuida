# Middleware for Distributed Systems

Implementação de um Middleware com uma pequena aplicação de calculadora como exemplo

Para compilar o .jar do middleware, foi usa maven com as dependencias embutidas e também compilado classes da aplicação:

```
mvn clean package
```

Para executar a aplicação após a compilação:

```
java -cp out/app:target/middleware-1.0-SNAPSHOT-jar-with-dependencies.jar application.MainApplication
```

Se estiver usando o VC Code com o plugin de java basta clicar no "run" da classe MainApplication para executar as tarefas acima automaticamente (também pode usar o botão "play" da ide)

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

A aplicação desenvolvida com o Middleware é uma calculadora e ela possue três entidades remotas:
- `calculator` - A calculadora, com as quatro operações matematicas básicas para inteiros, essa entidade foi declarada como `PER_REQUEST`;
- `history` - Um logger de histórico das operações da calculadora, foi declarado como `PER_CLIENT`;
- `logger` - Um logger global que observa todas as chamadas remotas e calcula cada tempo de execução, foi declaro como `STATIC`

Exemplo de requisição para histórico:

```
curl -X GET http://localhost:8080/history/listall \
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

A arquitetura interna do middleware segue a implementação dos seguintes padrões:

## Broker

## Basic Remoting Patterns

### Server Request Handler

### Invoker

### Marshaller

### Remote Object

### Remoting Error

## Identification Patterns

### Lookup

### Object Id

### Absolute Object Reference

O AOR é implementado pela classe `AbsoluteObjectReference`, ele representa o endereço absoluto dos objetos dentro dos vários módulos da aplicação distribuída.

## Lifecycle Management

O ciclo de vida das instâncias de cada tipo de objeto remote é gerenciado pelo `InstanceManager` que encapsula essas instâncias num `RemoteInstace`. Cada entidade remota deve ser anotada com `@InstaceScope(ScopeType)`, onde o valor passado na anotação vai representar o tipo de ciclo de vida da instância daque objeto remoto.

### Static Instance

O valor da anotação é de `ScopeType.STATIC`, são objetos únicos para todo o ciclo de vida da aplicação.

### Per-Request Instance

O valor da anotação é de `ScopeType.PER_REQUEST`, são objetos únicos para cada requisição ou invocação.

### Client-Dependent Instance

O valor da anotação é de `ScopeType.PER_CLIENT`, são objetos únicos para cada cliente dentro do ciclo de vida.

## Lifecycle Management (continuação)

### Lazy Acquisition

O `InstanceManager` gerência as instâncias de forma "lazy" por padrão.

### Pooling

O `PoolingManager` gerência pools que armazenam os índices dos objetos anotados com @Poolable, a anotação defini o número de instâncias de cada pool, quando o `InstaceManager` tiver instânciado essa quantidade de objetos, eles passam a ser reulizados, cada pool funciona em lógica de fila, ou seja, quando um objeto é invocado, seu id irá para o final da fila.

Na nossa aplicação o `Calculator` é anotado como `@Poolable

### Leasing

O `LeasingManager` gerência as instâncias de cada `RemoteInstace`, verificando a cada período de tempo se alguma delas tem seu lease vencido, após isso elas são destruídas.

Na nossa aplicação o `Logger` e o `History` são anotados como `@Leasable`

### Passivation

## Extension Patterns

### Invocation Interceptor

O padrão Invocation Interceptor foi desenvolvida a interface `Interceptor`, que precisa ser implementada pelas classe interceptoras, a interface fornece os metodos para interceptar chamadas remotas antes e depois do método ser executado, fornece o índice para ordenação de priorida das intercepções e um variável que verifica que é um interceptor global ou local, interceptores local serão invocados apenas quando os métodos anotados com @IntercepThis("NomeDoInterceptor") forem chamados também.

Os interceptores são gerenciados pelo `InterceptorManager` e chamados diretamente pelo `Invoker` no momento certo.

### Invocation Context

O padrão Invocation Context é implementado pela classe `InvocationRequest`

### Protocol Plug-In

Para a implementação do padrão Protocol Plug-in foi criada uma interface `ProtocolInterface`.

Para o `ProtocolInterface` existem duas implementações no Middleware:
- `HttpProtocol` que é o padrão da aplicação e usa os endpoints mostrados acima;
- `UdpProtocol` que pode ser usado na mesma lógica do endpoint `/invoke`:
     ```
     echo '{
     "clientId": "4102676a-0f0b-49fb-b6ed-18f0eac64c01",
     "object": "history",
     "method": "listall",
     "parameters": [],
     "parameterTypes": [],
     "requestType": "GET"
     }' | nc -u -w1 localhost 8080 | jq
     ```
Isso permite que o usuário possa usar a interface para desenvolver seus proprios protocolos de comunicação, sem passar pela lógica do restando do middleware
