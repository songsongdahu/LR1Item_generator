import java.util.ArrayList;


public class LR1Items {
	 private ArrayList<LR1Pro> array;
	 private int seq;
	 
	 public LR1Items(int seq){
		 array = new ArrayList<LR1Pro>();
		 this.seq = seq;
	 }
	 
	 public LR1Items(ArrayList<LR1Pro> array, int seq){
		 array = new ArrayList<LR1Pro>();
		 this.array = array;
		 this.seq = seq;
	 }
	 
	 public int size(){
		 return array.size();
	 }
	 
	 public void add(LR1Pro i){
		 this.array.add(i);
	 }
	 
	 public LR1Pro get(int i){
		 return this.array.get(i);	 
	 }
	 
	 public ArrayList<LR1Pro> getArray(){
		 return this.array;
	 }
	 
	 public void setArray(ArrayList<LR1Pro> array){
		 this.array = array;
	 }
	 
	 public String toString(){
		String s = "I"+seq+"\n";
		for(int i=0;i<array.size();i++){
			s += array.get(i).toString() + "\n";
		}
		return s;
	 }
	 
	 //不会有重复的LR1Item
	 public boolean equals(LR1Items anlr){
		 int rtn;
		 if(array.size()==anlr.size()){
			 for(int i=0;i<array.size();i++){
				 rtn = 0;
				 for(int j=0;j<anlr.size();j++){
					 if(array.get(i).equals(anlr.get(j))){
						 rtn = 1;
					 }
				 }
				 if(rtn==0){
					 return false;
				 }
			 }
			 return true;
		 }
		return false;
	 }
}