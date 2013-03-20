package com.anthony.ctf.utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlDocumentHelper {

	public static Node buildPlayerNode(Document doc, String user, String addy, double lat, double lng){
		//Build out content
		Element player = doc.createElement("player");
		Node name = doc.createElement("name");
		name.setTextContent(user);
		Node id = doc.createElement("id");
		id.setTextContent(addy);
		Node loc = doc.createElement("location");
		Node lati = doc.createElement("lat");
		lati.setTextContent(String.valueOf(lat));
		Node lngi = doc.createElement("lng");
		lngi.setTextContent(String.valueOf(lng));
		//Append elements to correct places
		loc.appendChild(lati);
		loc.appendChild(lngi);					
		player.appendChild(name);
		player.appendChild(id);
		player.appendChild(loc);
		return player;
	}
	
	

}
