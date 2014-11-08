LR1Item_generator
=========
LR1Item_generator是一个用来生成LR(1)项目集的程序，它以一个LR(1)文法的产生式集合作为输入，经过计算生成LR(1)的项目集以及分析表(parsing tables)。
*********
Class简介:

LR1:包含了first,closure,goto,item等LR(1)项集生成的主要方法

LR1Item:表示一个LR(1)的项，包含一组产生式和一个序号

LR1Pro:表示一条产生式

Symbol:文法符号类，包括终结符，非终结符和ε
*********
输入格式:

1.文法符号与文法符号之间使用@分隔开

2.终结符号前使用!标识

3.每一行表示一个产生式

示例

S->L=R    S@L@!=@R  

S->R      S@R       

L->aR     L@!a@R    

L->b      L@!b      

R->L      R@L       

*********
to do:

1.待修改的一些小问题，已经使用E-R-R-O-R标识了

2.comment和readme仍然不完善，并且缺少示例

3.closure还存在一些问题(有些文法可能无限循环|并且没有将只有lookahead不同的两项合并)，尚需改进
