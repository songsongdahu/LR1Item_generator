/*
	Author		まつまつ！！
	Date		2014/05
	Class		LR1Pro
	Describe	LR1Pro类代表LR1的产生式，形如S->A.B (a/$)
 */
import java.util.ArrayList;

public class LR1Pro {
	private item leftsymbol;
	private ArrayList<item> production;
	private ArrayList<item> lookahead;
	private int position;
	
	public LR1Pro(item leftsymbol,ArrayList<item> production){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.position = 0;
	}
	
	public LR1Pro(item leftsymbol,ArrayList<item> production,ArrayList<item> lookahead){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = 0;
	}
	
	public LR1Pro(item leftsymbol,ArrayList<item> production,ArrayList<item> lookahead,int position){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = position;
	}
	
	// 当前位置是否是终结符
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
	
	public int getPosition(){
		return position;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	public ArrayList<item> getLookahead(){
		return lookahead;
	}
	
	public void setLookahead(ArrayList<item> lookahead){
		this.lookahead = lookahead;
	}
	
	public item getLeftsymbol(){
		return leftsymbol;
	}
	
	public ArrayList<item> getProduction(){
		return production;
	}
	
	public boolean equals(LR1Pro anlr){
		if(this.leftsymbol!=anlr.leftsymbol){
			return false;
		}
		if(this.position!=anlr.position){
			return false;
		}
		for(int i=0;i<production.size();i++){
			if(!this.production.get(i).equals(anlr.production.get(i))){
				return false;
			}
		}
		for(int i=0;i<lookahead.size();i++){
			if(!this.lookahead.get(i).equals(anlr.lookahead.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public boolean equalsExcpLa(LR1Pro anlr){
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
