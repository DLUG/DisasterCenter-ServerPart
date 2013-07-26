package org.dlug.disastercenter.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLElement {
	private String mName;	// 노드 이름.
	private StringBuilder mValueBuilder;
	
	private HashMap<String, String> mAttributes;
	private ArrayList<XMLElement> mChildElements;
	private XMLElement mParentElement;
	
	
	public XMLElement() {
		mAttributes = new HashMap<String, String>();
		mChildElements = new ArrayList<XMLElement>();
		mValueBuilder = new StringBuilder();
	}
	
	// TODO XMLElement Build
	void setParent(XMLElement parent) {
		mParentElement = parent;
	}
	
	void setName(String name) {
		mName = name;
	}
	
	void setAttribute(String name, String value) {
		mAttributes.put(name, value);
	}
	
	void addChild(XMLElement childElement) {
		mChildElements.add(childElement);
	}
	
	void appendValue(String value) {
		mValueBuilder.append(value);
	}
	
	// TODO Get
	public XMLElement getParent() {
		return mParentElement;
	}
	
	public String getName() {
		return mName.toString();
	}
	
	
	public String getValue() {
		return mValueBuilder.toString();
	}
	
	public String getAttribute(String name) {
		return mAttributes.get(name);
	}
	
	public int getChildSize() {
		return mChildElements.size();
	}
	
	public XMLElement getChild(int index) {
		return mChildElements.get(index);
	}
	
	public List<XMLElement> getChild(String name) {
		ArrayList<XMLElement> findChildList = new ArrayList<XMLElement>(mChildElements.size());
		XMLElement[] childElements = mChildElements.toArray(new XMLElement[0]);
		for ( XMLElement childElement : childElements ) {
			if ( childElement.mName.equalsIgnoreCase(name) ) {
				findChildList.add(childElement);
			}
		}
		return findChildList;
	}
	
	public XMLElement[] getChilds() {
		return mChildElements.toArray(new XMLElement[0]);
	}
}