package active_perception;

import jason.*;
import jason.asSyntax.*;
import jason.asSemantics.*;
import java.util.Iterator;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class isUpdated extends DefaultInternalAction {

    @Override public boolean suspendIntention(){
      return true;
    }

    @Override public boolean canBeUsedInContext() {
        return true;
    }

    @Override public int getMinArgs() {
        return 1;
    }

    @Override public int getMaxArgs() {
        return 1;
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

      boolean isUpdate = false;

      Literal ap_belief = (Literal)args[0];
      Iterator<Literal> ibb =
                          ts.getAg().getBB().getCandidateBeliefs(ap_belief, new Unifier());
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

              int time_limit = (int)((NumberTerm) ap_belief.getAnnot("ap").getTerm(0)).solve();
              Calendar now = new GregorianCalendar();
              long time_elapsed = now.getTimeInMillis() - lu_calendar.getTimeInMillis();
              if(time_elapsed > time_limit){
                ibb.remove();
              }else{
                isUpdate = true;
              }
            }catch(NoValueException e){}
          }
        }
      }

  return isUpdate;
  }
}
