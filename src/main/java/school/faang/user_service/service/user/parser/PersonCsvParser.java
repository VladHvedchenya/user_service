package school.faang.user_service.service.user.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.importer.PersonCsvDto;
import school.faang.user_service.exception.DataValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class PersonCsvParser {
    private final CsvMapper csvMapper;

    public PersonCsvParser() {
        csvMapper = new CsvMapper();
        csvMapper.findAndRegisterModules();
    }

    public List<PersonCsvDto> parse(InputStream inputStream) {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        MappingIterator<PersonCsvDto> iterator;
        try {
            iterator = csvMapper
                    .readerFor(PersonCsvDto.class)
                    .with(schema)
                    .readValues(inputStream);
            return iterator.readAll();
        } catch (IOException e) {
            throw new DataValidationException("Failed to parse Csv file");
        }
    }
}
