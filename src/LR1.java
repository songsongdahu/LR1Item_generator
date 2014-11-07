/*
	Author	まつまつ！！
	Date	2014/11/07
	Update	add comments
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class LR1 {
	String[] sym1 = {"S","C","c","d","$"};
	/*	
	 * Name		firstForString
	 * Date		2014/05
	 * Discribe	Return the first sets for a string of symbol||返回一串文法符号的first集
	 */
	public ArrayList<item> firstForString(ArrayList<item> s,LR1Items G){
		//define return value||定义返回值
		ArrayList<item> result = new ArrayList<item>();
		
		//eps_count is the count of symbols which can shift to ε(until which cannot)||eps_count表示s中从第一个可以推出ε的符号的到不能推出ε的符号的计数
		int eps_count=0;
		
		//calculate the eps_count||计算eps_count的值
		while(eps_count<s.size()&&ifExist("0",first(s.get(eps_count),G))){
			eps_count++;
		}
		
		//the result is the first sets of this symbols||result的值就是所有可以推出ε的符号（直到不能）的first集的并
		for(int i=0;i<eps_count+1;i++){
			if(i<s.size()){
				result.addAll(first(s.get(i),G));
			}
		}
		
		//delete the duplicate items and ε||去除重复项和0
		selectDistinct1(result);
		
		//if all the symbols can shift to ε then add ε to the result||产生式中所有的符号都可以推出ε，则把ε添加到结果中
		if(eps_count==s.size()){
			result.add(new item(2));
		}
		
		return result;
	}
	/*	
	 * Name		First2
	 * Date		2014/05
	 * Discribe	Return the first sets for a symbol||返回单个文法符号的first集
	 */
	public ArrayList<item> first(item s,LR1Items G){
		//define return value||定义返回值
		ArrayList<item> result = new ArrayList<item>();
		
		if(isTerminal(s)){
			//terminal symbol, return itself||终结符号，直接返回s
			result.add(s);
		} else {
			//nonterminal symbol, find its production rules||非终结符号，寻找它的产生式
			for(int i=0;i<G.size();i++){
				if(G.get(i).getLeftsymbol().equals(s)){
					//now we find a production rules starting with s||存在s->xxx的产生式
					ArrayList<item> pro = G.get(i).getProduction();
					
					//eps_count is the count of symbols which can shift to ε(until which cannot)||eps_count表示s中从第一个可以推出ε的符号的到不能推出ε的符号的计数
					int eps_count=0;
					
					if(pro.get(0).getTml()==2){
						//if s shift to ε, add ε||存在s->0的产生式
						result.add(new item(2));
					} else {
						//calculate the eps_count||计算eps_count的值
						while(eps_count<pro.size()&&ifExist("0",first(pro.get(eps_count),G))){				
							eps_count++;
						}
						
						//the result is the first sets of this symbols||result的值就是所有可以推出ε的符号（直到不能）的first集的并
						for(int j=0;j<eps_count+1;j++){
							if(j<pro.size()){
								result.addAll(first(pro.get(j),G));
							}
						}
						
						//delete the duplicate items and ε||去除重复项和0
						selectDistinct1(result);
						
						//if all the symbols can shift to ε then add ε to the result||产生式中所有的符号都可以推出ε，则把ε添加到结果中
						if(eps_count==pro.size()){
							result.add(new item(2));
						}
					}
				}
			}
		}
		return result;
	}
	/*	
	 * Name		Closure
	 * Date		2014/05
	 * Discribe	Return the closure sets for productions||返回产生式的closure集
	 */
	public ArrayList<LR1Item> Closure(ArrayList<LR1Item> I,LR1Items G){
		
		//use a queue to calculate the closure set||使用queue来计算closure集
		Queue<LR1Item> que = new LinkedList<LR1Item>();
		
		//all the action is on the cloned productions (to protect the original one)||为了不改变之前的产生式集，所有计算都建立在克隆的产生式集中
		ArrayList<LR1Item> I2 = (ArrayList<LR1Item>) I.clone();
		
		//add all productions to queue||将所有产生式添加到queue中
		for(int i=0;i<I2.size();i++){
			que.add(I2.get(i));
		}
		
		//pump the first one||弹出第一条产生式
		LR1Item tempI = que.poll();
		
		while(tempI!=null){
			//if tempI is null, stop the loop||tempI为null意味着结束
			
			if(!tempI.isTerminal()){
				//if the first item of production is nonterminal||如果产生式第一项是非终结符
				
				//this loop is to find the production starting with tempI, and add them to the queue||找到这个非终结符的所有产生式并添加到queue中
				for(int i=0;i<G.size();i++){
					if(G.get(i).getLeftsymbol().equals(tempI.getProduction().get(tempI.getPosition()))){
						//βa存储到s中
						ArrayList<item> s = new ArrayList<item>();
						ArrayList<item> r = new ArrayList<item>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().size();j++){
							r.add(tempI.getProduction().get(j));
						}
						for(int j=0;j<tempI.getLookahead().size();j++){
							ArrayList<item> t = (ArrayList<item>)r.clone();
							t.add(tempI.getLookahead().get(j));
							t = firstForString(t,G);
							s.addAll(t);
						}
						selectDistinct1(s);
						//将B->.γ,b加入到集合I中
						LR1Item tempJ = new LR1Item(G.get(i).getLeftsymbol(), G.get(i).getProduction(), G.get(i).getLookahead(), G.get(i).getPosition());
						tempJ.setLookahead(s);
						que.add(tempJ);
					}
				}
			}
			I2.add(tempI);
			tempI = que.poll();
		}
		selectDistinct2(I2);
		return I2;
	}
	
	//GOTO
	public ArrayList<LR1Item> Goto(LR1Items I, String x, LR1Items G){
		ArrayList<LR1Item> J = new ArrayList<LR1Item>();
		for(int i=0;i<I.size();i++){
			//防止浅clone
			LR1Item temp = new LR1Item(I.get(i).getLeftsymbol(), I.get(i).getProduction(), I.get(i).getLookahead(), I.get(i).getPosition());
			if(temp.getPosition()<temp.getProduction().size()){
				if(temp.getProduction().get(temp.getPosition()).getDsb().equals(x)){
					temp.setPosition(temp.getPosition()+1);
					J.add(temp);
				}
			}
		}
		return Closure(J, G);
	}
	
	//main
	public ArrayList<LR1Items> items(LR1Items G){
		// initialize
		String[][] table = new String[10][5];
		ArrayList<item> sub1 = new ArrayList<item>();
		sub1.add(new item(1,"S"));
		ArrayList<item> s1lh = new ArrayList<item>();
		s1lh.add(new item(0,"$"));
		LR1Item s1 = new LR1Item(new item(1,"S'"),sub1,s1lh);
		LR1Items s = new LR1Items(0);
		s.add(s1);
		s.setArray(Closure(s.getArray(), G));
		ArrayList<LR1Items> C = new ArrayList<LR1Items>();
		C.add(s);
		
		// loop
		int num = 1;
		for(int i=0;i<C.size();i++){
			//判断是否为规约项
			boolean gy = false;
			if(C.get(i).size()==1&&C.get(i).get(0).getPosition()==C.get(i).get(0).getProduction().size()){
				gy = true;
			}
			//是规约项，将rx加入表中
			if(gy){
				LR1Item temp = C.get(i).get(0);
				for(int j=0;j<temp.getLookahead().size();j++){
					for(int k=0;k<sym1.length;k++){
						if(temp.getLookahead().get(j).getDsb().equals(sym1[k])){
							for(int l=0;l<G.size();l++){
								if(temp.equalsExcpLa(G.get(l))){
									table[i][k] = "r"+l;
								}
							}
						}
					}
				}
			}
			for(int j=0;j<sym1.length;j++){
				ArrayList<LR1Item> temp = Goto(C.get(i), sym1[j], G);
				LR1Items temps = new LR1Items(num);
				temps.setArray(temp);
				
				//如果非空并且不在C中则rtn为true
				boolean rtn = true;
				if(temp.size()==0){
					rtn = false;
				} else {
					for(int k=0;k<C.size();k++){
						//GOTO(I,X)在C中
						if(temps.equals(C.get(k))){
							table[i][j] = "s"+k;
							rtn = false;
						}
					}
				}
				if(rtn){
					C.add(temps);
					table[i][j] = "s"+(C.size()-1);
					num++;
				}
			}
		}
		printTable(table);
		return C;
	}
	
	public void printTable(String[][] t){
		System.out.print("\t");
		for(int i=0;i<t[0].length;i++){
			System.out.print(sym1[i]+"\t");
		}
		System.out.println();
		for(int i=0;i<t.length;i++){
			System.out.print("I"+i+"\t");
			for(int j=0;j<t[0].length;j++){
				System.out.print(t[i][j]+"\t");
			}
			System.out.println();
		}
	}
	
	//判断是否为终结符
	public boolean isTerminal(item s){
		if(s.getTml()==0){
			return true;
		} else {
			return false;
		}
	}
	
	//判断字符是否在序列中
	public boolean ifExist(String s,ArrayList<item> a){
		for(int i=0;i<a.size();i++){
			if(s.equals(a.get(i))){
				return true;
			}
		}
		return false;
	}
	
	//去除重复项和0
	public void selectDistinct1(ArrayList<item> a){
		for(int i=0;i<a.size();i++){
			if(a.get(i).getTml()==2){
				a.remove(i);
			}
			for(int j=i+1;j<a.size();j++){
				if(a.get(i).equals(a.get(j))){
					a.remove(j);
					j--;
				}
			}
		}
	}
	
	//去除重复的产生式
	public void selectDistinct2(ArrayList<LR1Item> a){
		for(int i=0;i<a.size();i++){
			for(int j=i+1;j<a.size();j++){
				if(a.get(i).equals(a.get(j))){
					a.remove(j);
					j--;
				}
			}
		}
	}
	
	//判断item是否已经存在
	public boolean ifExist(LR1Items lr, ArrayList<LR1Items> ar){
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
	public static LR1Items readInput(){
		LR1Items G = new LR1Items(999);
		Scanner scan = new Scanner(System.in);
		String nl = scan.nextLine();
		while(!nl.equals("")){
			String[] nls = nl.split("@");
			item ls = new item(1, nls[0]);
			ArrayList<item> pro = new ArrayList<item>();
			for(int i=1;i<nls.length;i++){
				item p;
				if(nls[i].equals("0")){
					p = new item(2);
				} else if(nls[i].charAt(0)!='!'){//非终结符
					p = new item(1, nls[i]);
				} else {
					p = new item(0, nls[i].substring(1));
				}
				pro.add(p);
			}
			LR1Item s = new LR1Item(ls, pro);
			G.add(s);
			nl = scan.nextLine();
		}
		scan.close();
		return G;
	}
	
	public static void main(String[] args) {
		LR1Items G = readInput();
		System.out.println(G);
		LR1 la = new LR1();
		System.out.println(la.items(G));
	}
}
