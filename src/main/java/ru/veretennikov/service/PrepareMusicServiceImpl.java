package ru.veretennikov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.veretennikov.config.AppProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrepareMusicServiceImpl implements PrepareMusicService {

    private static final String OTHER_INFORMATION = "#EXTINF:-1,";
    private final AppProperty appProperty;

    @Override
    public void execute() {

//        читаем информацию о текущем плейлисте
        Map<String, String> source;
        String fileInput = appProperty.getFileInput();
        source = readFileToMap(fileInput);

//        читаем информацию о скачанных ранее треках
        List<Map<String, String>> previouslyUploadedFiles = new ArrayList<>();
        if (appProperty.getPreviouslyUploadedFiles() != null){
            for (String previouslyUploadedFile : appProperty.getPreviouslyUploadedFiles()) {
                previouslyUploadedFiles.add(readFileToMap(previouslyUploadedFile));
            }
        }

//        исключаем скачанные ранее треки
        removeDuplicates(source, previouslyUploadedFiles);

//        считаем количество уникальных песен
        int countOfSongs = source.size();

//        формируем файл для менеджера закачек
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appProperty.getFileOutput()))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.append(System.lineSeparator());
            writer.write("<DownloadList  Version=\"6\"");
            writer.append(System.lineSeparator());
            writer.write(String.format("   NextID=\"%s\">", countOfSongs+1));
            writer.append(System.lineSeparator());
            writer.flush();

            String songName;
            String url;
            String baseDir = appProperty.getBaseDir();

            int id = 0;

            for (Map.Entry<String, String> entry : source.entrySet()) {

                id++;

                writer.write(" <DownloadFile>");
                writer.append(System.lineSeparator());
                writer.write(String.format("         <ID>%d</ID>", id));
                writer.append(System.lineSeparator());
                writer.write(String.format("         <URL>%s</URL>", entry.getValue()));
                writer.append(System.lineSeparator());
                writer.write(String.format("         <FileName>%s</FileName>", baseDir + entry.getKey()));
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

    private Map<String, String> readFileToMap(String fileInput) {

        System.out.println(String.format("Читаем файл %s", fileInput));

        Map<String, String> source = new HashMap<>();;
        int countSong = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileInput))) {
//            первая строка - служебная . пропускаем
            String s = reader.readLine();

            if (s != null){

                while (( s = reader.readLine()) != null){

                    String name = s.replace(OTHER_INFORMATION, "");

                    String url;
                    if ((url = reader.readLine()) != null){
                        source.put(name, url);
                        countSong++;
                    }

                }

            }

        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        System.out.println(String.format("Найдено треков: %d", countSong));
        System.out.println(String.format("Из них уникальных: %d", source.size()));

        return source;

    }

    private void removeDuplicates(Map<String, String> source, List<Map<String, String>> previouslyUploadedFiles) {

        if (source == null || source.isEmpty() || previouslyUploadedFiles == null)
            return;

        System.out.println(String.format("Отсеиваем ранее скачанные"));
        System.out.println(String.format("До обработки: %d", source.size()));

        for (Map<String, String> previouslyUploadedFile : previouslyUploadedFiles) {
            for (String songName : previouslyUploadedFile.keySet()) {
                source.remove(songName);
            }
        }

        System.out.println(String.format("После обработки: %d", source.size()));

    }

}
