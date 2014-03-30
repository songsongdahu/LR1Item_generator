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
		selectDistinct(result);
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
						selectDistinct(result);
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
	
	public ArrayList<LR1Item> Closure(ArrayList<LR1Item> I,LR1Items G){
		Queue<LR1Item> que = new LinkedList<LR1Item>();
		for(int i=0;i<I.size();i++){
			que.add(I.get(i));
		}
		LR1Item tempI = que.poll();
		while(tempI!=null){
			if(!tempI.isTerminal()){
				for(int i=0;i<G.size();i++){
					if(G.get(i).getLeftsymbol().equals(tempI.getProduction()[tempI.getPosition()])){
						//βa存储到s中
						ArrayList<String> s = new ArrayList<String>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().length;j++){
							s.add(tempI.getProduction()[j]);
						}
						s.add(tempI.getLookahead());
						s = First1(s,G);
						//将B->.γ,b加入到集合I中
						LR1Item tempJ = G.get(i);
						for(int j=0;j<s.size();j++){
							tempJ.setLookahead(s.get(j));
						}
						que.add(tempJ);
					}
				}
			}
			tempI = que.poll();
			I.add(tempI);
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
	
	// 去除重复项和0
	public void selectDistinct(ArrayList<String> a){
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
	
	// 打印序列
	public void printList(ArrayList<String> a){
		String prt = "";
		for(int i=0;i<a.size();i++){
			prt += a.get(i)+" ";
		}
		System.out.println(prt);
	}
	
	public static void main(String[] args) {
		String sub1[] = {"S"};
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
		G.add(s4);
		
		ArrayList<LR1Item> as1 = new ArrayList<LR1Item>();
		ArrayList<LR1Item> as2 = new ArrayList<LR1Item>();
		s1.setLookahead("$");
		as1.add(s1);
		LR1 la = new LR1();
		for(int i=0;i<as1.size();i++){
			System.out.println(as1.get(i));
		}
		as2 = la.Closure(as1,G);
		for(int i=0;i<as2.size();i++){
			System.out.println(as2.get(i));
		}
	}
}
