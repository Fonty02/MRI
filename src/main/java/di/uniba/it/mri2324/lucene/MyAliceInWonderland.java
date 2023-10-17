package di.uniba.it.mri2324.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;

import java.io.*;

public class MyAliceInWonderland {


    public static void main(String[] args) {
        try {
            BufferedReader reader=new BufferedReader(new FileReader("./resources/Alice_Adv_Lewis_Carroll_utf8.txt"));
            FSDirectory fsDirectory=FSDirectory.open(new File("./resources/myAliceIndex").toPath());
            IndexWriterConfig iwc=new IndexWriterConfig(new StandardAnalyzer());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter iwr=new IndexWriter(fsDirectory,iwc);

            FieldType ft = new FieldType(TextField.TYPE_STORED);
            ft.setTokenized(true); //done as default
            ft.setStoreTermVectors(true);
            ft.setStoreTermVectorPositions(true);
            ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);

            String line;
            String title = reader.readLine();
            reader.readLine();
            String author = reader.readLine();
            reader.readLine();
            String lastChapter="";
            String text="";
            while((line=reader.readLine())!=null) {
                if (line.startsWith("CHAPTER ")) {
                    lastChapter = line;
                } else {
                    text = "";
                    while (!line.equals("")) {
                        text += line;
                        line = reader.readLine();
                    }
                    if (!text.equals("")) {
                        System.out.println(title);
                        System.out.println(author);
                        System.out.println(lastChapter);
                        System.out.println(text.trim());
                        System.out.println("-----------");
                        Document doc = new Document();
                        doc.add(new TextField("titolo", title, Field.Store.YES));
                        doc.add(new TextField("autore", author, Field.Store.YES));
                        doc.add(new TextField("capitolo", lastChapter, Field.Store.YES));
                        doc.add(new Field("testo", text, ft));
                        iwr.addDocument(doc);
                    }
                }
            }
            iwr.close();

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
