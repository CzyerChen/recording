/**
 * Author:   claire
 * Date:    2020-06-12 - 16:31
 * Description: product entity
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-06-12 - 16:31          V1.3.8           product entity
 */
package com.basic.dom;

/**
 * 功能简述 <br/> 
 * 〈product entity〉
 *
 * @author claire
 * @date 2020-06-12 - 16:31
 * @since 1.3.8
 */
public class Product {
    private Integer id;
    private String name;
    private Double price;
    private Integer inventory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", inventory=" + inventory +
                '}';
    }
}
