
public class LR1Item {
	private String leftsymbol;
	private String[] production;
	private String lookahead;
	private int position;
	
	public LR1Item(String leftsymbol,String[] production,String lookahead){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = 0;
	}
	
	public LR1Item(String leftsymbol,String[] production,String lookahead,int position){
		this.leftsymbol = leftsymbol;
		this.production = production;
		this.lookahead = lookahead;
		this.position = position;
	}
	
	// 当前位置是否是非终结符
	public boolean isTerminal(){
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
	
	public String getLookahead(){
		return lookahead;
	}
	
	public void setLookahead(String lookahead){
		this.lookahead = lookahead;
	}
	
	public String getLeftsymbol(){
		return leftsymbol;
	}
	
	public String[] getProduction(){
		return production;
	}
	
	public String toString(){
		String pro="";
		for(int i=0;i<production.length;i++){
			if(i==position){
				pro += ".";
			}
			pro += production[i];
		}
		return leftsymbol+" -> "+pro+" , "+lookahead;
	}
	
}
