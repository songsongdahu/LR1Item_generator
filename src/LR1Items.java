import java.util.ArrayList;


public class LR1Items {
	 private ArrayList<LR1Item> array;
	 private int seq;
	 
	 public LR1Items(int seq){
		 array = new ArrayList<LR1Item>();
		 this.seq = seq;
	 }
	 
	 public LR1Items(ArrayList<LR1Item> array, int seq){
		 array = new ArrayList<LR1Item>();
		 this.array = array;
		 this.seq = seq;
	 }
	 
	 public int size(){
		 return array.size();
	 }
	 
	 public void add(LR1Item i){
		 this.array.add(i);
	 }
	 
	 public LR1Item get(int i){
		 return this.array.get(i);	 
	 }
	 
	 public ArrayList<LR1Item> getArray(){
		 return this.array;
	 }
	 
	 public void setArray(ArrayList<LR1Item> array){
		 this.array = array;
	 }
	 
	 public String toString(){
		String s = "I"+seq+"\n";
		for(int i=0;i<array.size();i++){
			s += array.get(i).toString() + "\n";
		}
		return s;
	 }
	 
	 public boolean equals(LR1Items anlr){
		 for(int i=0;i<array.size();i++){
			 for(int j=0;j<anlr.size();j++){
				 if(array.get(i).equals(anlr.get(j))){
					 
				 }
			 }
		 }
	 }
}
