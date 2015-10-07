package shtykh.quedit.numerator;

import shtykh.quedit.question.Question;

import java.util.List;

/**
 * Created by shtykh on 05/10/15.
 */
public interface QuestionNumerator extends Numerator {
	default void renumber(List<Question> questions) {
		for (int i = 0; i < questions.size(); i++) {
			questions.get(i).setNumber(getNumber(i));
		}
	}
}
