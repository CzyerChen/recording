/**
 * Author:   claire
 * Date:    2020-06-12 - 10:31
 * Description: learning dom main class
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-06-12 - 10:31                     learning dom main class
 */
package com.basic.dom;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 功能简述 <br/>
 * 〈learning dom main class〉
 *
 * @author claire
 * @date 2020-06-12 - 10:31
 */
public class DomBaseMain extends DefaultHandler {



    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, DocumentException {
        //=====================DOM==========================//
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//        Document document = documentBuilder.parse("store.xml");
//        NodeList productList = document.getElementsByTagName("product");
//        for (int i = 0; i < productList.getLength(); i++) {
//            Node product = productList.item(i);
//            Node firstChild = product.getFirstChild();
//            Node lastChild = product.getLastChild();
//            String nodeName = product.getNodeName();
//            short nodeType = product.getNodeType();
//            String nodeValue = product.getNodeValue();
//            NamedNodeMap namedNodeMap = product.getAttributes();
//            for (int j = 0; j < namedNodeMap.getLength(); j++) {
//                Node node = namedNodeMap.item(j);
//                String name = node.getNodeName();
//                String value = node.getNodeValue();
//                short type = node.getNodeType();
//                System.out.println("name:" + name + ";value:" + value);
//            }
//
//            NodeList childNodes = product.getChildNodes();
//            for (int k = 0; k < childNodes.getLength(); k++) {
//                Node item = childNodes.item(k);
//                String itemNodeName = item.getNodeName();
//                short itemNodeType = item.getNodeType();
//                String itemNodeValue = item.getNodeValue();
//                System.out.println("itemName:" + itemNodeName + ";itemValue:" + itemNodeValue);
//
//                NodeList children = item.getChildNodes();
//                for (int m = 0; m < children.getLength(); m++) {
//                    Node item1 = children.item(m);
//                    System.out.println("itemName:" + item1.getNodeName() + ";itemValue:" + item1.getNodeValue());
//                }
//            }
//        }


        //=====================SAX==========================//
//        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
//        SAXParser saxParser = saxParserFactory.newSAXParser();
//        SaxHandler saxHandler = new SaxHandler("product");
//        saxParser.parse(new InputSource("store.xml"), saxHandler);
//        List<Product> products = saxHandler.getProducts();
//        products.forEach(System.out::println);

        //=====================Dom4j==========================//
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File("store.xml"));
        Element rootElement = document.getRootElement();
        List<Element> elements = rootElement.elements();
        List<Product> products = new ArrayList<>();
        for(Element element : elements){
            Integer id = Integer.valueOf(element.attributeValue("id"));
            String name = element.element("name").getText();
            Double price = Double.valueOf(element.element("price").getText());
            Integer inventory = Integer.valueOf(element.elementText("inventory"));
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setPrice(price);
            product.setInventory(inventory);
            
            products.add(product);
        }
        products.forEach(System.out::println);

    }




}
