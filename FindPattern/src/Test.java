import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Test {

	public static void main(String[] args) {
		/*ArrayList<String> lrc = new ArrayList<String>();
		ArrayList<String> melo = new ArrayList<String>();
		
		for(int i = 0; i < 4; ++i) {
			lrc.add(Integer.toString(i));
			melo.add(Integer.toString(i+5));
		}
		SequencePair p0 = new SequencePair(lrc,melo);
		
		
		ArrayList<String> lrc1 = new ArrayList<String>();
		ArrayList<String> melo1 = new ArrayList<String>();
		
		for(int i = 0; i < 3; ++i) {
			lrc1.add(Integer.toString(i));
			melo1.add(Integer.toString(i+5));
		}
		SequencePair p1 = new SequencePair(lrc1,melo1);
		
		HashSet<SequencePair> set0 = new HashSet<SequencePair>();
		HashSet<SequencePair> set1 = new HashSet<SequencePair>();
		set1.add(p1);
		set0.add(p0);
		System.out.println(set0.size());
		set0.addAll(set1);
		System.out.println(set0.size());
		
		
		
		HashMap<Integer, HashSet<SequencePair> > map = new HashMap<Integer, HashSet<SequencePair> >();*/
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		map1.put("apple", 1);
		map2.put("apple", 2);
		
		//map1.putAll(map2);
		
		Iterator itr = map2.entrySet().iterator();
		
		while(itr.hasNext()) {
			Map.Entry entry = (Entry) itr.next();
			String key = (String) entry.getKey();
			Integer value = (Integer) entry.getValue();
			if(map1.containsKey(key)) 
				map1.put(key, map1.get(key) + value);
			
		}
		
		itr= map1.entrySet().iterator();
		while(itr.hasNext()) {
			Map.Entry entry = (Entry) itr.next();
			String key = (String) entry.getKey();
			Integer value = (Integer) entry.getValue();
			System.out.println(key + ":" + value);
		}
		
		
	}
}
