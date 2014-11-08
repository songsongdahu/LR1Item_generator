/*
	Author		�ޤĤޤģ���
	Date		2014/05
	Class		Symbol
	Describe	Symbol������ķ�����
 */
public class Symbol {
	//0 �ս�� 1 ���ս�� 2��
	private int tml;
	//����
	private String dsb;
	
	public Symbol(int tml){
		this.tml = tml;
		this.dsb = "";
	}
	
	public Symbol(int tml, String dsb){
		this.tml = tml;
		this.dsb = dsb;
	}
	
	public void setTml(int tml){
		this.tml = tml;
	}
	
	public int getTml(){
		return tml;
	}

	public String getDsb() {
		return dsb;
	}

	public void setDsb(String dsb) {
		this.dsb = dsb;
	}
	
	public boolean equals(Symbol sym){
		if(this.tml==sym.tml&&this.dsb.equals(sym.dsb)){
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		return dsb;
	}
}
