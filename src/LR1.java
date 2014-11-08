/*
	Author	まつまつ！！
	Date	2014/05
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class LR1 {
	String[] sym1 = {"S","C","c","d","$"};
	/*	
	 * Name			firstForString
	 * Date			2014/05
	 * Discribe		返回一串文法符号的first集||Return the "first" sets for a string of symbol
	 * Parameters	ArrayList<Symbol> syms:一串文法符号||a string of grammar symbol
	 * 				LR1Item gram:文法的所有产生式||the total productions of this grammar
	 * Return		first集||"first" set
	 */
	public ArrayList<Symbol> firstForString(ArrayList<Symbol> syms,LR1Item gram){
		//定义返回值||define return value
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		//eps_count表示s中从第一个可以推出ε的符号的到不能推出ε的符号的计数||eps_count is the count of symbols which can shift to ε(until which cannot)
		int eps_count=0;
		
		//计算eps_count的值||calculate the eps_count
		while(eps_count<syms.size()&&ifExist("0",first(syms.get(eps_count),gram))){
			eps_count++;
		}
		
		//result的值就是所有可以推出ε的符号（直到不能）的first集的并||the result is the "first" sets of this symbols
		for(int i=0;i<eps_count+1;i++){
			if(i<syms.size()){
				result.addAll(first(syms.get(i),gram));
			}
		}
		
		//去除重复项和ε||delete the duplicate items and ε
		selDistItem(result);
		
		//产生式中所有的符号都可以推出ε，则把ε添加到结果中||if all the symbols can shift to ε then add ε to the result
		if(eps_count==syms.size()){
			result.add(new Symbol(2));
		}
		
		return result;
	}
	/*	
	 * Name			first
	 * Date			2014/05
	 * Discribe		返回单个文法符号的first集||Return the "first" sets for a symbol
	 * Parameters	Symbol sym:一个文法符号||a grammar symbol
	 * 				LR1Item gram:文法的所有产生式||the total productions of this grammar
	 * Return		first集||"first" set
	 */
	public ArrayList<Symbol> first(Symbol sym,LR1Item gram){
		//定义返回值||define return value
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		if(isTerminal(sym)){
			//终结符号，直接返回s||terminal symbol, return itself
			result.add(sym);
		} else {
			//非终结符号，寻找它的产生式||if s is a nonterminal symbol, find its production rules
			for(int i=0;i<gram.size();i++){
				if(gram.get(i).getLeftsymbol().equals(sym)){
					//存在s->xxx的产生式||find a production starting with s
					ArrayList<Symbol> pro = gram.get(i).getProduction();
					
					//eps_count表示s中从第一个可以推出ε的符号的到不能推出ε的符号的计数||eps_count is the count of symbols which can shift to ε(until which cannot)
					int eps_count=0;
					
					if(pro.get(0).getTml()==2){
						//存在s->0的产生式||if s shift to ε, add ε
						result.add(new Symbol(2));
					} else {
						//计算eps_count的值||calculate the eps_count
						while(eps_count<pro.size()&&ifExist("0",first(pro.get(eps_count),gram))){				
							eps_count++;
						}
						
						//result的值就是所有可以推出ε的符号（直到不能）的first集的并||the result is the "first" sets of this symbols
						for(int j=0;j<eps_count+1;j++){
							if(j<pro.size()){
								result.addAll(first(pro.get(j),gram));
							}
						}
						
						//去除重复项和0||delete the duplicate items and ε
						selDistItem(result);
						
						//产生式中所有的符号都可以推出ε，则把ε添加到结果中||if all the symbols can shift to ε then add ε to the result
						if(eps_count==pro.size()){
							result.add(new Symbol(2));
						}
					}
				}
			}
		}
		return result;
	}
	/*	
	 * Name			Closure
	 * Date			2014/05
	 * Discribe		Return the closure sets for productions||返回产生式的closure集
	 * Parameters	ArrayList<LR1Pro> pro:一条产生式||a production
	 * 				LR1Item gram:文法的所有产生式||the total productions of this grammar
	 * Return		closure集||closure set
	 */
	public ArrayList<LR1Pro> Closure(ArrayList<LR1Pro> pro,LR1Item gram){
		
		//定义返回值||define return value
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//使用queue来计算closure集||use a queue to calculate the closure set
		Queue<LR1Pro> que = new LinkedList<LR1Pro>();
		
		//为了不改变之前的产生式集，所有计算都建立在克隆的产生式集中||all the action is on the cloned productions (to protect the original one)
		ArrayList<LR1Pro> pro_clone = (ArrayList<LR1Pro>) pro.clone();
		
		//将所有产生式添加到queue中||add all productions to queue
		for(int i=0;i<pro_clone.size();i++){
			que.add(pro_clone.get(i));
		}
		
		//弹出第一条产生式||pump the first one
		LR1Pro tempI = que.poll();
		
		while(tempI!=null){
			//tempI为null意味着结束||if tempI is null, stop the loop
			
			if(!tempI.isTerminal()){
				//如果产生式当前项（即将要移近的下一项）是非终结符
				
				//找到这个非终结符的所有产生式并添加到queue中||this loop is to find the production starting with tempI, and add them to the queue
				for(int i=0;i<gram.size();i++){
					if(gram.get(i).getLeftsymbol().equals(tempI.getProduction().get(tempI.getPosition()))){
						//s用来存放lookahead||sym_la is used to save the lookahead symbol
						ArrayList<Symbol> sym_la = new ArrayList<Symbol>();
						
						//sym_aft用来暂存产生式中当前处理的非终结符之后的部分||sym_aft is the part after the nonterminal symbol above
						ArrayList<Symbol> sym_aft = new ArrayList<Symbol>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().size();j++){
							sym_aft.add(tempI.getProduction().get(j));
						}
						
						//对每个lookahead进行循环||loop all of lookahead of this production
						for(int j=0;j<tempI.getLookahead().size();j++){
							//为了不改变sym_aft（要多次使用）此处对sym_aft进行clone||sym_aft_clone is the clone of sym_aft to not change the old one(because it will be used many times)
							ArrayList<Symbol> sym_aft_clone = (ArrayList<Symbol>)sym_aft.clone();
							
							//将sym_la和sym_aft连接起来，求first集，结果即是tempJ的lookahead||the "first" set of (sym_aft+sym_la) is the lookahead of tempJ
							sym_aft_clone.add(tempI.getLookahead().get(j));
							sym_aft_clone = firstForString(sym_aft_clone,gram);
							sym_la.addAll(sym_aft_clone);
						}
						
						//删除重复的项目||delete the duplicate items
						selDistItem(sym_la);
						
						//将B->.γ,b加入到队列中||tempJ is to be saved the production which will be added to queue
						LR1Pro tempJ = new LR1Pro(gram.get(i).getLeftsymbol(), gram.get(i).getProduction(), gram.get(i).getLookahead(), gram.get(i).getPosition());
						tempJ.setLookahead(sym_la);
						que.add(tempJ);
					}
				}
			}
			
			//进行完上述操作后将tempI加到结果集中||add tempI to result set after finding the productions generated from it
			result.add(tempI);
			
			//继续从队列中弹出产生式||pump the next one
			tempI = que.poll();
		}
		
		//删除重复的产生式||delete the duplicate productions
		selDistProd(result);
		return result;
	}
	/*	
	 * Name			Goto
	 * Date			2014/05
	 * Discribe		进行Goto操作||just Goto:)
	 * Parameters	LR1Item pros:一组产生式||a set of production
	 * 				String sym:文法符号||grammar symbol
	 * 				LR1Item gram:文法的所有产生式||the total productions of this grammar
	 * Return		Goto之后的集合||the Symbol set after Goto
	 */
	public ArrayList<LR1Pro> Goto(LR1Item pros, String sym, LR1Item gram){
		
		//定义返回值||define return value
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//对每个产生式进行移进操作||shift for every production
		for(int i=0;i<pros.size();i++){
			//防止浅clone||deep clone
			LR1Pro pros_clone = new LR1Pro(pros.get(i).getLeftsymbol(), pros.get(i).getProduction(), pros.get(i).getLookahead(), pros.get(i).getPosition());
			
			//第一个if的作用不明E-R-R-O-R??????????????????????????????????????????????????????????????????????
			if(pros_clone.getPosition()<pros_clone.getProduction().size()){
				//移近||shift
				if(pros_clone.getProduction().get(pros_clone.getPosition()).getDsb().equals(sym)){
					pros_clone.setPosition(pros_clone.getPosition()+1);
					result.add(pros_clone);
				}
			}
		}
		
		//返回移近之后的闭包集||return the closure set after shifting
		return Closure(result, gram);
	}
	/*	
	 * Name			items
	 * Date			2014/05
	 * Discribe		LR1项集生成||generate the LR1 items
	 * Parameters	LR1Item gram:文法的所有产生式||the total productions of this grammar
	 * Return		LR1项集||LR1 items
	 */
	public ArrayList<LR1Item> items(LR1Item gram){
		//初始化||initialize
		
		//table的size尚未确定
		String[][] table = new String[10][5];
		
		//建立第一项的第一条产生式item1||the first production of the first LR1 Symbol
		ArrayList<Symbol> item1_pro = new ArrayList<Symbol>();
		item1_pro.add(gram.get(0).getLeftsymbol());
		ArrayList<Symbol> item1_lh = new ArrayList<Symbol>();
		item1_lh.add(new Symbol(0,"$"));
		LR1Pro item1 = new LR1Pro(new Symbol(1,"S'"),item1_pro,item1_lh);
		
		//item1_clo是item1的闭包||item1_clo is the closure set of item1
		LR1Item item1_clo = new LR1Item(0);
		item1_clo.add(item1);
		item1_clo.setArray(Closure(item1_clo.getArray(), gram));
		
		//LR1_items用来存放所有的LR1项目||LR1_items is used to save the LR1 items
		ArrayList<LR1Item> LR1_items = new ArrayList<LR1Item>();
		
		//加入第一项||add the first item
		LR1_items.add(item1_clo);
		
		//loop
		int num = 1;
		for(int i=0;i<LR1_items.size();i++){
			//判断是否为规约项||whether the production is a production of reduce
			boolean isReduce = false;
			
			//规约项只能有一条，因此size为1;同时这一条"."所在的位置在产生式最后||the size of production of reduce must be one, and the position of "." must be at last
			//isReduce的存在意义E-R-R-O-R
			if(LR1_items.get(i).size()==1&&LR1_items.get(i).get(0).getPosition()==LR1_items.get(i).get(0).getProduction().size()){
				isReduce = true;
			}
			
			//是规约项，将"r+产生式的编号"加入表中||if the production is a production of reduce, add "r+the number of the reduce production" to the table
			if(isReduce){
				//因为只有一项所以get(0)||the size must be one so "get(0)" here
				LR1Pro red_item = LR1_items.get(i).get(0);
				
				//考虑如何修改为自动生成现在的sym数组E-R-R-O-R
				//lookahead（可能有多个）决定要填入哪几列||the column(s) that this item will be written depend on the lookahead(s)
				for(int j=0;j<red_item.getLookahead().size();j++){
					for(int k=0;k<sym1.length;k++){
						//k是lookahead的symbol在整个符号表中的位置||k is the No. of lookahead in the total symbol table
						if(red_item.getLookahead().get(j).getDsb().equals(sym1[k])){
							//red_num是产生式的编号||red_num is the No. of production
							//break???E-R-R-O-R
							for(int red_num=0;red_num<gram.size();red_num++){
								if(red_item.equalsExLa(gram.get(red_num))){
									table[i][k] = "r"+red_num;
								}
							}
						}
					}
				}
			}
			
			//对符号表中的所有符号尝试进行Goto||for all the symbols in table, try to Goto
			for(int j=0;j<sym1.length;j++){
				//Goto之后的项目存放到items_goto中||items_goto is the items after Goto
				//item_goto的存在意义？E-R-R-O-R
				ArrayList<LR1Pro> item_goto = Goto(LR1_items.get(i), sym1[j], gram);
				LR1Item items_goto = new LR1Item(num);
				items_goto.setArray(item_goto);
				
				//如果非空并且不在LR1_items中则isNewItem为true||if items_goto is not null and not already in LR1_items, make isNewItem value true
				boolean isNewItem = true;
				
				if(item_goto.size()==0){
					//如果为空||if items_goto is null
					isNewItem = false;
				} else {
					//如果不为空||if items_goto is not null
					for(int k=0;k<LR1_items.size();k++){
						//items_goto已经在LR1_items中||if items_goto is already in LR1_items
						if(items_goto.equals(LR1_items.get(k))){
							table[i][j] = "s"+k;
							isNewItem = false;
						}
					}
				}
				
				//如果移近出来的是一个新LR1Item，则在LR1_items中添加这个项目并且设置移进的值||if items_goto is a new LR1Pro, then add it to LR1_items and set shift value in the table 
				if(isNewItem){
					LR1_items.add(items_goto);
					table[i][j] = "s"+(LR1_items.size()-1);
					num++;
				}
			}
		}
		return LR1_items;
	}
	/*	
	 * Name			printTable
	 * Date			2014/05
	 * Discribe		打印一个二维数组||print a 2-D array
	 * Parameters	String[][] table:被打印的二维数组||the array to be printed
	 */
	public void printTable(String[][] table){
		System.out.print("\t");
		for(int i=0;i<table[0].length;i++){
			System.out.print(sym1[i]+"\t");
		}
		System.out.println();
		for(int i=0;i<table.length;i++){
			System.out.print("I"+i+"\t");
			for(int j=0;j<table[0].length;j++){
				System.out.print(table[i][j]+"\t");
			}
			System.out.println();
		}
	}
	/*	
	 * Name			isTerminal
	 * Date			2014/05
	 * Discribe		判断一个符号是否为终结符||judge whether the symbol is a terminal symbol or not
	 * Parameters	Symbol sym:被判断的符号||the symbol is to be judged
	 * Return		true:终结符||terminal symbol
	 * 				false:非终结符||nonterminal symbol
	 * E-R-R-O-R	存在意义稀薄，考虑废除？
	 */
	public boolean isTerminal(Symbol sym){
		if(sym.getTml()==0){
			return true;
		} else {
			return false;
		}
	}
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		判断一个字符是否已经在序列中（在计算first集，判断一个文法符号是否能推导出ε时使用）||judge whether the string is already in the array or not
	 * Parameters	Symbol sym:被判断的string||the string is to be judged
	 * 				ArrayList<Symbol> arr
	 * Return		true:存在||exist
	 * 				false：不存在||not exist
	 * E-R-R-O-R	存在意义稀薄，考虑废除？
	 */
	public boolean ifExist(String str,ArrayList<Symbol> arr){
		for(int i=0;i<arr.size();i++){
			if(str.equals(arr.get(i))){
				return true;
			}
		}
		return false;
	}
	/*	
	 * Name			selDistItem
	 * Date			2014/05
	 * Discribe		去除文法符号数组中的重复项和ε||delete the duplicate items and ε in the array of grammar symbol
	 * Parameters	ArrayList<Symbol> arr:文法符号数组
	 */
	public void selDistItem(ArrayList<Symbol> arr){
		for(int i=0;i<arr.size();i++){
			if(arr.get(i).getTml()==2){
				arr.remove(i);
			}
			for(int j=i+1;j<arr.size();j++){
				if(arr.get(i).equals(arr.get(j))){
					arr.remove(j);
					j--;
				}
			}
		}
	}
	/*	
	 * Name			selDistProd
	 * Date			2014/05
	 * Discribe		去除一个LR1项中的重复表达式||delete the duplicate productions in the LR1 item
	 * Parameters	ArrayList<Symbol> arr:LR1项中的产生式集合
	 */
	public void selDistProd(ArrayList<LR1Pro> arr){
		for(int i=0;i<arr.size();i++){
			for(int j=i+1;j<arr.size();j++){
				if(arr.get(i).equals(arr.get(j))){
					arr.remove(j);
					j--;
				}
			}
		}
	}
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		判断一个LR1 item是否已经在数组中||judge whether the LR1 item is already in the array or not
	 * Parameters	ArrayList<Symbol> arr:文法符号数组
	 */
	public boolean ifExist(LR1Item lr, ArrayList<LR1Item> ar){
		for(int i=0;i<ar.size();i++){
			if(lr.equals(ar.get(i))){
				return true;
			}
		}
		return false;
	}
	//print
	public void printList(ArrayList<String> a){
		String prt = "";
		for(int i=0;i<a.size();i++){
			prt += a.get(i)+" ";
		}
		System.out.println(prt);
	}
	//读取产生式
	public static LR1Item readInput(){
		LR1Item G = new LR1Item(999);
		Scanner scan = new Scanner(System.in);
		String nl = scan.nextLine();
		while(!nl.equals("")){
			String[] nls = nl.split("@");
			Symbol ls = new Symbol(1, nls[0]);
			ArrayList<Symbol> pro = new ArrayList<Symbol>();
			for(int i=1;i<nls.length;i++){
				Symbol p;
				if(nls[i].equals("0")){
					p = new Symbol(2);
				} else if(nls[i].charAt(0)!='!'){//非终结符
					p = new Symbol(1, nls[i]);
				} else {
					p = new Symbol(0, nls[i].substring(1));
				}
				pro.add(p);
			}
			LR1Pro s = new LR1Pro(ls, pro);
			G.add(s);
			nl = scan.nextLine();
		}
		scan.close();
		return G;
	}
	public static void main(String[] args) {
		LR1Item G = readInput();
		System.out.println(G);
		LR1 la = new LR1();
		System.out.println(la.items(G));
	}
}
