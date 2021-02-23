package com.kbsec.mydata.tr.service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TrAutoGeneratorService {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		TrAutoGeneratorService TrAutoGeneratorService = new TrAutoGeneratorService();
		String trName = "00015004";
//		TrAutoGeneratorService.xmlToOuputBeanFile(trName);
		TrAutoGeneratorService.xmlToInputBeanFile(trName);
		
		
	}
	
	private String filePath = "/Users/skyrun/git/backend/src/main/resources/jkass/TrxRule/";
	private static final String TR_XML_FILE_PREFIX = "Tkb_";
	private static final String OUTPUT_BEAN_SUFFIX = "_Res";
	private static final String INPUT_BEAN_SUFFIX = "_Req";
	
	// String filePath = "/Users/skyrun/git/backend/src/main/resources/jkass/TrxRule/";
	// String trName = "Tkb_SEAM0007";

	public boolean xmlToInputBeanFile(String trName) throws IOException, SAXException, ParserConfigurationException {
		return this.xmlToBeanFile(trName , INPUT_BEAN_SUFFIX, "Input");
	}
	
	public boolean xmlToOuputBeanFile(String trName) throws IOException, SAXException, ParserConfigurationException {
		return this.xmlToBeanFile(trName , OUTPUT_BEAN_SUFFIX, "Output");
	}
	
	private boolean xmlToBeanFile(String trName, String suffix, String pNodeName) throws IOException, SAXException, ParserConfigurationException {
		boolean isOk = false;

		
		StringBuilder strBuilder = new StringBuilder();

		List<String> lines = new ArrayList<String>();

		String packageName = "package com.youandi.backend.tr.domain;";
		String import1 = "import lombok.*;";
		String import2 = "import java.util.List;";
		String annotation1 = "@Data";
		String annotation2 = "@ToString";
		String annotation3 = "@EqualsAndHashCode(callSuper=false)";

//		String className = "public class " + TR_XML_FILE_PREFIX + trName + suffix + " extends TrHeader {";
		String className = "public class " + TR_XML_FILE_PREFIX + trName + suffix + " {";
		
//		String defaultField1 = "\t" + "private String clsf = \"\";";
		String defaultField2 = "\t" + "private TRX_HEADER TRX_HEADER = null; ";
	
		lines.add(packageName);
		lines.add("");
		lines.add(import1);
		lines.add(import2);
		lines.add("");
		lines.add(annotation1);
		lines.add(annotation2);
		lines.add(annotation3);
		lines.add(className);
//		lines.add(defaultField1);
		
		if(OUTPUT_BEAN_SUFFIX.equals(suffix)) {
			lines.add(defaultField2);
		}
		lines.add("");

		strBuilder.append(packageName + "\n");
		
		strBuilder.append("\n");
		strBuilder.append(import1 + "\n");
		strBuilder.append(import2 + "\n");
		
		strBuilder.append("\n");
		strBuilder.append(annotation1 + "\n");
		strBuilder.append(annotation2 + "\n");
		strBuilder.append(annotation3 + "\n");
		
		strBuilder.append(className + "\n");
//		strBuilder.append(defaultField1 + "\n");
		
		if(OUTPUT_BEAN_SUFFIX.equals(suffix)) {
			strBuilder.append(defaultField2 + "\n");
		}
		
		strBuilder.append("\n");

		// XML 문서 파싱
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		String xml = FileUtils.readFileToString(new File(filePath + TR_XML_FILE_PREFIX + trName + ".xml"), "utf-8");
		xml = xml.replaceAll("\n", "");
		xml = xml.replaceAll("\t", "");


		System.out.println(xml);
		Reader targetReader = new StringReader(xml);

		Document document = documentBuilder.parse(new InputSource(targetReader));

		// root 구하기
		Element root = document.getDocumentElement();

		NodeList childeren = root.getChildNodes(); // 자식 노드 목록 get
		for(int i = 0; i < childeren.getLength(); i++){
			Node node = childeren.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){ // 해당 노드의 종류 판정(Element일 때)
				Element ele = (Element)node;
				String nodeName = ele.getNodeName();
				System.out.println("node name: " + nodeName);
				if(pNodeName.equals(nodeName)) {
					// 이름이 student인 노드는 자식노드가 더 존재함
					System.out.println("** node name: " + nodeName);

					NodeList inputList = ele.getChildNodes();
					for(int a = 0; a < inputList.getLength(); a++){
						Node iNode = inputList.item(a);
						if(iNode.getNodeType() == Node.ELEMENT_NODE){
							Element iElement = (Element)iNode;
							String name = iElement.getAttribute("Name");
							System.out.println("*** Name : " + name);
							if("Record1".equals(name)) {
								System.out.println("1 node name: " + iElement.getNodeName() + ":" + name);
								String fieldDeclaration = "private List<Record1> " + "Recode1" + " = null;";
								
								strBuilder.append("\t" + fieldDeclaration + "\n\n");
								lines.add("\t" + fieldDeclaration + "\n");
								
								NodeList iNodeList = iElement.getChildNodes();
								lines.add("\t" + "@Data");
								lines.add("\t" + "@ToString");
								lines.add("\t" + "public clsss Record1 {");
								
								strBuilder.append("\t" + "@Data" + "\n");
								strBuilder.append("\t" + "@ToString" + "\n");
								strBuilder.append("\t" + "public class Record1 {" + "\n");

								for(int idx=0; idx < iNodeList.getLength(); idx++) {
									Node i1Node = iNodeList.item(idx);
									Element i1Element = (Element)i1Node;
									String i1Name = i1Element.getAttribute("Name");
									String i1FieldDeclaration = "private String " + i1Name + " = \"\";";
									strBuilder.append("\t\t" + i1FieldDeclaration + "\n");
									lines.add("\t\t" + i1FieldDeclaration);
								}
								strBuilder.append("\t" + "}" + "\n");
								lines.add("\t" + "}");

							} else {
								String fieldDeclaration = "private String " + name + " = \"\";";

								strBuilder.append("\t" + fieldDeclaration + "\n");
								lines.add("\t" + fieldDeclaration);
								// System.out.println("private String " + iElement.getAttribute("Name") + " = \"\";");
							}

						}
					}
				}
			}
		}
		strBuilder.append("}" + "\n");
		lines.add("}");

		System.out.println(strBuilder.toString());

		//Files.write(Paths.get(writeFileAbsPath), content.getBytes("utf8"),StandardOpenOption.CREATE);
		// FileUtils.writeLines(new File("/Users/skyrun/" + TrName + ".java"), lines, true);



		return isOk;
	}
}
