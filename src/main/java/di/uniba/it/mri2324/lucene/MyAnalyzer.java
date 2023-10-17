/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.mri2324.lucene;

import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 *
 * @author pierpaolo
 */
public class MyAnalyzer extends Analyzer {

    /**
     * Creazione di un Analyzer personalizzato
     */
    public static final CharArraySet STOP_WORDS;

    static {
        final List<String> stopWords = Arrays.asList("a", "an", "and", "are", "the", "is", "but", "by");
        //scrivo le mie stopword nella lista di stringhe

        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        //le trasformiamo in un set di caratteri
        //false indica che sono case sensitive (quindi tHE non lo elimina)
        //true indica che sono non case sensitive (quindi tHE lo elimina)

        STOP_WORDS = CharArraySet.unmodifiableSet(stopSet);
        //la rendo immutabile con unmodifiableSet e setto la costante di classe
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new LetterTokenizer(); //si basa sui caratteri non letterali
        TokenStream filter = new LowerCaseFilter(source); //trasforma tutto in minuscolo
        PorterStemFilter porter=new PorterStemFilter(filter); //applica lo stemming alle parole in lowercase
        filter = new StopFilter(porter, STOP_WORDS); //elimina le stopword
        return new TokenStreamComponents(source, filter); //ritorna il token stream, che vuole come parametri il tipo di tokenizzatore e filtro finale

    }

}
