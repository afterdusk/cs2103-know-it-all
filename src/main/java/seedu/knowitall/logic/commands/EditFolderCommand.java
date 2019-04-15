package seedu.knowitall.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.knowitall.commons.core.Messages;
import seedu.knowitall.commons.core.index.Index;
import seedu.knowitall.logic.CommandHistory;
import seedu.knowitall.logic.commands.exceptions.CommandException;
import seedu.knowitall.model.CardFolder;
import seedu.knowitall.model.Model;
import seedu.knowitall.model.VersionedCardFolder;

/**
 * Edits the details of an existing card in the card folder.
 */
public class EditFolderCommand extends Command {

    public static final String COMMAND_WORD = "editfolder";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the name of the folder identified "
            + "by the index number used in the displayed folder list. "
            + "Existing folder name will be overwritten.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "NEW_FOLDER_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + " CIRCULATORY SYSTEM ";

    public static final String MESSAGE_EDIT_FOLDER_SUCCESS = "Renamed Folder: %1$s";
    public static final String MESSAGE_DUPLICATE_FOLDER = "This folder already exists.";

    private final Index index;
    private final String newName;

    /**
     * @param index of the card in the filtered card list to edit
     * @param newName the name that we should rename the folder to
     */
    public EditFolderCommand(Index index, String newName) {
        requireNonNull(index);
        requireNonNull(newName);

        this.index = index;
        this.newName = newName;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        if (model.getState() != Model.State.IN_HOMEDIR) {
            throw new CommandException(Messages.MESSAGE_INVALID_COMMAND_INSIDE_FOLDER);
        }

        List<VersionedCardFolder> folderList = model.getFilteredFolders();

        if (index.getZeroBased() >= folderList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FOLDER_DISPLAYED_INDEX);
        }

        CardFolder folderToEdit = folderList.get(index.getZeroBased());

        String folderNameToEdit = folderToEdit.getFolderName().toLowerCase();

        if (!folderNameToEdit.equals(newName.toLowerCase()) && model.hasFolder(newName)) {
            throw new CommandException(MESSAGE_DUPLICATE_FOLDER);
        }

        model.renameFolder(index.getZeroBased(), newName);
        return new CommandResult(String.format(MESSAGE_EDIT_FOLDER_SUCCESS, newName), CommandResult.Type.EDITED_FOLDER);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditFolderCommand)) {
            return false;
        }

        // state check
        EditFolderCommand e = (EditFolderCommand) other;
        return index.equals(e.index)
                && newName.equals(e.newName);
    }

}
