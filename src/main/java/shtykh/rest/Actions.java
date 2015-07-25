package shtykh.rest;

import shtykh.parrots.Parrot;
import shtykh.parrots.what.Phrase;
import shtykh.parrots.what.SomethingWithComments;
import shtykh.util.html.form.build.ActionBuilder;
import shtykh.util.html.form.param.FormParameterSignature;

import static shtykh.util.html.form.param.FormParameterType.*;

/**
 * Created by shtykh on 19/07/15.
 */
public class Actions {
	static ActionBuilder editParrotAction;
	static ActionBuilder setTimeoutAction;
	static ActionBuilder editEventAction;
	
	static {
		try {
			editParrotAction = new ActionBuilder("editParrot")
					.addParam(Parrot.class.getDeclaredField("name"), 
							new FormParameterSignature("name", hidden))
					.addParam(Parrot.class.getDeclaredField("name"),
							new FormParameterSignature("rename", text))
					.addParam(SomethingWithComments.class.getDeclaredField("before"),
							new FormParameterSignature("before", textarea))
					.addParam(Phrase.class.getDeclaredField("cases"), 
							new FormParameterSignature("cases", textarea))
					.addParam(SomethingWithComments.class.getDeclaredField("after"), 
							new FormParameterSignature("after", textarea))
					.addParam(Parrot.class.getDeclaredField("color"), 
							new FormParameterSignature("color", color));
			
			setTimeoutAction = new ActionBuilder("setTimeOut")
					.addParam(Bot.class.getDeclaredField("timeout"), 
							new FormParameterSignature("timeout", number));
			
			editEventAction = new ActionBuilder("editEvent")
					.addParam(Event.class.getDeclaredField("isForced"), 
							new FormParameterSignature("force", checkbox))
					.addParam(Event.class.getDeclaredField("id"), 
							new FormParameterSignature("id", hidden))
					.addParam(Event.class.getDeclaredField("time"), 
							new FormParameterSignature("time", datetime_local));
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
