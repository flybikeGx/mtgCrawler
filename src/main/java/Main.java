

import com.dora.crawler.*;

import java.io.IOException;

public class Main {
    public static void main(String [] args) throws IOException {

        Crawler crawler = new Crawler();
//        try {
//            System.out.println(crawler.toCHS("Dream Coat"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        String [] cards = Deck.EldraziTax.split("\n");

        for (String card : cards) {
            System.out.println(crawler.getInfo(card,
                    Crawler.CHS_NAME));
            crawler.downloadPic(card);
        }

    }
}
