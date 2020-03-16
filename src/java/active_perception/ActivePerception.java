package active_perception;

import jason.asSemantics.Agent;
import jason.asSyntax.*;

import java.util.List;
import java.util.ArrayList;

public class ActivePerception{

	public static List<Literal> getApBeliefs(Literal context){
		List<Literal> beliefs = new ArrayList<Literal>();
		Integer arity = context.getArity();

		if(arity==0 && !context.getAnnots("ap").isEmpty()){
			beliefs.add(context);
		}else if(arity > 0){
			beliefs.addAll(getApBeliefs((Literal)context.getTermsArray()[0]));
			beliefs.addAll(getApBeliefs((Literal)context.getTermsArray()[1]));
		}
		return beliefs;
	}
}
