/*
	A-Z	非终结符
	a-z	终结符
	0	空
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class LR1 {
	String[] sym1 = {"A","S","C","c","d"};

	//一串文法符号的First
	public ArrayList<String> First1(ArrayList<String> s,LR1Items G){
		ArrayList<String> result = new ArrayList<String>();
		int i=0;
		while(i<s.size()&&ifExist("0",First2(s.get(i),G))){
			i++;
		}
		for(int j=0;j<i+1;j++){
			if(j<s.size()){
				result.addAll(First2(s.get(j),G));
			}
		}
		//去除重复项和0
		selectDistinct1(result);
		if(i==s.size()){
			//所有的都可以推出“空”
			result.add("0");
		}
		return result;
	}
	
	//单个文法符号的First
	public ArrayList<String> First2(String s,LR1Items G){
		ArrayList<String> result = new ArrayList<String>();
		if(isTerminal(s)){
			//终结符号
			result.add(s);
		} else {
			//非终结符号
			for(int i=0;i<G.size();i++){
				//存在s->xxx的产生式
				if(G.get(i).getLeftsymbol().equals(s)){
					int j=0;
					String[] pro = G.get(i).getProduction();
					//存在s->0的产生式
					if(pro[0].equals("0")){
						result.add("0");
					} else{
						while(j<pro.length&&ifExist("0",First2(pro[j],G))){				
							j++;
						}
						for(int k=0;k<j+1;k++){
							if(k<pro.length){
								result.addAll(First2(pro[k],G));
							}
						}
						//去除重复项和0
						selectDistinct1(result);
						if(j==pro.length){
							//所有的都可以推出“空”
							result.add("0");
						}
					}
				}
			}
		}
		return result;
	}
	
	//闭包
	public ArrayList<LR1Item> Closure(ArrayList<LR1Item> I,LR1Items G){
		Queue<LR1Item> que = new LinkedList<LR1Item>();
		ArrayList<LR1Item> I2 = (ArrayList<LR1Item>) I.clone();
		for(int i=0;i<I2.size();i++){
			que.add(I2.get(i));
		}
		LR1Item tempI = que.poll();
		while(tempI!=null){
			if(!tempI.isTerminal()){
				for(int i=0;i<G.size();i++){
					if(G.get(i).getLeftsymbol().equals(tempI.getProduction()[tempI.getPosition()])){
						//βa存储到s中
						ArrayList<String> s = new ArrayList<String>();
						ArrayList<String> r = new ArrayList<String>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().length;j++){
							r.add(tempI.getProduction()[j]);
						}
						for(int j=0;j<tempI.getLookahead().size();j++){
							ArrayList<String> t = (ArrayList<String>)r.clone();
							t.add(tempI.getLookahead().get(j));
							t = First1(t,G);
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
			if(temp.getPosition()<temp.getProduction().length){
				if(temp.getProduction()[temp.getPosition()].equals(x)){
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
		String sub1[] = {"S"};
		ArrayList<String> s1lh = new ArrayList<String>();
		s1lh.add("$");
		LR1Item s1 = new LR1Item("A",sub1,s1lh);
		LR1Items s = new LR1Items(0);
		s.add(s1);
		s.setArray(Closure(s.getArray(), G));
		ArrayList<LR1Items> C = new ArrayList<LR1Items>();
		C.add(s);
		
		// loop
		int num = 1;
		for(int i=0;i<C.size();i++){
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
							rtn = false;
						}
					}
				}
				if(rtn){
					C.add(temps);
					num++;
				}
			}
		}
		return C;
	}
	
	//判断是否为终结符
	public boolean isTerminal(String s){
		char ch = s.charAt(0);
		if(ch>='A'&&ch<='Z'){
			return false;
		} else {
			return true;
		}
	}
	
	//判断字符是否在序列中
	public boolean ifExist(String s,ArrayList<String> a){
		for(int i=0;i<a.size();i++){
			if(s.equals(a.get(i))){
				return true;
			}
		}
		return false;
	}
	
	//去除重复项和0
	public void selectDistinct1(ArrayList<String> a){
		for(int i=0;i<a.size();i++){
			if(a.get(i).equals("0")){
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
		LR1Items G = new LR1Items(999);;
		Scanner scan = new Scanner(System.in);
		String nl = scan.nextLine();
		while(!nl.equals("")){
			String ls = nl.substring(0, 1);
			String[] pro = new String[nl.length()-4];
			for(int i=0;i<nl.length()-4;i++){
				pro[i] = nl.substring(i+3, i+4);
			}
			LR1Item s = new LR1Item(ls, pro);
			G.add(s);
			nl = scan.nextLine();
		}
		scan.close();
		return G;
	}
	
	public static void main(String[] args) {
		/*LR1Items G = readInput();
		System.out.println(G);*/
		String sub1[] = {"S"};
		LR1Item s1 = new LR1Item("A",sub1);
		String sub2[] = {"C","C"};
		LR1Item s2 = new LR1Item("S",sub2);
		String sub3[] = {"c","C"};
		LR1Item s3 = new LR1Item("C",sub3);
		String sub4[] = {"d"};
		LR1Item s4 = new LR1Item("C",sub4);
		
		LR1Items G = new LR1Items(999);
		G.add(s1);
		G.add(s2);
		G.add(s3);
		G.add(s4);
		
		LR1 la = new LR1();
		/*System.out.println("closure前:");
		for(int i=0;i<as1.size();i++){
			System.out.println(as1.get(i));
		}
		System.out.println("closure后:");
		as2 = la.Closure(as1,G);
		for(int i=0;i<as2.size();i++){
			System.out.println(as2.get(i));
		}*/
		ArrayList<LR1Items> C = la.items(G);
		for(int i=0;i<C.size();i++){
			System.out.println(C.get(i));
		}
		
	}
}
