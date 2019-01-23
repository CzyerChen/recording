package learning;

import java.io.Serializable;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 23 15:14
 */
public class Person implements Serializable {

    private String id;
    private String name;

    public Person(String id,String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
