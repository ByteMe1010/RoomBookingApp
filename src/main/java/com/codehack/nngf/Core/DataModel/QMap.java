package com.codehack.nngf.Core.DataModel;

import java.util.*;

public class QMap <T,E,D,C>{
    Map<T,String> keys = new HashMap<>();
    Map<String,E> dta1 = new HashMap<>();
    Map<String,D> dta2 = new HashMap<>();
    Map<String,C> dta3 = new HashMap<>();

    public void add(T key,E dta1,D dta2,C dta3){
        String ID;
        if (keys.containsKey(key)){
            ID = keys.get(key);
        }else{
            ID = UUID.randomUUID().toString();
        }
        keys.put(key,ID);
        this.dta1.put(ID,dta1);
        this.dta2.put(ID,dta2);
        this.dta3.put(ID,dta3);
    }
    public void changeOne(T key,E value){
        if (!contains(key)) return;
        dta1.put(keys.get(key),value);
    }
    public void changeTwo(T key,D value){
        if (!contains(key)) return;
        dta2.put(keys.get(key),value);
    }
    public void changeThree(T key,C value){
        if (!contains(key)) return;
        dta3.put(keys.get(key),value);
    }
    public boolean contains(T key){
        return keys.containsKey(key);
    }
    public E getOne(T id){
        if (!keys.containsKey(id))
            return null;
        return dta1.get(keys.get(id));
    }
    public D getTwo(T id){
        if (!keys.containsKey(id))
            return null;
        return dta2.get(keys.get(id));
    }
    public C getThree(T id){
        if (!keys.containsKey(id))
            return null;
        return dta3.get(keys.get(id));
    }
    public void remove(T id){
        String ID;
        if (!keys.containsKey(id)) return;
        ID = keys.get(id);
        keys.remove(id);
        dta1.remove(ID);
        dta2.remove(ID);
        dta3.remove(ID);
    }
    public void removeAll(){
        keys.clear();
        dta1.clear();
        dta2.clear();
        dta3.clear();
    }
    public Set<T> keyset(){
        return keys.keySet();
    }
    public Collection<E> valuesOne(){
        return dta1.values();
    }
    public Collection<D> valuesTwo(){
        return dta2.values();
    }
    public Collection<C> valuesTree(){
        return dta3.values();
    }
    public List<Entry> dataSet(){
        List<Entry> d = new ArrayList<>();
        for (T id:keys.keySet()){
            d.add(get(id));
        }
        return d;
    }
    public Entry get(T id){
        if (!keys.containsKey(id)) return null;
        return new Entry(id,getOne(id),getTwo(id),getThree(id));
    }
    public class Entry{
        T key;
        E val1;
        D val2;
        C val3;

        public Entry(T key, E val1, D val2, C val3) {
            this.key = key;
            this.val1 = val1;
            this.val2 = val2;
            this.val3 = val3;
        }


        public T getKey() {
            return key;
        }

        public E getValueOne() {
            return val1;
        }

        public D getValueTwo() {
            return val2;
        }

        public C getValueThree() {
            return val3;
        }
    }
}
