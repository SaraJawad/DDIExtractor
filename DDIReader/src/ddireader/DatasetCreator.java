/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddireader;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sara Jawad
 */
public class DatasetCreator {

    ArrayList<String> filesList = new ArrayList<>();
    ArrayList<String> sentencesList = new ArrayList<>();
    ArrayList<String> classLabelList = new ArrayList<>();

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        DatasetCreator datasetCreator = new DatasetCreator();
        String FolderPath = "C:\\Users\\Sara Jawad\\Desktop\\NLP\\1. Drug Drug Relation\\1. Drug Drug Relation";
        File folder = new File(FolderPath);
        datasetCreator.listFilesForFolder(folder);    //we are calling this function (which reads files in a folder) and sending path stored in string 'folder'
        for (int i = 0; i < datasetCreator.filesList.size(); i++) {
            System.out.println("File Name = " + datasetCreator.filesList.get(i));
            File file = new File(FolderPath + "\\" + datasetCreator.filesList.get(i));
            datasetCreator.XMLReader(file);    //calling xmlReader function and setting folderpath to send as parameter in function
        }
        datasetCreator.usingPrintWriter();
        // TODO code application logic here
    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);   //this is recursive function incase if there exists a folder within a folder then it calls itsself 
                //basically first inner folder files are read then outer folder files are read! 
            } else {
                filesList.add(fileEntry.getName());  //incase there are no inner folders so then else works and reads only files and gets filenames
//                System.out.println();  //for printing all filenames in a folder
            }
        }
    }

    public void XMLReader(File file) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();       //fixes if extra spaces exists, to make it easy for xml reader
        NodeList nList = document.getElementsByTagName("sentence");    // tagname cuz tag starts with <sentence n we need to search for each sentence
        String eSent= "";
        for (int temp = 0; temp < nList.getLength(); temp++) {
            String sentence ="";
            Node node = nList.item(temp);
            System.out.println("");
            Element sElement = (Element) node;     //making a node of type Element
            if (sElement.hasChildNodes()) {
                sentence = eSent + sElement.getAttribute("text");
                eSent = "";
                sentencesList.add(sentence);
            }
            else
            {
                eSent = eSent + sElement.getAttribute("text");
                continue;
            }
            NodeList pList = sElement.getElementsByTagName("pair");    //storing all pair ids in a node list called pList
            boolean flag = true;
            String classLabel;
            for (int i = 0; i < pList.getLength() && flag; i++) {

                Node pair = pList.item(i);      //making a pair node which has pair id of each item from pain list (iterating one by one)
                Element pElement = (Element) pair;
                //NOW TO CHECK FOR DRUG INTERACTION WE WILL PUT CONDITION TO SEARCH FOR DDI WHICH IS TRUE ONLY(which interacts)

                if (pElement.getAttribute("ddi").equals("true")) {
                    flag = false;
                }
            }
            if (flag) {
                classLabel = "false";    //will add false to classlabe
            } else {
                classLabel = "true";    //will add true to classlabel
            }
            classLabelList.add(classLabel);
        }

    }
   
    public void usingPrintWriter() throws IOException {
        File fileWriter = new File("C:\\Users\\Sara Jawad\\Desktop\\Emoloyees XML\\DDIAllDataset1.csv");
        PrintWriter pw = new PrintWriter(fileWriter);
//        pw.println("Drug 1" + "," + "Drug 2");
        System.out.println("********Printing start here********** ");
        for (int i = 0; i < sentencesList.size(); i++) {

            pw.println("\"" + sentencesList.get(i).replaceAll("(\\r|\\n)", " ").replaceAll("\"", "") + "\"" + "," + classLabelList.get(i));
            System.out.println(sentencesList.get(i) + "%" + classLabelList.get(i));

        }
        pw.close();

    }

}
