package seedu.knowitall.logic.parser;

import static seedu.knowitall.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.knowitall.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.knowitall.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.knowitall.logic.commands.ImportCommand;
import seedu.knowitall.storage.csvmanager.CsvFile;



public class ImportCommandParserTest {

    private static final String EMPTY_STRING = "";
    private ImportCommandParser parser = new ImportCommandParser();

    @Test
    public void parse_validArgs_returnsExportCommand() {
        String testFile = "myCsvFile.csv";

        assertParseSuccess(parser, testFile, new ImportCommand(new CsvFile(testFile)));

        // assertParseSuccess(parser, "1 3 5", new ExportCommand(new ArrayList<Integer>(Arrays.asList(1,3,5))));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String testFileIncorrect = "hello.exe";

        assertParseFailure(parser, EMPTY_STRING, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                CsvFile.FILENAME_CONSTRAINTS));

        assertParseFailure(parser, testFileIncorrect, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ImportCommand.MESSAGE_USAGE));

    }
}
