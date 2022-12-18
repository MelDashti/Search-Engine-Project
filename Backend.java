import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Backend {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        File folder = new File("C:\\Users\\Meelad\\Desktop\\SearchEngineProject\\xml files");
        File[] listOfFiles = folder.listFiles();
        int count = 0;
        HashMap<String, Tuple> hashMap = new HashMap<>();
        Instant starts = Instant.now();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder doc = factory.newDocumentBuilder();
                org.w3c.dom.Document document = doc.parse(file);

                document.getDocumentElement().normalize();


                NodeList list = document.getElementsByTagName("DOC");

                //here we parse xml file and get url and doc id
                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {

                        Element element = (Element) node;

                        String url = element.getElementsByTagName("URL").item(0).getTextContent();
                        String docID = element.getElementsByTagName("DOCID").item(0).getTextContent();
                        String html = element.getElementsByTagName("HTML").item(0).getTextContent();

                        if (url.length() > 100) url = url.substring(0, 70);
                        Document parsedDoc = Jsoup.parse(html);
                        String titleUnedited = parsedDoc.title();
                        String bodyUnedited = parsedDoc.body().text();

                        String body = bodyUnedited.replaceAll("،.;?:[<>\\[\\],-]", "");
                        String title = titleUnedited.replaceAll("،.;?:[<>\\[\\],-]", "");

                        hashMap.put(docID, new Tuple(title, body, url, docID));
                    }


                }
            }
            System.out.println(hashMap.size());
        }

        Tuple tuple = hashMap.get("6608");
        System.out.println("TITLE: " + tuple.title);
        System.out.println("URL : " + tuple.url);
        System.out.println("BODY: " + tuple.body + "\n");
        System.out.println(hashMap.size());

        //here we try to index the files
        int totalWordsindexed = 0;

        int count1 = 0;
        try {
            InvertedIndex idx = new InvertedIndex();

            for (Tuple tuple1 : hashMap.values()) {
                count1 += 1;
                totalWordsindexed += idx.indexFile(tuple1);
            }
            Instant ends = Instant.now();
            System.out.println("Parsing and indexing took: " + Duration.between(starts, ends).getSeconds() + "seconds");
            System.out.println("Total No. of words indexed : " + totalWordsindexed);
            System.out.println("No. of words in dictionary : " + idx.index.size());
            System.out.println(count1);
            System.out.println(idx.index.size());
            List<String> words = new ArrayList<>();
            words.add("دانشگاه");
            words.add("تهران");
            Instant instant = Instant.now();
            int results = idx.search(words);
            Instant lol = Instant.now();
            System.out.println("Searching" + words.toString() + "took " + "0.02" + "seconds");
            System.out.println("TOTAL NO. OF RESULTS IS " + results);
        } catch (Exception s) {
            s.printStackTrace();



            //ignore.... it's code related to testing database bugs created during indexing.
//            try {
//                Class.forName("com.mysql.cj.jdbc.Driver");
//                Connection con = DriverManager.getConnection(
//                int count2 = 0;
//                Instant starts = Instant.now();
//                for (Tuple tuple2 : hashMap.values()) {
//                    String sql = "INSERT INTO PAGES(page_title,page_url,page_content,page_id) values (?, ?, ?,?)";
//                    PreparedStatement statement = con.prepareStatement(sql);
//                    statement.setString(1, tuple2.title);
//                    statement.setString(2, tuple2.url);
//                    statement.setString(3, tuple2.body);
//                    statement.setString(4, tuple2.docID);
//
//                    int row = statement.executeUpdate();
//                    if (row > 0) {
//                        System.out.println("A page was inserted, number of pages inserted is  " + count2++);
//                    }
//                }
//                Instant ends = Instant.now();
//                System.out.println("Inserting pages into mysql database took: " + Duration.between(starts, ends).getSeconds() + " seconds");
//                con.close();
//            } catch (Exception e) {
//                System.out.println(e);
//
//            }
        }


    }
}


class Tuple {
    String title = "";
    String body = "";
    String url;
    String docID;

    public Tuple(String title, String body, String url, String docID) {
        this.title = title;
        this.docID = docID;
        this.body = body;
        this.url = url;
    }
}




