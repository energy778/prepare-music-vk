package ru.veretennikov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veretennikov.config.AppProperty;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
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

//        формируем файл для менеджера закачек
        writeMapToFile(source);

    }

    private Map<String, String> readFileToMap(String fileInput) {

        logger.info("Читаем файл: {}", fileInput);

        Map<String, String> source = new HashMap<>();
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
            logger.error(ex.getMessage());
            return source;
        }

        logger.info("Найдено треков: {}", countSong);
        logger.info("Из них уникальных: {}", source.size());

        return source;

    }

    private void removeDuplicates(Map<String, String> source, List<Map<String, String>> previouslyUploadedFiles) {

        if (source == null || source.isEmpty() || previouslyUploadedFiles == null)
            return;

        logger.info("Отсеиваем ранее скачанные");
        logger.info("До обработки: {}", source.size());

        for (Map<String, String> previouslyUploadedFile : previouslyUploadedFiles) {
            for (String songName : previouslyUploadedFile.keySet()) {
                source.remove(songName);
            }
        }

        logger.info("После обработки: {}", source.size());

    }

    private void writeMapToFile(Map<String, String> source) {

        Locale loc = Locale.US;
        Date curDate = new Date();

////        String lastModified = "Tue, 14 Apr 2020 15:53:37 GMT";
//        String patternLastModified = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
//        String lastModified = new SimpleDateFormat(patternLastModified, loc).format(curDate);

//        String date = "04/19/2020 22:06:06";
        String patternDate = "MM/dd/yyyy HH:mm:ss";
        String date = new SimpleDateFormat(patternDate, loc).format(curDate);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(appProperty.getFileOutput()))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.append(System.lineSeparator());
            writer.write("<DownloadList  Version=\"6\"");
            writer.append(System.lineSeparator());
            writer.write(String.format("   NextID=\"%s\">", source.size() +1));
            writer.append(System.lineSeparator());
            writer.flush();

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
//                writer.write(String.format("         <LastModified>%s</LastModified>", lastModified));
//                writer.append(System.lineSeparator());
//                writer.write("         <ResumeMode>2</ResumeMode>");
//                writer.append(System.lineSeparator());
                writer.write(String.format("         <Date>%s</Date>", date));
                writer.append(System.lineSeparator());
//                writer.write("         <DownloadTime>1</DownloadTime>");
//                writer.append(System.lineSeparator());
                writer.write("         <NodeID>23</NodeID>");
                writer.append(System.lineSeparator());
//                writer.write("         <ContentType>audio/mpeg</ContentType>");
//                writer.append(System.lineSeparator());
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
            logger.error(ex.getMessage());
        }

    }

}
