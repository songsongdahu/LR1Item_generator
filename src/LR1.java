/*
	Author		まつまつ！！
	Date		2014/05
	Class		LR1
	Describe	LR(1)项集生成
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class LR1 {
	//symbols用来储存所有的文法符号(为了构建分析表)
	ArrayList<Symbol> symbols;
	
	/*	
	 * Name			firstForString
	 * Date			2014/05
	 * Discribe		返回一串文法符号的first集
	 * Parameters	ArrayList<Symbol> syms:一串文法符号
	 * 				LR1Item gram:文法的所有产生式
	 * Return		first集
	 */
	public ArrayList<Symbol> firstForString(ArrayList<Symbol> syms,LR1Item gram){
		//定义返回值
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		//eps_count表示s中从第一个可以推出ε的符号的到不能推出ε的符号的计数
		int eps_count=0;
		
		//计算eps_count的值
		while(eps_count<syms.size()&&ifExist(new Symbol(2),first(syms.get(eps_count),gram))){
			eps_count++;
		}
		
		//result的值就是所有可以推出ε的符号(直到不能)的first集的并
		for(int i=0;i<eps_count+1;i++){
			if(i<syms.size()){
				result.addAll(first(syms.get(i),gram));
			}
		}
		
		//去除重复项和ε
		selDistItem(result);
		
		//产生式中所有的符号都可以推出ε，则把ε添加到结果中
		if(eps_count==syms.size()){
			result.add(new Symbol(2));
		}
		
		return result;
	}
	
	/*	
	 * Name			first
	 * Date			2014/05
	 * Discribe		返回单个文法符号的first集
	 * Parameters	Symbol sym:一个文法符号
	 * 				LR1Item gram:文法的所有产生式
	 * Return		first集
	 */
	public ArrayList<Symbol> first(Symbol sym,LR1Item gram){
		//定义返回值
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		if(sym.getTml()==0){
			//终结符号，直接返回s
			result.add(sym);
		} else {
			//非终结符号，寻找它的产生式
			for(int i=0;i<gram.size();i++){
				if(gram.get(i).getLeftsymbol().equals(sym)){
					//存在s->xxx的产生式
					ArrayList<Symbol> pro = gram.get(i).getProduction();
					
					//eps_count表示s中从第一个可以推出ε的符号的到不能推出ε的符号的计数
					int eps_count=0;
					
					if(pro.get(0).getTml()==2){
						//存在s->0的产生式
						result.add(new Symbol(2));
					} else {
						//计算eps_count的值
						while(eps_count<pro.size()&&ifExist(new Symbol(2),first(pro.get(eps_count),gram))){				
							eps_count++;
						}
						
						//result的值就是所有可以推出ε的符号(直到不能)的first集的并
						for(int j=0;j<eps_count+1;j++){
							if(j<pro.size()){
								result.addAll(first(pro.get(j),gram));
							}
						}
						
						//去除重复项和0
						selDistItem(result);
						
						//产生式中所有的符号都可以推出ε，则把ε添加到结果中
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
	 * Discribe		返回产生式的closure集
	 * Parameters	ArrayList<LR1Pro> pro:一条产生式
	 * 				LR1Item gram:文法的所有产生式
	 * Return		closure集
	 */
	public ArrayList<LR1Pro> Closure(ArrayList<LR1Pro> pro,LR1Item gram){
		
		//定义返回值
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//使用queue来计算closure集
		Queue<LR1Pro> que = new LinkedList<LR1Pro>();
		
		//为了不改变之前的产生式集，所有计算都建立在克隆的产生式集中
		ArrayList<LR1Pro> pro_clone = (ArrayList<LR1Pro>) pro.clone();
		
		//将所有产生式添加到queue中
		for(int i=0;i<pro_clone.size();i++){
			que.add(pro_clone.get(i));
		}
		
		//弹出第一条产生式
		LR1Pro tempI = que.poll();
		
		while(tempI!=null){
			//tempI为null意味着结束
			
			if(!tempI.isTerminal()){
				//如果产生式当前项(即将要移近的下一项)是非终结符
				
				//找到这个非终结符的所有产生式并添加到queue中
				for(int i=0;i<gram.size();i++){
					if(gram.get(i).getLeftsymbol().equals(tempI.getProduction().get(tempI.getPosition()))){
						//s用来存放lookahead
						ArrayList<Symbol> sym_la = new ArrayList<Symbol>();
						
						//sym_aft用来暂存产生式中当前处理的非终结符之后的部分
						ArrayList<Symbol> sym_aft = new ArrayList<Symbol>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().size();j++){
							sym_aft.add(tempI.getProduction().get(j));
						}
						
						//对每个lookahead进行循环
						for(int j=0;j<tempI.getLookahead().size();j++){
							//为了不改变sym_aft(要多次使用)此处对sym_aft进行clone
							ArrayList<Symbol> sym_aft_clone = (ArrayList<Symbol>)sym_aft.clone();
							
							//将sym_la和sym_aft连接起来，求first集，结果即是tempJ的lookahead
							sym_aft_clone.add(tempI.getLookahead().get(j));
							sym_aft_clone = firstForString(sym_aft_clone,gram);
							sym_la.addAll(sym_aft_clone);
						}
						
						//删除重复的项目
						selDistItem(sym_la);
						
						//如果这个产生式和队列中，result和tempI不重复，将其加入到队列中
						LR1Pro tempJ = new LR1Pro(gram.get(i).getLeftsymbol(), gram.get(i).getProduction(), gram.get(i).getLookahead(), gram.get(i).getPosition());
						tempJ.setLookahead(sym_la);
						if(!tempJ.equals(tempI)&&!ifExist(tempJ,result)&&!ifExist(tempJ,que)){
							que.add(tempJ);
						}
					}
				}
			}
			
			//进行完上述操作后将tempI加到结果集中
			result.add(tempI);
			
			//继续从队列中弹出产生式
			tempI = que.poll();
		}
		
		//删除重复的产生式，并且合并只有lookahead不同的项
		megerPro(result);
		return result;
	}
	
	/*	
	 * Name			Goto
	 * Date			2014/05
	 * Discribe		进行Goto操作
	 * Parameters	LR1Item pros:一组产生式
	 * 				String sym:文法符号
	 * 				LR1Item gram:文法的所有产生式
	 * Return		Goto之后的集合
	 */
	public ArrayList<LR1Pro> Goto(LR1Item pros, Symbol sym, LR1Item gram){
		
		//定义返回值
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//对每个产生式进行移进操作
		for(int i=0;i<pros.size();i++){
			//防止浅clone||deep clone
			LR1Pro pros_clone = new LR1Pro(pros.get(i).getLeftsymbol(), pros.get(i).getProduction(), pros.get(i).getLookahead(), pros.get(i).getPosition());
			
			//第一个if用来放置数组越界，因为遇到规约的产生式也会尝试去移动(虽然不会有任何结果)，此时要获得"."后面的符号会导致越界报错
			if(pros_clone.getPosition()<pros_clone.getProduction().size()){
				//移近||shift
				if(pros_clone.getProduction().get(pros_clone.getPosition()).equals(sym)){
					pros_clone.setPosition(pros_clone.getPosition()+1);
					result.add(pros_clone);
				}
			}
		}
		
		//返回移近之后的闭包集
		return Closure(result, gram);
	}
	
	/*	
	 * Name			items
	 * Date			2014/05
	 * Discribe		LR1项集生成
	 * Parameters	LR1Item gram:文法的所有产生式
	 * Return		LR1项集
	 */
	public ArrayList<LR1Item> items(LR1Item gram){
		//初始化
		
		//parsingTable的size初始为1*symbols.size(),之后随着LR(1)项的增加而增加
		ArrayList<String[]> parsingTable = new ArrayList<String[]>();
		parsingTable.add(new String[symbols.size()]);
		
		//建立第一项的第一条产生式item1
		ArrayList<Symbol> item1_pro = new ArrayList<Symbol>();
		item1_pro.add(gram.get(0).getLeftsymbol());
		ArrayList<Symbol> item1_lh = new ArrayList<Symbol>();
		item1_lh.add(new Symbol(0,"$"));
		LR1Pro item1 = new LR1Pro(new Symbol(1,"S'"),item1_pro,item1_lh);
		
		//item1_clo是item1的闭包
		LR1Item item1_clo = new LR1Item(0);
		item1_clo.add(item1);
		item1_clo.setArray(Closure(item1_clo.getArray(), gram));
		
		//LR1_items用来存放所有的LR1项目
		ArrayList<LR1Item> LR1_items = new ArrayList<LR1Item>();
		
		//加入第一项
		LR1_items.add(item1_clo);
		
		//loop
		int num = 1;
		for(int i=0;i<LR1_items.size();i++){
			
			//规约项只能有一条，因此size为1;同时这一条"."所在的位置在产生式最后
			if(LR1_items.get(i).size()==1&&LR1_items.get(i).get(0).getPosition()==LR1_items.get(i).get(0).getProduction().size()){
				//是规约项，将"r+产生式的编号"加入表中
				
				//因为只有一项所以get(0)
				LR1Pro red_item = LR1_items.get(i).get(0);
				
				//lookahead(可能有多个)决定要填入哪几列
				for(int j=0;j<red_item.getLookahead().size();j++){
					for(int k=0;k<symbols.size();k++){
						//k是lookahead的symbol在整个符号表中的位置
						if(red_item.getLookahead().get(j).equals(symbols.get(k))){
							//red_num是产生式的编号
							for(int red_num=0;red_num<gram.size();red_num++){
								if(red_item.equalsExLa(gram.get(red_num))){
									parsingTable.get(i)[k] = "r"+red_num;
									break;
								}
							}
						}
					}
				}
			}
			
			//对符号表中的所有符号尝试进行Goto
			for(int j=0;j<symbols.size();j++){
				//Goto之后的项目存放到items_goto中
				ArrayList<LR1Pro> item_goto = Goto(LR1_items.get(i), symbols.get(j), gram);
				LR1Item items_goto = new LR1Item(num);
				items_goto.setArray(item_goto);
				
				//如果非空并且不在LR1_items中则isNewItem为true
				boolean isNewItem = true;
				
				if(item_goto.size()==0){
					//如果为空
					isNewItem = false;
				} else {
					//如果不为空
					for(int k=0;k<LR1_items.size();k++){
						//items_goto已经在LR1_items中
						if(items_goto.equals(LR1_items.get(k))){
							parsingTable.get(i)[j] = "s"+k;
							isNewItem = false;
						}
					}
				}
				
				//如果移近出来的是一个新LR1Item，则在LR1_items中添加这个项目并且设置移进的值
				if(isNewItem){
					LR1_items.add(items_goto);
					parsingTable.add(new String[symbols.size()]);
					parsingTable.get(i)[j] = "s"+(LR1_items.size()-1);
					num++;
				}
			}
		}
		printTable(parsingTable);
		return LR1_items;
	}
	
	/*	
	 * Name			printTable
	 * Date			2014/05
	 * Discribe		打印一个二维数组
	 * Parameters	ArrayList<String[]> table:被打印的二维数组
	 */
	public void printTable(ArrayList<String[]> table){
		System.out.print("--------------------This is the parsing table--------------------\n");
		for(int i=0;i<table.get(0).length;i++){
			System.out.print("\t"+symbols.get(i));
		}
		System.out.println();
		for(int i=0;i<table.size();i++){
			System.out.print("I"+i+"\t");
			for(int j=0;j<table.get(0).length;j++){
				if(table.get(i)[j]==null){
					System.out.print("\t");
				} else {
					System.out.print(table.get(i)[j]+"\t");
				}
			}
			System.out.println();
		}
	}
	
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		判断一个字符是否已经在序列中(在计算first集，判断一个文法符号是否能推导出ε时使用)
	 * Parameters	Symbol sym:被判断的string
	 * 				ArrayList<Symbol> arr
	 * Return		true:存在
	 * 				false：不存在
	 */
	public boolean ifExist(Symbol sym,ArrayList<Symbol> arr){
		for(int i=0;i<arr.size();i++){
			if(sym.equals(arr.get(i))){
				return true;
			}
		}
		return false;
	}
	
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		判断一个LR1 item是否已经在数组中
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
	
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		判断一个LR1 item是否已经在数组中
	 * Parameters	ArrayList<Symbol> arr:文法符号数组
	 */
	public boolean ifExist(LR1Pro pro, Queue<LR1Pro> que){
		ArrayList<LR1Pro> quelist = new ArrayList<LR1Pro>(que);
		for(int i=0;i<quelist.size();i++){
			if(quelist.get(i).equals(pro)){
				return true;
			}
		}
		return false;
	}
	
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		判断一个LR1 item是否已经在队列中
	 * Parameters	ArrayList<Symbol> arr:文法符号数组
	 */
	public boolean ifExist(LR1Pro pro, ArrayList<LR1Pro> arr){
		for(int i=0;i<arr.size();i++){
			if(pro.equals(arr.get(i))){
				return true;
			}
		}
		return false;
	}
	
	
	/*	
	 * Name			selDistItem
	 * Date			2014/05
	 * Discribe		去除文法符号数组中的重复项和ε
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
	 * Name			megerPro
	 * Date			2014/11/24
	 * Discribe		合并只有lookahead不同的表达式
	 * Parameters	ArrayList<Symbol> arr:LR1项中的产生式集合
	 */
	public void megerPro(ArrayList<LR1Pro> arr){
		for(int i=0;i<arr.size();i++){
			for(int j=i+1;j<arr.size();j++){
				//如果产生式除了la都相同(包括.的位置)
				if(arr.get(j).equalsExLa(arr.get(i))&&arr.get(j).getPosition()==arr.get(i).getPosition()){
					arr.get(i).addLookahead(arr.get(j).getLookahead());
					arr.remove(j);
					j--;
				}
			}
		}
	}
	
	//print
	public void printList(ArrayList<Symbol> arr){
		String prt = "";
		for(int i=0;i<arr.size();i++){
			prt += arr.get(i)+" ";
		}
		System.out.println(prt);
	}
	
	/*	
	 * Name			readInput
	 * Date			2014/11/08
	 * Discribe		读取输入的产生式，并且构造文法符号集合
	 */
	public LR1Item readInput(){
		//随意设定一个序号，代表输入的文法
		LR1Item Gram = new LR1Item(999);
		
		//按行读取
		Scanner scan = new Scanner(System.in);
		String nl = scan.nextLine();
		
		//Tsyms用来存储终结符,Nsyms用来存储非终结符
		ArrayList<Symbol> Tsyms = new ArrayList<Symbol>();
		ArrayList<Symbol> Nsyms = new ArrayList<Symbol>();
		
		
		while(!nl.equals("")){
			//将文法符号按照@分开
			String[] syms = nl.split("@");
			
			//第一个symbol为leftsymbol，之后的依次添加到production中
			Symbol lsym = new Symbol(1, syms[0]);
			
			//添加符号表中没有的符号
			if(!ifExist(lsym,Nsyms)){
				Nsyms.add(lsym);
			}
			
			ArrayList<Symbol> pro = new ArrayList<Symbol>();
			for(int i=1;i<syms.length;i++){
				Symbol sym;
				if(syms[i].equals("0")){
					//0表示ε
					sym = new Symbol(2);
				} else if(syms[i].charAt(0)!='!'){
					//非终结符
					sym = new Symbol(1, syms[i]);
					//添加符号表中没有的符号
					if(!ifExist(sym,Nsyms)){
						Nsyms.add(sym);
					}
				} else {
					//终结符
					sym = new Symbol(0, syms[i].substring(1));
					//添加符号表中没有的符号
					if(!ifExist(sym,Tsyms)){
						Tsyms.add(sym);
					}
				}
				pro.add(sym);
			}
			
			//将读取的产生式添加到Gram中
			Gram.add(new LR1Pro(lsym, pro));
			
			//继续读下一行
			nl = scan.nextLine();
		}
		scan.close();
		
		//将Tsyms和Nsyms合并到symbols
		symbols = new ArrayList<Symbol>();
		symbols.addAll(Tsyms);
		symbols.addAll(Nsyms);
		symbols.add(new Symbol(0,"$"));
		
		return Gram;
	}
	
	/*	
	 * Name			readTxt
	 * Date			2014/11/17
	 * Discribe		读取txt中的产生式，并且构造文法符号集合
	 */
	public LR1Item readTxt() throws IOException{
		//随意设定一个序号，代表输入的文法
		LR1Item Gram = new LR1Item(999);
		
		//按行读取
		File f = new File("Productions.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String nl = br.readLine();
		
		//Tsyms用来存储终结符,Nsyms用来存储非终结符
		ArrayList<Symbol> Tsyms = new ArrayList<Symbol>();
		ArrayList<Symbol> Nsyms = new ArrayList<Symbol>();
		
		
		while(nl!=null){
			//将文法符号按照@分开
			String[] syms = nl.split("@");
			
			//第一个symbol为leftsymbol，之后的依次添加到production中
			Symbol lsym = new Symbol(1, syms[0]);
			
			//添加符号表中没有的符号
			if(!ifExist(lsym,Nsyms)){
				Nsyms.add(lsym);
			}
			
			ArrayList<Symbol> pro = new ArrayList<Symbol>();
			for(int i=1;i<syms.length;i++){
				Symbol sym;
				if(syms[i].equals("0")){
					//0表示ε
					sym = new Symbol(2);
				} else if(syms[i].charAt(0)!='!'){
					//非终结符
					sym = new Symbol(1, syms[i]);
					//添加符号表中没有的符号
					if(!ifExist(sym,Nsyms)){
						Nsyms.add(sym);
					}
				} else {
					//终结符
					sym = new Symbol(0, syms[i].substring(1));
					//添加符号表中没有的符号
					if(!ifExist(sym,Tsyms)){
						Tsyms.add(sym);
					}
				}
				pro.add(sym);
			}
			
			//将读取的产生式添加到Gram中
			Gram.add(new LR1Pro(lsym, pro));
			
			//继续读下一行
			nl = br.readLine();
		}
		br.close();
		
		//将Tsyms和Nsyms合并到symbols
		symbols = new ArrayList<Symbol>();
		symbols.addAll(Tsyms);
		symbols.addAll(Nsyms);
		symbols.add(new Symbol(0,"$"));
		printList(symbols);
		
		return Gram;
	}
	
	
	public static void main(String[] args) throws IOException {
		LR1 la = new LR1();
		System.out.println("Please input:");
		LR1Item G = la.readInput();
//		LR1Item G = la.readTxt();
		System.out.println("-----------------------This is the grammar-----------------------");
		System.out.println(G);
		
		ArrayList<LR1Item> lr1item = la.items(G);
		System.out.println("---------------------This is the LR(1) items---------------------");
		for(int i=0;i<lr1item.size();i++){
			System.out.print(lr1item.get(i));
		}
	}
}
