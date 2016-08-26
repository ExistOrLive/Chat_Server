package server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class SynchronizedHashMap<K,V> {
     private HashMap<K,V> map=null;
     
     public SynchronizedHashMap(){
    	 map=new HashMap<K,V>();
     }
     
     public synchronized void put(K k,V v){
          map.put(k, v);  	 
     }
     
     public synchronized V remove(K k){
    	 
    	 return map.remove(k);
     }
     
     public synchronized V get(K k){
    	 return map.get(k);
     }
     public boolean containsKey(K k){
    	 return map.containsKey(k);
     }
     public Collection<V> values(){
    	 return map.values();
     }
     public Set<K> keySet(){
    	 return map.keySet();
     }
     
}
