# apiGateServiceFrame
该工程是为了解决不同结构的报文互转的问题。主要功能包括
1、解析目前的ESB的XML报文，转成map结构
2、将ESBXML报文的转换结果，转换成另外一种结构，最终转换成json。换句话说，将A结构->B结构，这两种结构可以完全是异构的
3、将json报文转换成ESB的XML报文
