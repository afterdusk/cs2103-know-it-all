package seedu.knowitall.logic;

import static org.junit.Assert.assertEquals;
import static seedu.knowitall.commons.core.Messages.MESSAGE_INVALID_CARD_DISPLAYED_INDEX;
import static seedu.knowitall.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.knowitall.logic.commands.CommandTestUtil.ANSWER_DESC_SAMPLE_1;
import static seedu.knowitall.logic.commands.CommandTestUtil.QUESTION_DESC_SAMPLE_1;
import static seedu.knowitall.testutil.TypicalCards.CARD_1;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.knowitall.logic.commands.AddCommand;
import seedu.knowitall.logic.commands.CommandResult;
import seedu.knowitall.logic.commands.HistoryCommand;
import seedu.knowitall.logic.commands.ListCommand;
import seedu.knowitall.logic.commands.exceptions.CommandException;
import seedu.knowitall.logic.parser.exceptions.ParseException;
import seedu.knowitall.model.Model;
import seedu.knowitall.model.ModelManager;
import seedu.knowitall.model.ReadOnlyCardFolder;
import seedu.knowitall.model.UserPrefs;
import seedu.knowitall.model.card.Card;
import seedu.knowitall.storage.CardFolderStorage;
import seedu.knowitall.storage.JsonCardFolderStorage;
import seedu.knowitall.storage.JsonUserPrefsStorage;
import seedu.knowitall.storage.StorageManager;
import seedu.knowitall.testutil.CardBuilder;
import seedu.knowitall.testutil.TypicalCards;


public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Model model = new ModelManager(TypicalCards.getTypicalFolderOneAsList());
    private Logic logic;

    @Before
    public void setUp() throws Exception {
        JsonCardFolderStorage cardFolderStorage = new JsonCardFolderStorage(temporaryFolder.newFile().toPath());
        List<CardFolderStorage> cardFolderStorageList = new ArrayList<>();
        cardFolderStorageList.add(cardFolderStorage);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.newFile().toPath());
        StorageManager storage = new StorageManager(cardFolderStorageList, userPrefsStorage);
        model.enterFolder(0);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
        assertHistoryCorrect(invalidCommand);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_CARD_DISPLAYED_INDEX);
        assertHistoryCorrect(deleteCommand);
    }

    @Test
    public void execute_validCommand_success() {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
        assertHistoryCorrect(listCommand);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() throws Exception {
        // Setup LogicManager with JsonCardFolderIoExceptionThrowingStub
        JsonCardFolderStorage cardFolderStorage =
                new JsonCardFolderIoExceptionThrowingStub(temporaryFolder.newFile().toPath());
        List<CardFolderStorage> cardFolderStorageList = new ArrayList<>();
        cardFolderStorageList.add(cardFolderStorage);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.newFile().toPath());
        StorageManager storage = new StorageManager(cardFolderStorageList, userPrefsStorage);
        logic = new LogicManager(model, storage);

        // Execute add command
        String addCommand = AddCommand.COMMAND_WORD + QUESTION_DESC_SAMPLE_1 + ANSWER_DESC_SAMPLE_1;
        Card expectedCard = new CardBuilder(CARD_1).withHint().build();
        ModelManager expectedModel = new ModelManager(TypicalCards.getTypicalFolderOneAsList());
        expectedModel.enterFolder(0);
        expectedModel.addCard(expectedCard);
        expectedModel.commitActiveCardFolder();
        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE + DUMMY_IO_EXCEPTION;
        assertCommandBehavior(CommandException.class, addCommand, expectedMessage, expectedModel);
        assertHistoryCorrect(addCommand);
    }

    @Test
    public void getFilteredCardList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        logic.getActiveFilteredCards().remove(0);
    }

    /**
     * Executes the command, confirms that no exceptions are thrown and that the result message is correct.
     * Also confirms that {@code expectedModel} is as specified.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage, Model expectedModel) {
        assertCommandBehavior(null, inputCommand, expectedMessage, expectedModel);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandBehavior(Class, String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<?> expectedException, String expectedMessage) {
        Model expectedModel = new ModelManager(Collections.singletonList(model.getActiveCardFolder()),
                new UserPrefs());
        expectedModel.enterFolder(model.getActiveCardFolderIndex());
        assertCommandBehavior(expectedException, inputCommand, expectedMessage, expectedModel);
    }

    /**
     * Executes the command, confirms that the result message is correct and that the expected exception is thrown,
     * and also confirms that the following two parts of the LogicManager object's state are as expected:<br>
     *      - the internal model manager data are same as those in the {@code expectedModel} <br>
     *      - {@code expectedModel}'s card folder was saved to the storage file.
     */
    private void assertCommandBehavior(Class<?> expectedException, String inputCommand,
                                           String expectedMessage, Model expectedModel) {

        try {
            CommandResult result = logic.execute(inputCommand);
            assertEquals(expectedException, null);
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (CommandException | ParseException e) {
            assertEquals(expectedException, e.getClass());
            assertEquals(expectedMessage, e.getMessage());
        }

        assertEquals(expectedModel, model);
    }

    /**
     * Asserts that the result display shows all the {@code expectedCommands} upon the execution of
     * {@code HistoryCommand}.
     */
    private void assertHistoryCorrect(String... expectedCommands) {
        try {
            CommandResult result = logic.execute(HistoryCommand.COMMAND_WORD);
            String expectedMessage = String.format(
                    HistoryCommand.MESSAGE_SUCCESS, String.join("\n", expectedCommands));
            assertEquals(expectedMessage, result.getFeedbackToUser());
        } catch (ParseException | CommandException e) {
            throw new AssertionError("Parsing and execution of HistoryCommand.COMMAND_WORD should succeed.", e);
        }
    }

    /**
     * A stub class to throw an {@code IOException} when the save method is called.
     */
    private static class JsonCardFolderIoExceptionThrowingStub extends JsonCardFolderStorage {
        private JsonCardFolderIoExceptionThrowingStub(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveCardFolder(ReadOnlyCardFolder cardFolder, Path filePath) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }
}
