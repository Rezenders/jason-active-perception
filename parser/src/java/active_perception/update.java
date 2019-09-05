package active_perception;

import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;
import jason.asSyntax.Trigger.TEOperator;
import jason.asSyntax.Trigger.TEType;

public class update extends DefaultInternalAction {

	@Override
	public boolean suspendIntention()   {
	   return true;
   	}

	@Override
	public Object execute( TransitionSystem ts,
							Unifier un,	Term[] args ) throws Exception {
		Circumstance C = ts.getC();
		Intention si = C.getSelectedIntention();
		Event evt = new Event(new Trigger(TEOperator.add, TEType.test,(Literal)args[0]), si);
		C.addEvent(evt);
		return true;
	}

}
