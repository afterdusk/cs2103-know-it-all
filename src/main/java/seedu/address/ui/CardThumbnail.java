package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.card.Card;

/**
 * An UI component that displays information of a {@code Card}.
 */
public class CardThumbnail extends UiPart<Region> {

    private static final String FXML = "CardListThumbnail.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on CardFolder level 4</a>
     */

    public final Card card;

    @FXML
    private HBox thumbnailPane;
    @FXML
    private Label question;
    @FXML
    private Label id;
    @FXML
    private Label answer;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label score;
    @FXML
    private FlowPane hints;

    public CardThumbnail(Card card, int displayedIndex) {
        super(FXML);
        this.card = card;
        id.setText(displayedIndex + ". ");
        question.setText(card.getQuestion().fullQuestion);
        answer.setText(card.getAnswer().fullAnswer);
        address.setText(card.getAddress().value);
        score.setText(card.getScore().toString());
        email.setText(card.getEmail().value);
        card.getHints().forEach(hint -> hints.getChildren().add(new Label(hint.hintName)));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CardThumbnail)) {
            return false;
        }

        // state check
        CardThumbnail card = (CardThumbnail) other;
        return id.getText().equals(card.id.getText())
                && this.card.equals(card.card);
    }
}