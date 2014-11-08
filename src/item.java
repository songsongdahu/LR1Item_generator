
public class item {
	private int tml;//0 ÖÕ½á·û 1 ·ÇÖÕ½á·û 2¿Õ
	private String dsb;//ÃèÊö
	
	public item(int tml){
		this.tml = tml;
		this.dsb = "";
	}
	
	public item(int tml, String dsb){
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
	
	public boolean equals(item anit){
		if(this.tml==anit.tml&&this.dsb.equals(anit.dsb)){
			return true;
		} else {
			return false;
		}
	}
	
	public String toString(){
		return dsb;
	}
}
