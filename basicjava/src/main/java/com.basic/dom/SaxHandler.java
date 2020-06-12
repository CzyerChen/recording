/**
 * Author:   claire
 * Date:    2020-06-12 - 16:30
 * Description: sax handler for xml
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-06-12 - 16:30          V1.3.8           sax handler for xml
 */
package com.basic.dom;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能简述 <br/> 
 * 〈sax handler for xml〉
 *
 * @author claire
 * @date 2020-06-12 - 16:30
 * @since 1.3.8
 */
public class SaxHandler extends DefaultHandler {
    private List<Product> products = null;
    private Product product;
    private String currentTag = null;
    private String currentValue = null;
    private String nodeName = null;

    public SaxHandler(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<Product> getProducts() {
        return products;
    }


    @Override
    public void startDocument() throws SAXException {
        // 读到一个开始标签会触发
        super.startDocument();

        products = new ArrayList<Product>();
    }

    @Override
    public void endDocument() throws SAXException {
        //自动生成的方法存根
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        //文档的开头调用
        super.startElement(uri, localName, name, attributes);

        if (name.equals(nodeName)) {
            product = new Product();
        }
        if (attributes != null && product != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getQName(i).equals("id")) {
                    product.setId(Integer.valueOf(attributes.getValue(i)));
                }
            }
        }
        currentTag = name;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        //处理在XML文件中读到的内容
        super.characters(ch, start, length);

        if (currentTag != null && product != null) {
            currentValue = new String(ch, start, length);
            if (!currentValue.trim().equals("") && !currentValue.trim().equals("\n")) {
                if (currentTag.equals("name")) {
                    product.setName(currentValue);
                } else if (currentTag.equals("price")) {
                    product.setPrice(Double.valueOf(currentValue));
                }else if(currentTag.equals("inventory")){
                    product.setInventory(Integer.valueOf(currentValue));
                }
            }
        }
        currentTag = null;
        currentValue = null;
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // 结束标签的时候调用
        super.endElement(uri, localName, name);

        if (name.equals(nodeName)) {
            products.add(product);
        }
    }

}
