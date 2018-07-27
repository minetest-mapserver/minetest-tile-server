package io.rudin.minetest.tileserver.parser;

public class Item {

    public String name;

    public int count, wear;

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", wear=" + wear +
                '}';
    }

    public boolean isEmpty(){
        return name == null || name.length() == 0 || count == 0;
    }

}
