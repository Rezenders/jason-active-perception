package active_perception;

import jason.asSemantics.*;
import jason.asSyntax.*;
import static jason.asSyntax.ASSyntax.*;

import java.util.logging.*;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;


public class reveal_concurrent extends ConcurrentInternalAction {
	CountDownLatch doneSignal = new CountDownLatch(1);

    // private Logger logger = Logger.getLogger("active_perception."+reveal.class.getName());
	@Override
	public boolean canBeUsedInContext() {
        return true;
    }

    @Override
    public Object execute(final TransitionSystem ts, Unifier un, final Term[] args) throws Exception {
        try {
            Atom belief_aux = (Atom)args[0];
			Literal belief = createLiteral(belief_aux.getFunctor());
			belief.addAnnot(ts.getAg().getBB().TSelf); //Change to active_perception?

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
                    new WaitGoalConcurrent(goal.getTrigger(), ts, key, belief);
                }
            });
			System.out.println("teste");
            return true;
        } catch (Exception e) {
            // logger.warning("Error in internal action 'active_perception.reveal'! "+e);
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
class WaitGoalConcurrent implements CircumstanceListener{
	private Trigger te;
    private TransitionSystem ts;
    private Circumstance c;
    private Intention si;
    final String key;
	Literal belief;
    reveal_concurrent r = new reveal_concurrent();

	WaitGoalConcurrent(Trigger te, TransitionSystem ts, String key, Literal belief){
		this.te = te;
		this.ts = ts;
        this.key = key;
		this.belief = belief;
		c = ts.getC();
		si = c.getSelectedIntention();
		c.addEventListener(this);
	}

	public void eventAdded(Event e) {}

	public void intentionDropped(Intention i) {}

	public void intentionAdded(Intention i) {
		IntendedMeans im = i.peek();
		if(im.isFinished()==true && te.equals(im.getTrigger())){
			boolean isEqual = false;
			Iterator<Literal> ibb = ts.getAg().getBB().getCandidateBeliefs(new PredicateIndicator("allowed_to_fly",0));
			while (ibb != null && ibb.hasNext()) {
				Literal l = ibb.next();
                if(l.equals(belief)){
					r.resumeInt(ts, key);
					isEqual = true;
				}
			}
			if(!isEqual){
				r.failInt(ts,key);
			}
		}
	}
	public void intentionResumed(Intention i) {}
	public void intentionSuspended(Intention i, String reason) {}

}
