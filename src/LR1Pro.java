/*
	Author		�ޤĤޤģ���
	Date		2014/05
	Class		LR1Pro
	Describe	LR1Pro�����LR1�Ĳ���ʽ������S->A.B (a/$)
 */
import java.util.ArrayList;

public class LR1Pro {
	//leftsymbol�ǲ���ʽ��ߵ�symbol����S->A.B (a/$)�е�S
	private Symbol leftsymbol;
	//production�ǲ���ʽ�ұߵ�symbol���ϣ���S->A.B (a/$)�е�A��B
	private ArrayList<Symbol> production;
	//lookahead�ǲ���ʽ��lookahead���ϣ���S->A.B (a/$)�е�a��$
	private ArrayList<Symbol> lookahead;
	//position��ָmark"."�ڲ���ʽ�ұߵ�λ��
	private int position;
	
	public LR1Pro(Symbol leftsymbol,ArrayList<Symbol> production){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.position = 0;
	}
	
	public LR1Pro(Symbol leftsymbol,ArrayList<Symbol> production,ArrayList<Symbol> lookahead){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = 0;
	}
	
	public LR1Pro(Symbol leftsymbol,ArrayList<Symbol> production,ArrayList<Symbol> lookahead,int position){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = position;
	}
	
	public int getPosition(){
		return position;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	public ArrayList<Symbol> getLookahead(){
		return lookahead;
	}
	
	public void addLookahead(ArrayList<Symbol> lasym){
		lookahead.addAll(lasym);
		selDistItem(lookahead);
	}
	
	public void setLookahead(ArrayList<Symbol> lookahead){
		this.lookahead = lookahead;
	}
	
	public Symbol getLeftsymbol(){
		return leftsymbol;
	}
	
	public ArrayList<Symbol> getProduction(){
		return production;
	}
	
	/*	
	 * Name			selDistItem
	 * Date			2014/05
	 * Discribe		ȥ���ķ���������(lookahead)�е��ظ���ͦ�
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
	 * Name			isTerminal
	 * Date			2014/05
	 * Discribe		�жϲ���ʽ��ǰλ���Ƿ����ս��
	 * Return		true:���ս��
	 * 				false:�Ƿ��ս��
	 */ 
	public boolean isTerminal(){
		if(position==production.size()){
			return true;
		}
		if(production.get(position).getTml()==0){
			return true;
		} else {
			return false;
		}
	}
	
	/*	
	 * Name			equals
	 * Date			2014/05
	 * Discribe		�ж���������ʽ�Ƿ���ȫ��ͬ
	 * Parameter	LR1Pro pro:��Աȵ���һ������ʽ
	 * Return		true:��ͬ
	 * 				false:��ͬ
	 */ 
	public boolean equals(LR1Pro pro){
		if(this.leftsymbol!=pro.leftsymbol){
			return false;
		}
		if(this.position!=pro.position){
			return false;
		}
		for(int i=0;i<production.size();i++){
			if(!this.production.get(i).equals(pro.production.get(i))){
				return false;
			}
		}
		for(int i=0;i<lookahead.size();i++){
			if(!this.lookahead.get(i).equals(pro.lookahead.get(i))){
				return false;
			}
		}
		return true;
	}
	
	/*	
	 * Name			equalsExLa
	 * Date			2014/05
	 * Discribe		�ж���������ʽ��leftsymbol��production�Ƿ���ͬ
	 * Parameter	LR1Pro pro:��Աȵ���һ������ʽ
	 * Return		true:��ͬ
	 * 				false:��ͬ
	 */ 
	public boolean equalsExLa(LR1Pro anlr){
		if(this.leftsymbol!=anlr.leftsymbol){
			return false;
		}
		for(int i=0;i<production.size();i++){
			if(!this.production.get(i).equals(anlr.production.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public String toString(){
		String pro="";
		String lo="";
		for(int i=0;i<production.size();i++){
			if(i==position){
				pro += ".";
			}
			pro += production.get(i).getDsb();
		}
		if(position==production.size()){
			pro += ".";
		}
		if(lookahead!=null&&lookahead.size()!=0){
			for(int i=0;i<lookahead.size()-1;i++){
				lo += lookahead.get(i)+"/";
			}
			lo +=  lookahead.get(lookahead.size()-1);
		}
		return leftsymbol+" -> "+pro+" , "+lo;
	}
	
}
