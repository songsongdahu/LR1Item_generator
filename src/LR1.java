/*
	Author	�ޤĤޤģ���
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
	 * Discribe		����һ���ķ����ŵ�first��||Return the "first" sets for a string of symbol
	 * Parameters	ArrayList<Symbol> syms:һ���ķ�����||a string of grammar symbol
	 * 				LR1Item gram:�ķ������в���ʽ||the total productions of this grammar
	 * Return		first��||"first" set
	 */
	public ArrayList<Symbol> firstForString(ArrayList<Symbol> syms,LR1Item gram){
		//���巵��ֵ||define return value
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		//eps_count��ʾs�дӵ�һ�������Ƴ��ŵķ��ŵĵ������Ƴ��ŵķ��ŵļ���||eps_count is the count of symbols which can shift to ��(until which cannot)
		int eps_count=0;
		
		//����eps_count��ֵ||calculate the eps_count
		while(eps_count<syms.size()&&ifExist("0",first(syms.get(eps_count),gram))){
			eps_count++;
		}
		
		//result��ֵ�������п����Ƴ��ŵķ��ţ�ֱ�����ܣ���first���Ĳ�||the result is the "first" sets of this symbols
		for(int i=0;i<eps_count+1;i++){
			if(i<syms.size()){
				result.addAll(first(syms.get(i),gram));
			}
		}
		
		//ȥ���ظ���ͦ�||delete the duplicate items and ��
		selDistItem(result);
		
		//����ʽ�����еķ��Ŷ������Ƴ��ţ���Ѧ���ӵ������||if all the symbols can shift to �� then add �� to the result
		if(eps_count==syms.size()){
			result.add(new Symbol(2));
		}
		
		return result;
	}
	/*	
	 * Name			first
	 * Date			2014/05
	 * Discribe		���ص����ķ����ŵ�first��||Return the "first" sets for a symbol
	 * Parameters	Symbol sym:һ���ķ�����||a grammar symbol
	 * 				LR1Item gram:�ķ������в���ʽ||the total productions of this grammar
	 * Return		first��||"first" set
	 */
	public ArrayList<Symbol> first(Symbol sym,LR1Item gram){
		//���巵��ֵ||define return value
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		if(isTerminal(sym)){
			//�ս���ţ�ֱ�ӷ���s||terminal symbol, return itself
			result.add(sym);
		} else {
			//���ս���ţ�Ѱ�����Ĳ���ʽ||if s is a nonterminal symbol, find its production rules
			for(int i=0;i<gram.size();i++){
				if(gram.get(i).getLeftsymbol().equals(sym)){
					//����s->xxx�Ĳ���ʽ||find a production starting with s
					ArrayList<Symbol> pro = gram.get(i).getProduction();
					
					//eps_count��ʾs�дӵ�һ�������Ƴ��ŵķ��ŵĵ������Ƴ��ŵķ��ŵļ���||eps_count is the count of symbols which can shift to ��(until which cannot)
					int eps_count=0;
					
					if(pro.get(0).getTml()==2){
						//����s->0�Ĳ���ʽ||if s shift to ��, add ��
						result.add(new Symbol(2));
					} else {
						//����eps_count��ֵ||calculate the eps_count
						while(eps_count<pro.size()&&ifExist("0",first(pro.get(eps_count),gram))){				
							eps_count++;
						}
						
						//result��ֵ�������п����Ƴ��ŵķ��ţ�ֱ�����ܣ���first���Ĳ�||the result is the "first" sets of this symbols
						for(int j=0;j<eps_count+1;j++){
							if(j<pro.size()){
								result.addAll(first(pro.get(j),gram));
							}
						}
						
						//ȥ���ظ����0||delete the duplicate items and ��
						selDistItem(result);
						
						//����ʽ�����еķ��Ŷ������Ƴ��ţ���Ѧ���ӵ������||if all the symbols can shift to �� then add �� to the result
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
	 * Discribe		Return the closure sets for productions||���ز���ʽ��closure��
	 * Parameters	ArrayList<LR1Pro> pro:һ������ʽ||a production
	 * 				LR1Item gram:�ķ������в���ʽ||the total productions of this grammar
	 * Return		closure��||closure set
	 */
	public ArrayList<LR1Pro> Closure(ArrayList<LR1Pro> pro,LR1Item gram){
		
		//���巵��ֵ||define return value
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//ʹ��queue������closure��||use a queue to calculate the closure set
		Queue<LR1Pro> que = new LinkedList<LR1Pro>();
		
		//Ϊ�˲��ı�֮ǰ�Ĳ���ʽ�������м��㶼�����ڿ�¡�Ĳ���ʽ����||all the action is on the cloned productions (to protect the original one)
		ArrayList<LR1Pro> pro_clone = (ArrayList<LR1Pro>) pro.clone();
		
		//�����в���ʽ��ӵ�queue��||add all productions to queue
		for(int i=0;i<pro_clone.size();i++){
			que.add(pro_clone.get(i));
		}
		
		//������һ������ʽ||pump the first one
		LR1Pro tempI = que.poll();
		
		while(tempI!=null){
			//tempIΪnull��ζ�Ž���||if tempI is null, stop the loop
			
			if(!tempI.isTerminal()){
				//�������ʽ��ǰ�����Ҫ�ƽ�����һ��Ƿ��ս��
				
				//�ҵ�������ս�������в���ʽ����ӵ�queue��||this loop is to find the production starting with tempI, and add them to the queue
				for(int i=0;i<gram.size();i++){
					if(gram.get(i).getLeftsymbol().equals(tempI.getProduction().get(tempI.getPosition()))){
						//s�������lookahead||sym_la is used to save the lookahead symbol
						ArrayList<Symbol> sym_la = new ArrayList<Symbol>();
						
						//sym_aft�����ݴ����ʽ�е�ǰ����ķ��ս��֮��Ĳ���||sym_aft is the part after the nonterminal symbol above
						ArrayList<Symbol> sym_aft = new ArrayList<Symbol>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().size();j++){
							sym_aft.add(tempI.getProduction().get(j));
						}
						
						//��ÿ��lookahead����ѭ��||loop all of lookahead of this production
						for(int j=0;j<tempI.getLookahead().size();j++){
							//Ϊ�˲��ı�sym_aft��Ҫ���ʹ�ã��˴���sym_aft����clone||sym_aft_clone is the clone of sym_aft to not change the old one(because it will be used many times)
							ArrayList<Symbol> sym_aft_clone = (ArrayList<Symbol>)sym_aft.clone();
							
							//��sym_la��sym_aft������������first�����������tempJ��lookahead||the "first" set of (sym_aft+sym_la) is the lookahead of tempJ
							sym_aft_clone.add(tempI.getLookahead().get(j));
							sym_aft_clone = firstForString(sym_aft_clone,gram);
							sym_la.addAll(sym_aft_clone);
						}
						
						//ɾ���ظ�����Ŀ||delete the duplicate items
						selDistItem(sym_la);
						
						//��B->.��,b���뵽������||tempJ is to be saved the production which will be added to queue
						LR1Pro tempJ = new LR1Pro(gram.get(i).getLeftsymbol(), gram.get(i).getProduction(), gram.get(i).getLookahead(), gram.get(i).getPosition());
						tempJ.setLookahead(sym_la);
						que.add(tempJ);
					}
				}
			}
			
			//����������������tempI�ӵ��������||add tempI to result set after finding the productions generated from it
			result.add(tempI);
			
			//�����Ӷ����е�������ʽ||pump the next one
			tempI = que.poll();
		}
		
		//ɾ���ظ��Ĳ���ʽ||delete the duplicate productions
		selDistProd(result);
		return result;
	}
	/*	
	 * Name			Goto
	 * Date			2014/05
	 * Discribe		����Goto����||just Goto:)
	 * Parameters	LR1Item pros:һ�����ʽ||a set of production
	 * 				String sym:�ķ�����||grammar symbol
	 * 				LR1Item gram:�ķ������в���ʽ||the total productions of this grammar
	 * Return		Goto֮��ļ���||the Symbol set after Goto
	 */
	public ArrayList<LR1Pro> Goto(LR1Item pros, String sym, LR1Item gram){
		
		//���巵��ֵ||define return value
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//��ÿ������ʽ�����ƽ�����||shift for every production
		for(int i=0;i<pros.size();i++){
			//��ֹǳclone||deep clone
			LR1Pro pros_clone = new LR1Pro(pros.get(i).getLeftsymbol(), pros.get(i).getProduction(), pros.get(i).getLookahead(), pros.get(i).getPosition());
			
			//��һ��if�����ò���E-R-R-O-R??????????????????????????????????????????????????????????????????????
			if(pros_clone.getPosition()<pros_clone.getProduction().size()){
				//�ƽ�||shift
				if(pros_clone.getProduction().get(pros_clone.getPosition()).getDsb().equals(sym)){
					pros_clone.setPosition(pros_clone.getPosition()+1);
					result.add(pros_clone);
				}
			}
		}
		
		//�����ƽ�֮��ıհ���||return the closure set after shifting
		return Closure(result, gram);
	}
	/*	
	 * Name			items
	 * Date			2014/05
	 * Discribe		LR1�����||generate the LR1 items
	 * Parameters	LR1Item gram:�ķ������в���ʽ||the total productions of this grammar
	 * Return		LR1�||LR1 items
	 */
	public ArrayList<LR1Item> items(LR1Item gram){
		//��ʼ��||initialize
		
		//table��size��δȷ��
		String[][] table = new String[10][5];
		
		//������һ��ĵ�һ������ʽitem1||the first production of the first LR1 Symbol
		ArrayList<Symbol> item1_pro = new ArrayList<Symbol>();
		item1_pro.add(gram.get(0).getLeftsymbol());
		ArrayList<Symbol> item1_lh = new ArrayList<Symbol>();
		item1_lh.add(new Symbol(0,"$"));
		LR1Pro item1 = new LR1Pro(new Symbol(1,"S'"),item1_pro,item1_lh);
		
		//item1_clo��item1�ıհ�||item1_clo is the closure set of item1
		LR1Item item1_clo = new LR1Item(0);
		item1_clo.add(item1);
		item1_clo.setArray(Closure(item1_clo.getArray(), gram));
		
		//LR1_items����������е�LR1��Ŀ||LR1_items is used to save the LR1 items
		ArrayList<LR1Item> LR1_items = new ArrayList<LR1Item>();
		
		//�����һ��||add the first item
		LR1_items.add(item1_clo);
		
		//loop
		int num = 1;
		for(int i=0;i<LR1_items.size();i++){
			//�ж��Ƿ�Ϊ��Լ��||whether the production is a production of reduce
			boolean isReduce = false;
			
			//��Լ��ֻ����һ�������sizeΪ1;ͬʱ��һ��"."���ڵ�λ���ڲ���ʽ���||the size of production of reduce must be one, and the position of "." must be at last
			//isReduce�Ĵ�������E-R-R-O-R
			if(LR1_items.get(i).size()==1&&LR1_items.get(i).get(0).getPosition()==LR1_items.get(i).get(0).getProduction().size()){
				isReduce = true;
			}
			
			//�ǹ�Լ���"r+����ʽ�ı��"�������||if the production is a production of reduce, add "r+the number of the reduce production" to the table
			if(isReduce){
				//��Ϊֻ��һ������get(0)||the size must be one so "get(0)" here
				LR1Pro red_item = LR1_items.get(i).get(0);
				
				//��������޸�Ϊ�Զ��������ڵ�sym����E-R-R-O-R
				//lookahead�������ж��������Ҫ�����ļ���||the column(s) that this item will be written depend on the lookahead(s)
				for(int j=0;j<red_item.getLookahead().size();j++){
					for(int k=0;k<sym1.length;k++){
						//k��lookahead��symbol���������ű��е�λ��||k is the No. of lookahead in the total symbol table
						if(red_item.getLookahead().get(j).getDsb().equals(sym1[k])){
							//red_num�ǲ���ʽ�ı��||red_num is the No. of production
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
			
			//�Է��ű��е����з��ų��Խ���Goto||for all the symbols in table, try to Goto
			for(int j=0;j<sym1.length;j++){
				//Goto֮�����Ŀ��ŵ�items_goto��||items_goto is the items after Goto
				//item_goto�Ĵ������壿E-R-R-O-R
				ArrayList<LR1Pro> item_goto = Goto(LR1_items.get(i), sym1[j], gram);
				LR1Item items_goto = new LR1Item(num);
				items_goto.setArray(item_goto);
				
				//����ǿղ��Ҳ���LR1_items����isNewItemΪtrue||if items_goto is not null and not already in LR1_items, make isNewItem value true
				boolean isNewItem = true;
				
				if(item_goto.size()==0){
					//���Ϊ��||if items_goto is null
					isNewItem = false;
				} else {
					//�����Ϊ��||if items_goto is not null
					for(int k=0;k<LR1_items.size();k++){
						//items_goto�Ѿ���LR1_items��||if items_goto is already in LR1_items
						if(items_goto.equals(LR1_items.get(k))){
							table[i][j] = "s"+k;
							isNewItem = false;
						}
					}
				}
				
				//����ƽ���������һ����LR1Item������LR1_items����������Ŀ���������ƽ���ֵ||if items_goto is a new LR1Pro, then add it to LR1_items and set shift value in the table 
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
	 * Discribe		��ӡһ����ά����||print a 2-D array
	 * Parameters	String[][] table:����ӡ�Ķ�ά����||the array to be printed
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
	 * Discribe		�ж�һ�������Ƿ�Ϊ�ս��||judge whether the symbol is a terminal symbol or not
	 * Parameters	Symbol sym:���жϵķ���||the symbol is to be judged
	 * Return		true:�ս��||terminal symbol
	 * 				false:���ս��||nonterminal symbol
	 * E-R-R-O-R	��������ϡ�������Ƿϳ���
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
	 * Discribe		�ж�һ���ַ��Ƿ��Ѿ��������У��ڼ���first�����ж�һ���ķ������Ƿ����Ƶ�����ʱʹ�ã�||judge whether the string is already in the array or not
	 * Parameters	Symbol sym:���жϵ�string||the string is to be judged
	 * 				ArrayList<Symbol> arr
	 * Return		true:����||exist
	 * 				false��������||not exist
	 * E-R-R-O-R	��������ϡ�������Ƿϳ���
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
	 * Discribe		ȥ���ķ����������е��ظ���ͦ�||delete the duplicate items and �� in the array of grammar symbol
	 * Parameters	ArrayList<Symbol> arr:�ķ���������
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
	 * Discribe		ȥ��һ��LR1���е��ظ����ʽ||delete the duplicate productions in the LR1 item
	 * Parameters	ArrayList<Symbol> arr:LR1���еĲ���ʽ����
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
	 * Discribe		�ж�һ��LR1 item�Ƿ��Ѿ���������||judge whether the LR1 item is already in the array or not
	 * Parameters	ArrayList<Symbol> arr:�ķ���������
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
	//��ȡ����ʽ
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
				} else if(nls[i].charAt(0)!='!'){//���ս��
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
