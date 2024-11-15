import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
         String url = "https://api.nasa.gov/planetary/apod" +               //создаем переменную с адресом и апи ключом из почты
                 "?api_key=PlBwupr36pDhD31oXdScZrnh43yxCEpf5J3TtlC3" +
                 "&date=2024-11-15";                                       // можно изменить дату для загрузки фото определенного дня.

        CloseableHttpClient client = HttpClients.createDefault();   //загружаем метод работы шттп клиента, можно по руководству на сайте производителя клиента
        ObjectMapper mapper = new ObjectMapper();                   // после 26 строки. Загружаем метод  запроса к фйлу json классом из библиотеки Jackson

        HttpGet request = new  HttpGet(url);                         // создаем запрос на сервер по адресу и с ключом
        CloseableHttpResponse response = client.execute(request);    //получаем и сохраняем ответ сервера с переменную response
       /* Scanner scanner = new Scanner(response.getEntity().getContent()); // в рамках метода сканер обращаемся в ответ сервера и запрашиваем тело ответа (getEntity) и то, что в этом теле лежит getContent
        // читаем документацию к методу сканер и там описаны эти команды.
        String answer = scanner.nextLine();
        System.out.println(answer);*/
        // выводим на экран инфу из файла и переносим ее в черновик answer.json
        // Т.к. джава не умеет читать формат json, то создаем класс конструктор, куда складываем инфу из файла через библиотеку Jackson databild (предварительно добавив ее в файл pom.xml)
        //После создания конструктора для чтения файла json обираем из кода запрос к ответу через сканер, т.к. теперь файл будет прочтен классом конструктором NasaAnswer
        // создаем 16 строку
       NasaAnswer answer = mapper.readValue(response.getEntity().getContent(), NasaAnswer.class);  // Этой командой сохраняем данные в переменную answer из файла json в программу с помощью класса NasaAnswer.
       System.out.println(answer.url);
        System.out.println(answer.explanation); // проверяем, все работает.
        String imageUrl = answer.url;
        String[] splitedAnswer = imageUrl.split("/");           //создаем массив для разбиения пути к файлу на части между слешами.
        String fileName = splitedAnswer[splitedAnswer.length-1];      // для имени файла выбираем последний элемент массива - там сохраняется имя файла как на сервере. Теперь файлы будут сохраняться с разными именами.

        HttpGet imageRequest = new HttpGet(imageUrl);   //Чтобы скачать картинку на ПК надо послать GET запрос на сервер
        CloseableHttpResponse image = client.execute(imageRequest);  //наш клиент выполняет такой запрос.
        FileOutputStream fos = new FileOutputStream(fileName); // встроенный класс java - дискриптор, который позволяет сохранять файл в указанный путь.
        image.getEntity().writeTo(fos);                                  // собственно сохраняем содержимое в файл.

    }



}

//https://apod.nasa.gov/apod/image/2411/IC348_B3_1024.jpg
//    ["https:", "" , "apod.nasa.gov" , "apod" , "2411" , "IC348_B3_1024.jpg" ];