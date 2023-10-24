/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2324.lucene.se.post;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Marco
 */
public class PostExample {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        FSDirectory fsdir = FSDirectory.open(new File("./resources/postex").toPath());
        DirectoryReader ireader = DirectoryReader.open(fsdir); //puo leggere l'indice

        int docid = 0;
        Fields fields = ireader.getTermVectors(docid); //accedi termvector del documento con docId 0
        for (String field : fields) {

        	Terms terms = fields.terms(field); //torna i termini indicizzati per il field
            TermsEnum termsEnum = terms.iterator();
            BytesRef term = null;
            while ((term = termsEnum.next()) != null) { //iteratore
                System.out.print(field+"-> "+term.utf8ToString()); //stampo nome del field -> nome del termine
                /*PostingsEnum postings = termsEnum.postings(null, PostingsEnum.FREQS);
                while (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                    System.out.println(":" + postings.freq());
                }*/
        
                PostingsEnum postings = termsEnum.postings(null, PostingsEnum.ALL); //iteratore della posting list
                while (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                    System.out.print(":" + postings.freq()); //TF
                    for (int i = 0; i < postings.freq(); i++) { //per ogni occorrenza
                        int position = postings.nextPosition(); //prendi la prossima posizione
                        System.out.println("\t" + position + "\t{" + postings.startOffset() + ", " + postings.endOffset() + "}");
                        //stampa posizione , offset partenza e offset destinazione
                    
                    }
                    System.out.println();
                }
            }
        }

    }
}
