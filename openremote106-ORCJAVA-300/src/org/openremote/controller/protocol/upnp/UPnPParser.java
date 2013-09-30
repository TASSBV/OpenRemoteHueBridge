/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.upnp;

import java.io.*;

import org.cybergarage.xml.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * Parser to go from an UPnP exchanged XML to the cyberlink UPnP stack format.
 * As the UPnP stack embedded parsers are for older versions, this one
 * integrates easily to OR. From the
 * org.cybergarage.xml.parser.XercesParser.class
 * 
 * @author Mathieu Gallissot
 */
public class UPnPParser extends org.cybergarage.xml.Parser {
	public org.cybergarage.xml.Node parse(org.cybergarage.xml.Node parentNode,
			org.w3c.dom.Node domNode, int rank) {
		int domNodeType = domNode.getNodeType();

		String domNodeName = domNode.getNodeName();
		String domNodeValue = domNode.getNodeValue();
		NamedNodeMap attrs = domNode.getAttributes();
		int arrrsLen = (attrs != null) ? attrs.getLength() : 0;

		if (domNodeType == org.w3c.dom.Node.TEXT_NODE) {
			parentNode.setValue(domNodeValue);
			return parentNode;
		}

		if (domNodeType != org.w3c.dom.Node.ELEMENT_NODE) {
			return parentNode;
		}

		org.cybergarage.xml.Node node = new org.cybergarage.xml.Node();
		node.setName(domNodeName);
		node.setValue(domNodeValue);

		if (parentNode != null) {
			parentNode.addNode(node);
		}

		NamedNodeMap attrMap = domNode.getAttributes();
		int attrLen = attrMap.getLength();
		
		for (int n = 0; n < attrLen; n++) {
			org.w3c.dom.Node attr = attrMap.item(n);
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			node.setAttribute(attrName, attrValue);
		}

		org.w3c.dom.Node child = domNode.getFirstChild();
		if (child == null) {
			node.setValue("");
			return node;
		}
		do {
			parse(node, child, rank + 1);
			child = child.getNextSibling();
		} while (child != null);

		return node;
	}

	public org.cybergarage.xml.Node parse(org.cybergarage.xml.Node parentNode,
			org.w3c.dom.Node domNode) {
		return parse(parentNode, domNode, 0);
	}

	public org.cybergarage.xml.Node parse(InputStream inStream)
			throws ParserException {
		org.cybergarage.xml.Node root = null;

		try {
			DOMParser parser = new DOMParser();
			InputSource inSrc = new InputSource(inStream);
			parser.parse(inSrc);

			Document doc = parser.getDocument();
			org.w3c.dom.Element docElem = doc.getDocumentElement();

			if (docElem != null) {
				root = parse(root, docElem);
			}

		} catch (Exception e) {
			throw new ParserException(e);
		}

		return root;
	}

}
