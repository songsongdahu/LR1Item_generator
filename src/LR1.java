/*
	A-Z	非终结符
	a-z	终结符
	0	空
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class LR1 {
	String[] sym1 = {"A","S","C"};
	String[] sym2 = {"c","d"};
	LR1Items G;
	
	public LR1(LR1Items G){
		this.G = G;
	}
	
	//一串文法符号的First
	public ArrayList<String> First1(ArrayList<String> s,LR1Items G){
		ArrayList<String> result = new ArrayList<String>();
		if(isTerminal(s.get(0))){
			//终结符
			result.add(s.get(0));
		} else {
			//非终结符
			for(int i=0;i<G.size();i++){
				//遍历G中的产生式
			}
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
						if(j==pro.length){
							//所有的都可以推出“空”
							result.add("0");
						}
						//去除重复项
						selectDistinct(result);
					}
				}
			}
		}
		return result;
	}
	
	//function First
	public ArrayList<LR1Item> Closure(ArrayList<LR1Item> I){
		Queue<LR1Item> que = new LinkedList<LR1Item>();
		for(int i=0;i<I.size();i++){
			que.add(I.get(i));
		}
		LR1Item tempI = que.poll();
		while(tempI!=null){
			// is terminal 
			if(tempI.isTerminal()){
				for(int i=0;i<G.size();i++){
					
				}
			}
		}
		return I;
	}
	public ArrayList<LR1Item> Goto(ArrayList<LR1Item> i, String x){
		
		return i;
	}
	public void items(LR1Items G){
		// initialize
		String sub1[] = {"S"};
		LR1Item s1 = new LR1Item("A",sub1,"$");
		LR1Items c = new LR1Items(0);
		c.add(s1);
		
		// loop
		for(int i=0;i<c.size();i++){
			for(int j=0;j<sym1.length;j++){
			}
		}
		
	}
	// 判断是否为终结符
	public boolean isTerminal(String s){
		char ch = s.charAt(0);
		if(ch>='A'&&ch<='Z'){
			return false;
		} else {
			return true;
		}
	}
	
	// 判断字符是否在序列中
	public boolean ifExist(String s,ArrayList<String> a){
		for(int i=0;i<a.size();i++){
			if(s.equals(a.get(i))){
				return true;
			}
		}
		return false;
	}
	
	// 去除重复项
	public void selectDistinct(ArrayList<String> a){
		for(int i=0;i<a.size();i++){
			for(int j=i+1;j<a.size();j++){
				if(a.get(i).equals(a.get(j))){
					a.remove(j);
					j--;
				}
			}
		}
	}
	
	// 打印序列
	public void printList(ArrayList<String> a){
		String prt = "";
		for(int i=0;i<a.size();i++){
			prt += a.get(i)+" ";
		}
		System.out.println(prt);
	}
	
	public static void main(String[] args) {
		/*String sub1[] = {"S"};
		LR1Item s1 = new LR1Item("A",sub1,"");
		String sub2[] = {"C","C"};
		LR1Item s2 = new LR1Item("S",sub2,"");
		String sub3[] = {"c","C"};
		LR1Item s3 = new LR1Item("C",sub3,"");
		String sub4[] = {"d"};
		LR1Item s4 = new LR1Item("C",sub4,"");
		
		LR1Items G = new LR1Items(999);
		G.add(s1);
		G.add(s2);
		G.add(s3);
		G.add(s4);*/
		String sub1[] = {"S"};
		LR1Item s1 = new LR1Item("A",sub1,"");
		String sub2[] = {"a"};
		LR1Item s2 = new LR1Item("S",sub2,"");
		String sub3[] = {"B","C"};
		LR1Item s3 = new LR1Item("S",sub3,"");
		String sub4[] = {"b"};
		LR1Item s4 = new LR1Item("B",sub4,"");
		String sub5[] = {"0"};
		LR1Item s5 = new LR1Item("B",sub5,"");
		String sub6[] = {"c"};
		LR1Item s6 = new LR1Item("C",sub6,"");
		
		LR1Items G = new LR1Items(999);
		G.add(s1);
		G.add(s2);
		G.add(s3);
		G.add(s4);
		G.add(s5);
		G.add(s6);
		
		LR1 la = new LR1(G);
		la.printList(la.First2("A", G));
	}
}
