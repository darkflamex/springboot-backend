package com.youandi.backend.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.youandi.backend.constant.TrConfig;

@Service
public class TrAutoGeneratorService {
	
	// @Autowired
	private TrConfig trConfig;
	private static final String TAB = "    ";
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		TrAutoGeneratorService TrAutoGeneratorService = new TrAutoGeneratorService();
		
		String trName = "00015004";

		String xmlFilePath = "/Users/skyrun/git/backend/src/main/resources/jkass/TrxRule/";
		String requestBeanPackage = "com.youandi.backend.tr.domain";
		String responseBeanPackage = "com.youandi.backend.tr.domain";
		
		if(TrAutoGeneratorService.trConfig == null) {
			TrAutoGeneratorService.trConfig = TrConfig.builder()
					.requestBeanPackage(requestBeanPackage)
					.responseBeanPackage(responseBeanPackage)
					.xmlFilePath(xmlFilePath)
					.build();
		}
			
		TrAutoGeneratorService.xmlToOuputBeanFile(trName);
//		TrAutoGeneratorService.xmlToInputBeanFile(trName);

	}
	
	public boolean xmlToInputBeanFile(String trName) throws IOException, SAXException, ParserConfigurationException {
		return this.xmlToBeanFile(trName , TrConfig.INPUT_BEAN_SUFFIX, "Input");
	}
	
	public boolean xmlToOuputBeanFile(String trName) throws IOException, SAXException, ParserConfigurationException {
		return this.xmlToBeanFile(trName , TrConfig.OUTPUT_BEAN_SUFFIX, "Output");
	}
	
	private boolean xmlToBeanFile(String trName, String suffix, String pNodeName) throws IOException, SAXException, ParserConfigurationException {
		boolean isOk = false;


		List<String> lines = new ArrayList<String>();

		// TODO Package Name Setting
		String packageName = "";
		
		List<String> packages = new ArrayList<String>();
		List<String> imports = new ArrayList<String>();
		List<String> annotations = new ArrayList<String>();
		List<String> classNames = new ArrayList<String>();
		List<String> defaultFields = new ArrayList<String>();
		
		String import1 = "import lombok.Data;" + "\n";
		String import2 = "import lombok.ToString;" + "\n";
		imports.add(import1);
		imports.add(import2);
		
		String annotation1 = "@Data" + "\n";
		String annotation2 = "@ToString" + "\n";
		annotations.add(annotation1);
		annotations.add(annotation2);
		
		
		if(TrConfig.OUTPUT_BEAN_SUFFIX.equals(suffix)) {
			
			packageName = "package " + trConfig.getResponseBeanPackage() + ";" + "\n";;
			packages.add(packageName);
			
			String import3 = "import lombok.EqualsAndHashCode;" + "\n";;
			imports.add(import3);
			
			String annotation3 = "@EqualsAndHashCode(callSuper=false)" + "\n";;
			annotations.add(annotation3);
			
			String className = "public class " + TrConfig.TR_XML_FILE_PREFIX + trName + suffix + " {" + "\n";
			classNames.add(className);
			
		} else {
			packageName = "package " + trConfig.getRequestBeanPackage() + ";" + "\n";
			packages.add(packageName);
		
			String import3 = "import lombok.Builder;" + "\n";
			imports.add(import3);
			
			String annotation3 = "@Builder" + "\n";
			annotations.add(annotation3);			

			String className = "public class " + TrConfig.TR_XML_FILE_PREFIX + trName + suffix + " {" + "\n";
			classNames.add(className);
		}
		
		defaultFields = parseXML(trName, suffix);
		
		
//		List<String> packages = new ArrayList<String>();
//		List<String> imports = new ArrayList<String>();
//		List<String> annotations = new ArrayList<String>();
//		List<String> classNames = new ArrayList<String>();
//		List<String> defaultFields = new ArrayList<String>();
		
		
		lines.addAll(packages);
		lines.add("\n");
		lines.addAll(imports);
		lines.add("\n");
		
		lines.addAll(annotations);
		lines.addAll(classNames);
		lines.add("\n");
		
		lines.addAll(defaultFields);
		
		for(String line : lines) {
			System.out.print(line);
		}
		
		return false;
	}
	
	
	private List<String> parseXML(String trName, String suffix) throws SAXException, IOException, ParserConfigurationException {
		List<String> defaultFields = new ArrayList<String>();
		String sNodeName = "";
		if(TrConfig.OUTPUT_BEAN_SUFFIX.equals(suffix)) {
			String defaultField = TAB + "private TRX_HEADER TRX_HEADER = null; " + "\n";
			defaultFields.add(defaultField);
			
			
			sNodeName = TrConfig.OUTPUT_NODE_NAME;
		} else {
			sNodeName = TrConfig.INPUT_NODE_NAME;
		}
		
		
		
		// XML 문서 파싱
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		String xml = FileUtils.readFileToString(new File(trConfig.getXmlFilePath() + TrConfig.TR_XML_FILE_PREFIX + trName + ".xml"), "utf-8");
		xml = xml.replaceAll("\n", "");
		xml = xml.replaceAll("\t", "");
		
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
				
				if(sNodeName.equals(nodeName)) {

					NodeList inputList = ele.getChildNodes();
					for(int a = 0; a < inputList.getLength(); a++){
						Node iNode = inputList.item(a);
						if(iNode.getNodeType() == Node.ELEMENT_NODE){
							Element iElement = (Element)iNode;
							String name = iElement.getAttribute("Name");
							
							if(!"Record1".equals(name)) {
								String nomalField = TAB + "private String " + name + " = \"\";" + "\n";
								defaultFields.add(nomalField);
							} else {
								NodeList iNodeList = iElement.getChildNodes();
					
								String recordField = TAB + "private List<Record1> " + "Recode1" + " = null;" + "\n";
								defaultFields.add(recordField);
								
								String recordClass1 = TAB + "@Data" + "\n";
								String recordClass2 = TAB + "@ToString" + "\n";
								String recordClass3 = TAB + "public clsss Record1 {" + "\n";
								
								defaultFields.add("\n");
								defaultFields.add(recordClass1);
								defaultFields.add(recordClass2);
								defaultFields.add(recordClass3);
									
								
								for(int idx=0; idx < iNodeList.getLength(); idx++) {
									Node i1Node = iNodeList.item(idx);
									Element i1Element = (Element)i1Node;
									String i1Name = i1Element.getAttribute("Name");
									String recordClassField = TAB+TAB + "private String " + i1Name + " = \"\";" + "\n";
									defaultFields.add(recordClassField);
								}
								String recordClass4 = TAB + "}" + "\n";
								defaultFields.add(recordClass4);
							} 
						}
					}
				}
			}
		}
		
		String classEnd = "}";
		defaultFields.add(classEnd);
		
		//Files.write(Paths.get(writeFileAbsPath), content.getBytes("utf8"),StandardOpenOption.CREATE);
		// FileUtils.writeLines(new File("/Users/skyrun/" + TrName + ".java"), lines, true);

		return defaultFields;

	}
}
