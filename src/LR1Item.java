/*
	Author		�ޤĤޤģ���
	Date		2014/05
	Class		LR1Item
	Describe	LR1Item�����LR1��item������һ��LR(1)���ʽ(LR1Pro)��һ�����
 */

import java.util.ArrayList;

public class LR1Item {
	//ʹ��Arraylist���洢����ʽ
	private ArrayList<LR1Pro> array;
	//LR1Item�����
	private int seq;
	
	public LR1Item(int seq){
		 array = new ArrayList<LR1Pro>();
		 this.seq = seq;
	}

	public LR1Item(ArrayList<LR1Pro> array, int seq){
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
	
	/*	
	 * Name			equals
	 * Date			2014/05
	 * Discribe		�Ա�����LR1Item�Ƿ���ͬ��������ͬ�Ĳ���ʽ��˳����Բ�ͬ��
	 * Parameter	LR1Item item:��Աȵ���һ��LR1Item
	 * Return		true:��ͬ
	 * 				false:��ͬ
	 */ 
	public boolean equals(LR1Item item){
		int rtn;
		if(array.size()==item.size()){
			for(int i=0;i<array.size();i++){
				rtn = 0;
				for(int j=0;j<item.size();j++){
					if(array.get(i).equals(item.get(j))){
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