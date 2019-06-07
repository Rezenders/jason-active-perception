package active_perception;

import jason.asSemantics.*;
import jason.asSyntax.*;
import java.util.logging.*;
import static jason.asSyntax.ASSyntax.*;


public class reveal extends ConcurrentInternalAction {

    private Logger logger = Logger.getLogger("active_perception."+reveal.class.getName());

    @Override
    public Object execute(final TransitionSystem ts, Unifier un, final Term[] args) throws Exception {
        try {
            Term belief = args[0];

    		Literal plan;
    		try {
    		    plan = (Literal)args[1];
    		} catch ( IndexOutOfBoundsException e ) {
    		    plan = createLiteral("active_perception_plan");
    		}

			NumberTerm time;
			try {
				if(args[2].isNumeric()){
					time = (NumberTerm)args[2];
				}else{
					time = createNumber(5000);
				}
			}catch ( IndexOutOfBoundsException e ) {
    		    time = createNumber(5000);
    		}

            Event goal = ts.getC().addAchvGoal(plan, Intention.EmptyInt);
            final String key = suspendInt(ts, "reveal", (int)time.solve()); //TIME?

            startInternalAction(ts, new Runnable() {
                public void run() {
                    new WaitGoal(goal.getTrigger(), ts, key);
                }
            });

			

            return true;
        } catch (Exception e) {
            logger.warning("Error in internal action 'active_perception.reveal'! "+e);
        }
        return false;
    }

    /** called back when some intention should be resumed/failed by timeout */
    @Override
    public void timeout(TransitionSystem ts, String intentionKey) {
		System.out.println("Time out in reveal!");
        failInt(ts, intentionKey);
    }
}

class WaitGoal implements CircumstanceListener{
	private Trigger te;
    private TransitionSystem ts;
    private Circumstance c;
    private Intention si;
    final String key;
    reveal r = new reveal();

	WaitGoal(Trigger te, TransitionSystem ts, String key){
		this.te = te;
		this.ts = ts;
        this.key = key;
		c = ts.getC();
		si = c.getSelectedIntention();
		c.addEventListener(this);
	}

	public void eventAdded(Event e) {}

	public void intentionDropped(Intention i) {}

	public void intentionAdded(Intention i) {
		IntendedMeans im = i.peek();
		if(im.isFinished()==true && te.equals(im.getTrigger())){
            r.resume(ts, key, false, null);
		}
	}
	public void intentionResumed(Intention i) {}
	public void intentionSuspended(Intention i, String reason) {}

}
