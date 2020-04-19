package ru.veretennikov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.veretennikov.config.AppProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrepareMusicServiceImpl implements PrepareMusicService {

    private final AppProperty appProperty;

    @Override
    public void execute() {

        String[] trashArr = new String[] {"#EXTM3U", "#EXTINF:-1,"};
        List<String> source = new ArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(appProperty.getFileInput()))) {
            String s;
            while(( s = reader.readLine()) != null){
                for (String searchWord : trashArr) {
                    s = s.replace(searchWord, "");
                    if (s.isEmpty())
                        break;
                }
                if (s.isEmpty())
                    continue;
                source.add(s);
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        int countOfSongs = source.size() / 2;
        if (source.size() % 2 != 0){
            source.remove(source.size() -1);
            countOfSongs = source.size() / 2;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appProperty.getFileOutput()))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.append(System.lineSeparator());
            writer.write("<DownloadList  Version=\"6\"");
            writer.append(System.lineSeparator());
            writer.write(String.format("   NextID=\"%s\">", countOfSongs +1));
            writer.append(System.lineSeparator());
            writer.flush();

            String songName;
            String url;
            String baseDir = appProperty.getBaseDir();

            int id = 1;

            for (int i = 0; i < source.size(); i+=2, id++) {

                songName = source.get(i);
                url = source.get(i+1);

                writer.write(" <DownloadFile>");
                writer.append(System.lineSeparator());
                writer.write(String.format("         <ID>%d</ID>", id));
                writer.append(System.lineSeparator());
                writer.write(String.format("         <URL>%s</URL>", url));
                writer.append(System.lineSeparator());
                writer.write(String.format("         <FileName>%s</FileName>", baseDir + songName));
                writer.append(System.lineSeparator());
                writer.write("         <State>0</State>");
                writer.append(System.lineSeparator());
                writer.write("         <Size></Size>");
                writer.append(System.lineSeparator());
                writer.write(String.format("         <SaveDir>%s</SaveDir>", baseDir));
                writer.append(System.lineSeparator());
                writer.write("         <LastModified>Tue, 14 Apr 2020 15:53:37 GMT</LastModified>");
                writer.append(System.lineSeparator());
                writer.write("         <ResumeMode>2</ResumeMode>");
                writer.append(System.lineSeparator());
                writer.write("         <Date>04/19/2020 22:06:06</Date>");
                writer.append(System.lineSeparator());
                writer.write("         <DownloadTime>1</DownloadTime>");
                writer.append(System.lineSeparator());
                writer.write("         <NodeID>23</NodeID>");
                writer.append(System.lineSeparator());
                writer.write("         <ContentType>audio/mpeg</ContentType>");
                writer.append(System.lineSeparator());
                writer.write(" </DownloadFile>");
                writer.append(System.lineSeparator());

                writer.flush();

            }

            writer.append(System.lineSeparator());
            writer.flush();

            writer.write("</DownloadList>");
            writer.append(System.lineSeparator());
            writer.flush();

        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

    }

}
