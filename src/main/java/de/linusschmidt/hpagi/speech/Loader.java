package de.linusschmidt.hpagi.speech;

import de.linusschmidt.hpagi.utilities.FileUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private FileUtil fileUtil;

    public Loader() {
        this.fileUtil = new FileUtil();
    }

    private List<SpeechDataBuffer> loadData(String file, int maxSize) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(this.fileUtil.createFileInFolder("data/datasets/speech", file)));
        int idx = 0;
        String line = "";
        List<SpeechDataBuffer> speechDataBuffers = new ArrayList<>();
        while((line = bufferedReader.readLine()) != null) {
            if(idx++ > maxSize && maxSize != -1) {
                break;
            }
            SpeechDataBuffer speechDataBuffer = new SpeechDataBuffer(line);
            speechDataBuffers.add(speechDataBuffer);
        }
        return speechDataBuffers;
    }

    public static void main(String[] args) throws IOException {
        Loader loader = new Loader();
        List<SpeechDataBuffer> speechDataBuffers = loader.loadData("deu_news_2015_3M-sentences.txt", 10);
        for(SpeechDataBuffer speechDataBuffer : speechDataBuffers) {
            speechDataBuffer.print();
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }
}
