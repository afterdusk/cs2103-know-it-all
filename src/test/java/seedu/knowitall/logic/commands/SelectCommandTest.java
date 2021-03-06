package seedu.knowitall.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.knowitall.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.knowitall.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.knowitall.logic.commands.CommandTestUtil.showCardAtIndex;
import static seedu.knowitall.testutil.TypicalCards.getTypicalFolderOneAsList;
import static seedu.knowitall.testutil.TypicalIndexes.INDEX_FIRST_CARD;
import static seedu.knowitall.testutil.TypicalIndexes.INDEX_SECOND_CARD;
import static seedu.knowitall.testutil.TypicalIndexes.INDEX_THIRD_CARD;

import org.junit.Before;
import org.junit.Test;

import seedu.knowitall.commons.core.Messages;
import seedu.knowitall.commons.core.index.Index;
import seedu.knowitall.logic.CommandHistory;
import seedu.knowitall.model.Model;
import seedu.knowitall.model.ModelManager;
import seedu.knowitall.model.UserPrefs;
import seedu.knowitall.testutil.TypicalIndexes;

/**
 * Contains integration tests (interaction with the Model) for {@code SelectCommand}.
 */
public class SelectCommandTest {
    private Model model = new ModelManager(getTypicalFolderOneAsList(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalFolderOneAsList(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() {
        model.enterFolder(TypicalIndexes.INDEX_FIRST_CARD_FOLDER.getZeroBased());
        expectedModel.enterFolder(TypicalIndexes.INDEX_FIRST_CARD_FOLDER.getZeroBased());
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Index lastCardIndex = Index.fromOneBased(model.getActiveFilteredCards().size());

        assertExecutionSuccess(INDEX_FIRST_CARD);
        assertExecutionSuccess(INDEX_THIRD_CARD);
        assertExecutionSuccess(lastCardIndex);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getActiveFilteredCards().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_CARD_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showCardAtIndex(model, INDEX_FIRST_CARD);
        showCardAtIndex(expectedModel, INDEX_FIRST_CARD);

        assertExecutionSuccess(INDEX_FIRST_CARD);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showCardAtIndex(model, INDEX_FIRST_CARD);
        showCardAtIndex(expectedModel, INDEX_FIRST_CARD);

        Index outOfBoundsIndex = INDEX_SECOND_CARD;
        // ensures that outOfBoundIndex is still in bounds of card folder list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getActiveCardFolder().getCardList().size());

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_CARD_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        SelectCommand selectFirstCommand = new SelectCommand(INDEX_FIRST_CARD);
        SelectCommand selectSecondCommand = new SelectCommand(INDEX_SECOND_CARD);

        // same object -> returns true
        assertTrue(selectFirstCommand.equals(selectFirstCommand));

        // same values -> returns true
        SelectCommand selectFirstCommandCopy = new SelectCommand(INDEX_FIRST_CARD);
        assertTrue(selectFirstCommand.equals(selectFirstCommandCopy));

        // different types -> returns false
        assertFalse(selectFirstCommand.equals(1));

        // null -> returns false
        assertFalse(selectFirstCommand.equals(null));

        // different card -> returns false
        assertFalse(selectFirstCommand.equals(selectSecondCommand));
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index},
     * and checks that the model's selected card is set to the card at {@code index} in the filtered card list.
     */
    private void assertExecutionSuccess(Index index) {
        SelectCommand selectCommand = new SelectCommand(index);
        String expectedMessage = String.format(SelectCommand.MESSAGE_SELECT_CARD_SUCCESS, index.getOneBased());
        expectedModel.setSelectedCard(model.getActiveFilteredCards().get(index.getZeroBased()));

        assertCommandSuccess(selectCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    /**
     * Executes a {@code SelectCommand} with the given {@code index}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String expectedMessage) {
        SelectCommand selectCommand = new SelectCommand(index);
        assertCommandFailure(selectCommand, model, commandHistory, expectedMessage);
    }
}
