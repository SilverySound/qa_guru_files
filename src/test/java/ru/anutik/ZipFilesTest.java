package ru.anutik;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import model.Address;
import model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Реализация чтения и проверки содержимого файлов из архива sample")
public class ZipFilesTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassLoader cl = ZipFilesTest.class.getClassLoader();


    @DisplayName("Реализация чтения и проверки содержимого xls файла из архива sample")
    @Test
    void xlsFileParsingTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/sample.zip"));
        ZipEntry entry = zf.getEntry("sample_excel.xlsx");
        if (entry != null) {
            try (InputStream inputStream = zf.getInputStream(entry)) {
                XLS xls = new XLS(inputStream);
                String actualValue = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                assertTrue(actualValue.contains("ExcelFile"));
            }
        } else {
            System.out.println("Файл sample_excel.xlsx не найден в архиве.");
        }
    }

    @DisplayName("Реализация чтения и проверки содержимого pdf файла из архива sample")
    @Test
    void pdfFileParsingTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/sample.zip"));
        ZipEntry entry = zf.getEntry("sample_pdf.pdf");
        if (entry != null) {
            try (InputStream inputStream = zf.getInputStream(entry)) {
                PDF pdf = new PDF(inputStream);
                assertEquals("ДС1_ООО_Альянс_Комплексная_Безопасность", pdf.title);
            }
        } else {
            System.out.println("Файл sample_pdf.xlsx не найден в архиве.");
        }
    }
    
    @DisplayName("Реализация чтения и проверки содержимого csv файла из архива sample")
    @Test
    void сsvFileParsingTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/sample.zip"));
        ZipEntry entry = zf.getEntry("sample.csv");
        if (entry != null) {
            try (InputStream inputStream = zf.getInputStream(entry)) {
                CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
                List<String[]> data = csvReader.readAll();
                assertEquals(2, data.size());
                Assertions.assertArrayEquals(
                        new String[]{"Day", "Month", "Year"},
                        data.get(0));
                Assertions.assertArrayEquals(
                        new String[]{"Monday", "April", "2020"},
                        data.get(1));
            }
        } else {
            System.out.println("Файл sample.csv не найден в архиве.");
        }
    }

    @DisplayName("Реализация разбора json файла библиотекой Jackson")
    @Test
    void jsonFileParsingTest() throws Exception {
       try (Reader reader = new InputStreamReader(Objects.requireNonNull(cl.getResourceAsStream("person.json"))))
            {
                Person actual = objectMapper.readValue(reader, Person.class);

                assertEquals(123, actual.getId());
                assertEquals("John", actual.getName());
                assertTrue(actual.isPermanent());

                Address address = actual.getAddress();
                assertEquals("Albany Dr", address.getStreet());
                assertEquals("San Jose", address.getCity());

                long[] phoneNumbers = actual.getPhoneNumbers();
                assertEquals(123456, phoneNumbers[0]);
                assertEquals(987654, phoneNumbers[1]);

                assertEquals("Manager", actual.getRole());

            }
    }
}



