package active_perception;

import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;
import jason.asSyntax.Trigger.TEOperator;
import jason.asSyntax.Trigger.TEType;
import java.util.Iterator;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class update extends ConcurrentInternalAction {

	@Override
	public Object execute( TransitionSystem ts,
							Unifier un,	Term[] args ) throws Exception {

		final String key = suspendInt(ts, "update", 0);

		startInternalAction(ts, new Runnable() { // to not block the agent thread, start a thread that performs the task and resume the intention latter
            public void run(){
				Literal ap_belief = (Literal)args[0];

				//Check if belief was updated within time limit
				Iterator<Literal> ibb =
		                        ts.getAg().getBB().getCandidateBeliefs((Literal)args[0], new Unifier());
				if(ibb != null){
					while(ibb!=null && ibb.hasNext()){
						Literal belief = ibb.next();
						if(belief.equalsAsStructure(ap_belief)){
							try{
								Literal lu_literal = belief.getAnnot("lu");
								Calendar lu_calendar = new GregorianCalendar();
								lu_calendar.set(Calendar.HOUR_OF_DAY,(int)((NumberTerm)lu_literal.getTerm(0)).solve());
								lu_calendar.set(Calendar.MINUTE, (int)((NumberTerm)lu_literal.getTerm(1)).solve());
								lu_calendar.set(Calendar.SECOND,(int)((NumberTerm)lu_literal.getTerm(2)).solve());
								lu_calendar.set(Calendar.MILLISECOND,(int)((NumberTerm)lu_literal.getTerm(3)).solve());
								lu_calendar.getTime();

								int time_limit = (int)((NumberTerm) belief.getAnnot("ap").getTerm(0)).solve();
								Calendar now = new GregorianCalendar();
								long time_elapsed = now.getTimeInMillis() - lu_calendar.getTimeInMillis();
								if(time_elapsed > time_limit){
									ibb.remove();
									updateBelief(ts, ap_belief);
								}else{
									resumeInt(ts, key);
								}
							}catch(NoValueException e){

							}
						}else{
							updateBelief(ts, ap_belief);
						}
					}
				}else{
					updateBelief(ts, ap_belief);
				}
            }
        });

		return true;
	}

	private void updateBelief(TransitionSystem ts, Literal bel){
		Circumstance C = ts.getC();
		Intention si = C.getSelectedIntention();
		Event evt = new Event(new Trigger(TEOperator.add, TEType.test,bel), si);
		C.addEvent(evt);
	}

	/** called back when some intention should be resumed/failed by timeout */
    @Override
    public void timeout(TransitionSystem ts, String intentionKey) {
		System.out.println("Time out in active_perception.update!");
        failInt(ts, intentionKey);
    }
}
