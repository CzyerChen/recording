/**
 * Author:   claire
 * Date:    2020-06-12 - 17:59
 * Description: 特殊事件
 * History:
 * <author>          <time>                   <version>          <desc>
 * claire          2020-06-12 - 17:59          V1.3.8           特殊事件
 */
package com.basic.dom;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Writer;

/**
 * 功能简述 <br/> 
 * 〈特殊事件〉
 *
 * @author claire
 * @date 2020-06-12 - 17:59
 * @since 1.3.8
 */
public class SpecificEvent implements XMLEvent {
    @Override
    public int getEventType() {
        return 0;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean isStartElement() {
        return false;
    }

    @Override
    public boolean isAttribute() {
        return false;
    }

    @Override
    public boolean isNamespace() {
        return false;
    }

    @Override
    public boolean isEndElement() {
        return false;
    }

    @Override
    public boolean isEntityReference() {
        return false;
    }

    @Override
    public boolean isProcessingInstruction() {
        return false;
    }

    @Override
    public boolean isCharacters() {
        return false;
    }

    @Override
    public boolean isStartDocument() {
        return false;
    }

    @Override
    public boolean isEndDocument() {
        return false;
    }

    @Override
    public StartElement asStartElement() {
        return null;
    }

    @Override
    public EndElement asEndElement() {
        return null;
    }

    @Override
    public Characters asCharacters() {
        return null;
    }

    @Override
    public QName getSchemaType() {
        return null;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {

    }
}
