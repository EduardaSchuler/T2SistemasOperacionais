# T2SistemasOperacionais
Trabalho 2 da disciplina de Sistemas Operacionais com o professor Sergio Johann

## Objetivo

Implementar um simulador de gerenciamento de memória paginada. O simulador recebe uma sequência de endereços virtuais e gera como saída os endereços físicos correspondentes, além de dumps das estruturas internas (TLB, tabela de páginas e memória física).

## Resumo da implementação

- TLB configurável (2^n entradas, onde `n = tlbEntriesBits`). A implementação atual usa política FIFO para substituição de entradas (substitui a entrada mais antiga por inserção).
- Tabela de páginas configurável em 1, 2 ou 3 níveis. A divisão dos bits do VPN entre os níveis é calculada dinamicamente com base na configuração (veja detalhes abaixo).
- Memória física representada por um vetor de molduras inicializadas com `-1` e preenchidas sob demanda.
- Substituição de molduras: LRU (Least Recently Used) — o simulador armazena o timestamp do último acesso a cada moldura e escolhe a menos recentemente acessada quando for necessário substituir.

> Observação: LRU na nossa implementação aplica-se à substituição de molduras da memória física (item h do enunciado). A TLB usa política FIFO (entradas mais antigas são removidas). As duas estratégias são complementares: LRU gerencia frames, enquanto a TLB mantém traduções recentes.

## Como compilar

Abra PowerShell no diretório do projeto e execute:
```powershell
javac -d .\bin .\*.java
```

## Como rodar

```powershell
java -cp .\bin MemoryPagingSimulator config.json enderecos.txt saida.txt
```

- `config.json`: arquivo de configuração (exemplo em `examples/config.json`).
- `enderecos.txt`: um endereço virtual decimal por linha (ex.: `0`, `4096`, `8192`, ...).
- `saida.txt`: arquivo de saída gerado pelo simulador.

## Formato de `config.json`

- `tlbEntriesBits` — número de bits para as entradas da TLB (a TLB terá 2^n entradas). Se 0 ou negativo → TLB desabilitada.
- `virtualAddrBits` — bits do espaço de endereçamento virtual (2^n endereços).
- `physicalAddrBits` — bits do espaço físico (2^n endereços físicos).
- `pageSizeBits` — número de bits do deslocamento de página (tamanho da página = 2^n).
- `pageTableLevels` — 1, 2 ou 3 (níveis da tabela de páginas).
- `textBits`, `dataBits`, `stackBits` — tamanhos dos segmentos em bits (o `bss` é calculado automaticamente).

Exemplo:
```json
{
	"tlbEntriesBits": 4,
	"virtualAddrBits": 16,
	"physicalAddrBits": 14,
	"pageSizeBits": 8,
	"pageTableLevels": 2,
	"textBits": 12,
	"dataBits": 12,
	"stackBits": 12
}
```

## Formato de `saida.txt`

- Linhas de tradução por referência:
	- `V=<virtual> -> P=<physical> frame=<n> TLB=<HIT|MISS>`
- `-- PAGE TABLE ---` → entradas da tabela de páginas (índices → frame).
- `--- PHYSICAL MEMORY ---` → cada frame: `Frame i -> vpn=<x> last=<t>` (`-1` = livre).
- `--- TLB ---` → entradas atuais da TLB: `VPN=<k> -> frame=<v>`.

## Decisões e observações de implementação

- A divisão de bits do VPN entre níveis de tabela de páginas é calculada dinamicamente (distribui os bits do VPN entre os níveis de forma equilibrada). Isso evita o uso de um valor fixo e torna a implementação compatível com diferentes tamanhos de endereços.
- A implementação atual usa `LinkedHashMap` com ordem de inserção para a TLB (FIFO). Se desejarmos LRU também na TLB, isso pode ser alterado na `LinkedHashMap` em modo access-order.
- `Config.fromJson` utiliza um parser simples para campos numéricos; o arquivo `config.json` deve seguir o formato mostrado no exemplo.

## Exemplos

Há um diretório `examples/` com um `config.json`, `enderecos.txt` e um `saida_expected.txt` gerado de exemplo. Use esses arquivos para testar rapidamente a execução do simulador.

## Entrega

O trabalho deve ser entregue no Moodle (código fonte) até a data definida pelo professor. A apresentação será realizada em aula nas máquinas do laboratório.
