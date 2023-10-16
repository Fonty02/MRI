package di.uniba.it.mri2324.lucene;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
*
* @author marco
*/
public class HelloWorld {

   /**
    * @param args the command line arguments
 * @throws ParseException 
    */
	public static void main(String[] args) throws ParseException {
       try {
           //Open a directory from the file system (index directory)
           FSDirectory fsdir = FSDirectory.open(new File("./resources/documenti_news").toPath());
           
           
           //IndexWriter configuration, sono le impostazioni del writer (tipo qui do analizzatore standard)
           IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
           
           //Index directory is created if not exists or overwritten
           //ATTENZIONE: CREATE RICREA DA CAPO l'INDICE, CREATE_OR_APPEND AGGIUNGE DOCUMENTI ALL'INDICE (quelli gia esistenti li riaggiunge)
           iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
           
           
           //Create IndexWriter, come parametro gli passo il path del file system e la configurazione del writer
           //CREERA' L INDICE INVERSO
           IndexWriter writer = new IndexWriter(fsdir, iwc);
           //Create document and add fields
           
           
           Document doc = new Document(); //vuoto
           doc.add(
        		   new TextField("titolo", "Articolo Web Numoro 1", Field.Store.YES)
        		   );
           //Il campo Field.Store.YES indica che il campo deve essere memorizzato cosi come passato (INTERO) nell'indice inverso
           doc.add(new TextField("introduzione", "questa è l'introduzione del mio documento", Field.Store.YES));
           doc.add(new TextField("contenuto", "questo è il contenuto del mio documento", Field.Store.NO));
           doc.add(new TextField("commenti", "questo è un commento di un utente di esempio", Field.Store.NO));
           //add document to index. Aggiunta e cancellazione su quello in RAM
           writer.addDocument(doc);
           
           
           //close IndexWriter e salva su DISCO FISSO
           writer.close();
           
           //Create the IndexSearcher, apre l'indice inverso scritto su disco fisso
           IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(fsdir));
           
           
           //Create the query parser with the default field and analyzer. Analogamente all'index writer, gli dico come
           //trattare la query (in questo caso analizzatore standard SEMPRE COME i documenti -> Lucene non controlla)
           //il primo parametro è il campo di default utilizzato per la ricerca SE NON SPECIFICATO dall'utente
           QueryParser qp = new QueryParser("commenti", new StandardAnalyzer());
           
           //Parse the query. Per costruisco la query uso l'oggetto QueryParser e gli passo la stringa da parsare
           Query q = qp.parse("utente");
           //Quindi ora cerco utente nel campo commenti


           //Search i top secondo parametro (10 in questo caso) documenti che matchano la query
           TopDocs topdocs = searcher.search(q, 10);

           //in questo caso stampo solo i documenti
           System.out.println("Found " + topdocs.totalHits.value + " document(s).");
           
       } catch (IOException ex) {
           Logger.getLogger(HelloWorld.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

}