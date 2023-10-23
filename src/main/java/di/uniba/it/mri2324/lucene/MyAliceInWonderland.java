package di.uniba.it.mri2324.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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

            FieldType ft = new FieldType(TextField.TYPE_STORED); //creo un mio FieldPersonalizzato
            //TextField.TYPE_STORED -> memorizza il testo così com'è
            ft.setTokenized(true); //deve effettuare la tokenizzazione (true), oppure no (false) (il TextField lo fa di default, al contrario dello StringField)
            ft.setStoreTermVectors(true); //Aggiungi il vettore dei termini
            ft.setStoreTermVectorPositions(true); //al vettore dei termini aggiungi le posizioni
            ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            //aggiungi FREQUENZA DOCUMENTO, POSIZIONE, OFFSET
            //POSIZIONE -> Valore che indica la posizione del termine all'interno del documento (fatto in base ai termini)
            //OFFSET -> Valore che indica la posizione del termine all'interno del documento (fatto in base ai caratteri)

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
            IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(fsDirectory));
            QueryParser qp=new QueryParser("testo",new StandardAnalyzer());
            Query q = qp.parse("\"The Had Matter\"~9999");
            TopDocs topdocs = searcher.search(q, 10);
            System.out.println("Query 1) Found " + topdocs.totalHits.value + " document(s)");
            ScoreDoc[] hits = topdocs.scoreDocs;
            for (ScoreDoc hit : hits) {
                Document hitDoc = searcher.doc(hit.doc); //hits[i].doc è l'id del documento. Mi permette di ritornare il documento
                System.out.println(hitDoc.get("testo") + ") " + hit.score);
            }
            System.out.println("\n");
            q = qp.parse("\"The Hat Matter\"~9999 AND \"Cheshire Cat\"~9999");
            topdocs = searcher.search(q, 10);
            System.out.println("Query 2) Found " + topdocs.totalHits.value + " document(s).");
            System.out.println("\n");
            q = qp.parse("(\"The Hat Matter\"~9999 OR \"Cheshire Cat\"~9999) AND \"Alice\""); // qua non manca un tilde9999?
            topdocs = searcher.search(q, 10);
            System.out.println("Query 3) Found " + topdocs.totalHits.value + " document(s).");
            System.out.println("\n");
            q = qp.parse("() AND \"Alice\"");
            topdocs = searcher.search(q, 10);
            System.out.println("Query 3) Found " + topdocs.totalHits.value + " document(s).");
            System.out.println("\n");
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
