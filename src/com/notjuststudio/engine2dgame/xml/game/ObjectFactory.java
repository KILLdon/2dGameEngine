//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.05 at 03:13:07 PM MSK 
//


package com.notjuststudio.engine2dgame.xml.game;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.notjuststudio.engine2dgame.xml.game package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Game_QNAME = new QName("", "Game");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.notjuststudio.engine2dgame.xml.game
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Game }
     * 
     */
    public Game createGame() {
        return new Game();
    }

    /**
     * Create an instance of {@link Source }
     * 
     */
    public Source createSource() {
        return new Source();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Game }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Game")
    public JAXBElement<Game> createGame(Game value) {
        return new JAXBElement<Game>(_Game_QNAME, Game.class, null, value);
    }

}