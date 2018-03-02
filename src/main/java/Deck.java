import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Card {
    String name;
    int number;
}

public class Deck {
    List<Card> cards;
    Deck() {
        cards = new ArrayList<Card>();
    }

    public void readDeck (File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        String line;
        while ((line = br.readLine())!=null) {
            if (line.length() < 1) continue;
            Pattern p = Pattern.compile("(^\\d*)\\s*(.*)");
            Matcher m = p.matcher(line);

            if (m.matches()) {
                Card card = new Card();
//                System.out.println(m.groupCount());
//                for (int i = 1; i <= m.groupCount(); i ++) {
//                    System.out.println(m.group(i));
//                }
//                System.out.println("");
                card.name = m.group(2);
                card.number = Integer.valueOf(m.group(1));
                cards.add(card);
            }
        }
    }

    interface Iterator{
        void run(String cardName, int number);
    };

    public void foreachCard(Iterator iter) {
        for (Card c : cards) {
            iter.run(c.name, c.number);
        }
    }


}
