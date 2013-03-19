package com.anthony.ctf.utilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StringHelper {
    public StringHelper(){}
    
    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    public static Document stringToXmlDoc(String text) {
    	Document document = null;
    	    try {
    	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	    	DocumentBuilder builder = factory.newDocumentBuilder();
    			InputStream inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
    			document = builder.parse(inputStream);
    	        }
    	 catch(Exception e){}
    	    return document;
    }
    
    public static Node xPathNodeList(Node node, String path) throws XPathExpressionException{	
    	XPath xpath = XPathFactory.newInstance().newXPath();
    	return (Node) xpath.evaluate(path, node, XPathConstants.NODE);
    }
    
}
