

import com.dora.crawler.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        final Crawler crawler = new Crawler();
//        try {
//            System.out.println(crawler.toCHS("Dream Coat"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        File f = new File("deck.txt");

        Deck deck = new Deck();
        deck.readDeck(f);

        File of = new File("中文卡表.txt");

        final FileWriter writer = new FileWriter(of);

        deck.foreachCard(new Deck.Iterator() {
            public void run(String card, int number) {
                try {
                    String html = crawler.getHTML(card);
                    String chs = crawler.toCHS(html, card);
                    writer.write(number +" "+ chs + "\n");

                    String chsHtml = crawler.getHTML(chs);
                    crawler.downloadPic(chsHtml, card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        writer.flush();
        writer.close();
    }
}
