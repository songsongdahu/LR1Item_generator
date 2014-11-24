/*
	Author		�ޤĤޤģ���
	Date		2014/05
	Class		LR1
	Describe	LR(1)�����
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
	//symbols�����������е��ķ�����(Ϊ�˹���������)
	ArrayList<Symbol> symbols;
	
	/*	
	 * Name			firstForString
	 * Date			2014/05
	 * Discribe		����һ���ķ����ŵ�first��
	 * Parameters	ArrayList<Symbol> syms:һ���ķ�����
	 * 				LR1Item gram:�ķ������в���ʽ
	 * Return		first��
	 */
	public ArrayList<Symbol> firstForString(ArrayList<Symbol> syms,LR1Item gram){
		//���巵��ֵ
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		//eps_count��ʾs�дӵ�һ�������Ƴ��ŵķ��ŵĵ������Ƴ��ŵķ��ŵļ���
		int eps_count=0;
		
		//����eps_count��ֵ
		while(eps_count<syms.size()&&ifExist(new Symbol(2),first(syms.get(eps_count),gram))){
			eps_count++;
		}
		
		//result��ֵ�������п����Ƴ��ŵķ���(ֱ������)��first���Ĳ�
		for(int i=0;i<eps_count+1;i++){
			if(i<syms.size()){
				result.addAll(first(syms.get(i),gram));
			}
		}
		
		//ȥ���ظ���ͦ�
		selDistItem(result);
		
		//����ʽ�����еķ��Ŷ������Ƴ��ţ���Ѧ���ӵ������
		if(eps_count==syms.size()){
			result.add(new Symbol(2));
		}
		
		return result;
	}
	
	/*	
	 * Name			first
	 * Date			2014/05
	 * Discribe		���ص����ķ����ŵ�first��
	 * Parameters	Symbol sym:һ���ķ�����
	 * 				LR1Item gram:�ķ������в���ʽ
	 * Return		first��
	 */
	public ArrayList<Symbol> first(Symbol sym,LR1Item gram){
		//���巵��ֵ
		ArrayList<Symbol> result = new ArrayList<Symbol>();
		
		if(sym.getTml()==0){
			//�ս���ţ�ֱ�ӷ���s
			result.add(sym);
		} else {
			//���ս���ţ�Ѱ�����Ĳ���ʽ
			for(int i=0;i<gram.size();i++){
				if(gram.get(i).getLeftsymbol().equals(sym)){
					//����s->xxx�Ĳ���ʽ
					ArrayList<Symbol> pro = gram.get(i).getProduction();
					
					//eps_count��ʾs�дӵ�һ�������Ƴ��ŵķ��ŵĵ������Ƴ��ŵķ��ŵļ���
					int eps_count=0;
					
					if(pro.get(0).getTml()==2){
						//����s->0�Ĳ���ʽ
						result.add(new Symbol(2));
					} else {
						//����eps_count��ֵ
						while(eps_count<pro.size()&&ifExist(new Symbol(2),first(pro.get(eps_count),gram))){				
							eps_count++;
						}
						
						//result��ֵ�������п����Ƴ��ŵķ���(ֱ������)��first���Ĳ�
						for(int j=0;j<eps_count+1;j++){
							if(j<pro.size()){
								result.addAll(first(pro.get(j),gram));
							}
						}
						
						//ȥ���ظ����0
						selDistItem(result);
						
						//����ʽ�����еķ��Ŷ������Ƴ��ţ���Ѧ���ӵ������
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
	 * Discribe		���ز���ʽ��closure��
	 * Parameters	ArrayList<LR1Pro> pro:һ������ʽ
	 * 				LR1Item gram:�ķ������в���ʽ
	 * Return		closure��
	 */
	public ArrayList<LR1Pro> Closure(ArrayList<LR1Pro> pro,LR1Item gram){
		
		//���巵��ֵ
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//ʹ��queue������closure��
		Queue<LR1Pro> que = new LinkedList<LR1Pro>();
		
		//Ϊ�˲��ı�֮ǰ�Ĳ���ʽ�������м��㶼�����ڿ�¡�Ĳ���ʽ����
		ArrayList<LR1Pro> pro_clone = (ArrayList<LR1Pro>) pro.clone();
		
		//�����в���ʽ��ӵ�queue��
		for(int i=0;i<pro_clone.size();i++){
			que.add(pro_clone.get(i));
		}
		
		//������һ������ʽ
		LR1Pro tempI = que.poll();
		
		while(tempI!=null){
			//tempIΪnull��ζ�Ž���
			
			if(!tempI.isTerminal()){
				//�������ʽ��ǰ��(����Ҫ�ƽ�����һ��)�Ƿ��ս��
				
				//�ҵ�������ս�������в���ʽ����ӵ�queue��
				for(int i=0;i<gram.size();i++){
					if(gram.get(i).getLeftsymbol().equals(tempI.getProduction().get(tempI.getPosition()))){
						//s�������lookahead
						ArrayList<Symbol> sym_la = new ArrayList<Symbol>();
						
						//sym_aft�����ݴ����ʽ�е�ǰ����ķ��ս��֮��Ĳ���
						ArrayList<Symbol> sym_aft = new ArrayList<Symbol>();
						for(int j=tempI.getPosition()+1;j<tempI.getProduction().size();j++){
							sym_aft.add(tempI.getProduction().get(j));
						}
						
						//��ÿ��lookahead����ѭ��
						for(int j=0;j<tempI.getLookahead().size();j++){
							//Ϊ�˲��ı�sym_aft(Ҫ���ʹ��)�˴���sym_aft����clone
							ArrayList<Symbol> sym_aft_clone = (ArrayList<Symbol>)sym_aft.clone();
							
							//��sym_la��sym_aft������������first�����������tempJ��lookahead
							sym_aft_clone.add(tempI.getLookahead().get(j));
							sym_aft_clone = firstForString(sym_aft_clone,gram);
							sym_la.addAll(sym_aft_clone);
						}
						
						//ɾ���ظ�����Ŀ
						selDistItem(sym_la);
						
						//����������ʽ�Ͷ����У�result��tempI���ظ���������뵽������
						LR1Pro tempJ = new LR1Pro(gram.get(i).getLeftsymbol(), gram.get(i).getProduction(), gram.get(i).getLookahead(), gram.get(i).getPosition());
						tempJ.setLookahead(sym_la);
						if(!tempJ.equals(tempI)&&!ifExist(tempJ,result)&&!ifExist(tempJ,que)){
							que.add(tempJ);
						}
					}
				}
			}
			
			//����������������tempI�ӵ��������
			result.add(tempI);
			
			//�����Ӷ����е�������ʽ
			tempI = que.poll();
		}
		
		//ɾ���ظ��Ĳ���ʽ�����Һϲ�ֻ��lookahead��ͬ����
		megerPro(result);
		return result;
	}
	
	/*	
	 * Name			Goto
	 * Date			2014/05
	 * Discribe		����Goto����
	 * Parameters	LR1Item pros:һ�����ʽ
	 * 				String sym:�ķ�����
	 * 				LR1Item gram:�ķ������в���ʽ
	 * Return		Goto֮��ļ���
	 */
	public ArrayList<LR1Pro> Goto(LR1Item pros, Symbol sym, LR1Item gram){
		
		//���巵��ֵ
		ArrayList<LR1Pro> result = new ArrayList<LR1Pro>();
		
		//��ÿ������ʽ�����ƽ�����
		for(int i=0;i<pros.size();i++){
			//��ֹǳclone||deep clone
			LR1Pro pros_clone = new LR1Pro(pros.get(i).getLeftsymbol(), pros.get(i).getProduction(), pros.get(i).getLookahead(), pros.get(i).getPosition());
			
			//��һ��if������������Խ�磬��Ϊ������Լ�Ĳ���ʽҲ�᳢��ȥ�ƶ�(��Ȼ�������κν��)����ʱҪ���"."����ķ��Żᵼ��Խ�籨��
			if(pros_clone.getPosition()<pros_clone.getProduction().size()){
				//�ƽ�||shift
				if(pros_clone.getProduction().get(pros_clone.getPosition()).equals(sym)){
					pros_clone.setPosition(pros_clone.getPosition()+1);
					result.add(pros_clone);
				}
			}
		}
		
		//�����ƽ�֮��ıհ���
		return Closure(result, gram);
	}
	
	/*	
	 * Name			items
	 * Date			2014/05
	 * Discribe		LR1�����
	 * Parameters	LR1Item gram:�ķ������в���ʽ
	 * Return		LR1�
	 */
	public ArrayList<LR1Item> items(LR1Item gram){
		//��ʼ��
		
		//parsingTable��size��ʼΪ1*symbols.size(),֮������LR(1)������Ӷ�����
		ArrayList<String[]> parsingTable = new ArrayList<String[]>();
		parsingTable.add(new String[symbols.size()]);
		
		//������һ��ĵ�һ������ʽitem1
		ArrayList<Symbol> item1_pro = new ArrayList<Symbol>();
		item1_pro.add(gram.get(0).getLeftsymbol());
		ArrayList<Symbol> item1_lh = new ArrayList<Symbol>();
		item1_lh.add(new Symbol(0,"$"));
		LR1Pro item1 = new LR1Pro(new Symbol(1,"S'"),item1_pro,item1_lh);
		
		//item1_clo��item1�ıհ�
		LR1Item item1_clo = new LR1Item(0);
		item1_clo.add(item1);
		item1_clo.setArray(Closure(item1_clo.getArray(), gram));
		
		//LR1_items����������е�LR1��Ŀ
		ArrayList<LR1Item> LR1_items = new ArrayList<LR1Item>();
		
		//�����һ��
		LR1_items.add(item1_clo);
		
		//loop
		int num = 1;
		for(int i=0;i<LR1_items.size();i++){
			
			//��Լ��ֻ����һ�������sizeΪ1;ͬʱ��һ��"."���ڵ�λ���ڲ���ʽ���
			if(LR1_items.get(i).size()==1&&LR1_items.get(i).get(0).getPosition()==LR1_items.get(i).get(0).getProduction().size()){
				//�ǹ�Լ���"r+����ʽ�ı��"�������
				
				//��Ϊֻ��һ������get(0)
				LR1Pro red_item = LR1_items.get(i).get(0);
				
				//lookahead(�����ж��)����Ҫ�����ļ���
				for(int j=0;j<red_item.getLookahead().size();j++){
					for(int k=0;k<symbols.size();k++){
						//k��lookahead��symbol���������ű��е�λ��
						if(red_item.getLookahead().get(j).equals(symbols.get(k))){
							//red_num�ǲ���ʽ�ı��
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
			
			//�Է��ű��е����з��ų��Խ���Goto
			for(int j=0;j<symbols.size();j++){
				//Goto֮�����Ŀ��ŵ�items_goto��
				ArrayList<LR1Pro> item_goto = Goto(LR1_items.get(i), symbols.get(j), gram);
				LR1Item items_goto = new LR1Item(num);
				items_goto.setArray(item_goto);
				
				//����ǿղ��Ҳ���LR1_items����isNewItemΪtrue
				boolean isNewItem = true;
				
				if(item_goto.size()==0){
					//���Ϊ��
					isNewItem = false;
				} else {
					//�����Ϊ��
					for(int k=0;k<LR1_items.size();k++){
						//items_goto�Ѿ���LR1_items��
						if(items_goto.equals(LR1_items.get(k))){
							parsingTable.get(i)[j] = "s"+k;
							isNewItem = false;
						}
					}
				}
				
				//����ƽ���������һ����LR1Item������LR1_items����������Ŀ���������ƽ���ֵ
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
	 * Discribe		��ӡһ����ά����
	 * Parameters	ArrayList<String[]> table:����ӡ�Ķ�ά����
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
	 * Discribe		�ж�һ���ַ��Ƿ��Ѿ���������(�ڼ���first�����ж�һ���ķ������Ƿ����Ƶ�����ʱʹ��)
	 * Parameters	Symbol sym:���жϵ�string
	 * 				ArrayList<Symbol> arr
	 * Return		true:����
	 * 				false��������
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
	 * Discribe		�ж�һ��LR1 item�Ƿ��Ѿ���������
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
	
	/*	
	 * Name			ifExist
	 * Date			2014/05
	 * Discribe		�ж�һ��LR1 item�Ƿ��Ѿ���������
	 * Parameters	ArrayList<Symbol> arr:�ķ���������
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
	 * Discribe		�ж�һ��LR1 item�Ƿ��Ѿ��ڶ�����
	 * Parameters	ArrayList<Symbol> arr:�ķ���������
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
	 * Discribe		ȥ���ķ����������е��ظ���ͦ�
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
	 * Name			megerPro
	 * Date			2014/11/24
	 * Discribe		�ϲ�ֻ��lookahead��ͬ�ı��ʽ
	 * Parameters	ArrayList<Symbol> arr:LR1���еĲ���ʽ����
	 */
	public void megerPro(ArrayList<LR1Pro> arr){
		for(int i=0;i<arr.size();i++){
			for(int j=i+1;j<arr.size();j++){
				//�������ʽ����la����ͬ(����.��λ��)
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
	 * Discribe		��ȡ����Ĳ���ʽ�����ҹ����ķ����ż���
	 */
	public LR1Item readInput(){
		//�����趨һ����ţ�����������ķ�
		LR1Item Gram = new LR1Item(999);
		
		//���ж�ȡ
		Scanner scan = new Scanner(System.in);
		String nl = scan.nextLine();
		
		//Tsyms�����洢�ս��,Nsyms�����洢���ս��
		ArrayList<Symbol> Tsyms = new ArrayList<Symbol>();
		ArrayList<Symbol> Nsyms = new ArrayList<Symbol>();
		
		
		while(!nl.equals("")){
			//���ķ����Ű���@�ֿ�
			String[] syms = nl.split("@");
			
			//��һ��symbolΪleftsymbol��֮���������ӵ�production��
			Symbol lsym = new Symbol(1, syms[0]);
			
			//��ӷ��ű���û�еķ���
			if(!ifExist(lsym,Nsyms)){
				Nsyms.add(lsym);
			}
			
			ArrayList<Symbol> pro = new ArrayList<Symbol>();
			for(int i=1;i<syms.length;i++){
				Symbol sym;
				if(syms[i].equals("0")){
					//0��ʾ��
					sym = new Symbol(2);
				} else if(syms[i].charAt(0)!='!'){
					//���ս��
					sym = new Symbol(1, syms[i]);
					//��ӷ��ű���û�еķ���
					if(!ifExist(sym,Nsyms)){
						Nsyms.add(sym);
					}
				} else {
					//�ս��
					sym = new Symbol(0, syms[i].substring(1));
					//��ӷ��ű���û�еķ���
					if(!ifExist(sym,Tsyms)){
						Tsyms.add(sym);
					}
				}
				pro.add(sym);
			}
			
			//����ȡ�Ĳ���ʽ��ӵ�Gram��
			Gram.add(new LR1Pro(lsym, pro));
			
			//��������һ��
			nl = scan.nextLine();
		}
		scan.close();
		
		//��Tsyms��Nsyms�ϲ���symbols
		symbols = new ArrayList<Symbol>();
		symbols.addAll(Tsyms);
		symbols.addAll(Nsyms);
		symbols.add(new Symbol(0,"$"));
		
		return Gram;
	}
	
	/*	
	 * Name			readTxt
	 * Date			2014/11/17
	 * Discribe		��ȡtxt�еĲ���ʽ�����ҹ����ķ����ż���
	 */
	public LR1Item readTxt() throws IOException{
		//�����趨һ����ţ�����������ķ�
		LR1Item Gram = new LR1Item(999);
		
		//���ж�ȡ
		File f = new File("Productions.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String nl = br.readLine();
		
		//Tsyms�����洢�ս��,Nsyms�����洢���ս��
		ArrayList<Symbol> Tsyms = new ArrayList<Symbol>();
		ArrayList<Symbol> Nsyms = new ArrayList<Symbol>();
		
		
		while(nl!=null){
			//���ķ����Ű���@�ֿ�
			String[] syms = nl.split("@");
			
			//��һ��symbolΪleftsymbol��֮���������ӵ�production��
			Symbol lsym = new Symbol(1, syms[0]);
			
			//��ӷ��ű���û�еķ���
			if(!ifExist(lsym,Nsyms)){
				Nsyms.add(lsym);
			}
			
			ArrayList<Symbol> pro = new ArrayList<Symbol>();
			for(int i=1;i<syms.length;i++){
				Symbol sym;
				if(syms[i].equals("0")){
					//0��ʾ��
					sym = new Symbol(2);
				} else if(syms[i].charAt(0)!='!'){
					//���ս��
					sym = new Symbol(1, syms[i]);
					//��ӷ��ű���û�еķ���
					if(!ifExist(sym,Nsyms)){
						Nsyms.add(sym);
					}
				} else {
					//�ս��
					sym = new Symbol(0, syms[i].substring(1));
					//��ӷ��ű���û�еķ���
					if(!ifExist(sym,Tsyms)){
						Tsyms.add(sym);
					}
				}
				pro.add(sym);
			}
			
			//����ȡ�Ĳ���ʽ��ӵ�Gram��
			Gram.add(new LR1Pro(lsym, pro));
			
			//��������һ��
			nl = br.readLine();
		}
		br.close();
		
		//��Tsyms��Nsyms�ϲ���symbols
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
