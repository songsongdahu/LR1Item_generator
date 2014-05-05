import java.util.ArrayList;


public class LR1Item {
	private String leftsymbol;
	private String[] production;
	private ArrayList<String> lookahead;
	private int position;
	
	public LR1Item(String leftsymbol,String[] production){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.position = 0;
	}
	
	public LR1Item(String leftsymbol,String[] production,ArrayList<String> lookahead){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = 0;
	}
	
	public LR1Item(String leftsymbol,String[] production,ArrayList<String> lookahead,int position){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = position;
	}
	
	// 当前位置是否是非终结符
	public boolean isTerminal(){
		if(position==production.length){
			return true;
		}
		char ch = production[position].charAt(0);
		if(ch>='A'&&ch<='Z'){
			return false;
		} else {
			return true;
		}
	}
	
	public int getPosition(){
		return position;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	public ArrayList<String> getLookahead(){
		return lookahead;
	}
	
	public void setLookahead(ArrayList<String> lookahead){
		this.lookahead = lookahead;
	}
	
	public String getLeftsymbol(){
		return leftsymbol;
	}
	
	public String[] getProduction(){
		return production;
	}
	
	public boolean equals(LR1Item anlr){
		if(this.leftsymbol!=anlr.leftsymbol){
			return false;
		}
		if(this.position!=anlr.position){
			return false;
		}
		for(int i=0;i<production.length;i++){
			if(!this.production[i].equals(anlr.production[i])){
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
	
	public String toString(){
		String pro="";
		String lo="";
		for(int i=0;i<production.length;i++){
			if(i==position){
				pro += ".";
			}
			pro += production[i];
		}
		if(position==production.length){
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
