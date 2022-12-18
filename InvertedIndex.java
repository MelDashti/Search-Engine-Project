

import java.io.IOException;
import java.util.*;

public class InvertedIndex {

    List<String> stopwords = Arrays.asList("r", ":", "()", "?", "؟", "*", "[", "]", "-");

    Map<String, List<String>> index = new HashMap<String, List<String>>();
    List<String> files = new ArrayList<String>();

    public int indexFile(Tuple tuple) throws IOException {
        String docID = tuple.docID;
        String text = tuple.title.concat(tuple.body);
        int pos = 0;
        for (String _word : text.split(" ")) {
            String word = _word.toLowerCase();
            if (stopwords.contains(_word)) {
                continue;
            }
            pos++;
            List<String> idx = index.get(_word);
            if (idx == null) {
                idx = new LinkedList<String>();
                index.put(_word, idx);
            }
            idx.add(docID);
        }
        System.out.println("indexed " + tuple.docID + " " + pos + " words");
        return pos;
    }

    public int search(List<String> words) {
        int noOfResults = 0;
        List<Set<String>> setList = new ArrayList<>();
        for (String _word : words) {
            Set<String> answer = new HashSet<String>();
            String word = _word.toLowerCase();
            List<String> idx = index.get(word);
            if (idx != null) {
                answer.addAll(idx);
            }
            setList.add(answer);
            noOfResults = answer.size();
            System.out.print(word);
            for (String f : answer) {
                System.out.print(" " + f);
            }
            System.out.println("");
        }

        Set<String> hello = and_postingList(setList);
        for (String docId : hello) {
            System.out.println("\n DOC ID: " + docId);
        }


        return hello.size();
    }

    public void orPostingList() {


    }

    public Set<String> and_postingList(List<Set<String>> sets) {
        Set<String> results = new HashSet<String>();
        results = sets.get(0);
        for (int i = 1; i < sets.size(); i++) {
            results.retainAll(sets.get(i));
        }
        return results;
    }


    public static void main(String[] args) {


    }

    private class temp {
        private String docID;
        private int position;

        public temp(String docID, int position) {
            this.docID = docID;
            this.position = position;
        }
    }
}