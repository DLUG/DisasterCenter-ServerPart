package org.dlug.disastercenter.common;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Kim Young-Soo (yskim6217@gmail.com)
 * @date 2013. 07. 04.
 */
public class XMLDocument {
	private String mDocumentText;
	private XMLElement mRootElement;
	private XMLElement mParsingElement;

	public XMLDocument(String documentText) {
		mDocumentText = documentText;
		
	}
	
	@Override
	public String toString() {
		return mDocumentText;
	}
	
	public XMLElement parse() {
		StringReader documentReader = null;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			
			documentReader = new StringReader(mDocumentText);
			InputSource documentSource = new InputSource(documentReader);
			
			parser.parse(documentSource, mDefaultHandler);
		} catch (Exception e) {
			mParsingElement = null;
			mRootElement = null;
		} finally {
			documentReader.close();
		}
		
		
		return mRootElement; 
	}
	
	private DefaultHandler mDefaultHandler = new DefaultHandler() {
		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			
			if ( mRootElement == null ) {
				// Root Element가 없을경우 생성.
				mRootElement = new XMLElement();
				
				// Element 이름 설정.
//				mRootElement.setName(localName);
				mRootElement.setName(qName);
				
				// Attribute 설정.
				int attrLength = attributes.getLength();
				for ( int i = 0; i < attrLength; i++ ) {
					String name = attributes.getLocalName(i);
					String value = attributes.getValue(i);
					
					mRootElement.setAttribute(name, value);
				}
				
				
				// 현재 진행중인 Element 포인터에 설정한다.
				mParsingElement = mRootElement;
			}
		    else  {
				// Root Element가 없을경우 생성.
		    	XMLElement newElement = new XMLElement();
				
				// Element 이름 설정.
//		    	newElement.setName(localName);
		    	newElement.setName(qName);
				
				
				// Attribute 설정.
				int attrLength = attributes.getLength();
				for ( int i = 0; i < attrLength; i++ ) {
					String name = attributes.getLocalName(i);
					String value = attributes.getValue(i);
					
					newElement.setAttribute(name, value);
				}
				
				// 시작된 Element 부모를 이전에 진행중이던 Element 로 설정한다.
				newElement.setParent(mParsingElement);
				
				// 현재 진행중인 Element의 자식으로 추가한다.
				mParsingElement.addChild(newElement);
				
				// 현재 진행중인 Element 포인터에 설정한다.
				mParsingElement = newElement;
			}

		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
			mParsingElement.appendValue(new String(ch, start, length).trim());
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);
			mParsingElement = mParsingElement.getParent();
		}		
		
		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			mParsingElement = null;
		}
	};
	
}