package deck;

import exceptions.EmptyListException;
import exceptions.FlashCLIArgumentException;

import static constants.ErrorMessages.CREATE_INVALID_ORDER;
import static constants.ErrorMessages.CREATE_MISSING_FIELD;
import static constants.ErrorMessages.CREATE_MISSING_DESCRIPTION;
import static constants.ErrorMessages.VIEW_OUT_OF_BOUNDS;
import static constants.ErrorMessages.EMPTY_LIST;
import static constants.SuccessMessages.CREATE_SUCCESS;
import static constants.SuccessMessages.DELETE_SUCCESS;
import static constants.SuccessMessages.VIEW_ANSWER_SUCCESS;
import static constants.SuccessMessages.VIEW_QUESTION_SUCCESS;
import static constants.SuccessMessages.EDIT_SUCCESS;
import static constants.SuccessMessages.LIST_SUCCESS;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents a deck that contains a collection of flashcards.
 *
 * <p>This class provides functionalities to manage flashcards within the deck,
 * including adding, viewing, editing, listing, and deleting flashcards.
 * Each deck has a name and maintains a list of flashcards.</p>
 *
 * <p>Instances of this class support various operations on flashcards,
 * such as retrieving a flashcard's question or answer, modifying its content,
 * and handling user input errors related to flashcard management.</p>
 *
 * <p>Logging is implemented to track actions performed on the deck,
 * ensuring better debugging and error handling.</p>
 */
public class Deck {

    private static final Logger logger = Logger.getLogger(Deck.class.getName());
    private String name;
    private final ArrayList<Flashcard> flashcards = new ArrayList<>();

    /**
     * Creates a new deck with the specified name.
     *
     * @param name the name of the deck.
     */
    public Deck(String name) {
        this.name = name.trim();
    }

    /**
     * Returns the name of the deck.
     *
     * @return the deck name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the deck.
     *
     * @param name the new name for the deck.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of flashcards in the deck.
     *
     * @return an {@code ArrayList} of {@code Flashcard} objects.
     */
    public ArrayList<Flashcard> getFlashcards() {
        return flashcards;
    }

    /**
     * Creates a new flashcard
     *
     * <p>The arguments must contain both a question (denoted by "/q") and an answer (denoted by "/a").
     * The question must appear before the answer in the input string.</p>
     *
     * @param arguments A string with the flashcard details
     * @return A success message indicating the flashcard has been created.
     * @throws FlashCLIArgumentException If required fields are missing,
     *         the question and answer are in the wrong order, or either field is empty.
     */

    public String createFlashcard(String arguments) throws FlashCLIArgumentException {
        logger.info("Starting to create a flashcard with arguments: " + arguments);

        boolean containsAllArguments = arguments.contains("/q") && arguments.contains("/a");
        if (!containsAllArguments) {
            logger.warning("Missing required fields: /q or /a");
            throw new FlashCLIArgumentException(CREATE_MISSING_FIELD);
        }

        int questionStart = arguments.indexOf("/q");
        int answerStart = arguments.indexOf("/a");

        assert questionStart >= 0 : "Index of /q should be valid";
        assert answerStart >= 0 : "Index of /a should be valid";

        logger.fine("Index of /q: " + questionStart + ", Index of /a: " + answerStart);

        if (questionStart > answerStart) {
            logger.warning("Invalid order: /q comes after /a");
            throw new FlashCLIArgumentException(CREATE_INVALID_ORDER);
        }

        assert questionStart < answerStart : "Question should come before answer in arguments";

        String question = arguments.substring(questionStart + "/q".length(), answerStart).trim();
        String answer = arguments.substring(answerStart + "/a".length()).trim();

        if (question.isEmpty() || answer.isEmpty()) {
            logger.warning("Missing description: question or answer is empty");
            throw new FlashCLIArgumentException(CREATE_MISSING_DESCRIPTION);
        }

        Flashcard newFlashcard = new Flashcard(question, answer);
        flashcards.add(newFlashcard);

        logger.info("Successfully created a flashcard: Question: " + question + ", Answer: " + answer);
        return String.format(CREATE_SUCCESS,
                newFlashcard.getQuestion(), newFlashcard.getAnswer(), flashcards.size());
    }

    /**
     * Views the flashcard question
     *
     * @param index index of flashcard to view
     * @return the question in the format of VIEW_QUESTION_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String viewFlashcardQuestion(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToView = flashcards.get(arrayIndex);
        assert flashcardToView != null : "flashcard object should not be null";
        String question = flashcardToView.getQuestion();
        assert !question.isEmpty() : "Question should not be empty when viewing flashcards";
        return String.format(VIEW_QUESTION_SUCCESS, index, question);
    }

    /**
     * Views flashcard answer
     * @param index index of flashcard
     * @return the answer in the format of VIEW_ANSWER_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String viewFlashcardAnswer(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToView = flashcards.get(arrayIndex);
        assert flashcardToView != null : "flashcard object should not be null";
        String answer = flashcardToView.getAnswer();
        assert !answer.isEmpty() : "Answer should not be empty when viewing flashcards";
        return String.format(VIEW_ANSWER_SUCCESS, index, answer);
    }

    /**
     * Edits the flashcard
     *
     * @param index     index of flashcard to view
     * @param arguments user inputs containing updated question and answer
     * @return the updated flashcard in the format of EDIT_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String editFlashcard(int index, String arguments)
            throws ArrayIndexOutOfBoundsException,
            FlashCLIArgumentException {
        boolean containsAllArguments = arguments.contains("/q") && arguments.contains("/a");
        if (!containsAllArguments) {
            throw new FlashCLIArgumentException(CREATE_MISSING_FIELD);
        }
        int questionStart = arguments.indexOf("/q");
        int answerStart = arguments.indexOf("/a");

        if (questionStart > answerStart) {
            throw new FlashCLIArgumentException(CREATE_INVALID_ORDER);
        }

        String updatedQuestion = arguments.substring(questionStart + "/q".length(), answerStart).trim();
        String updatedAnswer = arguments.substring(answerStart + "/a".length()).trim();
        if (updatedQuestion.isEmpty() || updatedAnswer.isEmpty()) {
            throw new FlashCLIArgumentException(CREATE_MISSING_DESCRIPTION);
        }
        Flashcard updatedFlashcard = new Flashcard(updatedQuestion, updatedAnswer);

        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;

        Flashcard oldFlashcard = flashcards.get(arrayIndex);
        String oldQuestion = oldFlashcard.getQuestion();
        String oldAnswer = oldFlashcard.getAnswer();
        flashcards.set(arrayIndex, updatedFlashcard);
        return String.format(EDIT_SUCCESS,
                oldQuestion, updatedQuestion, oldAnswer, updatedAnswer);
    }

    /**
     * lists out the questions of the flashcards
     * @return list of questions in the format of LIST_SUCCESS
     * @throws EmptyListException if the list is empty
     */
    public String listFlashcards() throws EmptyListException {
        if (flashcards.isEmpty()) {
            throw new EmptyListException(EMPTY_LIST);
        }

        StringBuilder list = new StringBuilder();
        int i = 1;
        for (Flashcard question : flashcards) {
            String currentQuestion = question.getQuestion();
            list.append(i).append(". ").append(currentQuestion);
            if (i != flashcards.size()) {
                list.append("\n");
            }
            i++;
        }

        return String.format(LIST_SUCCESS, list);
    }

    /**
     * Deletes the flashcard
     * @param index index of flashcard
     * @return the flashcard details in the format of DELETE_SUCCESS
     * @throws ArrayIndexOutOfBoundsException if the index is outside of list size
     */
    public String deleteFlashcard(int index) throws ArrayIndexOutOfBoundsException {
        if (index <= 0 || index > flashcards.size()) {
            throw new ArrayIndexOutOfBoundsException(VIEW_OUT_OF_BOUNDS);
        }
        int arrayIndex = index - 1;
        Flashcard flashcardToDelete = flashcards.get(arrayIndex);
        assert flashcardToDelete != null : "flashcard object should not be null";
        flashcards.remove(arrayIndex);
        return String.format(DELETE_SUCCESS, flashcardToDelete);
    }
}
