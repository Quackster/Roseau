package org.alexdev.roseau.game.room.entity;

import org.alexdev.roseau.game.GameVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatUtilityTest {

    @BeforeEach
    void setUp() {
        GameVariables.CHAT_FILTER_WORDS = "fuck,sex,damn";
        GameVariables.CHAT_FILTER_REPLACEMENT = "bobba";
    }

    @Test
    void noFilterConfigured_returnsWordsUnchanged() {
        GameVariables.CHAT_FILTER_WORDS = null;

        String[] input = {"hello", "world"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"hello", "world"}, result);
    }

    @Test
    void emptyFilterList_returnsWordsUnchanged() {
        GameVariables.CHAT_FILTER_WORDS = "";

        String[] input = {"hello", "world"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"hello", "world"}, result);
    }

    @Test
    void noReplacementConfigured_returnsWordsUnchanged() {
        GameVariables.CHAT_FILTER_REPLACEMENT = null;

        String[] input = {"fuck", "world"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"fuck", "world"}, result);
    }

    @Test
    void exactMatch_isReplaced() {
        String[] input = {"fuck", "hello"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba", "hello"}, result);
    }

    @Test
    void caseInsensitiveMatch_isReplaced() {
        String[] input = {"FUCK", "Fuck"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba", "bobba"}, result);
    }

    @Test
    void dots_areStrippedBeforeMatch() {
        String[] input = {"f.uck"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }

    @Test
    void dashes_areStrippedBeforeMatch() {
        String[] input = {"f-u-c-k"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }

    @Test
    void underscores_areStrippedBeforeMatch() {
        String[] input = {"f_u_c_k"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }

    @Test
    void numbers_areStrippedBeforeMatch() {
        String[] input = {"f0uck"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }

    @Test
    void mixedSeparators_areStrippedBeforeMatch() {
        String[] input = {"f...u...c...k"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }

    @Test
    void multipleSeparatorsTogether_areStrippedBeforeMatch() {
        String[] input = {"f.u-c_k"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }

    @Test
    void nonBannedWord_isUnchanged() {
        String[] input = {"hello"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"hello"}, result);
    }

    @Test
    void multipleWords_someFiltered() {
        String[] input = {"hello", "fuck", "world", "sex"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"hello", "bobba", "world", "bobba"}, result);
    }

    @Test
    void multipleBannedWords_allReplaced() {
        GameVariables.CHAT_FILTER_WORDS = "fuck,sex,damn";

        String[] input = {"fuck", "sex", "damn"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba", "bobba", "bobba"}, result);
    }

    @Test
    void whitespaceInFilterList_isTrimmed() {
        GameVariables.CHAT_FILTER_WORDS = " fuck , sex ";

        String[] input = {"fuck", "sex"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba", "bobba"}, result);
    }

    @Test
    void singleBannedWordInMultiWordSentence() {
        String[] input = {"this", "is", "a", "fuck", "sentence"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"this", "is", "a", "bobba", "sentence"}, result);
    }

    @Test
    void asterisks_areStrippedBeforeMatch() {
        String[] input = {"f*ck"};
        String[] result = ChatUtility.filterWords(input);

        assertArrayEquals(new String[]{"bobba"}, result);
    }
}
